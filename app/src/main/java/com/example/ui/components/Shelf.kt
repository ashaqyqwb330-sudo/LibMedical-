// @builder:file app/src/main/java/com/example/ui/components/Shelf.kt
package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Shelf(
    modifier: Modifier = Modifier,
    spacing: Dp = 12.dp,
    shelfHeight: Dp = 220.dp, // ارتفاع الرف (أكبر من الكتب لإظهار الخشب)
    showReflection: Boolean = true, // عرض انعكاس الكتب
    content: @Composable RowScope.() -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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

        // الرف الخشبي
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(bottomStart = 2.dp, bottomEnd = 2.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF2C1810), // بني داكن جداً
                            Color(0xFF5C3A28), // بني متوسط
                            Color(0xFF3E2218), // بني داكن
                            Color(0xFF2C1810)  // بني داكن جداً
                        )
                    )
                )
        )

        // حافة الرف السفلية (تأثير 3D)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .clip(RoundedCornerShape(bottomStart = 2.dp, bottomEnd = 2.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF1A0E08),
                            Color(0xFF2C1810)
                        )
                    )
                )
        )
    }
}
// @builder:end