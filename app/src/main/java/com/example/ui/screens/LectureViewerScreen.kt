package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LectureContent
import com.example.data.LectureContentProvider
import com.example.data.LectureSection
import com.example.data.StudyPlanProvider
import com.example.ui.components.GlassCard
import com.example.ui.components.StaggeredEntrance
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LectureViewerScreen(
    courseId: String,
    contentType: String,
    onBack: () -> Unit
) {
    val isTheory = remember(contentType) { contentType.equals("THEORY", ignoreCase = true) }
    
    // جلب البيانات من المزود
    val lecture = remember(courseId, contentType) {
        LectureContentProvider.getLectureContent(courseId, contentType)
    }

    // جلب اسم المقرر الحقيقي من الخطة الدراسية
    val courseName = remember(courseId) {
        val course = StudyPlanProvider.getStages()
            .flatMap { it.semesters }
            .flatMap { it.courses }
            .firstOrNull { 
                it.id.equals(courseId, ignoreCase = true) || 
                it.nameAr.replace(" ", "_").replace(":", "").replace("-", "_").trim().equals(courseId, ignoreCase = true) 
            }
        course?.nameAr ?: "مقرر الطوارئ الميداني"
    }

    // تنسيق خلفية مخصصة وفخمة حسب طبيعة المحاضرة
    val bgBrush = remember(isTheory) {
        if (isTheory) {
            // كحلي هادئ مائل إلى الزرقة مع رمادي داكن لإضفاء طابع "جلسة علمية عميقة" وبقراءة مريحة للعينين
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF020914),
                    Color(0xFF061226),
                    Color(0xFF040F20)
                )
            )
        } else {
            // أخضر عسكري غامق تكتيكي ممزوج بالكحلي لإضفاء مظهر "ميدان عملي تشغيلي" مفعم بالحياة والواقعية
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF02120C),
                    Color(0xFF061F16),
                    Color(0xFF03100B)
                )
            )
        }
    }

    // اللون الرئيسي المميز للهوية البصرية لكل محاضرة
    val themeColor = remember(isTheory) {
        if (isTheory) Secondary // الذهبي الفخم للمحاضرات النظرية الأكاديمية
        else Color(0xFF2ECC71) // الأخضر المضيء المفعم بالحيوية للورشات والتدريبات العملية
    }

    val scaffoldContainerColor = Color.Transparent

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgBrush)
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isTheory) "المحاضرة النظرية 📑" else "التدريب العملي الميداني 🩺",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = themeColor
                            )
                            Text(
                                text = courseName,
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع",
                                tint = themeColor
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            containerColor = scaffoldContainerColor,
            modifier = Modifier.systemBarsPadding()
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // هيدر المحاضرة الماسي الجميل (Hero Graphic View)
                StaggeredEntrance(delayPerItem = 80) {
                    LectureHeaderSection(
                        isTheory = isTheory,
                        lecture = lecture,
                        themeColor = themeColor
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // بطاقة الأهداف التعليمية الأنيقة (Learning Objectives Highlight)
                StaggeredEntrance(delayPerItem = 140) {
                    ObjectivesCard(
                        objectives = lecture.objectives,
                        themeColor = themeColor
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // عرض الأقسام بطرق مختلفة كلياً حسب نوع المحاضرة لإثارة دهشة المستخدم وإسعاده!
                if (isTheory) {
                    // المحتويات النظرية: تصميم واجهة "كتاب طبي فاخر" ذو تباين ناعم وتنقل متدرج
                    TheoreticalContentList(sections = lecture.sections, themeColor = themeColor)
                } else {
                    // المحتويات العملية: تصميم "منصة خطى مسلسلة (Tactical Stepper)" ملهمة وحيوية للميدان
                    PracticalStepByStepSection(sections = lecture.sections, themeColor = themeColor)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// هيدر المحاضرة الفريد بتصميم بطاقة عائمة
@Composable
fun LectureHeaderSection(
    isTheory: Boolean,
    lecture: LectureContent,
    themeColor: Color
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderAlpha = 0.25f
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // أيقونة شارة مضيئة بنبض نيون ناعم
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(themeColor.copy(alpha = 0.12f))
                    .border(1.5.dp, themeColor.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isTheory) Icons.Filled.List else Icons.Filled.Build,
                    contentDescription = null,
                    tint = themeColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // المسمى الرئيسي الفخم للمحاضرة
            Text(
                text = lecture.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // تفاصيل المحاضر والزمان بقوالب صغيرة ذكية
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // المحاضر المشرف
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.04f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = themeColor.copy(alpha = 0.8f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = lecture.instructor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // مدة المحاضرة
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.04f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null,
                        tint = themeColor.copy(alpha = 0.8f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = lecture.duration,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextGold
                    )
                }
            }
        }
    }
}

// بطاقة الأهداف التعليمية الأنيقة من الدرجة الأولى للتدريس
@Composable
fun ObjectivesCard(
    objectives: List<String>,
    themeColor: Color
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderAlpha = 0.2f
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = themeColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "الأهداف التعليمية للمحاضرة",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = themeColor
                )
            }

            objectives.forEachIndexed { index, objective ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(themeColor)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = objective,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// تصميم أقسام المحاضرة النظرية: كصفحات كتاب طبي فخم وهادئ
@Composable
fun TheoreticalContentList(
    sections: List<LectureSection>,
    themeColor: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "المتن العلمي والتحليل الإكلينيكي 🩺",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = themeColor,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        sections.forEachIndexed { index, section ->
            StaggeredEntrance(delayPerItem = 200 + index * 60) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderAlpha = 0.15f
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // عنوان الفقرة الأكاديمي مع رقمها الترتيبي الروماني أو الحسابي
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "${index + 1}. ${section.heading}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            // علامة كتاب ورقية صغيرة دلالة على الكتاب والدرس النظري
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = null,
                                tint = themeColor.copy(alpha = 0.4f),
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // نص الفقرة الطبية التفصيلية بخط فسيح ومريح بالقراءة
                        Text(
                            text = section.body,
                            fontSize = 13.sp,
                            color = TextSecondary,
                            lineHeight = 22.sp,
                            textAlign = TextAlign.Justify
                        )

                        // تلميحة أو اقتباس طبي للمراجعة الذكية للطلبة
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(themeColor.copy(alpha = 0.05f))
                                .border(0.5.dp, themeColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = themeColor,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "توصية أكاديمية: تم استنباط هذا البند من لوائح الرعاية الطبية العالمية لفرز المصابين.",
                                    fontSize = 10.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// تصميم أقسام المحاضرة العملية: مصفوفة خطوات متقدمة (Operational Tactical Stepper)
@Composable
fun PracticalStepByStepSection(
    sections: List<LectureSection>,
    themeColor: Color
) {
    // تتبع الخطوة الحالية التي يقرأها المتدرب بنظام زوارق تفاعلي
    var activeStepIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "خطوات المناورة والإجراء السريري ⚙️",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = themeColor,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // الأفق الدائري للخطوات المنجزة والتي تحت التنفيذ لتوجيه عقلي سريع
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            sections.forEachIndexed { index, _ ->
                val isCompleted = index < activeStepIndex
                val isActive = index == activeStepIndex

                val stepColor = when {
                    isActive -> themeColor
                    isCompleted -> themeColor.copy(alpha = 0.4f)
                    else -> Color.White.copy(alpha = 0.15f)
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeStepIndex = index }
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (isActive) themeColor else Color.White.copy(alpha = 0.06f))
                            .border(1.5.dp, stepColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = themeColor,
                                modifier = Modifier.size(18.dp)
                            )
                        } else {
                            Text(
                                text = "${index + 1}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isActive) Color.Black else TextPrimary
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "المرحلة ${index + 1}",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isActive) themeColor else TextSecondary
                    )
                }

                // سهم رابط بين الدوائر
                if (index < sections.size - 1) {
                    Box(
                        modifier = Modifier
                            .height(1.5.dp)
                            .weight(0.5f)
                            .background(
                                if (index < activeStepIndex) themeColor.copy(alpha = 0.4f)
                                else Color.White.copy(alpha = 0.15f)
                            )
                    )
                }
            }
        }

        // عرض تفاصيل الخطوة النشطة مع أنيميشن انتقال وتلاشي فائق السلاسة والسرعة
        AnimatedContent(
            targetState = activeStepIndex,
            transitionSpec = {
                slideInHorizontally { width -> -width } + fadeIn() togetherWith
                slideOutHorizontally { width -> width } + fadeOut()
            },
            label = "step_content_anim"
        ) { step ->
            val section = sections[step]
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderAlpha = 0.3f
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    // شارة دليل الخطوات التكتيكية
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(themeColor.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "تدريب نشط وعملي 🔥",
                                color = themeColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Text(
                            text = "خطوة ${step + 1} من ${sections.size}",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // عنوان الخطوة السريرية المصورة بوضوح عالي
                    Text(
                        text = section.heading,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // النص التطبيقي التفصيلي للإجراء والمسار العلاجي
                    Text(
                        text = section.body,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // لوحة التوجيه السريالي التكتيكية (Tactical Practical Notice)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFE74C3C).copy(alpha = 0.1f))
                            .border(1.dp, Color(0xFFE74C3C).copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = null,
                                tint = Color(0xFFE74C3C),
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(top = 1.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "تنبيه خطوة حرجة (Critical Action Point):",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE74C3C)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "يجب إجراء هذا البند بإتقان ودقة تامة لتجنب تضرر المصاب البنيوي في البيئات المعادية عالية التوتر.",
                                    fontSize = 10.sp,
                                    color = TextSecondary,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // أزرار التنقل الرائعة والسريعة أسفل البطاقة
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // السابق
                        Button(
                            onClick = { if (activeStepIndex > 0) activeStepIndex-- },
                            enabled = activeStepIndex > 0,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.08f),
                                contentColor = TextPrimary,
                                disabledContainerColor = Color.Transparent,
                                disabledContentColor = Color.White.copy(alpha = 0.15f)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text("السابق", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        // التالي
                        Button(
                            onClick = { if (activeStepIndex < sections.size - 1) activeStepIndex++ },
                            enabled = activeStepIndex < sections.size - 1,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = themeColor,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text("التالي", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
