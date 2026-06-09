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
                                        onClick = { selectedCourseForDetail = course }
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
                    }
                }
            },
            containerColor = Color(0xFF0F1F33),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(16.dp)
        )
    }
}
