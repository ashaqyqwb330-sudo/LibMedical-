// @builder:file app/src/main/java/com/example/ui/screens/CourseContentDetailScreen.kt
package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StudyPlanProvider
import com.example.model.ContentType
import com.example.model.CourseContent
import com.example.ui.components.GlassCard
import com.example.ui.components.GoldButton
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseContentDetailScreen(
    courseId: String,
    contentIndex: Int,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // البحث عن المقرر والمحتوى
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
    val content = remember(course, contentIndex) {
        course?.contents?.getOrNull(contentIndex)
    }

    if (course == null || content == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("المحتوى غير موجود", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = content.titleAr,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextGold
                        )
                        Text(
                            text = course.nameAr,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFF030810), Primary, Color(0xFF0F1F33))))
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // أيقونة ونوع المحتوى
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x15FFFFFF)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val icon = getContentIcon(content.type)
                    val typeLabel = getContentTypeLabel(content.type)
                    val typeColor = getContentTypeColor(content.type)

                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(typeColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            icon,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = typeColor
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = typeLabel,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = typeColor
                    )
                    Text(
                        text = content.descriptionAr.ifBlank { "محتوى تعليمي معتمد في المنهج الطبي العسكري" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // قائمة المحتويات التفصيلية حسب النوع
            when (content.type) {
                ContentType.TEXT -> TextContentSection()
                ContentType.VIDEO -> VideoContentSection()
                ContentType.PDF -> PdfContentSection(courseId, contentIndex)
                ContentType.PRESENTATION -> PresentationContentSection()
                ContentType.INTERACTIVE -> InteractiveContentSection()
                ContentType.VR_SIMULATION -> VrSimulationSection()
            }
        }
    }
}

// ========== أقسام المحتوى حسب النوع ==========

@Composable
private fun TextContentSection() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "المحتوى النظري",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextGold
        )
        Spacer(modifier = Modifier.height(12.dp))

        // قائمة مواضيع نظرية وهمية (يمكن جلبها من DataProvider)
        val topics = listOf(
            "مقدمة وتعريفات أساسية",
            "التصنيفات والتقسيمات الرئيسية",
            "آليات العمل والاستجابة",
            "الحالات الشائعة والتشخيص",
            "البروتوكولات العلاجية المعتمدة",
            "خلاصة ومراجعة شاملة"
        )
        topics.forEachIndexed { index, topic ->
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Secondary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${index + 1}", color = Secondary, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = topic,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun VideoContentSection() {
    val context = LocalContext.current
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "الفيديوهات التعليمية",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextGold
        )
        Spacer(modifier = Modifier.height(12.dp))

        val videos = listOf(
            "المحاضرة التمهيدية (30 دقيقة)" to "مقدمة شاملة للمادة",
            "شرح الحالات السريرية (45 دقيقة)" to "تحليل عملي للحالات الشائعة",
            "ورشة تطبيقية (20 دقيقة)" to "تطبيق عملي للخطوات الأساسية",
            "مراجعة شاملة (15 دقيقة)" to "ملخص سريع قبل الاختبار"
        )
        videos.forEach { (title, desc) ->
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable {
                        Toast.makeText(context, "سيتم فتح الفيديو قريباً", Toast.LENGTH_SHORT).show()
                    }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.PlayArrow,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(title, fontWeight = FontWeight.SemiBold, color = Color.White)
                        Text(desc, fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
private fun PdfContentSection(courseId: String, contentIndex: Int) {
    val context = LocalContext.current
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "المستندات والملفات",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextGold
        )
        Spacer(modifier = Modifier.height(12.dp))

        val docs = listOf(
            "الدليل الكامل للمقرر" to "PDF - 45 صفحة",
            "ملخص النقاط الرئيسية" to "PDF - 12 صفحة",
            "جداول وخرائط ذهنية" to "PDF - 8 صفحات"
        )
        docs.forEach { (title, desc) ->
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable {
                        Toast.makeText(context, "سيتم فتح المستند قريباً", Toast.LENGTH_SHORT).show()
                    }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Info,
                        contentDescription = null,
                        tint = Color(0xFFE74C3C),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(title, fontWeight = FontWeight.SemiBold, color = Color.White)
                        Text(desc, fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
private fun PresentationContentSection() {
    val context = LocalContext.current
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "العروض التقديمية",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextGold
        )
        Spacer(modifier = Modifier.height(12.dp))

        val presentations = listOf(
            "المحاضرة 1: المقدمة والتعريفات" to "24 شريحة",
            "المحاضرة 2: التشخيص والفحص" to "32 شريحة",
            "المحاضرة 3: العلاج والتدخل" to "28 شريحة"
        )
        presentations.forEach { (title, desc) ->
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable {
                        Toast.makeText(context, "سيتم فتح العرض التقديمي قريباً", Toast.LENGTH_SHORT).show()
                    }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Share,
                        contentDescription = null,
                        tint = Color(0xFF3498DB),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(title, fontWeight = FontWeight.SemiBold, color = Color.White)
                        Text(desc, fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
private fun InteractiveContentSection() {
    val context = LocalContext.current
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "الأنشطة التفاعلية وبنك الأسئلة",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextGold
        )
        Spacer(modifier = Modifier.height(12.dp))

        // قسم بنك الأسئلة
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0x15FFFFFF)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("بنك الأسئلة (MCQ)", fontWeight = FontWeight.Bold, color = TextOrange)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "س1: ما هو التعريف الصحيح لهذا المصطلح؟",
                    color = Color.White,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                listOf(
                    "أ. الإجابة الأولى (غير صحيحة)",
                    "ب. الإجابة الثانية (صحيحة) ✓",
                    "ج. الإجابة الثالثة (غير صحيحة)",
                    "د. الإجابة الرابعة (غير صحيحة)"
                ).forEach { answer ->
                    Text(
                        text = answer,
                        color = if (answer.contains("✓")) Color.Green else TextSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        GoldButton(
            text = "فتح التمرين التفاعلي",
            onClick = {
                Toast.makeText(context, "سيتم فتح التمرين التفاعلي قريباً", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun VrSimulationSection() {
    val context = LocalContext.current
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "المحاكاة والواقع الافتراضي",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextGold
        )
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0x15FFFFFF)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Filled.Build,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = TextOrange
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "محاكاة طبية تفاعلية",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextGold
                )
                Text(
                    "تجربة محاكاة غامرة للتدريب على الإجراءات الطبية في بيئة آمنة وواقعية.",
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                GoldButton(
                    text = "بدء المحاكاة",
                    onClick = {
                        Toast.makeText(context, "سيتم تشغيل المحاكاة قريباً", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// ========== دوال مساعدة ==========

fun getContentIcon(type: ContentType): ImageVector = when (type) {
    ContentType.TEXT -> Icons.Filled.List
    ContentType.VIDEO -> Icons.Filled.PlayArrow
    ContentType.PDF -> Icons.Filled.Info
    ContentType.PRESENTATION -> Icons.Filled.Share
    ContentType.INTERACTIVE -> Icons.Filled.Create
    ContentType.VR_SIMULATION -> Icons.Filled.Build
}

fun getContentTypeLabel(type: ContentType): String = when (type) {
    ContentType.TEXT -> "المحاضرات النظرية"
    ContentType.VIDEO -> "الفيديوهات التعليمية"
    ContentType.PDF -> "المستندات والأدلة"
    ContentType.PRESENTATION -> "العروض التقديمية"
    ContentType.INTERACTIVE -> "الأنشطة التفاعلية"
    ContentType.VR_SIMULATION -> "المحاكاة والواقع الافتراضي"
}

fun getContentTypeColor(type: ContentType): Color = when (type) {
    ContentType.TEXT -> Color(0xFF4CAF50)
    ContentType.VIDEO -> Color(0xFFF44336)
    ContentType.PDF -> Color(0xFFE74C3C)
    ContentType.PRESENTATION -> Color(0xFF3498DB)
    ContentType.INTERACTIVE -> Color(0xFFFF9800)
    ContentType.VR_SIMULATION -> Color(0xFF9C27B0)
}
// @builder:end