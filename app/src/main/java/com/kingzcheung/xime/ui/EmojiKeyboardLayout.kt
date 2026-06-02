package com.kingzcheung.xime.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kingzcheung.xime.clipboard.ClipboardManager
import com.kingzcheung.xime.plugin.ExtensionManager
import com.kingzcheung.xime.plugin.core.api.CategoryLayoutConfig
import com.kingzcheung.xime.plugin.core.api.EmojiItem
import com.kingzcheung.xime.plugin.core.api.PluginIcon

data class EmojiCategory(
    val name: String,
    val icon: String,
    val pluginIcon: PluginIcon? = null,
    val emojis: List<String>,
    val isPlugin: Boolean = false,
    val pluginId: String? = null,
    val emojiItems: List<EmojiItem>? = null,
    val layoutConfig: CategoryLayoutConfig? = null
)

object EmojiData {
    val categories = listOf(
        EmojiCategory(
            name = "笑脸",
            icon = "😊",
            emojis = listOf(
                "😀", "😃", "😄", "😁", "😆", "😅", "🤣", "😂", "🙂", "🙃",
                "😉", "😊", "😇", "🥰", "😍", "🤩", "😘", "😗", "😚", "😙",
                "🥲", "😋", "😛", "😜", "🤪", "😝", "🤑", "🤗", "🤭", "🤫",
                "🤔", "🤐", "🤨", "😐", "😑", "😶", "😏", "😒", "🙄", "😬",
                "🤥", "😌", "😔", "😪", "🤤", "😴", "😷", "🤒", "🤕", "🤢",
                "🤮", "🤧", "🥵", "🥶", "🥴", "😵", "🤯", "🤠", "🥳", "🥸",
                "😎", "🤓", "🧐", "😕", "😟", "🙁", "☹️", "😮", "😯", "😲",
                "😳", "🥺", "😦", "😧", "😨", "😰", "😥", "😢", "😭", "😱",
                "😖", "😣", "😞", "😓", "😩", "😫", "🥱", "😤", "😡", "😠",
                "🤬", "😈", "👿", "💀", "☠️", "💩", "🤡", "👹", "👺", "👻"
            )
        ),
        EmojiCategory(
            name = "手势",
            icon = "👋",
            emojis = listOf(
                "👋", "🤚", "🖐️", "✋", "🖖", "👌", "🤌", "🤏", "✌️", "🤞",
                "🤟", "🤘", "🤙", "👈", "👉", "👆", "🖕", "👇", "☝️", "👍",
                "👎", "✊", "👊", "🤛", "🤜", "👏", "🙌", "👐", "🤲", "🤝",
                "🙏", "✍️", "💅", "🤳", "💪", "🦾", "🦿", "🦵", "🦶", "👂",
                "🦻", "👃", "🧠", "🫀", "🫁", "🦷", "🦴", "👀", "👁️", "👅",
                "👄", "💪", "🦵", "🦶", "👂", "🦻", "👃", "🧠", "🫀", "🫁"
            )
        ),
        EmojiCategory(
            name = "动物",
            icon = "🐶",
            emojis = listOf(
                "🐶", "🐱", "🐭", "🐰", "🦊", "🐻", "🐼", "🐨", "🐯",
                "🦁", "🐮", "🐷", "🐸", "🐵", "🙈", "🙉", "🙊", "🐒", "🐔",
                "🐧", "🐦", "🐤", "🐣", "🐥", "🦆", "🦅", "🦉", "🦇", "🐺",
                "🐗", "🐴", "🦄", "🐝", "🐛", "🦋", "🐌", "🐞", "🐜", "🦟",
                "🦗", "🕷️", "🦂", "🐢", "🐍", "🦎", "🦖", "🦕", "🐙", "🦑",
                "🦐", "🦞", "🦀", "🐡", "🐠", "🐟", "🐬", "🐳", "🐋", "🦈"
            )
        ),
        EmojiCategory(
            name = "食物",
            icon = "🍎",
            emojis = listOf(
                "🍎", "🍐", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🫐", "🍈",
                "🍒", "🍑", "🥭", "🍍", "🥥", "🥝", "🍅", "🍆", "🥑", "🥦",
                "🥬", "🥒", "🌶️", "🫑", "🌽", "🥕", "🧄", "🧅", "🥔", "🍠",
                "🍞", "🍩", "🥖", "🥖", "🍪", "🧀", "🥚", "🍳", "🧈", "🥞",
                "🧇", "🥓", "🥩", "🍗", "🍖", "🦴", "🌭", "🍔", "🍟", "🍕",
                "🫓", "🥪", "🌯", "🥗", "🌮", "🍙", "🍚", "🍲", "🥘", "🧀"
            )
        ),
        EmojiCategory(
            name = "活动",
            icon = "⚽",
            emojis = listOf(
                "⚽", "🏀", "🏈", "⚾", "🥎", "🎾", "🏐", "🏉", "🥏", "🎱",
                "🪀", "🏓", "🏸", "🏒", "🏑", "🥍", "🏏", "🥏", "🪃", "🥅",
                "⛳", "🪁", "🏹", "🎣", "🤿", "🥊", "🥋", "🏃", "🛹", "🛼",
                "🪂", "⛸️", "🥌", "⛷️", "🏂", "⛸️", "🪂", "🏋️", "🤼", "🤸",
                "🤺", "🤾", "🥏", "⛳", "🏇", "🧘", "🏄", "🏊", "🤽", "🚣",
                "🧗", "🚵", "🚴", "🎖️", "🏆", "🥇", "🥈", "🥉", "🎖️", "🎪"
            )
        ),
        EmojiCategory(
            name = "物品",
            icon = "💻",
            emojis = listOf(
                "⌚", "📱", "☎️", "💻", "⌨️", "🖥️", "🖨️", "🖱️", "🖲️", "🕹️",
                "🗜️", "💿", "💾", "📀", "📀", "📼", "📷", "📸", "📹", "🎬",
                "📽️", "🎞️", "☎️", "📞", "📟", "📠", "📺", "📻", "🎤", "🎛️",
                "🎮", "🧭", "⏱️", "⏲️", "⏰", "🕰️", "⏳", "⌛", "📡", "🔋",
                "🔌", "💡", "🔦", "🕯️", "🪔", "🧯", "🛢️", "💵", "💵", "💴",
                "💶", "💷", "👛", "💳", "💎", "⚖️", "🧰", "🔧", "🔨", "⛏️"
            )
        ),
        EmojiCategory(
            name = "符号",
            icon = "❤️",
            emojis = listOf(
                "❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔",
                "❗", "💕", "💕", "💓", "💗", "✨", "💘", "🎁", "♻️", "☮️",
                "✝️", "☪️", "🕉️", "☸️", "✡️", "🔯", "✡️", "☯️", "☦️", "🙏",
                "⛎", "♈", "♉", "♊", "♋", "♌", "♎", "♏", "♐", "♑",
                "♒", "♓", "🪪", "⚛️", "✅", "☢️", "☣️", "📵", "📱",
                "🈶", "🈚", "🈸", "🈺", "🌙", "⭐", "⚔️", "🏵️", "🏅", "㊙️"
            )
        )
    )
}

@Composable
fun EmojiKeyboardLayout(
    onEmojiSelect: (String) -> Unit,
    onImageEmojiSelect: ((String) -> Unit)? = null,
    onBack: () -> Unit,
    backgroundColor: Color,
    textColor: Color,
    bottomPaddingDp: Int = 0,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = remember { ClipboardManager.getInstance(context) }

    var selectedCategoryIndex by remember { mutableStateOf(0) }

    val allCategories by ExtensionManager.emojiCategoriesFlow.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val emojiColumns = if (isLandscape) 15 else 8

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
    ) {
        // 导航区
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = if (isLandscape) 50.dp else 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        if (textColor == Color(0xFFE8EAED)) Color(0xFF374151) else Color(
                            0xFFF3F4F6
                        )
                    )
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "返回",
                    tint = textColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = if (isLandscape) 50.dp else 4.dp)
                .padding(bottom = 4.dp)
        ) {
            val currentCategory =
                allCategories.getOrElse(selectedCategoryIndex) { allCategories[0] }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                if (currentCategory.isPlugin && currentCategory.emojiItems != null) {
                    val config = currentCategory.layoutConfig
                    val defaultCols =
                        if (currentCategory.emojiItems.any { it.imageUrl != null }) 6 else 8
                    val columns = config?.columns ?: if (isLandscape) 15 else defaultCols
                    val itemHeightDp = config?.itemHeightDp
                        ?: (if (currentCategory.emojiItems.any { it.imageUrl != null }) 60 else 40)

                    currentCategory.emojiItems.chunked(columns).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowItems.forEach { item ->
                                PluginEmojiButton(
                                    emojiItem = item,
                                    defaultHeightDp = itemHeightDp,
                                    backgroundColor = backgroundColor,
                                    textColor = textColor,
                                    onClick = {
                                        val imageUrl = item.imageUrl
                                        if (imageUrl != null && onImageEmojiSelect != null) {
                                            onImageEmojiSelect(imageUrl)
                                        } else if (imageUrl != null) {
                                            val success =
                                                clipboardManager.copyImageToSystemClipboard(
                                                    imageUrl,
                                                    item.displayText
                                                )
                                            if (success) {
                                                Toast.makeText(
                                                    context,
                                                    "已复制表情，可粘贴发送",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "复制失败",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        } else {
                                            onEmojiSelect(item.insertText)
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            repeat(columns - rowItems.size) {
                                Spacer(modifier = Modifier
                                    .weight(1f)
                                    .height((itemHeightDp).dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                } else {
                    val emojis = currentCategory.emojis
                    val columns = if (isLandscape) 15 else 8

                    emojis.chunked(columns).forEach { rowEmojis ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            rowEmojis.forEach { emoji ->
                                EmojiButton(
                                    emoji = emoji,
                                    onClick = { onEmojiSelect(emoji) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            repeat(columns - rowEmojis.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .padding(horizontal = if (isLandscape) 50.dp else 4.dp, vertical = 0.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                allCategories.forEachIndexed { index, category ->
                    EmojiCategoryTab(
                        icon = category.icon,
                        pluginIcon = category.pluginIcon,
                        isSelected = index == selectedCategoryIndex,
                        onClick = { selectedCategoryIndex = index },
                        backgroundColor = backgroundColor,
                        textColor = textColor,
                        modifier = Modifier.width(36.dp)
                    )
                }
            }

            KeyButton(
                text = "删除",
                onClick = { onEmojiSelect("delete") },
                backgroundColor = backgroundColor,
                textColor = textColor,
                modifier = Modifier.width(48.dp),
                fontSize = 12.sp
            )
        }

        // 底部留空（竖屏至少 40dp，与普通键盘一致）
        Spacer(modifier = Modifier.height(if (isLandscape) 15.dp else maxOf(bottomPaddingDp, 40).dp))
    }
}

@Composable
fun EmojiCategoryTab(
    icon: String,
    pluginIcon: PluginIcon? = null,
    isSelected: Boolean,
    onClick: () -> Unit,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .height(30.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(
                if (isSelected) textColor.copy(alpha = 0.15f)
                else backgroundColor
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (pluginIcon?.assetName != null) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(pluginIcon.assetName)
                    .crossfade(true)
                    .build(),
                contentDescription = icon,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                contentScale = ContentScale.Fit
            )
        } else {
            Text(
                text = pluginIcon?.text ?: icon,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun EmojiButton(
    emoji: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(4.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun PluginEmojiButton(
    emojiItem: EmojiItem,
    onClick: () -> Unit,
    defaultHeightDp: Int = 40,
    backgroundColor: Color = Color.Unspecified,
    textColor: Color = Color.Unspecified,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val config = emojiItem.displayConfig
    val heightDp = config?.heightDp ?: defaultHeightDp
    val aspectRatio = config?.aspectRatio

    val isLightTheme =
        (backgroundColor.red + backgroundColor.green + backgroundColor.blue) / 3f > 0.5f
    val buttonBackgroundColor = if (isLightTheme) Color.White.copy(alpha = 0.8f)
    else Color.LightGray.copy(alpha = 0.15f)
    val contentColor = if (isLightTheme) Color.Black else textColor

    Box(
        modifier = modifier
            .height(heightDp.dp)
            .then(
                if (emojiItem.imageUrl != null && aspectRatio != null) Modifier.aspectRatio(
                    aspectRatio
                )
                else if (emojiItem.imageUrl != null) Modifier.aspectRatio(1f)
                else Modifier.fillMaxWidth()
            )
            .clip(RoundedCornerShape(4.dp))
            .background(buttonBackgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        if (emojiItem.imageUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(emojiItem.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = emojiItem.displayText,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp),
                contentScale = ContentScale.Fit
            )
        } else {
            Text(
                text = emojiItem.displayText,
                fontSize = 12.sp,
                color = contentColor,
                textAlign = TextAlign.Center,
                maxLines = 2,
                softWrap = true,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}