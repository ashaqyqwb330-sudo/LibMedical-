package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DataProvider
import com.example.ui.components.GlassCard
import com.example.ui.components.GoldButton
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibrarySourceScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val dataProvider = remember { DataProvider(context) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var isEnhanced by remember { mutableStateOf(dataProvider.isEnhancedModeActive()) }
    var savedUri by remember { mutableStateOf(dataProvider.getSavedLibraryUri()) }

    // SAF Document Tree Directory Picker contract
    val directoryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                // Request persistent URI permission so it lives after app/device restarts
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, takeFlags)

                // Validate if it has our master assets map app_assets_map_v3.json
                val treeFile = androidx.documentfile.provider.DocumentFile.fromTreeUri(context, uri)
                if (treeFile != null && treeFile.exists()) {
                    val mapFile = dataProvider.findFileInDocumentTree(uri, "app_assets_map_v3.json")
                    if (mapFile != null && mapFile.exists()) {
                        dataProvider.saveLibraryUri(uri.toString())
                        isEnhanced = true
                        savedUri = uri.toString()
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "✨ تم ربط المكتبة الطبية المتقدمة بنجاح وتفعيل رتبة النشر المتقدمة!",
                                duration = SnackbarDuration.Short
                            )
                        }
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "❌ المجلد المختار لا يحتوي على ملف التكوين الأساسي (app_assets_map_v3.json)!",
                                duration = SnackbarDuration.Long
                            )
                        }
                    }
                } else {
                    Toast.makeText(context, "فشل فحص المجلد المالي المختار.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "حدث خطأ أثناء فحص وطلب الصلاحية: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = Color(0xFF162540),
                contentColor = TextGold,
                actionColor = TextPrimary,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.border(1.dp, TextGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            )
        }},
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "مدير الموارد الطبية",
                        color = TextGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "رجوع",
                            tint = TextGold
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF030810)
                )
            )
        },
        containerColor = Color(0xFF030810)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF030810), Primary, Color(0xFF0F1F33))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. مؤشر حالة التطبيق (Active State Indicator Card)
                AnimatedContent(
                    targetState = isEnhanced,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(400))
                    },
                    label = "status_anim"
                ) { enhancedActive ->
                    if (enhancedActive) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.2.dp, TextGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                .testTag("library_status_card_enhanced"),
                            colors = CardDefaults.cardColors(containerColor = Color(0x1F19D3B4)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(18.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "نشط",
                                    tint = TextGold,
                                    modifier = Modifier.size(36.dp)
                                )
                                Column {
                                    Text(
                                        text = "المكتبة الطبية المتقدمة نشطة ✨",
                                        color = TextGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "يتم الآن رصد وتصفح آلاف المقررات، والكتب السريرية، وأدلة الميدان والإنعاش الحيوية المتقدمة بنجاح.",
                                        color = TextPrimary,
                                        fontSize = 12.sp,
                                        lineHeight = 18.sp
                                    )
                                    if (savedUri != null) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "المسار الحالي: ${Uri.decode(savedUri).substringAfterLast("/")}",
                                            color = TextSecondary,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Secondary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                                .testTag("library_status_card_basic"),
                            colors = CardDefaults.cardColors(containerColor = Color(0x1F162540)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(18.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "تنبيه",
                                    tint = Color(0xFFE2B960),
                                    modifier = Modifier.size(36.dp)
                                )
                                Column {
                                    Text(
                                        text = "المكتبة الداخلية الأساسية نشطة",
                                        color = Color(0xFFE2B960),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "يعمل التطبيق الآن بالإصدار المعياري الافتراضي وبمناهجه الأساسية فقط. لرفع الكفاءة، قم بربط مجلد المكتبة المتقدمة أدناه.",
                                        color = TextPrimary,
                                        fontSize = 12.sp,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. كتم الإجراءات الرئيسية لربط وإدارة المورد
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "التحكم في مصادر المناهج والكتب",
                            color = TextGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "يتيح هذا الزر اختيار مجلد المجلدات والوسائط الكاملة للمدونات الطبية العسكرية المعززة (MidApp_Library) المحفوظة على هاتفك لدمجها تلقائياً بالكامل في الفرز.",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        GoldButton(
                            text = "🛠️ تحديد مسار المكتبة المتقدمة",
                            onClick = {
                                try {
                                    directoryPickerLauncher.launch(null)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    Toast.makeText(context, "تعذر تشغيل مستعرض الملفات المخصص للجهاز.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("select_library_button")
                        )

                        if (isEnhanced) {
                            Button(
                                onClick = {
                                    dataProvider.clearLibraryUri()
                                    isEnhanced = false
                                    savedUri = null
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = "🔄 تم إعادة التعيين والعودة لقاعدة البيانات الأساسية بنجاح.",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C1E1E)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                                    .border(1.dp, Color(0xFFE57373).copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                    .testTag("reset_library_button")
                            ) {
                                Text(text = "❌ إعادة ضبط واسترداد الحالة للافتراضي", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // 3. قسم تعليمات الإعداد والمزامنة الميدانية الدقيقة
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = PrimaryLight.copy(alpha = 0.2f)),
                    border = BorderStroke(1.dp, Secondary.copy(alpha = 0.25f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "معلومات",
                                tint = TextGold
                            )
                            Text(
                                text = "دليل الإعداد العسكري السريع 🧭",
                                color = TextGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Divider(color = Secondary.copy(alpha = 0.15f))

                        val instructions = listOf(
                            "💾 خطوة 1: انسخ مجلد 'MidApp_Library' الصادر بالكامل من نظام المطورين بالكمبيوتر وضعه في بطاقة الذاكرة أو جهاز التخزين الداخلي للهاتف.",
                            "📁 خطوة 2: انقر على زر تحديد مسار المكتبة المتقدمة بالأعلى، ثم اختر مجلد 'MidApp_Library' تحديداً واضغط على 'منح إمكانية الوصول'.",
                            "🔄 خطوة 3: سيقوم التطبيق بالباقي حيث سيتم تفعيل الوضع المتقدم المتكامل لرفع جميع الكتب، الصور الطبية ومخرجات المحاضرات فوراً."
                        )

                        instructions.forEach { rule ->
                            Text(
                                text = rule,
                                color = TextPrimary,
                                fontSize = 11.5.sp,
                                lineHeight = 18.sp,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}
