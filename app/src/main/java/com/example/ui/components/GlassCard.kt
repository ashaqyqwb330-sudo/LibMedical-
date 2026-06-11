// @builder:file app/src/main/java/com/example/ui/components/GlassCard.kt
package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.TextGold

@Composable
fun GlassCard(
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    borderAlpha: Float = 0.15f,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    enablePersistentLaser: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed = if (onClick != null) {
        interactionSource.collectIsPressedAsState().value
    } else false

    // توهج عند الضغط
    val glowAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.3f else 0.1f,
        animationSpec = tween(300),
        label = "glass_glow"
    )

    val modifierWithClick = if (onClick != null) {
        modifier.clickable(
            interactionSource = interactionSource,
            indication = null
        ) { onClick() }
    } else modifier

    Box(
        modifier = modifierWithClick
            .clip(RoundedCornerShape(cornerRadius))
            .border(
                1.dp,
                TextGold.copy(alpha = if (isPressed) borderAlpha * 2 else borderAlpha),
                RoundedCornerShape(cornerRadius)
            )
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.08f),
                        Color.White.copy(alpha = 0.03f),
                        Color.White.copy(alpha = 0.06f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(400f, 800f)
                )
            )
    ) {
        // تأثير انعكاس زجاجي (خط أبيض رفيع في الأعلى)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.White.copy(alpha = 0f),
                            Color.White.copy(alpha = glowAlpha * 2),
                            Color.White.copy(alpha = 0f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}
// @builder:end
