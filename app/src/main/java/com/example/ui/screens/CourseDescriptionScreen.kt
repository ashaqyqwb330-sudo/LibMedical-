package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.data.CourseDescriptionProvider
import com.example.model.CourseDescription
import com.example.model.Level
import com.example.model.Semester
import com.example.ui.components.Book3DCard
import com.example.ui.components.GlassCard
import com.example.ui.components.Shelf
import com.example.ui.components.staggeredEntrance
import com.example.ui.theme.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import android.webkit.WebView
import android.webkit.WebViewClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDescriptionScreen(
    onBack: () -> Unit,
    onCourseClick: (CourseDescription) -> Unit = {}
) {
    val context = LocalContext.current
    val data = remember { CourseDescriptionProvider.load(context) }
    val levels = remember(data) { data?.levels ?: emptyList() }

    var selectedLevelIndex by remember { mutableIntStateOf(0) }
    var selectedSemesterIndex by remember { mutableIntStateOf(0) }

    val currentLevel = levels.getOrNull(selectedLevelIndex)
    val currentSemester = currentLevel?.semesters?.getOrNull(selectedSemesterIndex)
    val currentCourses = currentSemester?.courses ?: emptyList()

    var selectedCourseForDetail by remember { mutableStateOf<CourseDescription?>(null) }
    var fullscreenCourseHtml by remember { mutableStateOf<CourseDescription?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "توصيفات المقررات",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextGold
                        )
                        Text(
                            text = "${levels.size} مستويات مفعّلة وموثّقة",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
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
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF030810), Primary, Color(0xFF0F1F33))
                    )
                )
                .padding(padding)
        ) {
            // تبويبات المستويات (أفقية)
            if (levels.isNotEmpty()) {
                ScrollableTabRow(
                    selectedTabIndex = selectedLevelIndex,
                    containerColor = Color.Transparent,
                    contentColor = Secondary,
                    edgePadding = 16.dp,
                    indicator = { tabPositions ->
                        if (selectedLevelIndex < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedLevelIndex]),
                                color = TextGold
                            )
                        }
                    },
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    levels.forEachIndexed { index, level ->
                        Tab(
                            selected = selectedLevelIndex == index,
                            onClick = {
                                selectedLevelIndex = index
                                selectedSemesterIndex = 0
                            },
                            text = {
                                Text(
                                    text = level.name,
                                    fontSize = 11.sp,
                                    fontWeight = if (index == selectedLevelIndex) FontWeight.Bold else FontWeight.Normal,
                                    color = if (index == selectedLevelIndex) TextGold else TextSecondary,
                                    maxLines = 1
                                )
                            }
                        )
                    }
                }
            }

            // فصول المستوى المحدد (بطاقات أفقية)
            if (currentLevel != null && currentLevel.semesters.isNotEmpty()) {
                Text(
                    text = "اختر الفصل الدراسي للتحضير والتصفح:",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(currentLevel.semesters.size) { index ->
                        val sem = currentLevel.semesters[index]
                        val isSelected = index == selectedSemesterIndex
                        val borderAlpha = if (isSelected) 0.8f else 0.2f
                        val borderBrush = if (isSelected) Secondary else Color.White.copy(alpha = 0.15f)
                        Card(
                            modifier = Modifier
                                .width(120.dp)
                                .height(60.dp)
                                .clickable { selectedSemesterIndex = index },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0x301A2F3F) else Color(0x10FFFFFF)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderBrush)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = sem.name,
                                    color = if (isSelected) TextGold else TextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "${sem.courses.size} مقرر",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                // عرض المقررات داخل الفصل المحدد (رفوف)
                if (currentCourses.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("لا توجد مقررارات", color = TextSecondary)
                    }
                } else {
                    val shelves = currentCourses.chunked(3)
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 24.dp, top = 12.dp, start = 16.dp, end = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(28.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(shelves) { shelfIndex, shelfCourses ->
                            Shelf(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .staggeredEntrance(shelfIndex)
                            ) {
                                shelfCourses.forEach { course ->
                                    Book3DCard(
                                        bookTitle = course.nameAr,
                                        coverPath = null,
                                        activeBaseDir = null,
                                        onClick = { onCourseClick(course) }
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("لا توجد بيانات متوفرة", color = TextSecondary)
                }
            }
        }
    }

    // Elegant RTL Detail Dialog for Course Description
    if (selectedCourseForDetail != null) {
        val course = selectedCourseForDetail!!
        AlertDialog(
            onDismissRequest = { selectedCourseForDetail = null },
            confirmButton = {
                TextButton(
                    onClick = {
                        onCourseClick(course)
                        selectedCourseForDetail = null
                    }
                ) {
                    Text("فتح المقررات وعروضها 🎓", color = TextGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedCourseForDetail = null }) {
                    Text("إغلاق", color = TextSecondary)
                }
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "📖 تفاصيل التوصيف",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextGold,
                        textAlign = TextAlign.Right
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .background(Color(0x301A2F3F), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = course.id,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Secondary
                        )
                    }
                }
            },
            text = {
                CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.text.style.ResolvedTextDirection.Rtl.let { androidx.compose.ui.unit.LayoutDirection.Rtl }) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = course.nameAr,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "المستوى: ${currentLevel?.name ?: ""} - ${currentSemester?.name ?: ""}",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                        Text(
                            text = course.description.ifEmpty { "لا يوجد توصيف تفصيلي متاح حالياً لهدا المقرر." },
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            lineHeight = 20.sp,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (!course.descriptionHtml.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    fullscreenCourseHtml = course
                                    selectedCourseForDetail = null
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Secondary,
                                    contentColor = Color(0xFF060E1F)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "📄 تصفح التوصيف الأكاديمي التفصيلي (PDF)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            },
            containerColor = Color(0xFF0F1F33),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(16.dp)
        )
    }

    // Fullscreen Dialog for detailed HTML/PDF presentation
    if (fullscreenCourseHtml != null) {
        val course = fullscreenCourseHtml!!
        Dialog(
            onDismissRequest = { fullscreenCourseHtml = null },
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFF060E1F)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    TopAppBar(
                        title = {
                            Column {
                                Text(
                                    text = course.nameAr,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextGold
                                )
                                Text(
                                    text = "تطوير وضمان الجودة - وثيقة مواصفات المقرر الرسمية",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { fullscreenCourseHtml = null }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = TextGold)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0xFF0F1F33)
                        )
                    )
                    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF060E1F))) {
                        HtmlSyllabusViewer(
                            htmlContent = course.descriptionHtml ?: "",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HtmlSyllabusViewer(
    htmlContent: String,
    modifier: Modifier = Modifier
) {
    val styledHtml = remember(htmlContent) {
        val css = """
            <style>
              body {
                background-color: #060E1F;
                color: #F5EFE0;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                direction: rtl;
                text-align: right;
                padding: 16px;
                margin: 0;
                line-height: 1.6;
              }
              h1, h2, h3, h4, .main-title, .section-title, .syllabus-title {
                color: #D4AF37 !important;
                text-shadow: 0 1px 2px rgba(0,0,0,0.5);
              }
              .section-title {
                border-bottom: 2px solid #D4AF37;
                padding-bottom: 6px;
                margin-top: 28px;
                margin-bottom: 14px;
                font-size: 16px;
                font-weight: bold;
              }
              table {
                border-collapse: collapse;
                width: 100% !important;
                margin: 18px 0;
                background-color: #0C1A2C;
                color: #F5EFE0;
                box-shadow: 0 4px 8px rgba(0,0,0,0.3);
                border-radius: 8px;
                overflow: hidden;
              }
              th {
                background-color: #0F1F33 !important;
                color: #D4AF37 !important;
                font-weight: bold;
                border: 1px solid #1A2F4C !important;
                padding: 12px 10px;
                font-size: 13px;
              }
              td {
                border: 1px solid #1A2F4C !important;
                padding: 10px 8px;
                font-size: 12px;
                text-align: right;
                color: #D0C8B0;
              }
              tr:nth-child(even) {
                background-color: #0E223B;
              }
              .filled-data, .filled-data-center {
                color: #F5EFE0 !important;
              }
              .signature-table {
                margin-top: 24px;
                width: 100%;
              }
              .signature-table td {
                border: none !important;
                text-align: center;
              }
              .signature-line {
                border-bottom: 1px dashed #D4AF37;
                height: 14px;
                margin-bottom: 8px;
                width: 80%;
                margin-left: auto;
                margin-right: auto;
              }
              .page {
                background-color: #0B1A2C;
                border: 1px solid #1A2F4C;
                border-radius: 12px;
                padding: 20px;
                margin-bottom: 24px;
                box-shadow: 0 6px 12px rgba(0,0,0,0.4);
              }
              .syllabus-title {
                font-size: 18px;
                font-weight: bold;
                text-align: center;
                margin-top: 20px;
                margin-bottom: 20px;
              }
              /* Scrollbar styling */
              ::-webkit-scrollbar {
                width: 6px;
                height: 6px;
              }
              ::-webkit-scrollbar-track {
                background: #060E1F;
              }
              ::-webkit-scrollbar-thumb {
                background: #D4AF37;
                border-radius: 4px;
              }
            </style>
        """.trimIndent()
        
        "<html><head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">$css</head><body>$htmlContent</body></html>"
    }

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.apply {
                    javaScriptEnabled = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    supportZoom()
                    builtInZoomControls = true
                    displayZoomControls = false
                    textZoom = 100
                }
                setBackgroundColor(0x060E1F)
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL(null, styledHtml, "text/html", "UTF-8", null)
        },
        modifier = modifier
    )
}
