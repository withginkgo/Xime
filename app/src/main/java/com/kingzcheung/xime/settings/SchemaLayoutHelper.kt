package com.kingzcheung.xime.settings

import android.content.Context
import android.util.Log
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import java.io.File

/**
 * 解析 schema 文件中的自定义字段 `x_keyboard_layouts`
 *
 * Rime 引擎忽略不识别的 YAML 字段，因此我们可以利用该机制
 * 在 schema 文件中声明方案支持的键盘布局。
 */
object SchemaLayoutHelper {
    private const val TAG = "SchemaLayoutHelper"
    private val yaml = Yaml(configuration = YamlConfiguration(strictMode = false))

    fun getSupportedLayouts(context: Context, schemaId: String): List<KeyboardLayout> {
        val content = readSchemaFile(context, schemaId) ?: return defaultLayouts()
        return parseLayouts(content)
    }

    fun supportsNineKey(context: Context, schemaId: String): Boolean {
        return getSupportedLayouts(context, schemaId).any { it == KeyboardLayout.NINEKEY }
    }

    private fun parseLayouts(content: String): List<KeyboardLayout> {
        return try {
            val wrapper = yaml.decodeFromString<SchemaYaml>(content)
            wrapper.x_keyboard_layouts?.map { KeyboardLayout.fromId(it.id) }
                ?: defaultLayouts()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to parse x_keyboard_layouts", e)
            defaultLayouts()
        }
    }

    private fun readSchemaFile(context: Context, schemaId: String): String? {
        val isBuiltIn = context.assets.list("rime")?.any { it == "$schemaId.schema.yaml" } == true
        if (isBuiltIn) {
            return try {
                context.assets.open("rime/$schemaId.schema.yaml")
                    .bufferedReader().readText()
            } catch (e: Exception) {
                Log.w(TAG, "Failed to read built-in schema: $schemaId", e)
                null
            }
        }
        val sharedDir = File(context.filesDir, "rime/shared")
        val schemaFile = File(sharedDir, "$schemaId.schema.yaml")
        if (schemaFile.exists()) {
            return try { schemaFile.readText() }
            catch (e: Exception) {
                Log.w(TAG, "Failed to read schema file: ${schemaFile.absolutePath}", e)
                null
            }
        }
        return null
    }

    private fun defaultLayouts(): List<KeyboardLayout> = listOf(KeyboardLayout.FULL)

    @Serializable
    private data class SchemaYaml(
        val x_keyboard_layouts: List<LayoutEntry>? = null
    )

    @Serializable
    private data class LayoutEntry(
        val id: String,
        val name: String = ""
    )
}
