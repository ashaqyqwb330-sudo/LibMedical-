// @builder:file app/src/main/java/com/example/ui/components/Book3DCard.kt
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
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import java.io.File

@Composable
fun Book3DCard(
    bookTitle: String,
    coverPath: String?,
    activeBaseDir: File?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 140.dp,
    height: Dp = 200.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // تأثير الضغط: تصغير بسيط مع ارتفاع
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "book_scale"
    )

    // تأثير الظل: يزيد عند الضغط
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 12.dp else 6.dp,
        animationSpec = tween(200),
        label = "book_elevation"
    )

    // تأثير التوهج الذهبي عند الضغط
    val glowColor by animateColorAsState(
        targetValue = if (isPressed) Color(0xFFD4AF37).copy(alpha = 0.4f) else Color.White.copy(alpha = 0.08f),
        animationSpec = tween(200),
        label = "book_glow"
    )

    // دوران خفيف للكتاب (تأثير 3D)
    val rotationX by animateFloatAsState(
        targetValue = if (isPressed) -5f else 0f,
        animationSpec = tween(300),
        label = "book_rot_x"
    )
    val rotationY by animateFloatAsState(
        targetValue = if (isPressed) 3f else 0f,
        animationSpec = tween(300),
        label = "book_rot_y"
    )

    // استخراج رمز المقرر من العنوان (للأيقونة)
    val courseEmoji = remember(bookTitle) {
        when {
            bookTitle.contains("تشريح") || bookTitle.contains("التشريح") -> "🦴"
            bookTitle.contains("قلب") || bookTitle.contains("القلبي") -> "🫀"
            bookTitle.contains("تنفس") || bookTitle.contains("التنفسي") -> "🫁"
            bookTitle.contains("هضم") || bookTitle.contains("الهضمي") -> "🍕"
            bookTitle.contains("بولي") || bookTitle.contains("تناسلي") -> "🧬"
            bookTitle.contains("عصبي") || bookTitle.contains("الأعصاب") -> "🧠"
            bookTitle.contains("دم") || bookTitle.contains("الدموي") -> "🩸"
            bookTitle.contains("أدوية") || bookTitle.contains("الأدوية") -> "💊"
            bookTitle.contains("جراحة") || bookTitle.contains("الجراحة") -> "🔪"
            bookTitle.contains("أطفال") || bookTitle.contains("الأطفال") -> "👶"
            bookTitle.contains("نساء") || bookTitle.contains("توليد") -> "🤰"
            bookTitle.contains("عيون") || bookTitle.contains("العيون") -> "👁️"
            bookTitle.contains("أنف") || bookTitle.contains("أذن") -> "👂"
            bookTitle.contains("جلدية") || bookTitle.contains("الجلدية") -> "🧴"
            bookTitle.contains("أشعة") || bookTitle.contains("الأشعة") -> "🩻"
            bookTitle.contains("نفسي") || bookTitle.contains("النفسي") -> "🧘"
            bookTitle.contains("طوارئ") || bookTitle.contains("الطوارئ") -> "🚨"
            bookTitle.contains("تخدير") || bookTitle.contains("التخدير") -> "😴"
            bookTitle.contains("ثقافة") || bookTitle.contains("إسلامية") -> "📖"
            bookTitle.contains("لغة") || bookTitle.contains("إنجليزية") -> "🔤"
            bookTitle.contains("حاسوب") || bookTitle.contains("كمبيوتر") -> "💻"
            bookTitle.contains("فيزياء") || bookTitle.contains("كيمياء") -> "⚗️"
            bookTitle.contains("أخلاق") || bookTitle.contains("الأخلاق") -> "⚖️"
            bookTitle.contains("مصطلحات") || bookTitle.contains("المصطلحات") -> "📝"
            bookTitle.contains("إحصاء") || bookTitle.contains("الإحصاء") -> "📊"
            bookTitle.contains("مناعة") || bookTitle.contains("المناعة") -> "🛡️"
            bookTitle.contains("تغذية") || bookTitle.contains("التغذية") -> "🥗"
            bookTitle.contains("صماء") || bookTitle.contains("الغدد") -> "🦋"
            bookTitle.contains("بحث") || bookTitle.contains("تخرج") -> "🎓"
            else -> "📗"
        }
    }

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
    ) {
        // ظل الكتاب (طبقة سفلى)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = elevation, x = elevation / 2)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black.copy(alpha = 0.3f))
        )

        // جسم الكتاب الرئيسي
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF1A2740),
                            Color(0xFF0F1F33),
                            Color(0xFF0B1A2E)
                        )
                    )
                )
                .border(1.5.dp, glowColor, RoundedCornerShape(12.dp))
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // شريط علوي ذهبي (كعب الكتاب)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                TextGold.copy(alpha = 0.4f),
                                TextGold,
                                TextGold.copy(alpha = 0.4f)
                            )
                        )
                    )
            )

            // أيقونة المقرر
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.06f)),
                contentAlignment = Alignment.Center
            ) {
                Text(courseEmoji, fontSize = 24.sp)
            }

            // عنوان الكتاب
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = bookTitle,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextGold,
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )
            }

            // شريط سفلي ذهبي
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                TextGold.copy(alpha = 0.2f),
                                TextGold.copy(alpha = 0.5f),
                                TextGold.copy(alpha = 0.2f)
                            )
                        )
                    )
            )
        }

        // تأثير انعكاس خفيف (شعاع ضوء)
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .fillMaxHeight(0.15f)
                .align(Alignment.TopCenter)
                .offset(y = 4.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White.copy(alpha = 0.04f))
        )
    }
}
// @builder:end