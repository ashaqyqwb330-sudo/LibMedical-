// @builder:file app/src/main/java/com/example/ui/screens/ChapterScreen.kt
package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DataProvider
import com.example.data.DirectSubjectItem
import com.example.data.StudyPlanProvider
import com.example.ui.components.GlassCard
import com.example.ui.theme.*

@Composable
fun ChapterScreen(
    chapterName: String,
    chapterId: String,
    onBack: () -> Unit,
    onNavigate: (String, Map<String, String>) -> Unit,
    onNavigateToStudyPlan: () -> Unit = {},
    // معاملات جديدة:
    isTab2: Boolean = false,                // هل نحن في التبويب الثاني؟
    onOpenPdfGeneral: ((String, String) -> Unit)? = null, // للتبويب الأول (مسار PDF مباشر)
    onNavigateToCourseDetail: ((String) -> Unit)? = null   // للتبويب الثاني (المحتويات الـ12)
) {
    val context = LocalContext.current
    val repository = remember { DataProvider(context) }
    val generals = remember { repository.getGeneralSubjects(chapterId) }
    // استخدام الدوال المباشرة للحصول على المسارات
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF030810), Primary, Color(0xFF0F1F33))))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("↩️", fontSize = 24.sp, modifier = Modifier.clickable { onBack() }.padding(8.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(chapterName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextGold)
                if (semesterInfo != null) {
                    Text(
                        "إجمالي الساعات: ${semesterInfo.totalCreditHours} معتمدة | ${semesterInfo.totalActualHours} فعلية",
                        fontSize = 11.sp, color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // قسم المواد العامة
        if (generalsDirect.isNotEmpty()) {
            Text("📚 المواد العامة والمشتركة", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextOrange, modifier = Modifier.padding(bottom = 10.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                itemsIndexed(generalsDirect) { index, item ->
                    GlassCard(
                        modifier = Modifier
                            .width(200.dp)
                            .height(110.dp)
                            .clickable {
                                if (isTab2 && onNavigateToCourseDetail != null) {
                                    // التبويب الثاني: حوار ثم المحتويات الـ12
                                    val courseId = item.title.replace(" ", "_").trim()
                                    onNavigateToCourseDetail(courseId)
                                } else if (!isTab2 && onOpenPdfGeneral != null) {
                                    // التبويب الأول: إظهار الحوار
                                    selectedGeneralSubject = item
                                } else {
                                    // السلوك القديم (للتوافق)
                                    onNavigate(
                                        "subject_content",
                                        mapOf(
                                            "chapterId" to chapterId,
                                            "deviceName" to "general_subject",
                                            "subjectTitle" to item.title,
                                            "subjectIndex" to index.toString(),
                                            "isGeneral" to "true"
                                        )
                                    )
                                }
                            }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("📋", fontSize = 28.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(item.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextGold, textAlign = TextAlign.Center, maxLines = 2)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = Secondary.copy(alpha = 0.5f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // قسم الأجهزة
        if (deviceNames.isNotEmpty()) {
            Text("🫁 بلوكات أجهزة وأعضاء الجسم", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextSecondary, modifier = Modifier.padding(bottom = 10.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(deviceNames) { _, device ->
                    GlassCard(
                        modifier = Modifier
                            .width(180.dp)
                            .height(120.dp)
                            .clickable {
                                // توجيه الجهاز إلى المسار المناسب حسب التبويب
                                onNavigate(
                                    if (isTab2) "device_subjects_12" else "device_subjects",
                                    mapOf("chapterId" to chapterId, "deviceName" to device)
                                )
                            }
                    ) {
                        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            val emoji = when {
                                device.contains("الهيكل العضلي") -> "🦴"
                                device.contains("القلبي") || device.contains("القلب") -> "🫀"
                                device.contains("التنفسي") || device.contains("التنفس") -> "🫁"
                                device.contains("الهضمي") -> "🍕"
                                device.contains("البولي") || device.contains("التناسلي") -> "🧼"
                                device.contains("الدموي") || device.contains("اللمفاوي") -> "🩸"
                                device.contains("الصمائي") -> "🧬"
                                device.contains("العصبي") -> "🧠"
                                else -> "🫁"
                            }
                            Text(emoji, fontSize = 34.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(device, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextGold, textAlign = TextAlign.Center, maxLines = 2)
                        }
                    }
                }
            }
        }

        if (generalsDirect.isEmpty() && deviceNames.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("لا توجد مواد أو أجهزة مسجلة في هذا الفصل.", color = TextSecondary, fontSize = 16.sp)
            }
        }
    }

    // حوار المادة العامة (للتبويب الأول فقط)
    if (selectedGeneralSubject != null && !isTab2 && onOpenPdfGeneral != null) {
        val item = selectedGeneralSubject!!
        AlertDialog(
            onDismissRequest = { selectedGeneralSubject = null },
            title = { Text("معلومات المنهج الدراسي", color = TextGold, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text = {
                Column {
                    Text("العنوان العلمي: ${item.title}", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("ملاحظة: يجري تحميل المستند الطبي من الخادم الآمن.", color = TextOrange, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onOpenPdfGeneral(item.title, item.directPdfPath)
                    selectedGeneralSubject = null
                }) { Text("قراءة في التطبيق 📖", color = Secondary, fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { selectedGeneralSubject = null }) { Text("إغلاق", color = Color.White.copy(alpha = 0.6f)) } },
            containerColor = PrimaryLight
        )
    }
}
// @builder:end