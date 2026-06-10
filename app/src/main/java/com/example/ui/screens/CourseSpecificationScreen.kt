package com.example.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.CourseDescriptionProvider
import com.example.model.CourseDescription
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseSpecificationScreen(
    courseId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val data = remember { CourseDescriptionProvider.load(context) }
    val course = remember(courseId) {
        data?.levels
            ?.flatMap { it.semesters }
            ?.flatMap { it.courses }
            ?.firstOrNull { it.id == courseId }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            course?.nameAr ?: "توصيف مقرر",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextGold
                        )
                        Text(
                            "الرمز: $courseId",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F1F33)
                )
            )
        }
    ) { padding ->
        val htmlContent = course?.descriptionHtml
        if (course == null || htmlContent.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "لا يوجد توصيف مفصل لهذا المقرر",
                    color = TextSecondary,
                    fontSize = 16.sp
                )
            }
        } else {
            // عرض HTML داخل WebView
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = false
                        settings.defaultTextEncodingName = "utf-8"
                        // السماح بالتصغير/التكبير
                        settings.builtInZoomControls = true
                        settings.displayZoomControls = false
                        // تحسين الأداء
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                    }
                },
                update = { webView ->
                    val fullHtml = """
                        <!DOCTYPE html>
                        <html dir="rtl" lang="ar">
                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <style>
                                body {
                                    font-family: 'Cairo', 'Arial', sans-serif;
                                    padding: 8px;
                                    background: #0F1F33;
                                    color: #E6E8EC;
                                    line-height: 1.6;
                                }
                                table {
                                    width: 100%;
                                    border-collapse: collapse;
                                    margin: 8px 0;
                                    background: #1C2333;
                                }
                                th, td {
                                    border: 1px solid #2A3140;
                                    padding: 6px;
                                    text-align: right;
                                }
                                th {
                                    background: #1e2a3a;
                                    color: #D4AF37;
                                    font-weight: bold;
                                }
                                .header, .main-title, .section-title, .subsection-title, h3 {
                                    color: #D4AF37;
                                }
                                .filled-data {
                                    padding: 4pt;
                                }
                                .signature-table td {
                                    border: none;
                                    text-align: center;
                                }
                                .page-break {
                                    border-top: 2px solid #D4AF37;
                                    margin-top: 20px;
                                    padding-top: 10px;
                                }
                                .page {
                                    margin-bottom: 16px;
                                }
                                img {
                                    max-width: 100%;
                                    height: auto;
                                }
                            </style>
                        </head>
                        <body>
                            $htmlContent
                        </body>
                        </html>
                    """.trimIndent()
                    webView.loadDataWithBaseURL(null, fullHtml, "text/html", "UTF-8", null)
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        }
    }
}
