// @builder:file app/src/main/java/com/example/ui/screens/BooksScreen.kt
package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DataProvider
import com.example.model.BookEntry
import com.example.ui.components.Book3DCard
import com.example.ui.components.Shelf
import com.example.ui.components.StaggeredEntrance
import com.example.ui.theme.*

@Composable
fun BooksScreen(
    chapterId: String,
    onBack: () -> Unit,
    onNavigateToPdf: (BookEntry) -> Unit
) {
    val context = LocalContext.current
    val repository = remember { DataProvider(context) }
    
    // تحميل الكتب مع معالجة الأخطاء
    val books = remember(chapterId) {
        try {
            repository.getBooksInChapter(chapterId).first
        } catch (e: Exception) {
            emptyList()
        }
    }

    // اسم الفصل للعرض
    val chapterName = remember(chapterId) {
        repository.getChapters().firstOrNull { it.id == chapterId }?.name ?: "الفصل ${chapterId.removePrefix("class")}"
    }
    
    var selectedBook by remember { mutableStateOf<BookEntry?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "books_anim")
    val headerGlow by infiniteTransition.animateFloat(
        0.3f, 0.7f,
        infiniteRepeatable(tween(2500), RepeatMode.Reverse),
        label = "header_glow"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF030810), Primary, Color(0xFF0F1F33))
                )
            )
    ) {
        // ===== رأس الصفحة =====
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            TextGold.copy(alpha = headerGlow * 0.2f),
                            TextGold.copy(alpha = headerGlow * 0.1f),
                            Color.Transparent
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) { Text("↩️", fontSize = 20.sp) }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(chapterName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextGold)
                    Text("${books.size} كتاب متاح", fontSize = 11.sp, color = TextSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (books.isEmpty()) {
            // ===== حالة فارغة محسّنة =====
            Box(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = TextGold.copy(alpha = 0.06f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TextGold.copy(alpha = 0.2f))
                ) {
                    Column(
                        modifier = Modifier.padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(TextGold.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📚", fontSize = 36.sp)
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            "لا توجد كتب مباشرة",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextGold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "هذا الفصل لا يحتوي على كتب مباشرة. يمكنك تصفح المواد المتخصصة من خلال التبويب المناسب.",
                            fontSize = 14.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        } else {
            // ===== عرض الكتب =====
            val shelves = books.chunked(3)
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(shelves.size) { shelfIndex ->
                    StaggeredEntrance(delayPerItem = 80 * shelfIndex) {
                        Shelf(spacing = 14.dp, modifier = Modifier.fillMaxWidth()) {
                            shelves[shelfIndex].forEach { book ->
                                Book3DCard(
                                    bookTitle = book.title,
                                    coverPath = book.cover_path,
                                    activeBaseDir = repository.activeBaseDir,
                                    onClick = { selectedBook = book }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // حوار التفاصيل
    selectedBook?.let { book ->
        AlertDialog(
            onDismissRequest = { selectedBook = null },
            title = {
                Text(
                    text = "معلومات المنهج الدراسي",
                    color = TextGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "العنوان العلمي: ${book.title}",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ملف القراءة المرفق: ${book.file}",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "ملاحظة: يجري تحميل المستند الطبي من الخادم الآمن للقوات المسلحة لحفظ الأصول الفكرية.",
                        color = TextOrange,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            val toOpen = selectedBook
                            if (toOpen != null) {
                                selectedBook = null
                                onNavigateToPdf(toOpen)
                            }
                        }
                    ) {
                        Text("قراءة في التطبيق 📖", color = Secondary, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = {
                            val toOpen = selectedBook
                            if (toOpen != null) {
                                repository.openBook(toOpen)
                            }
                            selectedBook = null
                        }
                    ) {
                        Text("تطبيق خارجي 📁", color = TextGold, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedBook = null }) {
                    Text("إغلاق", color = Color.White.copy(alpha = 0.6f))
                }
            },
            containerColor = PrimaryLight
        )
    }
}
// @builder:end