// @builder:file app/src/main/java/com/example/ui/screens/DeviceScreen.kt
package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DataProvider
import com.example.data.DirectSubjectItem
import com.example.data.StudyPlanProvider
import com.example.ui.theme.*

@Composable
fun DeviceScreen(
    chapterId: String,
    deviceName: String,
    onBack: () -> Unit,
    onOpenPdf: ((title: String, pdfPath: String) -> Unit)? = null,
    onOpenCourseDetail: ((String) -> Unit)? = null,
    isTab2: Boolean = false  // للتمييز البصري
) {
    val context = LocalContext.current
    val repository = remember { DataProvider(context) }
    val subjects = remember(chapterId, deviceName) {
        repository.getDeviceSubjectsDirect(chapterId, deviceName)
    }

    val systemInfo = remember(deviceName) {
        StudyPlanProvider.getMedicalSystems().firstOrNull { it.nameAr == deviceName }
    }

    var selectedSubject by remember { mutableStateOf<DirectSubjectItem?>(null) }

    // لون خلفية مختلف قليلاً للتبويب الثاني
    val bgColor = if (isTab2) Color(0xFF0A1A2F) else Color(0xFF020810)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(bgColor, Primary, Color(0xFF0F1F33)))
            )
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.08f)).clickable { onBack() },
                contentAlignment = Alignment.Center
            ) { Text("↩️", fontSize = 20.sp) }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(deviceName, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextGold)
                if (systemInfo != null) Text("${systemInfo.subsystems.size} تخصصاً", fontSize = 11.sp, color = TextSecondary)
            }
            if (isTab2) {
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(8.dp))
                        .background(TextGold.copy(alpha = 0.15f)).padding(horizontal = 8.dp, vertical = 4.dp)
                ) { Text("12 قسماً", fontSize = 10.sp, color = TextGold) }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (subjects.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("لا توجد مقررات مدرجة لهذا الجهاز بعد.", color = TextSecondary)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize()) {
                items(subjects) { subject ->
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, spring())

                    Card(
                        modifier = Modifier.fillMaxWidth().scale(scale)
                            .clickable(interactionSource, null) { selectedSubject = subject },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0x15FFFFFF)),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isPressed) TextGold.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.06f)
                        )
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("📖", fontSize = 30.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(subject.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextGold, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("◀", fontSize = 16.sp, color = TextSecondary)
                        }
                    }
                }
            }
        }
    }

    // حوار التفاصيل
    selectedSubject?.let { subject ->
        AlertDialog(
            onDismissRequest = { selectedSubject = null },
            title = { Text("تفاصيل المادة", color = TextGold, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("المادة: ${subject.title}", color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("الجهاز: $deviceName", color = TextSecondary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(if (isTab2) "سيتم فتح المحتوى التعليمي الكامل." else "سيتم فتح الملف مباشرة.", color = TextOrange)
                }
            },
            confirmButton = {
                if (onOpenCourseDetail != null) {
                    TextButton(onClick = {
                        val courseId = "${deviceName}-${subject.title}".replace(" ", "_")
                        onOpenCourseDetail(courseId)
                        selectedSubject = null
                    }) { Text("عرض المحتويات 📚", color = Secondary) }
                } else if (onOpenPdf != null) {
                    TextButton(onClick = {
                        onOpenPdf(subject.title, subject.directPdfPath)
                        selectedSubject = null
                    }) { Text("فتح الملف 📄", color = Secondary) }
                }
            },
            dismissButton = { TextButton(onClick = { selectedSubject = null }) { Text("إغلاق") } },
            containerColor = Color(0xFF0F1F33)
        )
    }
}
// @builder:end