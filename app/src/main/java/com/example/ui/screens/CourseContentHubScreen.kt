package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StudyPlanProvider
import com.example.model.ContentType
import com.example.model.CourseContent
import com.example.ui.components.GlassCard
import com.example.ui.components.StaggeredEntrance
import com.example.ui.theme.*

// بيانات تعريفية لكل بطاقة من بطاقات المحتويات الـ 12
data class ContentHubItem(
    val titleAr: String,
    val titleEn: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val statusText: String,
    val keyword: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseContentHubScreen(
    courseId: String,
    onBack: () -> Unit,
    onContentClick: (Int) -> Unit
) {
    val context = LocalContext.current

    // جلب تفاصيل المقرر الدراسي أو إنشاء مقرر افتراضي غني بالبيانات
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

    // تجهيز قائمة بطاقات الأقسام الـ 12 المنسقة بألوان وأيقونات مميزة ومبهرة بصرياً
    val hubItems = remember {
        listOf(
            ContentHubItem(
                titleAr = "المحاضرات النظرية",
                titleEn = "Theoretical Lectures",
                description = "المفاهيم الأساسية، والبروتوكولات الإسعافية والطبية النظرية الحديثة.",
                icon = Icons.Filled.List,
                color = Color(0xFF2ECC71),
                statusText = "متوفر 📑",
                keyword = "النظرية"
            ),
            ContentHubItem(
                titleAr = "المحاضرات العملية",
                titleEn = "Practical Sessions",
                description = "دليل التدريبات والتطبيقات السريرية والميدانية التخصصية.",
                icon = Icons.Filled.Build,
                color = Color(0xFF3498DB),
                statusText = "نشط 🩺",
                keyword = "العملية"
            ),
            ContentHubItem(
                titleAr = "الدليل المرئي المصور",
                titleEn = "Visual Atlas",
                description = "أطلس طبي ملون لتوضيح صور الأجهزة والإصابات والتشريح.",
                icon = Icons.Filled.Star,
                color = Color(0xFF9C27B0),
                statusText = "عالي الدقة ✨",
                keyword = "المرئي"
            ),
            ContentHubItem(
                titleAr = "دليل الطالب للمقرر",
                titleEn = "Student Manual",
                description = "مسار الطالب واللوائح والمستندات الأكاديمية التوجيهية.",
                icon = Icons.Filled.Person,
                color = Color(0xFFF39C12),
                statusText = "محدث 📖",
                keyword = "الطالب"
            ),
            ContentHubItem(
                titleAr = "دليل المدرب للمقرر",
                titleEn = "Instructor Manual",
                description = "أدلة تدريسية ومعايير التقييم وتخطيط الأنشطة للمدرب.",
                icon = Icons.Filled.Settings,
                color = Color(0xFFE74C3C),
                statusText = "سري للغاية 🛡️",
                keyword = "المدرب"
            ),
            ContentHubItem(
                titleAr = "المراجع الرئيسية للمقرر",
                titleEn = "Core References",
                description = "الكتب والمراجع الطبية الحيوية المعتمدة للمنهج الدراسي.",
                icon = Icons.Filled.Info,
                color = Color(0xFF1ABC9C),
                statusText = "تحميل مباشر ⬇️",
                keyword = "المراجع"
            ),
            ContentHubItem(
                titleAr = "الفيديوهات التعليمية",
                titleEn = "Video Tutorials",
                description = "شروحات مرئية مسجلة ودروس سريرية حية من المعسكرات والمطارات.",
                icon = Icons.Filled.PlayArrow,
                color = Color(0xFFE74C3C),
                statusText = "6 مقاطع فيديو 🎥",
                keyword = "الفيديو"
            ),
            ContentHubItem(
                titleAr = "الأنشطة التفاعلية",
                titleEn = "Interactive Activities",
                description = "أنشطة تفصيلية وألعاب طبية عسكرية لتنشيط وبناء الفكر السريري.",
                icon = Icons.Filled.Create,
                color = Color(0xFF16A085),
                statusText = "تفاعلية 🎮",
                keyword = "الأنشطة"
            ),
            ContentHubItem(
                titleAr = "بنك الأسئلة للمقرر",
                titleEn = "Question Bank",
                description = "نماذج اختبارات تفاعلية، أسئلة تحصيلية وأجوبة نموذجية.",
                icon = Icons.Filled.CheckCircle,
                color = Color(0xFFF1C40F),
                statusText = "12 نموذج 📝",
                keyword = "بنك"
            ),
            ContentHubItem(
                titleAr = "العروض التقديمية للمقرر",
                titleEn = "Course Slides",
                description = "عروض تقديمية مختصرة تدعم الشرح واسترجاع المعلومات بكفاءة.",
                icon = Icons.Filled.Share,
                color = Color(0xFF34495E),
                statusText = "ملخص جاهز 📊",
                keyword = "العروض"
            ),
            ContentHubItem(
                titleAr = "المحاكاة والواقع الافتراضي",
                titleEn = "Simulation & VR",
                description = "محاكاة غامرة لغرف العمليات ووسائل التدريب بالواقع المعزز الهجين.",
                icon = Icons.Filled.PlayArrow,
                color = Color(0xFF9B59B6),
                statusText = "تفاعلي ثلاثي الأبعاد 🥽",
                keyword = "المحاكاة"
            ),
            ContentHubItem(
                titleAr = "نافذة الملاحظات عن المقرر",
                titleEn = "Faculty Notes",
                description = "التقارير والمقترحات والرسائل التحديثية الصادرة من هيئة التدريس.",
                icon = Icons.Filled.Email,
                color = Color(0xFF95A5A6),
                statusText = "تنبيه هام 💬",
                keyword = "الملاحظات"
            )
        )
    }

    // حالة البحث والفلترة
    var searchQuery by remember { mutableStateOf("") }
    val filteredItems = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            hubItems
        } else {
            hubItems.filter {
                it.titleAr.contains(searchQuery, ignoreCase = true) ||
                it.titleEn.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    // تصميم الخلفية المتدرجة المستقبلية عالية التقنية (Dark Cosmic Blue Gradient with Radial Touch)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF030810),
                        Color(0xFF081224),
                        Color(0xFF0C1B33),
                        Color(0xFF050B14)
                    )
                )
            )
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "مركز المحتويات المتقدمة 🛡️",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextGold
                            )
                            Text(
                                text = "بوابة التعليم والتدريب الطبي العسكري الكلي",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع",
                                tint = TextGold
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            containerColor = Color.Transparent,
            modifier = Modifier.systemBarsPadding()
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                // هيدر المقرر الدراسي الجميل والفريد (تحفة فنية بتصميم GlassCard)
                StaggeredEntrance(delayPerItem = 100) {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        borderAlpha = 0.3f
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = course.nameAr,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Secondary.copy(alpha = 0.15f))
                                        .border(0.5.dp, Secondary, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "المقرر الـ 12 📚",
                                        color = TextGold,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // الساعات المعتمدة
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("الساعات المعتمدة", fontSize = 10.sp, color = TextSecondary)
                                    Text("${course.totalCreditHours} ساعة", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextGold)
                                }
                                // الساعات الفعلية
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("الساعات التدريبية", fontSize = 10.sp, color = TextSecondary)
                                    Text("${course.totalActualHours} ساعة", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                                // مؤشر الميدانية / الجاهزية
                                Column(modifier = Modifier.weight(1.2f)) {
                                    Text("مؤشر الجاهزية للدور", fontSize = 10.sp, color = TextSecondary)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFF2ECC71))
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("تدريب نشط 100%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2ECC71))
                                    }
                                }
                            }
                        }
                    }
                }

                // صندوق البحث التفاعلي الأنيق المزود بأيقونة تصفية لتجربة مستخدم رفيعة
                StaggeredEntrance(delayPerItem = 180) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        placeholder = {
                            Text(
                                "ابحث عن المحاضرات، الأسئلة، الأدلة، أو المحاكاة...",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "بحث",
                                tint = TextGold
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Filled.Clear,
                                        contentDescription = "مسح",
                                        tint = TextSecondary
                                    )
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TextGold,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                            focusedContainerColor = Color(0x110B1A2A),
                            unfocusedContainerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                // عرض شبكة الأقسام الـ 12 المذهلة مع الفلترة والتحميل التدريجي الذكي
                if (filteredItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "لم يتم العثور على أي نتائج تطابق البحث الخاص بك.",
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 160.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        itemsIndexed(filteredItems) { index, item ->
                            StaggeredEntrance(delayPerItem = 80 * index + 150) {
                                ContentHubCard(
                                    item = item,
                                    onClick = {
                                        // البحث عن الفهرس الحقيقي لهذا القسم المطابق في قائمة محتويات المقرر للتوجيه الآمن
                                        val realIndex = getHubItemIndex(item.keyword, course.contents)
                                        if (realIndex != -1) {
                                            onContentClick(realIndex)
                                        } else {
                                            Toast.makeText(context, "القسم غير مهيأ حالياً", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// دالة مساعدة لتوفير المطابقة الديناميكية والآمنة للفهرس
private fun getHubItemIndex(keyword: String, contents: List<CourseContent>): Int {
    val exactIdx = contents.indexOfFirst { it.titleAr.contains(keyword, ignoreCase = true) }
    if (exactIdx != -1) return exactIdx
    
    // محاول مرادفات تكميلية
    if (keyword == "النظرية") {
        val backup = contents.indexOfFirst { it.titleAr.contains("نظري") }
        if (backup != -1) return backup
    }
    if (keyword == "العملية") {
        val backup = contents.indexOfFirst { it.titleAr.contains("عملي") }
        if (backup != -1) return backup
    }
    if (keyword == "الملاحظات") {
        val backup = contents.indexOfFirst { it.titleAr.contains("ملاحظات") }
        if (backup != -1) return backup
    }
    
    return 0
}

// تصميم بطاقة القسم الفريدة وعالية الفخامة (Themed Neomorphic-feeling Glass Container)
@Composable
fun ContentHubCard(
    item: ContentHubItem,
    onClick: () -> Unit
) {
    // أنيميشن عند ضغط الإصبع لإضفاء شعور رائع بالاستجابة
    var isPressed by remember { mutableStateOf(false) }
    val scale = animateFloatAsState(if (isPressed) 0.96f else 1.0f, label = "press_scale")
    val alpha = animateFloatAsState(if (isPressed) 0.85f else 1.0f, label = "press_alpha")

    Box(
        modifier = Modifier
            .scale(scale.value)
            .alpha(alpha.value)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x180B1A2A))
            .border(
                BorderStroke(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            item.color.copy(alpha = 0.4f),
                            Color.White.copy(alpha = 0.05f)
                        )
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable {
                onClick()
            }
            .padding(12.dp)
            .fillMaxWidth()
            .height(200.dp) // ارتفاع ثابت متناسق للحصول على شبكة مرتبة كخلية النحل المذهلة
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // القسم العلوي: الأيقونة الملونة والشارة المميزة
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // حاوية أيقونة مضيئة بشكل نيون ناعم وملون
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(item.color.copy(alpha = 0.15f))
                        .border(1.dp, item.color.copy(alpha = 0.6f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = item.color,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // الشارة التوضيحية الأنيقة (حسابية، مميزة)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .border(0.5.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = item.statusText,
                        fontSize = 8.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // المسمى العربي الرئيسي بخط عريض وفخم
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.titleAr,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                // المسمى الإنجليزي المعبر ذو الإيحاء المستقبلي
                Text(
                    text = item.titleEn,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    color = item.color.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // الوصف الشامل والتوجيه بأسلوب مصفوفة عسكري رائع
                Text(
                    text = item.description,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // مؤشر السهم الأنيق للولوج
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowLeft,
                    contentDescription = "دخول",
                    tint = item.color.copy(alpha = 0.8f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
