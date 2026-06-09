package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
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
import com.example.ui.components.Book3DCard
import com.example.ui.components.GlassCard
import com.example.ui.components.Shelf
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectoryScreen(
    onBack: () -> Unit,
    onNavigateToPdf: (BookEntry) -> Unit,
    onNavigateToChapter: (String, String) -> Unit,
    onNavigateToBooks: (String) -> Unit,
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
    
    var selectedDeviceDetail by remember { mutableStateOf<String?>(null) }

    val chapters = remember { repository.getChapters() }
    val courseGuides = remember { repository.allBooks.filter { it.type == "book" || it.type == "general" } }
    
    val hospitalDevices = remember {
        repository.allBooks.filter { it.type == "subject" }
            .groupBy { book ->
                book.title.substringAfterLast(" - ", "أخرى").trim()
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF030810), Primary, Color(0xFF0F1F33))))
            .padding(16.dp)
    ) {
        // شريط العنوان
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "↩️", fontSize = 24.sp,
                modifier = Modifier.clickable { onBack() }.padding(8.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "الدليل الطبي المنظم", fontSize = 22.sp,
                fontWeight = FontWeight.Bold, color = TextGold, modifier = Modifier.weight(1f))
        }

        Text(
            text = "تصفح فوري ومصنف شامل للمقررات الدراسية، الأدلة المنظمة، وبلوكات أجهزة الجسم الحيوية.",
            fontSize = 13.sp, color = TextSecondary,
            modifier = Modifier.padding(bottom = 16.dp), lineHeight = 18.sp
        )

        // مربع البحث
        OutlinedTextField(
            value = searchQuery, onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            placeholder = { Text("بحث سريع في هذا القسم...", color = TextSecondary) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "ابحث", tint = Secondary) },
            trailingIcon = {
                IconButton(onClick = { startVoiceInput() }) { Text("🎤", fontSize = 20.sp) }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Secondary,
                unfocusedBorderColor = TextSecondary.copy(alpha = 0.5f),
                focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                focusedContainerColor = Color(0x10FFFFFF), unfocusedContainerColor = Color(0x10FFFFFF)
            ),
            shape = RoundedCornerShape(12.dp), singleLine = true
        )

        // التبويبات
        TabRow(
            selectedTabIndex = selectedTab, containerColor = Color.Transparent,
            contentColor = Secondary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]), color = Secondary)
            },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            listOf("🎓 المناهج الدراسية", "📖 أدلة المقررات", "🫁 بلوكات أجهزة الجسم", "📝 توصيفات المقررات")
                .forEachIndexed { index, title ->
                    Tab(selected = selectedTab == index,
                        onClick = {
                            if (index == 3) {
                                onNavigateToCourseDescriptions()
                            } else {
                                selectedTab = index
                                selectedDeviceDetail = null
                            }
                        },
                        text = {
                            Text(text = title, fontSize = 13.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) TextGold else TextSecondary)
                        })
                }
        }

        // منطقة المحتوى
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> {
                    // 🎓 المناهج الدراسية (شبكة)
                    val filteredChapters = chapters.filter { it.name.contains(searchQuery, ignoreCase = true) }
                    if (filteredChapters.isEmpty()) {
                        EmptyStateView("الفصول أو المناهج المطلوبة غير متوفرة.")
                    } else {
                        LazyVerticalGrid(columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()) {
                            items(filteredChapters) { chapter ->
                                GlassCard(modifier = Modifier.fillMaxWidth().height(130.dp)
                                    .clickable {
                                        val (_, generals, devices) = repository.getBooksInChapter(chapter.id)
                                        if (generals.isEmpty() && devices.isEmpty())
                                            onNavigateToBooks(chapter.name)
                                        else onNavigateToChapter(chapter.id, chapter.name)
                                    }) {
                                    Column(modifier = Modifier.fillMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center) {
                                        Text(text = chapter.icon, fontSize = 28.sp)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(text = chapter.name, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                                            color = TextGold, textAlign = TextAlign.Center, maxLines = 2, lineHeight = 15.sp)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(text = "${chapter.bookCount} مقرر وملف علمي", fontSize = 10.sp, color = TextSecondary)
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // ⭐ 📖 أدلة المقررات – عرض بالرفوف و Book3DCard (محدث)
                    val filteredGuides = courseGuides.filter { it.title.contains(searchQuery, ignoreCase = true) }
                    if (filteredGuides.isEmpty()) {
                        EmptyStateView("لا توجد أدلة مقررات أو كتب علمية تطابق بحثك.")
                    } else {
                        val shelves = filteredGuides.chunked(3)
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(28.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(shelves.size) { shelfIndex ->
                                Shelf(modifier = Modifier.fillMaxWidth()) {
                                    shelves[shelfIndex].forEach { book ->
                                        Book3DCard(
                                            bookTitle = book.title,
                                            coverPath = book.cover_path,
                                            activeBaseDir = repository.activeBaseDir,
                                            onClick = {
                                                val courseId = book.title
                                                    .replace(" ", "_")
                                                    .replace(":", "")
                                                    .replace("-", "_")
                                                    .trim()
                                                onNavigateToCourseDetail(courseId)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // 🫁 ...
                    if (selectedDeviceDetail == null) {
                        val filteredDevices = hospitalDevices.keys.filter { it.contains(searchQuery, ignoreCase = true) }
                        if (filteredDevices.isEmpty()) {
                            EmptyStateView("لم يتم العثور على بلوكات أجهزة جسم تطابق البحث.")
                        } else {
                            LazyVerticalGrid(columns = GridCells.Fixed(1),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxSize()) {
                                items(filteredDevices) { deviceName ->
                                    val deviceBooks = hospitalDevices[deviceName] ?: emptyList()
                                    Card(colors = CardDefaults.cardColors(containerColor = Color(0x18FFFFFF)),
                                        modifier = Modifier.fillMaxWidth()
                                            .border(1.dp, Secondary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                            .clickable { selectedDeviceDetail = deviceName }) {
                                        Row(modifier = Modifier.fillMaxWidth().padding(14.dp),
                                            verticalAlignment = Alignment.CenterVertically) {
                                            Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(10.dp))
                                                .background(Color(0xFF1B314B)), contentAlignment = Alignment.Center) {
                                                Text(text = getDeviceEmojiIcon(deviceName), fontSize = 26.sp)
                                            }
                                            Spacer(modifier = Modifier.width(14.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(text = deviceName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextGold)
                                                Text(text = "يحتوي على ${deviceBooks.size} مساقات", fontSize = 11.sp, color = TextSecondary)
                                            }
                                            Text(text = "👈", fontSize = 18.sp)
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // عرض تفاصيل الجهاز
                        val deviceName = selectedDeviceDetail!!
                        val booksForDevice = hospitalDevices[deviceName]?.filter {
                            it.title.contains(searchQuery, ignoreCase = true)
                        } ?: emptyList()
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                verticalAlignment = Alignment.CenterVertically) {
                                Button(onClick = { selectedDeviceDetail = null },
                                    colors = ButtonDefaults.buttonColors(containerColor = Secondary),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(30.dp)) {
                                    Text("عودة للبلوكات ↩️", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(text = deviceName, fontSize = 15.sp, fontWeight = FontWeight.Bold,
                                    color = Color.White, modifier = Modifier.weight(1f))
                            }
                            if (booksForDevice.isEmpty()) {
                                EmptyStateView("لا تتوفر مراجع داخل هذا الجهاز.")
                            } else {
                                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxSize()) {
                                    items(booksForDevice) { book ->
                                        Card(colors = CardDefaults.cardColors(containerColor = Color(0x10FFFFFF)),
                                            modifier = Modifier.fillMaxWidth()
                                                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                                                .clickable {
                                                    onNavigateToPdf(book)
                                                }) {
                                            Row(modifier = Modifier.fillMaxWidth().padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically) {
                                                Text("📄", fontSize = 22.sp)
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text(text = book.title, fontSize = 13.sp, color = Color.White, modifier = Modifier.weight(1f))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
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

private fun getDeviceEmojiIcon(deviceName: String): String {
    return when {
        deviceName.contains("الهيكل العضلي") || deviceName.contains("العضلات") -> "🦴"
        deviceName.contains("القلبي") || deviceName.contains("الأوعية") || deviceName.contains("القلب") -> "🫀"
        deviceName.contains("التنفسي") || deviceName.contains("التنفس") -> "🫁"
        deviceName.contains("الهضمي") || deviceName.contains("الهضم") -> "🍕"
        deviceName.contains("البولي") || deviceName.contains("التناسلي") -> "🧼"
        deviceName.contains("الدموي") || deviceName.contains("اللمفاوي") || deviceName.contains("الدم") -> "🩸"
        deviceName.contains("الصمائي") || deviceName.contains("الغدد") -> "🧬"
        deviceName.contains("العصبي") -> "🧠"
        deviceName.contains("عسكرية") -> "🛡️"
        else -> "🏥"
    }
}
