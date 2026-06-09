package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StudyPlanProvider
import com.example.model.ContentType
import com.example.model.CourseContent
import com.example.ui.components.GlassCard
import com.example.ui.components.GoldButton
import com.example.ui.components.Shelf
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    courseId: String,
    onBack: () -> Unit,
    onContentClick: (Int) -> Unit = {}
) {
    val course = remember {
        StudyPlanProvider.getStages()
            .flatMap { it.semesters }
            .flatMap { it.courses }
            .firstOrNull { it.id == courseId }
    }

    if (course == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFF030810), Primary, Color(0xFF0F1F33)))),
            contentAlignment = Alignment.Center
        ) {
            Text("المقرر غير موجود", style = MaterialTheme.typography.bodyLarge, color = Color.White)
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(course.nameAr, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextGold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = TextGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFF030810), Primary, Color(0xFF0F1F33))))
                .padding(padding)
                .padding(16.dp)
        ) {
            // معلومات أساسية
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("رمز المقرر: ${course.id}", fontWeight = FontWeight.Bold, color = TextGold, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("الساعات المعتمدة: ${course.totalCreditHours} ساعة", color = TextPrimary)
                    Text("الساعات الفعلية: ${course.totalActualHours} ساعة", color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "محتوى المقرر (${course.contents.size} قسماً)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextGold
            )
            Text(
                "اختر أي قسم لعرض تفاصيله الكاملة المحاكية والتعليمية",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // عرض المحتويات كرف مع Shelf
            Shelf(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    course.contents.forEachIndexed { index, content ->
                        ContentCard(
                            content = content,
                            onClick = { onContentClick(index) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ContentCard(content: CourseContent, onClick: () -> Unit = {}) {
    val icon = when (content.type) {
        ContentType.TEXT -> Icons.Filled.List
        ContentType.VIDEO -> Icons.Filled.PlayArrow
        ContentType.PDF -> Icons.Filled.Info
        ContentType.PRESENTATION -> Icons.Filled.Share
        ContentType.INTERACTIVE -> Icons.Filled.Create
        ContentType.VR_SIMULATION -> Icons.Filled.Build
    }
    // نقوم بجلب الألوان مباشرة من الدالة المساعدة العامة المعرفة بـ CourseContentDetailScreen
    val typeColor = getContentTypeColor(content.type)

    Card(
        modifier = Modifier
            .width(160.dp)
            .animateContentSize()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0x1FFFFFFF))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(32.dp), tint = typeColor)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content.titleAr,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                maxLines = 1
            )
            if (content.descriptionAr.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = content.descriptionAr,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 2,
                    lineHeight = 14.sp
                )
            }
        }
    }
}
