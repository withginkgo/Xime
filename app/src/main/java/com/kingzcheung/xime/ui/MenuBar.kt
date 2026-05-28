package com.kingzcheung.xime.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.Assignment
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.twotone.Assignment
import androidx.compose.material.icons.twotone.Bolt
import androidx.compose.material.icons.twotone.BorderTop
import androidx.compose.material.icons.twotone.DarkMode
import androidx.compose.material.icons.twotone.ElectricBolt
import androidx.compose.material.icons.twotone.EmojiEmotions
import androidx.compose.material.icons.twotone.Keyboard
import androidx.compose.material.icons.twotone.LightMode
import androidx.compose.material.icons.twotone.Quickreply
import androidx.compose.material.icons.twotone.Rotate90DegreesCcw
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material.icons.twotone.SettingsOverscan
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class MenuItem(
    val icon: Painter,
    val label: String,
    val action: () -> Unit
)

@Composable
fun MenuBar(
    isVisible: Boolean,
    isDarkTheme: Boolean,
    backgroundColor: Color,
    onDismiss: () -> Unit,
    onClipboard: () -> Unit,
    onQuickSend: () -> Unit,
    onKeyboardResize: () -> Unit,
    onEmoji: () -> Unit,
    onReloadConfig: () -> Unit,
    onSettings: () -> Unit,
    onSchemaList: () -> Unit,
    onToggleDarkMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return
    
    val textColor = if (isDarkTheme) Color(0xFFE8EAED) else Color(0xFF202124)
    val itemBgColor = if (isDarkTheme) Color(0xFF45474A) else Color.White
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    
    val menuItems = listOf(
        MenuItem(rememberVectorPainter(Icons.AutoMirrored.TwoTone.Assignment), "剪贴板", onClipboard),
        MenuItem(rememberVectorPainter(Icons.TwoTone.Quickreply), "快捷发送", onQuickSend),
        MenuItem(rememberVectorPainter(Icons.TwoTone.SettingsOverscan), "键盘调节", onKeyboardResize),
        MenuItem(rememberVectorPainter(Icons.TwoTone.EmojiEmotions), "表情", onEmoji),
        MenuItem(rememberVectorPainter(if (isDarkTheme) Icons.TwoTone.LightMode else Icons.TwoTone.DarkMode), if (isDarkTheme) "浅色模式" else "深色模式", onToggleDarkMode),
        MenuItem(rememberVectorPainter(Icons.TwoTone.Rotate90DegreesCcw), "部署方案", onReloadConfig),
        MenuItem(rememberVectorPainter(Icons.TwoTone.Keyboard), "键盘选择", onSchemaList),
        MenuItem(rememberVectorPainter(Icons.TwoTone.Settings), "设置", onSettings)
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(horizontal = if (isLandscape) 50.dp else 16.dp, vertical = if (isLandscape) 8.dp else 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isLandscape) {
            // 横屏：一行 8 列
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                menuItems.forEach { item ->
                    MenuItemButton(
                        item = item,
                        bgColor = itemBgColor,
                        textColor = textColor,
                        modifier = Modifier.weight(1f),
                        isLandscape = true
                    )
                }
            }
        } else {
            // 竖屏：2 行 × 4 列
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                menuItems.chunked(4).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { item ->
                            MenuItemButton(
                                item = item,
                                bgColor = itemBgColor,
                                textColor = textColor,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        repeat(4 - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MenuItemButton(
    item: MenuItem,
    bgColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    isLandscape: Boolean = false
) {
    Column(
        modifier = modifier
            .then(if (isLandscape) Modifier.height(72.dp) else Modifier.aspectRatio(1f))
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable { item.action() }
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = item.icon,
            contentDescription = item.label,
            tint = textColor.copy(alpha = 0.7f),
            modifier = Modifier.size(if (isLandscape) 18.dp else 24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = item.label,
            color = textColor,
            fontSize = if (isLandscape) 9.sp else 10.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}