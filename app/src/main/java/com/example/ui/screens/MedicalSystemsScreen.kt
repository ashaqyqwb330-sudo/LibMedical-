package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StudyPlanProvider
import com.example.data.MedicalSystem
import com.example.ui.components.GlassCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalSystemsScreen(
    onBack: () -> Unit
) {
    val medicalSystems = remember { StudyPlanProvider.getMedicalSystems() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "نظام الأجهزة الطبية الحيوية",
                        fontSize = 18.sp,
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
            // معلومات تقديمية
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x0CFFFFFF))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = TextGold,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "التكامل المنهجي للأجهزة الطبية",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "مراجعة المجموعات والأجهزة الحيوية وأهم العلوم الطبية العسكرية المرتبطة بها لتغطية الاحتياج المعرفي الكامل للضباط الأطباء.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(medicalSystems) { system ->
                    MedicalSystemCard(system = system)
                }
            }
        }
    }
}

@Composable
fun MedicalSystemCard(system: MedicalSystem) {
    val icon = remember(system.nameAr) {
        when {
            system.nameAr.contains("العضلي") -> Icons.Default.Settings
            system.nameAr.contains("القلبي") -> Icons.Default.Favorite
            system.nameAr.contains("التنفسي") -> Icons.Default.Info
            system.nameAr.contains("الهضمي") -> Icons.Default.Menu
            system.nameAr.contains("العصبي") -> Icons.Default.Face
            system.nameAr.contains("البولي") -> Icons.Default.Favorite
            system.nameAr.contains("الصمائي") -> Icons.Default.Info
            system.nameAr.contains("السريرية") -> Icons.Default.Info
            system.nameAr.contains("غازات") -> Icons.Default.Check
            else -> Icons.Default.Star
        }
    }

    val systemColor = remember(system.nameAr) {
        when {
            system.nameAr.contains("القلبي") -> Color(0xFFE74C3C)
            system.nameAr.contains("التنفسي") -> Color(0xFF3498DB)
            system.nameAr.contains("العصبي") -> Color(0xFF9B59B6)
            system.nameAr.contains("غازات") -> Color(0xFFF1C40F)
            else -> TextGold
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x16FFFFFF))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(systemColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = systemColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = system.nameAr,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 2,
                minLines = 2,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // عرض العلوم الفرعية في صف صغير أو نص
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "العلوم المنهجية:",
                    fontSize = 10.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = system.subsystems.joinToString(" • "),
                    fontSize = 11.sp,
                    color = systemColor,
                    maxLines = 1,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
