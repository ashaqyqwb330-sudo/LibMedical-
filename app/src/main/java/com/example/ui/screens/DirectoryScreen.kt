
// @builder:file app/src/main/java/com/example/ui/screens/DirectoryScreen.kt
package com.example.ui.screens

import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DataProvider
import com.example.model.BookEntry
import com.example.ui.components.GlassCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectoryScreen(
    onBack: () -> Unit,
    onNavigateToPdf: (BookEntry) -> Unit,
    onNavigateToChapter: (String, String) -> Unit,
    onNavigateToBooks: (String) -> Unit,
    onNavigateToChapter12: (String, String) -> Unit = { _, _ -> },
    onNavigateToCourseDetail: (String) -> Unit = {},
    onNavigateToCourseDescriptions: () -> Unit = {}
) {
    val context = LocalContext.current
    val repository = remember { DataProvider(context) }
    
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val data = result.data
            val resultsList = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!resultsList.isNullOrEmpty()) {
                searchQuery = resultsList[0]
            }
        }
    }

    fun startVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-SA")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ar-SA")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "ابحث صوتياً في الدليل 🎤")
        }
        try {
            speechRecognizerLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "التعرف على الصوت غير مدعوم على جهازك", Toast.LENGTH_SHORT).show()
        }
    }

    val chapters = remember { repository.getChapters() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF020810),
                        Color(0xFF0A1A2F),
                        Primary,
                        Color(0xFF0F1F33)
                    )
                )
            )
    ) {
        // ========== شريط العنوان المحسّن ==========
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            TextGold.copy(alpha = 0.08f),
                            TextGold.copy(alpha = 0.15f),
                            TextGold.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("↩️", fontSize = 20.sp)
                }
                
                Spacer(modifier = Modifier.width(14.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📋", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "الدليل الطبي المنظم",
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextGold
                        )
                    }
                    Text(
                        text = "المكتبة الطبية العسكرية الموحدة",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(start = 28.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // ========== نص وصفي محسّن ==========
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = TextGold.copy(alpha = 0.06f)
            ),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                TextGold.copy(alpha = 0.1f)
            )
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("💡", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "تصفح فوري ومصنف للمقررات والأقسام التعليمية وأجهزة الجسم",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 17.sp,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ========== مربع البحث المحسّن ==========
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.04f))
                .border(1.dp, TextGold.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "🔍  ابحث عن فصل أو مقرر أو جهاز طبي...",
                        color = TextSecondary.copy(alpha = 0.5f),
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "بحث",
                        tint = TextGold.copy(alpha = 0.6f),
                        modifier = Modifier.size(22.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Text("✖️", fontSize = 13.sp, color = TextSecondary)
                        }
                    } else {
                        IconButton(onClick = { startVoiceInput() }) {
                            Text("🎤", fontSize = 18.sp)
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = TextGold
                ),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ========== التبويبات المحسّنة ==========
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = Secondary,
            divider = {},
            indicator = { tabPositions ->
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedTab])
                        .height(3.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(TextGold, TextOrange, TextGold)
                            )
                        )
                )
            },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .background(
                        if (selectedTab == 0) TextGold.copy(alpha = 0.15f) else Color.Transparent,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(vertical = 4.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text("🎓", fontSize = if (selectedTab == 0) 22.sp else 18.sp)
                    Text(
                        text = "المناهج",
                        fontSize = if (selectedTab == 0) 13.sp else 11.sp,
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == 0) TextGold else TextSecondary
                    )
                }
            }
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .background(
                        if (selectedTab == 1) TextGold.copy(alpha = 0.15f) else Color.Transparent,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(vertical = 4.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text("📚", fontSize = if (selectedTab == 1) 22.sp else 18.sp)
                    Text(
                        text = "الأقسام",
                        fontSize = if (selectedTab == 1) 13.sp else 11.sp,
                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == 1) TextGold else TextSecondary
                    )
                }
            }
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .background(
                        if (selectedTab == 2) TextGold.copy(alpha = 0.15f) else Color.Transparent,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(vertical = 4.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text("📝", fontSize = if (selectedTab == 2) 22.sp else 18.sp)
                    Text(
                        text = "التوصيفات",
                        fontSize = if (selectedTab == 2) 13.sp else 11.sp,
                        fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == 2) TextGold else TextSecondary
                    )
                }
            }
        }

        // ========== محتوى التبويبات ==========
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> {
                    // 🎓 المناهج الدراسية – تصميم محسّن
                    val filteredChapters = chapters.filter { it.name.contains(searchQuery, ignoreCase = true) }
                    
                    if (filteredChapters.isEmpty()) {
                        EmptyStateView("الفصول أو المناهج المطلوبة غير متوفرة.")
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item(span = { GridItemSpan(2) }) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🎓", fontSize = 24.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("الفصول الدراسية", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextGold)
                                    Spacer(modifier = Modifier.weight(1f))
                                    Box(
                                        modifier = Modifier
                                            .width(40.dp)
                                            .height(3.dp)
                                            .background(Brush.horizontalGradient(listOf(TextGold, TextOrange)))
                                    )
                                }
                            }
                            
                            items(filteredChapters) { chapter ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(150.dp)
                                        .clickable {
                                            val (_, generals, devices) = repository.getBooksInChapter(chapter.id)
                                            if (generals.isEmpty() && devices.isEmpty())
                                                onNavigateToBooks(chapter.name)
                                            else onNavigateToChapter(chapter.id, chapter.name)
                                        },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0x15FFFFFF)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize().padding(14.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(52.dp)
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(Brush.linearGradient(listOf(Color(0xFF1B314B), Color(0xFF0F1F33)))),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = chapter.icon, fontSize = 26.sp)
                                        }
                                        
                                        Text(
                                            text = chapter.name,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextGold,
                                            textAlign = TextAlign.Center,
                                            maxLines = 2,
                                            lineHeight = 17.sp
                                        )
                                        
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(TextGold.copy(alpha = 0.12f))
                                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "${chapter.bookCount} مقرر",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = TextOrange
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // 📚 الأقسام التعليمية – تصميم محسّن
                    val filteredChapters = chapters.filter { it.name.contains(searchQuery, ignoreCase = true) }
                    
                    if (filteredChapters.isEmpty()) {
                        EmptyStateView("لا توجد فصول دراسية متاحة.")
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("📚", fontSize = 24.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("تصفح الفصول الدراسية", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextGold)
                                    Spacer(modifier = Modifier.weight(1f))
                                    Box(
                                        modifier = Modifier
                                            .width(40.dp)
                                            .height(3.dp)
                                            .background(Brush.horizontalGradient(listOf(TextGold, TextOrange)))
                                    )
                                }
                            }
                            
                            items(filteredChapters) { chapter ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onNavigateToChapter12(chapter.id, chapter.name) },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0x15FFFFFF)),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (chapter.bookCount > 0) TextGold.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.06f)
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(56.dp)
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(
                                                    Brush.linearGradient(
                                                        listOf(
                                                            if (chapter.bookCount > 0) Color(0xFF1B314B) else Color(0xFF1A1A2E),
                                                            if (chapter.bookCount > 0) Color(0xFF0F1F33) else Color(0xFF16213E)
                                                        )
                                                    )
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = chapter.icon, fontSize = 28.sp)
                                        }
                                        
                                        Spacer(modifier = Modifier.width(16.dp))
                                        
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = chapter.name,
                                                fontWeight = FontWeight.Bold,
                                                color = TextGold,
                                                fontSize = 16.sp,
                                                maxLines = 1
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(TextGold.copy(alpha = 0.12f))
                                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                                ) {
                                                    Text(
                                                        text = "${chapter.bookCount} مقرر",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = TextOrange
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = if (chapter.bookCount > 0) "متاح للتصفح" else "قريباً",
                                                    fontSize = 11.sp,
                                                    color = if (chapter.bookCount > 0) Color(0xFF4CAF50) else TextSecondary
                                                )
                                            }
                                        }
                                        
                                        Spacer(modifier = Modifier.width(8.dp))
                                        
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(TextGold.copy(alpha = 0.1f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("◀", fontSize = 14.sp, color = TextGold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // 📝 توصيفات المقررات
                    LaunchedEffect(Unit) { onNavigateToCourseDescriptions() }
                }
            }
        }
    }
}

@Composable
fun EmptyStateView(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🔍", fontSize = 42.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = message, color = TextSecondary, fontSize = 14.sp, textAlign = TextAlign.Center)
        }
    }
}
// @builder:end