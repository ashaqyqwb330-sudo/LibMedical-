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
        var found = StudyPlanProvider.getStages()
            .flatMap { it.semesters }
            .flatMap { it.courses }
            .firstOrNull { 
                it.id.equals(courseId, ignoreCase = true) || 
                it.nameAr.replace(" ", "_").replace(":", "").replace("-", "_").trim().equals(courseId, ignoreCase = true) 
            }
            
        if (found == null) {
            val decodedName = courseId.replace("_", " ").trim()
            val sampleContents = listOf(
                CourseContent("المحاضرات النظرية", ContentType.TEXT, "المقدمة والمفاهيم الأساسية والبروتوكولات الطبية المعتمدة للمقرر."),
                CourseContent("المحاضرات العملية", ContentType.TEXT, "شرح تفصيلي للمهارات والتدريب السريري والميداني المطبق."),
                CourseContent("الدليل المرئي المصور", ContentType.PDF, "أطلس تشريحي ودليل مصور عالي الدقة يوضح المكونات الحيوية للجسم."),
                CourseContent("دليل الطالب للمقرر", ContentType.PDF, "الدليل التعليمي الموجه لطلبة الميدان لمتابعة المخرجات الأكاديمية."),
                CourseContent("دليل المدرب للمقرر", ContentType.PDF, "دليل الهيئة التدريسية والخطط الدراسية الميدانية المعتمدة."),
                CourseContent("المراجع الرئيسية للمقرر", ContentType.TEXT, "الكتب الأكاديمية والمصادر الطبية الرسمية المعتمدة للمطالعة والتحضير."),
                CourseContent("الفيديوهات الخاصة بالمقرر", ContentType.VIDEO, "دروس مرئية مسجلة وورش عمل سريرية ميدانية تفاعلية."),
                CourseContent("الأنشطة الخاصة بالمقرر", ContentType.INTERACTIVE, "الأنشطة الميدانية والمهام السريرية المصممة لتعزيز الاستيعاب العملي."),
                CourseContent("بنك الأسئلة للمقرر", ContentType.INTERACTIVE, "مجموعة أسئلة تملأ الفجوة المعرفية وتحضر للاختبارات السريرية والنظرية."),
                CourseContent("العروض التقديمية للمقرر", ContentType.PRESENTATION, "عروض شرائح المحاضرات ملخصة ومدعمة بالرسوم والصور البيانية التوضيحية."),
                CourseContent("المحاكاة والواقع الافتراضي", ContentType.VR_SIMULATION, "سيناريوهات إسعافية غامرة تحاكي غرف العمليات والطوارئ الطبية الحربية بدقة."),
                CourseContent("دليل المهارات والتدريب السريري", ContentType.PDF, "كتاب المهارات والتدابير الطبية الإسعافية الطارئة الواجب إتقانها."),
                CourseContent("نافذة الملاحظات عن المقرر", ContentType.TEXT, "توصيات وملاحظات الهيئة المشرفة على المناهج لتحديث وتحسين المفردات.")
            )
            found = com.example.model.Course(
                id = courseId,
                nameAr = decodedName,
                totalCreditHours = 3.0,
                totalActualHours = 96,
                contents = sampleContents
            )
        }
        found
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
