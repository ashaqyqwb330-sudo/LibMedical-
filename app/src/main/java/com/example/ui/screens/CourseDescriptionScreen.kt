package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CourseDescriptionProvider
import com.example.model.CourseDescription
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
    val levels = remember { CourseDescriptionProvider.getLevelNames() }
    val coursesByLevel = remember { CourseDescriptionProvider.getCoursesByLevel() }
    var selectedLevelIndex by remember { mutableIntStateOf(0) }

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
                            text = "النسخ العسكرية - ${levels.size} مستويات",
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
                .background(Brush.verticalGradient(listOf(Color(0xFF030810), Primary, Color(0xFF0F1F33))))
                .padding(padding)
        ) {
            // تبويبات المستويات
            ScrollableTabRow(
                selectedTabIndex = selectedLevelIndex,
                containerColor = Color.Transparent,
                contentColor = Secondary,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedLevelIndex]),
                        color = TextGold
                    )
                },
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                levels.forEachIndexed { index, levelName ->
                    val shortName = levelName
                        .replace("المستوى ", "م ")
                        .replace(" - الفصل ", ".")
                        .replace("المستوى المتقدم", "متقدم")
                    Tab(
                        selected = selectedLevelIndex == index,
                        onClick = { selectedLevelIndex = index },
                        text = {
                            Text(
                                text = shortName,
                                fontSize = 11.sp,
                                fontWeight = if (selectedLevelIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedLevelIndex == index) TextGold else TextSecondary,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    )
                }
            }

            // محتوى المستوى المحدد
            val currentLevelName = levels.getOrElse(selectedLevelIndex) { "" }
            val currentCourses = coursesByLevel[currentLevelName] ?: emptyList()

            if (currentCourses.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("لا توجد مقررات لهذا المستوى", color = TextSecondary)
                }
            } else {
                // بطاقة ملخص المستوى
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = currentLevelName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextGold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${currentCourses.size} مقرر دراسي",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }

                // عرض المقررات على رفوف
                val shelves = currentCourses.chunked(3)
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
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
        }
    }
}
