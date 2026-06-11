// @builder:file app/src/main/java/com/example/ui/components/StaggeredEntrance.kt
// (إضافة تأثير دخول متدرج للعناصر)
package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun StaggeredEntrance(
    modifier: Modifier = Modifier,
    delayPerItem: Int = 80, // تأخير بالمللي ثانية لكل عنصر
    initialOffsetY: Float = 30f, // الإزاحة الأولية العمودية
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    Box(
        modifier = modifier
            .animateContentSize(
                animationSpec = tween(durationMillis = 400)
            )
    ) {
        if (visible) {
            content()
        }
    }
}

// دالة امتداد لتطبيق تأثير الدخول المتدرج على أي Composable باستخدام Spring لفيزياء ممتازة
fun Modifier.staggeredEntrance(index: Int, delayPerItem: Int = 80): Modifier = this.composed {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay((index * delayPerItem).toLong())
        visible = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "stagger_alpha"
    )

    val translateY by animateDpAsState(
        targetValue = if (visible) 0.dp else 24.dp,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMedium),
        label = "stagger_translate"
    )

    this.graphicsLayer {
        this.alpha = alpha
        this.translationY = translateY.toPx()
    }
}
// @builder:end