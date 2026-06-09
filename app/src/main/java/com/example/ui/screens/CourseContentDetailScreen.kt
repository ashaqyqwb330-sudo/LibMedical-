package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

    val course = remember(courseId) {
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
            Text("المحتوى غير موجود", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
        }
        return
    }

    val typeColor = getContentTypeColor(content.type)
    val typeIcon = getContentIcon(content.type)
    val typeLabel = getContentTypeLabel(content.type)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = content.titleAr, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextGold)
                        Text(text = course.nameAr, fontSize = 12.sp, color = TextSecondary)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = TextGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F1F33)
                )
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
            // بطاقة رأسية مميزة
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x15FFFFFF)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // دائرة الأيقونة
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(RoundedCornerShape(25.dp))
                            .background(
                                Brush.radialGradient(
                                    listOf(typeColor.copy(alpha = 0.3f), typeColor.copy(alpha = 0.05f))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            typeIcon,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = typeColor
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = typeLabel,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = typeColor
                    )
                    Text(
                        text = content.descriptionAr.ifBlank { "محتوى تعليمي معتمد في المنهج الطبي العسكري" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp),
                        lineHeight = 22.sp
                    )
                    // شريط تقدم زخرفي
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(typeColor.copy(alpha = 0.6f))
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // المحتوى حسب النوع
            when (content.type) {
                ContentType.TEXT -> TextContentSection(typeColor)
                ContentType.VIDEO -> VideoContentSection(typeColor)
                ContentType.PDF -> PdfContentSection(typeColor)
                ContentType.PRESENTATION -> PresentationContentSection(typeColor)
                ContentType.INTERACTIVE -> InteractiveContentSection(typeColor)
                ContentType.VR_SIMULATION -> VrSimulationSection(typeColor)
            }
        }
    }
}

// ========== أقسام المحتوى المحسّنة ==========

@Composable
private fun TextContentSection(accentColor: Color) {
    Column(modifier = Modifier.padding(16.dp)) {
        SectionHeader("المحتوى النظري", "📄", accentColor)

        val topics = listOf(
            "مقدمة وتعريفات أساسية" to "تمهيد شامل للموضوع مع شرح المفاهيم الأساسية",
            "التصنيفات والتقسيمات الرئيسية" to "عرض منهجي للتصنيفات المعتمدة",
            "آليات العمل والاستجابة" to "شرح تفصيلي للآليات الفسيولوجية",
            "الحالات الشائعة والتشخيص" to "مراجعة الحالات السريرية الأكثر شيوعاً",
            "البروتوكولات العلاجية المعتمدة" to "خطوات العلاج وفق الأدلة الطبية",
            "خلاصة ومراجعة شاملة" to "ملخص لأهم النقاط والمفاهيم"
        )
        topics.forEachIndexed { index, (title, desc) ->
            GlassCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp))
                            .background(accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${index + 1}", color = accentColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(text = title, style = MaterialTheme.typography.bodyLarge, color = Color.White, fontWeight = FontWeight.SemiBold)
                        Text(text = desc, fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
private fun VideoContentSection(accentColor: Color) {
    val context = LocalContext.current
    Column(modifier = Modifier.padding(16.dp)) {
        SectionHeader("الفيديوهات التعليمية", "🎥", accentColor)

        val videos = listOf(
            "المحاضرة التمهيدية" to "30 دقيقة – مقدمة شاملة للمادة",
            "شرح الحالات السريرية" to "45 دقيقة – تحليل عملي للحالات الشائعة",
            "ورشة تطبيقية" to "20 دقيقة – تطبيق عملي للخطوات الأساسية",
            "مراجعة شاملة" to "15 دقيقة – ملخص سريع قبل الاختبار"
        )
        videos.forEach { (title, desc) ->
            GlassCard(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    .clickable { Toast.makeText(context, "سيتم فتح الفيديو قريباً", Toast.LENGTH_SHORT).show() }
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp))
                            .background(accentColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = accentColor, modifier = Modifier.size(26.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(title, fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 15.sp)
                        Text(desc, fontSize = 12.sp, color = TextSecondary)
                    }
                    Text("◀", color = TextSecondary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun PdfContentSection(accentColor: Color) {
    val context = LocalContext.current
    Column(modifier = Modifier.padding(16.dp)) {
        SectionHeader("المستندات والملفات", "📑", accentColor)

        val docs = listOf(
            "الدليل الكامل للمقرر" to "PDF – 45 صفحة – مرجع شامل",
            "ملخص النقاط الرئيسية" to "PDF – 12 صفحة – للمراجعة السريعة",
            "جداول وخرائط ذهنية" to "PDF – 8 صفحات – تنظيم بصري"
        )
        docs.forEach { (title, desc) ->
            GlassCard(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    .clickable { Toast.makeText(context, "سيتم فتح المستند قريباً", Toast.LENGTH_SHORT).show() }
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp))
                            .background(accentColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Info, contentDescription = null, tint = accentColor, modifier = Modifier.size(26.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(title, fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 15.sp)
                        Text(desc, fontSize = 12.sp, color = TextSecondary)
                    }
                    Icon(Icons.Filled.Share, contentDescription = null, tint = TextSecondary)
                }
            }
        }
    }
}

@Composable
private fun PresentationContentSection(accentColor: Color) {
    val context = LocalContext.current
    Column(modifier = Modifier.padding(16.dp)) {
        SectionHeader("العروض التقديمية", "📊", accentColor)

        val presentations = listOf(
            "المحاضرة 1: المقدمة والتعريفات" to "24 شريحة – أساسيات المادة",
            "المحاضرة 2: التشخيص والفحص" to "32 شريحة – الجانب السريري",
            "المحاضرة 3: العلاج والتدخل" to "28 شريحة – البروتوكولات العلاجية"
        )
        presentations.forEach { (title, desc) ->
            GlassCard(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    .clickable { Toast.makeText(context, "سيتم فتح العرض التقديمي قريباً", Toast.LENGTH_SHORT).show() }
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp))
                            .background(accentColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.List, contentDescription = null, tint = accentColor, modifier = Modifier.size(26.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(title, fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 15.sp)
                        Text(desc, fontSize = 12.sp, color = TextSecondary)
                    }
                    Text("◀", color = TextSecondary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun InteractiveContentSection(accentColor: Color) {
    val context = LocalContext.current
    Column(modifier = Modifier.padding(16.dp)) {
        SectionHeader("الأنشطة التفاعلية وبنك الأسئلة", "🎮", accentColor)

        // بطاقة بنك الأسئلة
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0x15FFFFFF)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Info, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("بنك الأسئلة (MCQ)", fontWeight = FontWeight.Bold, color = accentColor, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("س1: ما هو التعريف الصحيح لهذا المصطلح؟", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                listOf(
                    "أ. الإجابة الأولى" to false,
                    "ب. الإجابة الثانية (صحيحة) ✓" to true,
                    "ج. الإجابة الثالثة" to false,
                    "د. الإجابة الرابعة" to false
                ).forEach { (answer, isCorrect) ->
                    Text(
                        text = answer,
                        color = if (isCorrect) Color(0xFF4CAF50) else TextSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(vertical = 3.dp),
                        fontWeight = if (isCorrect) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        GoldButton(
            onClick = { Toast.makeText(context, "سيتم فتح التمرين التفاعلي قريباً", Toast.LENGTH_SHORT).show() },
            modifier = Modifier.fillMaxWidth(),
            text = "فتح التمرين التفاعلي"
        )
    }
}

@Composable
private fun VrSimulationSection(accentColor: Color) {
    val context = LocalContext.current
    Column(modifier = Modifier.padding(16.dp)) {
        SectionHeader("المحاكاة والواقع الافتراضي", "🥽", accentColor)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0x15FFFFFF)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(100.dp).clip(RoundedCornerShape(25.dp))
                        .background(
                            Brush.radialGradient(
                                listOf(accentColor.copy(alpha = 0.3f), accentColor.copy(alpha = 0.05f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = accentColor
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "محاكاة طبية تفاعلية",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = TextGold
                )
                Text(
                    "تجربة محاكاة غامرة للتدريب على الإجراءات الطبية في بيئة آمنة وواقعية.",
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp),
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(20.dp))
                GoldButton(
                    onClick = { Toast.makeText(context, "سيتم تشغيل المحاكاة قريباً", Toast.LENGTH_SHORT).show() },
                    modifier = Modifier.fillMaxWidth(),
                    text = "بدء المحاكاة"
                )
            }
        }
    }
}

// ========== مكونات مساعدة ==========

@Composable
private fun SectionHeader(title: String, emoji: String, accentColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
        Text(emoji, fontSize = 24.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextGold
        )
        Spacer(modifier = Modifier.weight(1f))
        Box(modifier = Modifier.width(30.dp).height(3.dp).clip(RoundedCornerShape(2.dp)).background(accentColor))
    }
}

fun getContentIcon(type: ContentType): ImageVector = when (type) {
    ContentType.TEXT -> Icons.Filled.List
    ContentType.VIDEO -> Icons.Filled.PlayArrow
    ContentType.PDF -> Icons.Filled.Info
    ContentType.PRESENTATION -> Icons.Filled.List
    ContentType.INTERACTIVE -> Icons.Filled.Create
    ContentType.VR_SIMULATION -> Icons.Filled.PlayArrow
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
