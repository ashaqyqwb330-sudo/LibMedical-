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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.CachePolicy
import com.example.data.DataProvider
import com.example.model.BookEntry
import com.example.ui.theme.TextGold
import kotlin.math.sin

@Composable
fun Book3DCard(
    bookTitle: String,
    coverPath: String?,
    activeBaseDir: java.io.File?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 150.dp,
    height: Dp = 220.dp
) {
    val view = LocalView.current
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // تأثيرات الحركة المتقدمة
    val infiniteTransition = rememberInfiniteTransition(label = "book_effects")

    // توهج متحرك
    val glowAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing)
        ),
        label = "glow_angle"
    )

    // تأثير الضغط المتقدم
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "press_scale"
    )

    val pressRotationX by animateFloatAsState(
        targetValue = if (isPressed) -8f else 0f,
        animationSpec = tween(300),
        label = "press_rot_x"
    )

    val pressRotationY by animateFloatAsState(
        targetValue = if (isPressed) 5f else 0f,
        animationSpec = tween(300),
        label = "press_rot_y"
    )

    // توهج النيون عند الضغط
    val neonGlow by animateColorAsState(
        targetValue = if (isPressed) TextGold.copy(alpha = 0.8f) else TextGold.copy(alpha = 0.2f),
        animationSpec = tween(400),
        label = "neon_glow"
    )

    // ارتفاع ديناميكي
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 16.dp else 8.dp,
        animationSpec = tween(300),
        label = "elevation"
    )

    // استخراج أيقونة ذكية
    val emoji = remember(bookTitle) {
        getBookEmoji(bookTitle)
    }

    val dataProvider = remember { DataProvider(context) }
    var isLoadFailed by remember(coverPath, bookTitle) { mutableStateOf(false) }

    // البحث الديناميكي عن الغلاف (SAF أو جهاز أو assets) كأغلفة حقيقية أو عبر البحث الذكي بالاسم
    val coverImageSource = remember(coverPath, bookTitle) {
        var resolved: Any? = null
        if (!coverPath.isNullOrEmpty()) {
            val dummyBook = BookEntry(
                chapter = 1,
                title = bookTitle,
                type = "",
                file = "",
                cover_path = coverPath
            )
            resolved = dataProvider.getBookCover(dummyBook)
        }
        if (resolved == null) {
            resolved = dataProvider.findCoverFile(bookTitle)
        }
        resolved
    }

    // التحميل التدريجي (Progressive Loading): تحميل نموذج صغير مسبقاً ثم الترقية للجودة الكاملة
    val tempCoverSource = remember(coverPath, bookTitle) {
        dataProvider.getTempCoverPath(bookTitle)
    }

    var currentImageRequest by remember(coverImageSource, tempCoverSource) {
        mutableStateOf<Any?>(
            if (tempCoverSource != null) {
                ImageRequest.Builder(context)
                    .data(tempCoverSource)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .build()
            } else if (coverImageSource != null) {
                ImageRequest.Builder(context)
                    .data(coverImageSource)
                    .size(60, 90) // فك ترميز فائق السرعة وبجودة مخفضة كصورة مصغرة فورية
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .build()
            } else null
        )
    }

    LaunchedEffect(coverImageSource, tempCoverSource) {
        if (coverImageSource != null) {
            // ترقية الطلب بسلاسة ليكون بالجودة الكاملة مع تأثير Crossfade ناعم
            currentImageRequest = ImageRequest.Builder(context)
                .data(coverImageSource)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .crossfade(400)
                .build()
        }
    }

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .scale(pressScale)
            .shadow(elevation, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
    ) {
        // خلفية الكتاب الأساسية (تدرج داكن)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF1E2F47),
                            Color(0xFF101E32),
                            Color(0xFF0B162A)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(300f, 600f)
                    )
                )
        )

        // تحميل وعرض صورة الغلاف باستخدام Coil إن وجدت بأسلوب فائق السلاسة والسرعة
        if (currentImageRequest != null && !isLoadFailed) {
            AsyncImage(
                model = currentImageRequest,
                contentDescription = bookTitle,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                onError = { isLoadFailed = true },
                onSuccess = { isLoadFailed = false }
            )

            // طبقة تظليل تدرجية داكنة فوق الصورة لضمان خطوط العنوان واضحة ومقروءة بالكامل
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.4f),
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )
        }

        // توهج نيون متحرك
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.sweepGradient(
                        colors = listOf(
                            Color.Transparent,
                            neonGlow,
                            Color.Transparent,
                            Color.Transparent
                        ),
                        center = Offset(width.value / 2, height.value / 2)
                    )
                )
        )

        // شريط ذهبي علوي (كعب الكتاب)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            TextGold.copy(alpha = 0.6f),
                            TextGold,
                            TextGold.copy(alpha = 0.6f)
                        )
                    )
                )
        )

        // محتوى الكتاب
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // لا نعرض الإيموجي الكبير إلا إذا لم يكن هناك صورة غلاف أو فشل تحميلها
            if (coverImageSource == null || isLoadFailed) {
                // أيقونة المقرر في دائرة متوهجة برّاقة
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    TextGold.copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            )
                        )
                        .border(1.dp, TextGold.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(emoji, fontSize = 28.sp)
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            // عنوان الكتاب
            Text(
                text = bookTitle,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 17.sp,
                style = androidx.compose.ui.text.TextStyle(
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.8f),
                        offset = Offset(1f, 2f),
                        blurRadius = 4f
                    )
                )
            )

            // شريط ذهبي سفلي
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                TextGold.copy(alpha = 0.3f),
                                TextGold.copy(alpha = 0.6f),
                                TextGold.copy(alpha = 0.3f)
                            )
                        )
                    )
            )
        }
    }
}

private fun getBookEmoji(title: String): String = when {
    title.contains("تشريح") -> "🦴"
    title.contains("قلب") || title.contains("القلبي") -> "🫀"
    title.contains("تنفس") || title.contains("التنفسي") -> "🫁"
    title.contains("هضم") || title.contains("الهضمي") -> "🍕"
    title.contains("بولي") || title.contains("تناسلي") -> "🧬"
    title.contains("عصبي") || title.contains("الأعصاب") -> "🧠"
    title.contains("دم") || title.contains("الدموي") -> "🩸"
    title.contains("أدوية") || title.contains("الأدوية") -> "💊"
    title.contains("جراحة") -> "🔪"
    title.contains("أطفال") -> "👶"
    title.contains("نساء") || title.contains("توليد") -> "🤰"
    title.contains("عيون") -> "👁️"
    title.contains("جلد") -> "🧴"
    title.contains("أشعة") -> "🩻"
    title.contains("نفسي") -> "🧘"
    title.contains("طوارئ") -> "🚨"
    title.contains("تخدير") -> "😴"
    title.contains("ثقافة") || title.contains("إسلام") -> "📖"
    title.contains("لغة") -> "🔤"
    title.contains("حاسوب") || title.contains("كمبيوتر") -> "💻"
    title.contains("كيمياء") || title.contains("فيزياء") -> "⚗️"
    title.contains("أخلاق") -> "⚖️"
    title.contains("إحصاء") -> "📊"
    title.contains("مناعة") -> "🛡️"
    title.contains("تغذية") -> "🥗"
    title.contains("غدد") || title.contains("صماء") -> "🦋"
    title.contains("بحث") || title.contains("تخرج") -> "🎓"
    else -> "📗"
}
// @builder:end