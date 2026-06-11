// @builder:file app/src/main/java/com/example/ui/screens/ChapterScreen.kt
package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DataProvider
import com.example.data.DirectSubjectItem
import com.example.data.StudyPlanProvider
import com.example.ui.components.Book3DCard
import com.example.ui.components.GlassCard
import com.example.ui.components.Shelf
import com.example.ui.components.StaggeredEntrance
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// جسيمات خلفية متحركة
@Composable
fun ParticleBackground(modifier: Modifier = Modifier) {
    val particles = remember { List(20) { ParticleState(Random.nextFloat(), Random.nextFloat(), Random.nextFloat() * 0.6f + 0.4f) } }
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val time by infiniteTransition.animateFloat(0f, 1f, infiniteRepeatable(tween(4000, easing = LinearEasing)), label = "time")

    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { p ->
            val x = (p.baseX + time * 0.1f) % 1.1f - 0.05f
            val y = (p.baseY + sin(time * p.speed * 2 * Math.PI.toFloat())) * 0.1f
            val alpha = (0.15f + 0.1f * cos(time * p.speed * 3 * Math.PI.toFloat()))
            drawCircle(
                color = Color.White.copy(alpha = alpha),
                radius = 2.5f * p.size,
                center = Offset(size.width * x, size.height * (p.baseY + y))
            )
        }
    }
}

class ParticleState(val baseX: Float, val baseY: Float, val speed: Float, val size: Float = 1f)

@Composable
fun ChapterScreen(
    chapterName: String,
    chapterId: String,
    onBack: () -> Unit,
    onNavigate: (String, Map<String, String>) -> Unit,
    onNavigateToStudyPlan: () -> Unit = {},
    isTab2: Boolean = false,
    onOpenPdfGeneral: ((String, String) -> Unit)? = null,
    onNavigateToCourseDetail: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    val repository = remember { DataProvider(context) }
    
    val generalsDirect = remember(chapterId) { repository.getGeneralSubjectsDirect(chapterId) }
    val (_, _, devicesMap) = remember { repository.getBooksInChapter(chapterId) }
    val deviceNames = remember { devicesMap.keys.toList() }

    val chapterNumber = remember(chapterId) { chapterId.removePrefix("class").toIntOrNull() ?: 1 }
    val semesterInfo = remember(chapterNumber) {
        StudyPlanProvider.getStages().flatMap { it.semesters }.firstOrNull { it.id == chapterNumber }
    }

    var selectedGeneralSubject by remember { mutableStateOf<DirectSubjectItem?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "ch_anim")
    val headerGlow by infiniteTransition.animateFloat(0.3f, 0.8f, infiniteRepeatable(tween(2500), RepeatMode.Reverse), label = "hdr")
    val borderDashOffset by infiniteTransition.animateFloat(0f, 40f, infiniteRepeatable(tween(1000, easing = LinearEasing)), label = "border_dash")

    val isEmpty = generalsDirect.isEmpty() && deviceNames.isEmpty()

    Box(modifier = Modifier.fillMaxSize()) {
        // خلفية متحركة + جسيمات
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF020810), Color(0xFF0A1A2F), Primary, Color(0xFF0F1F33))
                    )
                )
        ) {
            ParticleBackground(modifier = Modifier.fillMaxSize().weight(1f))
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // ===== رأس سينمائي مع خط زخرفي =====
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(Color.Transparent, TextGold.copy(alpha = headerGlow * 0.3f), TextGold.copy(alpha = headerGlow * 0.15f), Color.Transparent)))
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.08f)).clickable { onBack() }, contentAlignment = Alignment.Center) { Text("↩️", fontSize = 20.sp) }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(chapterName, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextGold)
                        if (semesterInfo != null) Text("${semesterInfo.totalCreditHours} ساعة معتمدة | ${semesterInfo.totalActualHours} ساعة فعلية", fontSize = 11.sp, color = TextSecondary)
                    }
                    if (isTab2) {
                        Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(TextGold.copy(alpha = 0.2f)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                            Text("12 قسماً", fontSize = 10.sp, color = TextGold, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // خط زخرفي متقطع يتحرك
            Canvas(modifier = Modifier.fillMaxWidth().height(2.dp)) {
                drawLine(
                    color = TextGold.copy(alpha = 0.6f),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), borderDashOffset)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isEmpty) {
                // ===== حالة فارغة – فنية جداً =====
                Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val floatAnim by infiniteTransition.animateFloat(0f, -15f, infiniteRepeatable(tween(1500, easing = EaseInOutCubic), RepeatMode.Reverse), label = "float")
                        val rotAnim by infiniteTransition.animateFloat(-3f, 3f, infiniteRepeatable(tween(2000, easing = EaseInOutCubic), RepeatMode.Reverse), label = "rot")
                        Box(modifier = Modifier.offset(y = floatAnim.dp).size(110.dp).clip(RoundedCornerShape(30.dp)).background(Brush.radialGradient(listOf(TextGold.copy(alpha = 0.25f), Color.Transparent))), contentAlignment = Alignment.Center) {
                            Text("📭", fontSize = 52.sp)
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("لا توجد مواد أو أجهزة", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextGold)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("هذا الفصل الدراسي لا يحتوي حالياً على أي مواد عامة أو أجهزة طبية.\nسيتم إضافتها قريباً.", fontSize = 14.sp, color = TextSecondary, textAlign = TextAlign.Center, lineHeight = 22.sp)
                        Spacer(modifier = Modifier.height(24.dp))
                        // خط ذهبي تحته نقاط متحركة
                        Box(modifier = Modifier.width(100.dp).height(3.dp).background(Brush.horizontalGradient(listOf(TextGold, TextOrange, TextGold))))
                    }
                }
            } else {
                // ===== محتوى الفصل =====
                Column(modifier = Modifier.weight(1f)) {
                    if (generalsDirect.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionHeader("📚 المواد العامة والمشتركة", TextOrange)
                        Spacer(modifier = Modifier.height(8.dp))
                        // رفوف مع دخول متدرج
                        val shelves = generalsDirect.chunked(3)
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(20.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(shelves.size) { shelfIndex ->
                                StaggeredEntrance(delayPerItem = 100 * shelfIndex) {
                                    Shelf(spacing = 12.dp, modifier = Modifier.wrapContentWidth()) {
                                        shelves[shelfIndex].forEach { item ->
                                            Book3DCard(
                                                bookTitle = item.title,
                                                coverPath = null,
                                                activeBaseDir = null,
                                                onClick = {
                                                    if (isTab2 && onNavigateToCourseDetail != null) {
                                                        onNavigateToCourseDetail(item.title.replace(" ", "_"))
                                                    } else if (!isTab2 && onOpenPdfGeneral != null) {
                                                        selectedGeneralSubject = item
                                                    }
                                                },
                                                modifier = Modifier
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        if (deviceNames.isNotEmpty()) {
                            Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Secondary.copy(alpha = 0.4f))
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    if (deviceNames.isNotEmpty()) {
                        SectionHeader("🫁 الأجهزة الطبية للفصل", Secondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            itemsIndexed(deviceNames) { index, device ->
                                StaggeredEntrance(delayPerItem = 80 * index) {
                                    EnhancedDeviceCard(
                                        deviceName = device,
                                        onClick = {
                                            onNavigate(
                                                if (isTab2) "device_subjects_12" else "device_subjects",
                                                mapOf("chapterId" to chapterId, "deviceName" to device)
                                            )
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

    // حوار المادة العامة
    if (selectedGeneralSubject != null && !isTab2 && onOpenPdfGeneral != null) {
        val item = selectedGeneralSubject!!
        AlertDialog(
            onDismissRequest = { selectedGeneralSubject = null },
            title = { Text("تفاصيل المادة", color = TextGold, fontWeight = FontWeight.Bold) },
            text = { Column { Text("العنوان: ${item.title}", color = Color.White); Spacer(modifier = Modifier.height(12.dp)); Text("يمكنك فتح الملف مباشرة.", color = TextSecondary) } },
            confirmButton = { TextButton(onClick = { onOpenPdfGeneral(item.title, item.directPdfPath); selectedGeneralSubject = null }) { Text("فتح الملف 📄", color = Secondary) } },
            dismissButton = { TextButton(onClick = { selectedGeneralSubject = null }) { Text("إغلاق") } },
            containerColor = Color(0xFF0F1F33)
        )
    }
}

@Composable
fun SectionHeader(title: String, accentColor: Color) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = accentColor)
        Spacer(modifier = Modifier.weight(1f))
        Box(modifier = Modifier.width(30.dp).height(3.dp).background(Brush.horizontalGradient(listOf(accentColor, Color.Transparent))))
    }
}

@Composable
fun EnhancedDeviceCard(deviceName: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.94f else 1f, spring())
    val emoji = getDeviceEmoji(deviceName)
    val borderColor by animateColorAsState(
        targetValue = if (isPressed) Secondary.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.08f),
        animationSpec = tween(300)
    )

    Card(
        modifier = Modifier.width(160.dp).height(120.dp).scale(scale).clickable(interactionSource, null) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x15FFFFFF)),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(emoji, fontSize = 36.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(deviceName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Secondary, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

private fun getDeviceEmoji(name: String): String = when {
    name.contains("الهيكل العضلي") -> "🦴"
    name.contains("القلبي") -> "🫀"
    name.contains("التنفسي") -> "🫁"
    name.contains("الهضمي") -> "🍕"
    name.contains("البولي") || name.contains("التناسلي") -> "🧬"
    name.contains("الدموي") || name.contains("اللمفاوي") -> "🩸"
    name.contains("الصمائي") || name.contains("الغدد") -> "🦋"
    name.contains("العصبي") -> "🧠"
    else -> "🏥"
}
// @builder:end