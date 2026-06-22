// @builder:file app/src/main/java/com/example/ui/screens/UnifiedTab2Screen.kt
package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DataProvider
import com.example.ui.components.Book3DCard
import com.example.ui.components.MaterialDialog
import com.example.ui.components.Shelf
import com.example.ui.components.StaggeredEntrance
import com.example.ui.theme.*

/**
 * شاشة موحدة للتبويب الثاني.
 * تستطيع عرض كتب، مواد عامة، أو مواد تخصصية بناءً على dataType.
 * السلوك يُحقن من الخارج عبر دوال (callbacks).
 */
enum class Tab2DataType { BOOKS, GENERAL_SUBJECTS, DEVICE_SUBJECTS }

@Composable
fun UnifiedTab2Screen(
    chapterId: String,
    deviceName: String? = null, // مطلوب فقط إذا كان dataType = DEVICE_SUBJECTS
    dataType: Tab2DataType,
    onBack: () -> Unit,
    // دالة السلوك: تُستدعى عند تأكيد الحوار
    onItemConfirmed: (itemId: String) -> Unit
) {
    val context = LocalContext.current
    val repository = remember { DataProvider(context) }

    // تحميل البيانات حسب النوع
    val items: List<String> = remember(chapterId, deviceName, dataType) {
        try {
            when (dataType) {
                Tab2DataType.BOOKS -> repository.getBooksInChapter(chapterId).first.map { it.title }
                Tab2DataType.GENERAL_SUBJECTS -> repository.getGeneralSubjectsDirect(chapterId).map { it.title }
                Tab2DataType.DEVICE_SUBJECTS -> {
                    val dev = deviceName ?: return@remember emptyList()
                    repository.getDeviceSubjectsDirect(chapterId, dev).map { it.title }
                }
            }
        } catch (e: Exception) { emptyList() }
    }

    // اسم الفصل للعرض
    val chapterName = remember(chapterId) {
        repository.getChapters().firstOrNull { it.id == chapterId }?.name ?: "الفصل الدراسي"
    }

    var selectedItem by remember { mutableStateOf<String?>(null) }

    // تأثيرات بصرية
    val infiniteTransition = rememberInfiniteTransition(label = "unified_tab2_glow")
    val headerGlow by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(2500), RepeatMode.Reverse),
        label = "header_glow"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF030810), Primary, Color(0xFF0F1F33))))
    ) {
        // رأس الصفحة
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(Color.Transparent, TextGold.copy(alpha = headerGlow * 0.2f), Color.Transparent)))
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
                    val pageTitle = when (dataType) {
                        Tab2DataType.BOOKS -> "الكتب المتاحة"
                        Tab2DataType.GENERAL_SUBJECTS -> "المواد العامة"
                        Tab2DataType.DEVICE_SUBJECTS -> deviceName ?: "المواد التخصصية"
                    }
                    Text(pageTitle, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextGold)
                    Text(chapterName, fontSize = 11.sp, color = TextSecondary)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(TextGold.copy(alpha = 0.2f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("12 قسماً", fontSize = 10.sp, color = TextGold, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (items.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("لا توجد عناصر", color = TextSecondary)
            }
        } else {
            val shelves = items.chunked(3)
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(shelves.size) { shelfIndex ->
                    StaggeredEntrance(delayPerItem = 80 * shelfIndex) {
                        Shelf(spacing = 14.dp, modifier = Modifier.fillMaxWidth()) {
                            shelves[shelfIndex].forEach { item ->
                                Book3DCard(
                                    bookTitle = item,
                                    coverPath = null,
                                    activeBaseDir = null,
                                    onClick = { selectedItem = item }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // حوار موحد للجميع
    selectedItem?.let { item ->
        val dialogMessage = when (dataType) {
            Tab2DataType.BOOKS -> "المقرر: $item\n\nاضغط على زر العرض للدخول لشرائح الـ 12 قسماً والملخصات."
            Tab2DataType.GENERAL_SUBJECTS -> "المادة المشتركة: $item\n\nاضغط على زر العرض لفتح الأقسام والمقرر الدراسي."
            Tab2DataType.DEVICE_SUBJECTS -> "المادة التخصصية لمنظومة ($deviceName):\n$item\n\nاضغط على زر العرض لفتح المرفقات التعليمية والمحاكاة الـ 12."
        }
        MaterialDialog(
            title = "تفاصيل المنهج الدراسي الـ 12",
            message = dialogMessage,
            confirmText = "عرض المحتويات 📚",
            onDismiss = { selectedItem = null },
            onConfirm = {
                val courseId = item.replace(" ", "_").trim()
                onItemConfirmed(courseId)
                selectedItem = null
            }
        )
    }
}
