// @builder:file app/src/main/java/com/example/ui/components/Shelf.kt
package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
fun Shelf(
    modifier: Modifier = Modifier,
    spacing: Dp = 14.dp,
    shelfHeight: Dp = 230.dp,
    showSpotlight: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shelf_glow")

    // توهج خافت متحرك على الرف
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shelf_glow_alpha"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        // الكتب فوق الرف
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalAlignment = Alignment.Bottom,
            content = content
        )

        // سطح الرف الرئيسي (خشب فاخر)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(bottomStart = 3.dp, bottomEnd = 3.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF1A0E08),
                            Color(0xFF3E2218),
                            Color(0xFF5C3828),
                            Color(0xFF3E2218),
                            Color(0xFF1A0E08)
                        )
                    )
                )
        )

        // خط ذهبي رفيع أعلى الرف
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            TextGold.copy(alpha = 0f),
                            TextGold.copy(alpha = glowAlpha),
                            TextGold.copy(alpha = 0f)
                        )
                    )
                )
        )

        // حافة الرف السفلية (تأثير 3D)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(bottomStart = 3.dp, bottomEnd = 3.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0F0604),
                            Color(0xFF1A0E08),
                            Color(0xFF2C1810)
                        )
                    )
                )
        )
    }
}
// @builder:end