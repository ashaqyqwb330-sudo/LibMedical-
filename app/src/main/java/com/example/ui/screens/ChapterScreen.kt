// @builder:file app/src/main/java/com/example/ui/screens/ChapterScreen.kt
package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DataProvider
import com.example.data.DirectSubjectItem
import com.example.data.StudyPlanProvider
import com.example.ui.components.GlassCard
import com.example.ui.components.StaggeredEntrance
import com.example.ui.theme.*

@Composable
fun ChapterScreen(
    chapterName: String,
    chapterId: String,
    onBack: () -> Unit,
    onNavigate: (String, Map<String, String>) -> Unit,
    onNavigateToStudyPlan: () -> Unit = {},
    isTab2: Boolean = false,
    onOpenPdfGeneral: ((String, String) -> Unit)? = null,
    onNavigateToCourseDetail: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    val repository = remember { DataProvider(context) }
    val generalsDirect = remember(chapterId) { repository.getGeneralSubjectsDirect(chapterId) }
    val (_, _, devicesMap) = remember { repository.getBooksInChapter(chapterId) }
    val deviceNames = remember { devicesMap.keys.toList() }

    val chapterNumber = remember(chapterId) {
        chapterId.removePrefix("class").toIntOrNull() ?: 1
    }
    val semesterInfo = remember(chapterNumber) {
        StudyPlanProvider.getStages()
            .flatMap { it.semesters }
            .firstOrNull { it.id == chapterNumber }
    }

    var selectedGeneralSubject by remember { mutableStateOf<DirectSubjectItem?>(null) }

    // تأثيرات حركية
    val infiniteTransition = rememberInfiniteTransition(label = "chapter_anim")

    val headerGlow by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(2500), RepeatMode.Reverse),
        label = "header_glow"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF020810),
                        Color(0xFF0A1A2F),
                        Primary,
                        Color(0xFF0F1F33)
                    )
                )
            )
    ) {
        // ===== رأس الصفحة السينمائي =====
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            TextGold.copy(alpha = headerGlow * 0.2f),
                            TextGold.copy(alpha = headerGlow * 0.1f),
                            Color.Transparent
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // زر الرجوع الزجاجي
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) { Text("↩️", fontSize = 20.sp) }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = chapterName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextGold
                    )
                    if (semesterInfo != null) {
                        Text(
                            text = "إجمالي ${semesterInfo.totalCreditHours} ساعة معتمدة | ${semesterInfo.totalActualHours} ساعة فعلية",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ===== قسم المواد العامة (إن وجدت) =====
        if (generalsDirect.isNotEmpty()) {
            SectionHeader("📚 المواد العامة والمشتركة", TextOrange)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(generalsDirect) { index, item ->
                    EnhancedSubjectCard(
                        title = item.title,
                        emoji = getSubjectEmoji(item.title),
                        accentColor = TextOrange,
                        onClick = {
                            if (isTab2 && onNavigateToCourseDetail != null) {
                                onNavigateToCourseDetail(item.title.replace(" ", "_"))
                            } else if (!isTab2 && onOpenPdfGeneral != null) {
                                selectedGeneralSubject = item
                            }
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Secondary.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(12.dp))
        }

        // ===== قسم الأجهزة الطبية (إن وجدت) =====
        if (deviceNames.isNotEmpty()) {
            SectionHeader("🫁 الأجهزة الطبية للفصل", Secondary)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(deviceNames) { _, device ->
                    EnhancedDeviceCard(
                        deviceName = device,
                        onClick = {
                            onNavigate(
                                if (isTab2) "device_subjects_12" else "device_subjects",
                                mapOf("chapterId" to chapterId, "deviceName" to device)
                            )
                        }
                    )
                }
            }
        }

        // حالة فارغة
        if (generalsDirect.isEmpty() && deviceNames.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("لا توجد مواد أو أجهزة لهذا الفصل.", color = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }

    // حوار المادة العامة (للتبويب الأول)
    if (selectedGeneralSubject != null && !isTab2 && onOpenPdfGeneral != null) {
        val item = selectedGeneralSubject!!
        AlertDialog(
            onDismissRequest = { selectedGeneralSubject = null },
            title = { Text("تفاصيل المادة", color = TextGold, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("العنوان: ${item.title}", color = Color.White)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("يمكنك فتح الملف مباشرة.", color = TextSecondary)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onOpenPdfGeneral(item.title, item.directPdfPath)
                    selectedGeneralSubject = null
                }) { Text("فتح الملف 📄", color = Secondary) }
            },
            dismissButton = { TextButton(onClick = { selectedGeneralSubject = null }) { Text("إغلاق") } },
            containerColor = Color(0xFF0F1F33)
        )
    }
}

// ========== مكونات مساعدة محسّنة ==========

@Composable
fun SectionHeader(title: String, accentColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = accentColor)
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .width(30.dp)
                .height(3.dp)
                .background(Brush.horizontalGradient(listOf(accentColor, Color.Transparent)))
        )
    }
}

@Composable
fun EnhancedSubjectCard(
    title: String,
    emoji: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.94f else 1f, spring())

    Card(
        modifier = Modifier
            .width(180.dp)
            .height(130.dp)
            .scale(scale)
            .clickable(interactionSource, null) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x15FFFFFF)),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isPressed) accentColor.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.08f)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(emoji, fontSize = 36.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextGold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun EnhancedDeviceCard(
    deviceName: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.94f else 1f, spring())

    val emoji = when {
        deviceName.contains("الهيكل العضلي") -> "🦴"
        deviceName.contains("القلبي") -> "🫀"
        deviceName.contains("التنفسي") -> "🫁"
        deviceName.contains("الهضمي") -> "🍕"
        deviceName.contains("البولي") || deviceName.contains("التناسلي") -> "🧬"
        deviceName.contains("الدموي") || deviceName.contains("اللمفاوي") -> "🩸"
        deviceName.contains("الصمائي") || deviceName.contains("الغدد") -> "🦋"
        deviceName.contains("العصبي") -> "🧠"
        else -> "🏥"
    }

    Card(
        modifier = Modifier
            .width(160.dp)
            .height(120.dp)
            .scale(scale)
            .clickable(interactionSource, null) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x15FFFFFF)),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isPressed) Secondary.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.08f)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(emoji, fontSize = 36.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = deviceName,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Secondary,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun getSubjectEmoji(title: String): String = when {
    title.contains("تشريح") -> "🦴"
    title.contains("أدوية") -> "💊"
    title.contains("جراحة") -> "🔪"
    title.contains("أطفال") -> "👶"
    title.contains("نساء") || title.contains("توليد") -> "🤰"
    title.contains("باطن") -> "🩺"
    title.contains("طوارئ") -> "🚨"
    title.contains("تخدير") -> "😴"
    title.contains("أشعة") -> "🩻"
    title.contains("عيون") -> "👁️"
    title.contains("جلد") -> "🧴"
    title.contains("نفسي") -> "🧘"
    title.contains("أنف") || title.contains("أذن") -> "👂"
    title.contains("عظام") -> "🦴"
    title.contains("أعصاب") || title.contains("عصبي") -> "🧠"
    title.contains("شرعي") || title.contains("سموم") -> "⚖️"
    title.contains("ثقافة") || title.contains("إسلام") -> "📖"
    title.contains("لغة") -> "🔤"
    title.contains("إحصاء") -> "📊"
    title.contains("مناعة") -> "🛡️"
    title.contains("تغذية") -> "🥗"
    title.contains("بحث") -> "🎓"
    else -> "📘"
}
// @builder:end