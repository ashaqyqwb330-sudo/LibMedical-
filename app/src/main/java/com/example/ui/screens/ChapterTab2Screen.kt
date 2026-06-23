// @builder:file app/src/main/java/com/example/ui/screens/ChapterTab2Screen.kt
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DataProvider
import com.example.data.DirectSubjectItem
import com.example.data.StudyPlanProvider
import com.example.ui.components.Book3DCard
import com.example.ui.components.MaterialDialog
import com.example.ui.components.Shelf
import com.example.ui.components.StaggeredEntrance
import com.example.ui.theme.*

@Composable
fun ChapterTab2Screen(
    chapterId: String,
    chapterName: String,
    onBack: () -> Unit,
    onNavigateToDeviceTab2: (String, String) -> Unit,
    onNavigateToCourseDetail: (String) -> Unit
) {
    val context = LocalContext.current
    val repository = remember { DataProvider(context) }
    
    var selectedGeneralSubject by remember { mutableStateOf<DirectSubjectItem?>(null) }
    
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

    val infiniteTransition = rememberInfiniteTransition(label = "chapter_tab2")
    val headerGlow by infiniteTransition.animateFloat(0.3f, 0.7f, infiniteRepeatable(tween(2500), RepeatMode.Reverse), label = "hdr_glow")
    val borderDashOffset by infiniteTransition.animateFloat(0f, 40f, infiniteRepeatable(tween(1000, easing = LinearEasing)), label = "dash_offset")

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
                    Text(chapterName, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextGold)
                    if (semesterInfo != null) {
                        Text("${semesterInfo.totalCreditHours} ساعة معتمدة | ${semesterInfo.totalActualHours} ساعة فعلية", fontSize = 11.sp, color = TextSecondary)
                    } else {
                        Text("مناهج الفصل الدراسي", fontSize = 11.sp, color = TextSecondary)
                    }
                }
                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(TextGold.copy(alpha = 0.2f)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                    Text("المحتويات الـ12", fontSize = 10.sp, color = TextGold, fontWeight = FontWeight.Bold)
                }
            }
        }

        // خط زخرفي متحرك
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
            // توجيه ذكي للكتب في حالة الفصول الدراسية العادية إذا استُدعيت بطريقة ما
            UnifiedTab2Screen(
                chapterId = chapterId,
                dataType = Tab2DataType.BOOKS,
                onBack = onBack,
                onItemConfirmed = onNavigateToCourseDetail
            )
        } else {
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
                                            coverPath = item.coverPath,
                                            activeBaseDir = repository.activeBaseDir,
                                            onClick = {
                                                selectedGeneralSubject = item
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
                                        onNavigateToDeviceTab2(chapterId, device)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    selectedGeneralSubject?.let { item ->
        MaterialDialog(
            title = "تفاصيل المقرر الدراسي الـ 12",
            message = "المقرر: ${item.title}\n\nيتضمن هذا القسم الأقسام التعليمية الـ 12 المشتملة على العروض التقديمية والملخصات ومقاطع الفيديو بنظم المحاكاة الطبية العسكرية.",
            confirmText = "عرض المحتويات 📚",
            onDismiss = { selectedGeneralSubject = null },
            onConfirm = {
                val courseId = item.title.replace(" ", "_").trim()
                onNavigateToCourseDetail(courseId)
                selectedGeneralSubject = null
            }
        )
    }
}
