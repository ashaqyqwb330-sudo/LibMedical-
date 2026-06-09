package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StudyPlanProvider
import com.example.data.Stage
import com.example.data.Semester
import com.example.ui.components.GlassCard
import com.example.ui.components.GoldButton
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyPlanScreen(
    onBack: () -> Unit,
    onSemesterClick: (Int) -> Unit,
    onMedicalSystemsClick: () -> Unit
) {
    val stages = remember { StudyPlanProvider.getStages() }
    var expandedStageId by remember { mutableStateOf<Int?>(1) } // Default Stage 1 expanded

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "الخطة الدراسية المعتمدة",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextGold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = TextGold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFF030810), Primary, Color(0xFF0F1F33))))
                .padding(padding)
        ) {
            // زر الانتقال للمجموعات الطبية / الأجهزة الطبية العسكرية
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                GoldButton(
                    text = "استكشاف نظام الأجهزة الطبية العسكرية",
                    onClick = onMedicalSystemsClick,
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                )
            }

            Text(
                text = "مراحل التأهيل والدبلوم العالي",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(stages) { stage ->
                    StageCard(
                        stage = stage,
                        isExpanded = expandedStageId == stage.id,
                        onToggle = {
                            expandedStageId = if (expandedStageId == stage.id) null else stage.id
                        },
                        onSemesterClick = onSemesterClick
                    )
                }
            }
        }
    }
}

@Composable
fun StageCard(
    stage: Stage,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onSemesterClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isExpanded) Color(0x1FAD8C3C) else Color(0x12FFFFFF)
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            if (isExpanded) TextGold.copy(alpha = 0.2f) else Color(0x1AFFFFFF),
                            shape = RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Filled.List else Icons.Filled.Star,
                        contentDescription = null,
                        tint = if (isExpanded) TextGold else Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "المرحلة ${stage.id}",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isExpanded) TextGold else TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stage.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    tint = TextSecondary,
                    contentDescription = null
                )
            }

            if (isExpanded) {
                Divider(
                    color = Color(0x1AFFFFFF),
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    stage.semesters.forEach { semester ->
                        SemesterItem(
                            semester = semester,
                            onClick = { onSemesterClick(semester.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SemesterItem(
    semester: Semester,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x14FFFFFF))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Secondary.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${semester.id}",
                    color = Secondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = semester.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LabelValueRow(label = "الساعات المعتمدة:", value = "${semester.totalCreditHours}")
                    LabelValueRow(label = "الساعات الفعلية:", value = "${semester.totalActualHours}")
                }
            }
            Icon(
                imageVector = Icons.Filled.KeyboardArrowLeft,
                contentDescription = "عرض التفاصيل",
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun LabelValueRow(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, fontSize = 11.sp, color = TextSecondary)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = value, fontSize = 11.sp, color = TextGold, fontWeight = FontWeight.Bold)
    }
}
