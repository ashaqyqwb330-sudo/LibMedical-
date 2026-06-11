// @builder:file app/src/main/java/com/example/ui/screens/ChapterScreen.kt
package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DataProvider
import com.example.data.DirectSubjectItem
import com.example.data.StudyPlanProvider
import com.example.ui.components.Book3DCard
import com.example.ui.components.Shelf
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
    
    // استخراج البيانات بشكل آمن
    val generalsDirect = remember(chapterId) {
        try {
            repository.getGeneralSubjectsDirect(chapterId)
        } catch (e: Exception) { emptyList() }
    }
    val (_, _, devicesMap) = remember(chapterId) {
        try {
            repository.getBooksInChapter(chapterId)
        } catch (e: Exception) { Triple(emptyList(), emptyList(), emptyMap()) }
    }
    val deviceNames = remember { devicesMap.keys.toList() }

    val chapterNumber = remember(chapterId) { chapterId.removePrefix("class").toIntOrNull() ?: 1 }
    val semesterInfo = remember(chapterNumber) {
        try { StudyPlanProvider.getStages().flatMap { it.semesters }.firstOrNull { it.id == chapterNumber } } catch (e: Exception) { null }
    }

    var selectedGeneralSubject by remember { mutableStateOf<DirectSubjectItem?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "ch_anim")
    val headerGlow by infiniteTransition.animateFloat(0.3f, 0.7f, infiniteRepeatable(tween(2500), RepeatMode.Reverse), label = "hdr")
    val borderDashOffset by infiniteTransition.animateFloat(0f, 40f, infiniteRepeatable(tween(1000, easing = LinearEasing)), label = "border_dash")

    val isEmpty = generalsDirect.isEmpty() && deviceNames.isEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF020810), Color(0xFF0A1A2F), Primary, Color(0xFF0F1F33))
                )
            )
    ) {
        // ===== رأس الصفحة =====
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(Color.Transparent, TextGold.copy(alpha = headerGlow * 0.25f), TextGold.copy(alpha = headerGlow * 0.12f), Color.Transparent)))
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.08f)).clickable { onBack() }, contentAlignment = Alignment.Center) { Text("↩️", fontSize = 20.sp) }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(chapterName, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextGold)
                    if (semesterInfo != null) Text("${semesterInfo.totalCreditHours} ساعة معتمدة | ${semesterInfo.totalActualHours} ساعة فعلية", fontSize = 11.sp, color = TextSecondary)
                    else Text("الفصل الدراسي", fontSize = 11.sp, color = TextSecondary)
                }
                if (isTab2) {
                    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(TextGold.copy(alpha = 0.2f)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                        Text("12 قسماً", fontSize = 10.sp, color = TextGold, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // خط زخرفي
        Canvas(modifier = Modifier.fillMaxWidth().height(2.dp)) {
            drawLine(
                color = TextGold.copy(alpha = 0.6f),
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = 1.5f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), borderDashOffset)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (isEmpty) {
            // حالة فارغة
            Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(20.dp)).background(TextGold.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                        Text("📭", fontSize = 36.sp)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text("لا توجد مواد أو أجهزة", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextGold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("هذا الفصل الدراسي لا يحتوي حالياً على أي مواد عامة أو أجهزة طبية. سيتم إضافتها قريباً.", fontSize = 14.sp, color = TextSecondary, textAlign = TextAlign.Center, lineHeight = 22.sp)
                }
            }
        } else {
            // محتوى الفصل
            Column(modifier = Modifier.weight(1f)) {
                if (generalsDirect.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    SectionHeader("📚 المواد العامة والمشتركة", TextOrange)
                    Spacer(modifier = Modifier.height(8.dp))
                    val shelves = generalsDirect.chunked(3)
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(shelves.size) { shelfIndex ->
                            StaggeredEntrance(delayPerItem = 80 * shelfIndex) {
                                Shelf(spacing = 12.dp, modifier = Modifier.width(IntrinsicSize.Max)) {
                                    shelves[shelfIndex].forEach { item ->
                                        Book3DCard(
                                            bookTitle = item.title,
                                            coverPath = null,
                                            activeBaseDir = null,
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
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    if (deviceNames.isNotEmpty()) {
                        Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Secondary.copy(alpha = 0.4f))
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                if (deviceNames.isNotEmpty()) {
                    SectionHeader("🫁 الأجهزة الطبية للفصل", Secondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        itemsIndexed(deviceNames) { index, device ->
                            StaggeredEntrance(delayPerItem = 80 * index) {
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
                }
            }
        }
    }

    // حوار المادة العامة
    if (selectedGeneralSubject != null && !isTab2 && onOpenPdfGeneral != null) {
        val item = selectedGeneralSubject!!
        AlertDialog(
            onDismissRequest = { selectedGeneralSubject = null },
            title = { Text("تفاصيل المادة", color = TextGold, fontWeight = FontWeight.Bold) },
            text = { Column { Text("العنوان: ${item.title}", color = Color.White); Spacer(modifier = Modifier.height(12.dp)); Text("يمكنك فتح الملف مباشرة.", color = TextSecondary) } },
            confirmButton = { TextButton(onClick = { onOpenPdfGeneral(item.title, item.directPdfPath); selectedGeneralSubject = null }) { Text("فتح الملف 📄", color = Secondary) } },
            dismissButton = { TextButton(onClick = { selectedGeneralSubject = null }) { Text("إغلاق") } },
            containerColor = Color(0xFF0F1F33)
        )
    }
}

@Composable
fun SectionHeader(title: String, accentColor: Color) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = accentColor)
        Spacer(modifier = Modifier.weight(1f))
        Box(modifier = Modifier.width(30.dp).height(3.dp).background(Brush.horizontalGradient(listOf(accentColor, Color.Transparent))))
    }
}

@Composable
fun EnhancedDeviceCard(deviceName: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.94f else 1f, spring())
    val borderColor by animateColorAsState(
        targetValue = if (isPressed) Secondary.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.08f),
        animationSpec = tween(300)
    )

    Card(
        modifier = Modifier.width(160.dp).height(120.dp).scale(scale).clickable(interactionSource, null) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x15FFFFFF)),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(getDeviceEmoji(deviceName), fontSize = 36.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(deviceName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Secondary, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

private fun getDeviceEmoji(name: String): String = when {
    name.contains("الهيكل العضلي") -> "🦴"
    name.contains("القلبي") -> "🫀"
    name.contains("التنفسي") -> "🫁"
    name.contains("الهضمي") -> "🍕"
    name.contains("البولي") || name.contains("التناسلي") -> "🧬"
    name.contains("الدموي") || name.contains("اللمفاوي") -> "🩸"
    name.contains("الصمائي") || name.contains("الغدد") -> "🦋"
    name.contains("العصبي") -> "🧠"
    else -> "🏥"
}
// @builder:end