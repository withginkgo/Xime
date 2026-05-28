package com.kingzcheung.xime.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 数字键 → 对应的大写字母（发往 Rime t9_pinyin schema）
 */
private val DIGIT_TO_T9_LETTER = mapOf(
    "2" to "A", "3" to "D", "4" to "G", "5" to "J",
    "6" to "M", "7" to "P", "8" to "T", "9" to "W"
)

/**
 * 拼音九宫格键盘布局（T9）
 *
 * 直接发送大写字母键码到 Rime，由 t9_pinyin schema 处理转换。
 *
 * @param onKeyPress 按键回调（大写字母 A/D/G/J/M/P/T/W、或 delete/space/enter/symbol/mode_change/emoji/'）
 */
@Composable
fun NineKeyKeyboardLayout(
    onKeyPress: (String) -> Unit,
    keyBackgroundColor: Color,
    keyTextColor: Color,
    specialKeyBackgroundColor: Color,
    keyboardBackgroundColor: Color = Color.Transparent,
    modifier: Modifier = Modifier,
    onKeyPressDown: ((String) -> Unit)? = null
) {
    Box(modifier = modifier.background(keyboardBackgroundColor)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(keyboardBackgroundColor)
                .padding(vertical = 6.dp, horizontal = 4.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(3f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // 左侧列：符号键
                    val leftItems = listOf("，", "。", "？", "！")
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .padding(end = 3.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        leftItems.forEachIndexed { index, item ->
                            PunctuationKey(
                                text = item,
                                onClick = { onKeyPress(item) },
                                backgroundColor = keyBackgroundColor,
                                textColor = keyTextColor,
                                modifier = Modifier.weight(1f),
                                onPress = { onKeyPressDown?.invoke(item) },
                                isFirst = index == 0,
                                isLast = index == leftItems.lastIndex
                            )
                        }
                    }

                    // 9键网格
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(4f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            NineKeyButton(
                                digit = "1", letters = "分词",
                                onClick = { onKeyPress("'") },
                                backgroundColor = keyBackgroundColor, textColor = keyTextColor,
                                modifier = Modifier.weight(1f),
                                onPress = { onKeyPressDown?.invoke("'") }
                            )
                            NineKeyButton(digit = "2", letters = "ABC", onClick = { onKeyPress("A") }, backgroundColor = keyBackgroundColor, textColor = keyTextColor, modifier = Modifier.weight(1f), onPress = { onKeyPressDown?.invoke("2") })
                            NineKeyButton(digit = "3", letters = "DEF", onClick = { onKeyPress("D") }, backgroundColor = keyBackgroundColor, textColor = keyTextColor, modifier = Modifier.weight(1f), onPress = { onKeyPressDown?.invoke("3") })
                            SwipeableIconKeyButton(
                                icon = rememberVectorPainter(Icons.AutoMirrored.Filled.Backspace),
                                onClick = { onKeyPress("delete") },
                                backgroundColor = specialKeyBackgroundColor, iconColor = keyTextColor,
                                modifier = Modifier.weight(1f),
                                onSwipe = { },
                                onLongClick = { onKeyPress("delete") },
                                onPress = { onKeyPressDown?.invoke("delete") }
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            NineKeyButton(digit = "4", letters = "GHI", onClick = { onKeyPress("G") }, backgroundColor = keyBackgroundColor, textColor = keyTextColor, modifier = Modifier.weight(1f), onPress = { onKeyPressDown?.invoke("4") })
                            NineKeyButton(digit = "5", letters = "JKL", onClick = { onKeyPress("J") }, backgroundColor = keyBackgroundColor, textColor = keyTextColor, modifier = Modifier.weight(1f), onPress = { onKeyPressDown?.invoke("5") })
                            NineKeyButton(digit = "6", letters = "MNO", onClick = { onKeyPress("M") }, backgroundColor = keyBackgroundColor, textColor = keyTextColor, modifier = Modifier.weight(1f), onPress = { onKeyPressDown?.invoke("6") })
                            KeyButton(text = "换行", onClick = { onKeyPress("enter") }, backgroundColor = specialKeyBackgroundColor, textColor = keyTextColor, modifier = Modifier.weight(1f), onPress = { onKeyPressDown?.invoke("enter") })
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            NineKeyButton(digit = "7", letters = "PQRS", onClick = { onKeyPress("P") }, backgroundColor = keyBackgroundColor, textColor = keyTextColor, modifier = Modifier.weight(1f), onPress = { onKeyPressDown?.invoke("7") })
                            NineKeyButton(digit = "8", letters = "TUV", onClick = { onKeyPress("T") }, backgroundColor = keyBackgroundColor, textColor = keyTextColor, modifier = Modifier.weight(1f), onPress = { onKeyPressDown?.invoke("8") })
                            NineKeyButton(digit = "9", letters = "WXYZ", onClick = { onKeyPress("W") }, backgroundColor = keyBackgroundColor, textColor = keyTextColor, modifier = Modifier.weight(1f), onPress = { onKeyPressDown?.invoke("9") })
                            IconKeyButton(icon = rememberVectorPainter(Icons.Default.EmojiEmotions), onClick = { onKeyPress("emoji") }, backgroundColor = specialKeyBackgroundColor, iconColor = keyTextColor, modifier = Modifier.weight(1f), onPress = { onKeyPressDown?.invoke("emoji") })
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    KeyButton(text = "符号", onClick = { onKeyPress("symbol") }, backgroundColor = specialKeyBackgroundColor, textColor = keyTextColor, modifier = Modifier.weight(1f), onPress = { onKeyPressDown?.invoke("symbol") })
                    KeyButton(text = "123", onClick = { onKeyPress("mode_change") }, backgroundColor = specialKeyBackgroundColor, textColor = keyTextColor, modifier = Modifier.weight(1f), onPress = { onKeyPressDown?.invoke("mode_change") })
                    KeyButton(text = "空格", onClick = { onKeyPress("space") }, backgroundColor = specialKeyBackgroundColor, textColor = keyTextColor, modifier = Modifier.weight(2f), onPress = { onKeyPressDown?.invoke("space") })
                    KeyButton(text = "确定", onClick = { onKeyPress("enter") }, backgroundColor = specialKeyBackgroundColor, textColor = keyTextColor, modifier = Modifier.weight(1.2f), onPress = { onKeyPressDown?.invoke("enter") })
                }
            }
        }
    }
}

/**
 * 标点符号按键（左侧列，连成一体无间隙）
 */
@Composable
private fun PunctuationKey(
    text: String,
    onClick: () -> Unit,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    onPress: (() -> Unit)? = null,
    isFirst: Boolean = false,
    isLast: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val shape = RoundedCornerShape(
        topStart = if (isFirst) 8.dp else 0.dp,
        topEnd = if (isFirst) 8.dp else 0.dp,
        bottomStart = if (isLast) 8.dp else 0.dp,
        bottomEnd = if (isLast) 8.dp else 0.dp
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (isPressed) backgroundColor.copy(alpha = 0.7f) else backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    onPress?.invoke()
                    onClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * 九宫格按键：数字置右上角，字母居中显示
 */
@Composable
private fun NineKeyButton(
    digit: String,
    letters: String,
    onClick: () -> Unit,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    onPress: (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        modifier = modifier
            .fillMaxHeight()
            .shadow(1.dp, RoundedCornerShape(8.dp), ambientColor = Color(0x80000000), spotColor = Color(0x80000000))
            .clip(RoundedCornerShape(8.dp))
            .background(if (isPressed) backgroundColor.copy(alpha = 0.7f) else backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    onPress?.invoke()
                    onClick()
                }
            )
    ) {
        if (digit.isNotEmpty()) {
            Text(
                text = digit,
                color = textColor.copy(alpha = 0.5f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 0.dp, end = 6.dp)
            )
        }
        if (letters.isNotEmpty()) {
            Text(
                text = letters.uppercase(),
                color = textColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
