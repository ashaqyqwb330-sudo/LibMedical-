
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
                        "books" -> {
                            val target = params["chapterId"] ?: params["chapterName"] ?: "class1"
                            navController.navigate("books/${Uri.encode(target)}")
                        }
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
                onNavigateToBooks = { chapterId ->
                    navController.navigate("books/${Uri.encode(chapterId)}")
                },
                onNavigateToChapter12 = { chapterId, chapterName ->
                    navController.navigate("chapter_tab2/$chapterId/${Uri.encode(chapterName)}")
                },
                onNavigateToBooksTab2 = { chapterId ->
                    navController.navigate("books_tab2/$chapterId")
                },
                onNavigateToGeneralsTab2 = { chapterId ->
                    navController.navigate("generals_tab2/$chapterId")
                },
                onNavigateToChapterTab2 = { chapterId, chapterName ->
                    navController.navigate("chapter_tab2/$chapterId/${Uri.encode(chapterName)}")
                },
                onNavigateToCourseDetail = { courseId ->
                    navController.navigate("content_hub/${Uri.encode(courseId)}")
                },
                onNavigateToCourseDescriptions = { navController.navigate("course_descriptions") }
            )
        }

        // شاشة الكتب المباشرة للفصل الدراسي
        composable(
            "books/{chapterId}",
            arguments = listOf(navArgument("chapterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val chapterId = backStackEntry.arguments?.getString("chapterId") ?: ""
            BooksScreen(
                chapterId = chapterId,
                onBack = { navController.popBackStack() },
                onNavigateToPdf = { book ->
                    navController.navigate("pdf_viewer/${Uri.encode(book.title)}/${Uri.encode(book.file)}")
                }
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
        // فصل (تبويب ثاني) (متوافق مع المسار القديم)
        composable(
            "chapter_12/{chapterId}/{chapterName}",
            arguments = listOf(
                navArgument("chapterId") { type = NavType.StringType },
                navArgument("chapterName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val chapterId = backStackEntry.arguments?.getString("chapterId") ?: ""
            val chapterName = Uri.decode(backStackEntry.arguments?.getString("chapterName") ?: "")
            ChapterTab2Screen(
                chapterId = chapterId,
                chapterName = chapterName,
                onBack = { navController.popBackStack() },
                onNavigateToDeviceTab2 = { cid, device ->
                    navController.navigate("device_tab2/$cid/${Uri.encode(device)}")
                },
                onNavigateToCourseDetail = { courseId ->
                    navController.navigate("content_hub/${Uri.encode(courseId)}")
                }
            )
        }

        // فصل (تبويب ثاني) (المسار الجديد)
        composable(
            "chapter_tab2/{chapterId}/{chapterName}",
            arguments = listOf(
                navArgument("chapterId") { type = NavType.StringType },
                navArgument("chapterName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val chapterId = backStackEntry.arguments?.getString("chapterId") ?: ""
            val chapterName = Uri.decode(backStackEntry.arguments?.getString("chapterName") ?: "")
            ChapterTab2Screen(
                chapterId = chapterId,
                chapterName = chapterName,
                onBack = { navController.popBackStack() },
                onNavigateToDeviceTab2 = { cid, device ->
                    navController.navigate("device_tab2/$cid/${Uri.encode(device)}")
                },
                onNavigateToCourseDetail = { courseId ->
                    navController.navigate("content_hub/${Uri.encode(courseId)}")
                }
            )
        }

        // جهاز (تبويب ثاني) (متوافق مع المسار القديم)
        composable(
            "device_12/{chapterId}/{deviceName}",
            arguments = listOf(
                navArgument("chapterId") { type = NavType.StringType },
                navArgument("deviceName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val chapterId = backStackEntry.arguments?.getString("chapterId") ?: ""
            val deviceName = Uri.decode(backStackEntry.arguments?.getString("deviceName") ?: "")
            UnifiedTab2Screen(
                chapterId = chapterId,
                deviceName = deviceName,
                dataType = Tab2DataType.DEVICE_SUBJECTS,
                onBack = { navController.popBackStack() },
                onItemConfirmed = { courseId ->
                    navController.navigate("content_hub/${Uri.encode(courseId)}")
                }
            )
        }

        // جهاز (تبويب ثاني) (المسار الجديد)
        composable(
            "device_tab2/{chapterId}/{deviceName}",
            arguments = listOf(
                navArgument("chapterId") { type = NavType.StringType },
                navArgument("deviceName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val chapterId = backStackEntry.arguments?.getString("chapterId") ?: ""
            val deviceName = Uri.decode(backStackEntry.arguments?.getString("deviceName") ?: "")
            UnifiedTab2Screen(
                chapterId = chapterId,
                deviceName = deviceName,
                dataType = Tab2DataType.DEVICE_SUBJECTS,
                onBack = { navController.popBackStack() },
                onItemConfirmed = { courseId ->
                    navController.navigate("content_hub/${Uri.encode(courseId)}")
                }
            )
        }

        // مسار موحد للمواد العامة في التبويب الثاني
        composable(
            "generals_tab2/{chapterId}",
            arguments = listOf(navArgument("chapterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val chapterId = backStackEntry.arguments?.getString("chapterId") ?: ""
            UnifiedTab2Screen(
                chapterId = chapterId,
                dataType = Tab2DataType.GENERAL_SUBJECTS,
                onBack = { navController.popBackStack() },
                onItemConfirmed = { courseId ->
                    navController.navigate("content_hub/${Uri.encode(courseId)}")
                }
            )
        }

        // كتب direct (تبويب ثاني) (المسار الجديد)
        composable(
            "books_tab2/{chapterId}",
            arguments = listOf(navArgument("chapterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val chapterId = backStackEntry.arguments?.getString("chapterId") ?: ""
            UnifiedTab2Screen(
                chapterId = chapterId,
                dataType = Tab2DataType.BOOKS,
                onBack = { navController.popBackStack() },
                onItemConfirmed = { courseId ->
                    navController.navigate("content_hub/${Uri.encode(courseId)}")
                }
            )
        }

        // شاشة المحتويات الـ12 الجديدة والمحسنة كلياً
        composable(
            "content_hub/{courseId}",
            arguments = listOf(navArgument("courseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val courseId = Uri.decode(backStackEntry.arguments?.getString("courseId") ?: "")
            CourseContentHubScreen(
                courseId = courseId,
                onBack = { navController.popBackStack() },
                onContentClick = { index ->
                    if (index == 0) {
                        navController.navigate("lecture_viewer/${Uri.encode(courseId)}/THEORY")
                    } else if (index == 1) {
                        navController.navigate("lecture_viewer/${Uri.encode(courseId)}/PRACTICAL")
                    } else {
                        navController.navigate("course_content/${Uri.encode(courseId)}/$index")
                    }
                }
            )
        }

        // شاشة عرض المحاضرات النظرية والعملية الفخمة كلياً
        composable(
            "lecture_viewer/{courseId}/{contentType}",
            arguments = listOf(
                navArgument("courseId") { type = NavType.StringType },
                navArgument("contentType") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val courseId = Uri.decode(backStackEntry.arguments?.getString("courseId") ?: "")
            val contentType = backStackEntry.arguments?.getString("contentType") ?: "THEORY"
            LectureViewerScreen(
                courseId = courseId,
                contentType = contentType,
                onBack = { navController.popBackStack() }
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
                onCourseClick = { courseId -> navController.navigate("content_hub/${Uri.encode(courseId)}") }
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