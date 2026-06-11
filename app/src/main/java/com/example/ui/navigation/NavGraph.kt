
// @builder:file app/src/main/java/com/example/ui/navigation/NavGraph.kt
package com.example.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ui.screens.*

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        
        // ========== الشاشات الرئيسية ==========
        composable("home") {
            HomeScreen(onNavigate = { navController.navigate(it) })
        }
        composable("diploma") {
            DiplomaScreen(
                onNavigate = { screen, params ->
                    when (screen) {
                        "books" -> navController.navigate("books/${Uri.encode(params["chapterName"] ?: "")}")
                        "chapter_books" -> navController.navigate("chapter/${params["chapterId"]}/${Uri.encode(params["chapterName"] ?: "")}")
                        "pdf_viewer" -> navController.navigate("pdf_viewer/${Uri.encode(params["title"] ?: "")}/${Uri.encode(params["file"] ?: "")}")
                    }
                },
                onNavigateToStudyPlan = { navController.navigate("study_plan") }
            )
        }

        // ========== الدليل المنظم (3 تبويبات) ==========
        composable("directory") {
            DirectoryScreen(
                onBack = { navController.popBackStack() },
                onNavigateToPdf = { book ->
                    navController.navigate("pdf_viewer/${Uri.encode(book.title)}/${Uri.encode(book.file)}")
                },
                onNavigateToChapter = { chapterId, chapterName ->
                    navController.navigate("chapter/$chapterId/${Uri.encode(chapterName)}")
                },
                onNavigateToBooks = { chapterName ->
                    navController.navigate("books/${Uri.encode(chapterName)}")
                },
                onNavigateToChapter12 = { chapterId, chapterName ->
                    navController.navigate("chapter_12/$chapterId/${Uri.encode(chapterName)}")
                },
                onNavigateToCourseDetail = { courseId ->
                    navController.navigate("course/${Uri.encode(courseId)}")
                },
                onNavigateToCourseDescriptions = { navController.navigate("course_descriptions") }
            )
        }

        // ========== التبويب الأول: المناهج الدراسية ==========
        // فصل عادي (سلوك افتراضي)
        composable(
            "chapter/{chapterId}/{chapterName}",
            arguments = listOf(
                navArgument("chapterId") { type = NavType.StringType },
                navArgument("chapterName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val chapterId = backStackEntry.arguments?.getString("chapterId") ?: ""
            val chapterName = Uri.decode(backStackEntry.arguments?.getString("chapterName") ?: "")
            ChapterScreen(
                chapterName = chapterName,
                chapterId = chapterId,
                onBack = { navController.popBackStack() },
                onNavigate = { screen, params ->
                    when (screen) {
                        "device_subjects" -> navController.navigate("device/$chapterId/${Uri.encode(params["deviceName"] ?: "")}")
                    }
                },
                isTab2 = false,
                onOpenPdfGeneral = { title, pdfPath ->
                    navController.navigate("pdf_viewer/${Uri.encode(title)}/${Uri.encode(pdfPath)}")
                }
            )
        }

        // جهاز (تبويب أول) ← يفتح PDF مباشر
        composable(
            "device/{chapterId}/{deviceName}",
            arguments = listOf(
                navArgument("chapterId") { type = NavType.StringType },
                navArgument("deviceName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val chapterId = backStackEntry.arguments?.getString("chapterId") ?: ""
            val deviceName = Uri.decode(backStackEntry.arguments?.getString("deviceName") ?: "")
            DeviceScreen(
                chapterId = chapterId,
                deviceName = deviceName,
                onBack = { navController.popBackStack() },
                onOpenPdf = { title, pdfPath ->
                    navController.navigate("pdf_viewer/${Uri.encode(title)}/${Uri.encode(pdfPath)}")
                }
            )
        }

        // ========== التبويب الثاني: الأقسام التعليمية ==========
        // فصل (تبويب ثاني) ← يفتح المحتويات الـ12
        composable(
            "chapter_12/{chapterId}/{chapterName}",
            arguments = listOf(
                navArgument("chapterId") { type = NavType.StringType },
                navArgument("chapterName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val chapterId = backStackEntry.arguments?.getString("chapterId") ?: ""
            val chapterName = Uri.decode(backStackEntry.arguments?.getString("chapterName") ?: "")
            ChapterScreen(
                chapterName = chapterName,
                chapterId = chapterId,
                onBack = { navController.popBackStack() },
                onNavigate = { screen, params ->
                    when (screen) {
                        "device_subjects_12" -> navController.navigate("device_12/$chapterId/${Uri.encode(params["deviceName"] ?: "")}")
                    }
                },
                isTab2 = true,
                onNavigateToCourseDetail = { courseId ->
                    navController.navigate("course/${Uri.encode(courseId)}")
                }
            )
        }

        // جهاز (تبويب ثاني) ← يفتح المحتويات الـ12
        composable(
            "device_12/{chapterId}/{deviceName}",
            arguments = listOf(
                navArgument("chapterId") { type = NavType.StringType },
                navArgument("deviceName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val chapterId = backStackEntry.arguments?.getString("chapterId") ?: ""
            val deviceName = Uri.decode(backStackEntry.arguments?.getString("deviceName") ?: "")
            DeviceScreen(
                chapterId = chapterId,
                deviceName = deviceName,
                onBack = { navController.popBackStack() },
                onOpenCourseDetail = { courseId ->
                    navController.navigate("course/${Uri.encode(courseId)}")
                },
                isTab2 = true  // تمييز بصري
            )
        }

        // شاشة المحتويات الـ12 (للتبويب الثاني فقط)
        composable(
            "course/{courseId}",
            arguments = listOf(navArgument("courseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val courseId = Uri.decode(backStackEntry.arguments?.getString("courseId") ?: "")
            CourseDetailScreen(
                courseId = courseId,
                onBack = { navController.popBackStack() },
                onContentClick = { index ->
                    navController.navigate("course_content/${Uri.encode(courseId)}/$index")
                }
            )
        }

        // تفاصيل المحتوى
        composable(
            "course_content/{courseId}/{contentIndex}",
            arguments = listOf(
                navArgument("courseId") { type = NavType.StringType },
                navArgument("contentIndex") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val courseId = Uri.decode(backStackEntry.arguments?.getString("courseId") ?: "")
            val contentIndex = backStackEntry.arguments?.getInt("contentIndex") ?: 0
            CourseContentDetailScreen(
                courseId = courseId,
                contentIndex = contentIndex,
                onBack = { navController.popBackStack() }
            )
        }

        // ========== باقي الشاشات ==========
        composable("pdf_viewer/{bookTitle}/{bookFilePath}",
            arguments = listOf(
                navArgument("bookTitle") { type = NavType.StringType },
                navArgument("bookFilePath") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val bookTitle = Uri.decode(backStackEntry.arguments?.getString("bookTitle") ?: "")
            val bookFilePath = Uri.decode(backStackEntry.arguments?.getString("bookFilePath") ?: "")
            PdfViewerScreen(
                bookTitle = bookTitle,
                bookFilePath = bookFilePath,
                onBack = { navController.popBackStack() }
            )
        }

        composable("study_plan") {
            StudyPlanScreen(
                onBack = { navController.popBackStack() },
                onSemesterClick = { semesterId -> navController.navigate("semester/$semesterId") },
                onMedicalSystemsClick = { navController.navigate("medical_systems") }
            )
        }

        composable("semester/{semesterId}",
            arguments = listOf(navArgument("semesterId") { type = NavType.IntType })
        ) { backStackEntry ->
            val semesterId = backStackEntry.arguments?.getInt("semesterId") ?: 1
            SemesterScreen(
                semesterId = semesterId,
                onBack = { navController.popBackStack() },
                onCourseClick = { courseId -> navController.navigate("course/${Uri.encode(courseId)}") }
            )
        }

        composable("medical_systems") {
            MedicalSystemsScreen(onBack = { navController.popBackStack() })
        }

        composable("course_descriptions") {
            CourseDescriptionScreen(
                onBack = { navController.popBackStack() },
                onCourseClick = { course ->
                    navController.navigate("course_specification/${Uri.encode(course.id)}")
                }
            )
        }

        composable("course_specification/{courseId}",
            arguments = listOf(navArgument("courseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
            CourseSpecificationScreen(
                courseId = courseId,
                onBack = { navController.popBackStack() }
            )
        }

        // الشاشات الأخرى
        composable("search") { SearchScreen(onBack = { navController.popBackStack() }, onNavigateToPdf = { book -> navController.navigate("pdf_viewer/${Uri.encode(book.title)}/${Uri.encode(book.file)}") }) }
        composable("skills") { SkillsScreen(onBack = { navController.popBackStack() }) }
        composable("reports") { ReportsScreen(onBack = { navController.popBackStack() }) }
        composable("calculators") { CalculatorsScreen(onBack = { navController.popBackStack() }) }
        composable("inventory") { InventoryDashboardScreen(onBack = { navController.popBackStack() }) }
        composable("qr_scanner") { QrScannerScreen(onBack = { navController.popBackStack() }, onNavigateToPdf = { book -> navController.navigate("pdf_viewer/${Uri.encode(book.title)}/${Uri.encode(book.file)}") }) }
        composable("simulation") { SimulationCenterScreen(onBack = { navController.popBackStack() }) }
    }
}
// @builder:end