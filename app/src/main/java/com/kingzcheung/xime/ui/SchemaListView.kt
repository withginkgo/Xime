package com.kingzcheung.xime.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kingzcheung.xime.settings.SchemaInfo
import com.kingzcheung.xime.settings.SettingsPreferences

@Composable
fun SchemaListView(
    schemas: List<SchemaInfo>,
    currentSchemaId: String,
    currentLayoutId: String?,
    isDarkTheme: Boolean,
    backgroundColor: Color,
    accentColor: Color,
    onSelectSchema: (String, String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val itemBgColor = if (isDarkTheme) Color(0xFF45474A) else Color.White
    val textColor = if (isDarkTheme) Color(0xFFE8EAED) else Color(0xFF202124)
    val subTextColor = if (isDarkTheme) Color(0xFF9AA0A6) else Color(0xFF5F6368)
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val columns = if (isLandscape) 8 else 4
    val context = androidx.compose.ui.platform.LocalContext.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(horizontal = if (isLandscape) 50.dp else 16.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (schemas.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "没有可用的键盘方案",

                    color = subTextColor,
                    fontSize = 13.sp
                )
            }
        } else {
            val rows = schemas.chunked(columns)
            rows.forEachIndexed { rowIndex, rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(if (isLandscape) 8.dp else 12.dp)
                ) {
                    rowItems.forEach { schema ->
                        // 选中判定：schemaId + displayLayoutId 都匹配
                        val isSelected = schema.schemaId == currentSchemaId &&
                            (schema.displayLayoutId == null || schema.displayLayoutId == currentLayoutId)

                        SchemaGridItem(
                            schema = schema,
                            isSelected = isSelected,
                            bgColor = itemBgColor,
                            textColor = textColor,
                            subTextColor = subTextColor,
                            accentColor = accentColor,
                            layoutHint = null,
                            onSelect = { onSelectSchema(schema.schemaId, schema.displayLayoutId) },
                            modifier = Modifier.weight(1f),
                            isLandscape = isLandscape
                        )
                    }
                    if (rowItems.size < columns) {
                        repeat(columns - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
                if (rowIndex < rows.size - 1) {
                    Spacer(modifier = Modifier.height(if (isLandscape) 8.dp else 12.dp))
                }
            }
        }
    }
}

@Composable
private fun SchemaGridItem(
    schema: SchemaInfo,
    isSelected: Boolean,
    bgColor: Color,
    textColor: Color,
    subTextColor: Color,
    accentColor: Color,
    layoutHint: String? = null,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
    isLandscape: Boolean = false
) {
    Column(
        modifier = modifier
            .then(if (isLandscape) Modifier.height(72.dp) else Modifier.aspectRatio(1f))
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable { onSelect() }
            .padding(if (isLandscape) 4.dp else 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Keyboard,
            contentDescription = schema.name,
            tint = if (isSelected) accentColor else textColor,
            modifier = Modifier.size(if (isLandscape) 18.dp else 24.dp)
        )
        Spacer(modifier = Modifier.height(if (isLandscape) 2.dp else 4.dp))
        Text(
            text = schema.name,
            color = if (isSelected) accentColor else textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        if (layoutHint != null) {
            Text(
                text = layoutHint,
                color = if (isSelected) accentColor.copy(alpha = 0.7f) else subTextColor,
                fontSize = 8.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}