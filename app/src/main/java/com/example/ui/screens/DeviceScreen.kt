// @builder:file app/src/main/java/com/example/ui/screens/DeviceScreen.kt
package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DataProvider
import com.example.data.StudyPlanProvider
import com.example.model.BookEntry
import com.example.ui.components.GlassCard
import com.example.ui.theme.*

@Composable
fun DeviceScreen(
    chapterId: String,
    deviceName: String,
    onBack: () -> Unit,
    // المعامل القديم: يفتح شاشة المحتويات الثلاثة
    onSubjectClick: (subjectIndex: Int, subjectTitle: String) -> Unit
) {
    val context = LocalContext.current
    val repository = remember { DataProvider(context) }
    val subjects = remember { repository.getDeviceSubjects(chapterId, deviceName) }

    val systemInfo = remember(deviceName) {
        StudyPlanProvider.getMedicalSystems().firstOrNull { it.nameAr == deviceName }
    }

    // حالة لعرض الحوار
    var selectedSubjectIndex by remember { mutableIntStateOf(-1) }
    var selectedSubjectTitle by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF020810), Primary, Color(0xFF0F1F33))))
            .padding(16.dp)
    ) {
        // شريط العنوان
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "↩️",
                fontSize = 24.sp,
                modifier = Modifier
                    .clickable { onBack() }
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = deviceName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextGold
                )
                if (systemInfo != null) {
                    Text(
                        text = "${systemInfo.subsystems.size} تخصصاً فرعياً",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (subjects.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "لا توجد مقررات مدرجة لهذا الجهاز بعد.",
                    color = TextSecondary,
                    fontSize = 16.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(subjects) { index, subject ->
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .clickable {
                                // عند النقر: إظهار الحوار
                                selectedSubjectIndex = index
                                selectedSubjectTitle = subject
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text("📖", fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = subject,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextGold,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }

    // حوار التفاصيل (مثل المواد العامة تماماً)
    if (selectedSubjectIndex >= 0) {
        AlertDialog(
            onDismissRequest = { selectedSubjectIndex = -1 },
            title = {
                Text(
                    text = "معلومات المنهج الدراسي",
                    color = TextGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "العنوان العلمي: $selectedSubjectTitle",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "الجهاز: $deviceName",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "ملاحظة: يجري تحميل المستند الطبي من الخادم الآمن للقوات المسلحة لحفظ الأصول الفكرية.",
                        color = TextOrange,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            // فتح عارض PDF مباشرة
                            onSubjectClick(selectedSubjectIndex, selectedSubjectTitle)
                            selectedSubjectIndex = -1
                        }
                    ) {
                        Text("قراءة في التطبيق 📖", color = Secondary, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedSubjectIndex = -1 }) {
                    Text("إغلاق", color = Color.White.copy(alpha = 0.6f))
                }
            },
            containerColor = PrimaryLight
        )
    }
}
// @builder:end
