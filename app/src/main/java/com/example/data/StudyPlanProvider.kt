package com.example.data

import com.example.model.Course
import com.example.model.CourseContent
import com.example.model.ContentType

data class Stage(
    val id: Int,
    val name: String,
    val semesters: List<Semester>
)

data class Semester(
    val id: Int,
    val name: String,
    val totalCreditHours: Double,
    val totalActualHours: Int,
    val courses: List<Course> = emptyList()
)

data class MedicalSystem(
    val nameAr: String,
    val subsystems: List<String>
)

object StudyPlanProvider {
    fun getStages(): List<Stage> {
        val sampleContents = listOf(
            CourseContent("المقدمة والمفاهيم الأساسية", ContentType.TEXT, "تعريف بالمواضيع الأساسية والأهداف التعليمية للوحدة."),
            CourseContent("فيديو توضيحي تفصيلي", ContentType.VIDEO, "شرح تفاعلي مرئي للمحاضرة الأولى مع الرسوم التوضيحية."),
            CourseContent("الدليل العلمي والمصادر", ContentType.PDF, "كتاب المرجع المعتمد شاملاً الأسئلة والمراجع الملحقة."),
            CourseContent("عرض المحاضرة التقديمي", ContentType.PRESENTATION, "شريحة عرض الدرس ملخصة ومبسطة ومرفقة بالصور الميدانية."),
            CourseContent("تمرين تفاعلي وبنك الأسئلة", ContentType.INTERACTIVE, "مجموعة أسئلة اختيار من متعدد تفاعلية لتقييم الاستيعاب الفوري."),
            CourseContent("بيئة المحاكاة الافتراضية", ContentType.VR_SIMULATION, "تدبير افتراضي لسيناريوهات الإسعاف والطوارئ والمناورات السريرية.")
        )

        val firstSemesterCourses = listOf(
            Course(
                id = "ANA101",
                nameAr = "علم التشريح البشري الممنهج I",
                totalCreditHours = 6.0,
                totalActualHours = 128,
                contents = sampleContents
            ),
            Course(
                id = "PHY101",
                nameAr = "علم الفسيولوجيا الطبي الأول",
                totalCreditHours = 5.5,
                totalActualHours = 112,
                contents = sampleContents
            ),
            Course(
                id = "MMT101",
                nameAr = "المصطلحات الطبية العسكرية والإخلاء",
                totalCreditHours = 4.0,
                totalActualHours = 80,
                contents = sampleContents
            ),
            Course(
                id = "EMC101",
                nameAr = "الرعاية العسكرية والإسعافات الأولية",
                totalCreditHours = 8.0,
                totalActualHours = 176,
                contents = sampleContents
            )
        )

        val secondSemesterCourses = listOf(
            Course(
                id = "ANA102",
                nameAr = "علم التشريح وعلم الأنسجة II",
                totalCreditHours = 5.0,
                totalActualHours = 100,
                contents = sampleContents
            ),
            Course(
                id = "BIO102",
                nameAr = "الكيمياء الحيوية السريرية",
                totalCreditHours = 4.5,
                totalActualHours = 90,
                contents = sampleContents
            ),
            Course(
                id = "PRE102",
                nameAr = "الطب الوقائي والبيئي العسكري",
                totalCreditHours = 6.0,
                totalActualHours = 130,
                contents = sampleContents
            )
        )

        return listOf(
            Stage(
                id = 1,
                name = "المرحلة الأولى (التأسيس في العلوم الأساسية للميدان)",
                semesters = listOf(
                    Semester(1, "الفصل الدراسي الأول", 23.5, 496, firstSemesterCourses),
                    Semester(2, "الفصل الدراسي الثاني", 15.5, 320, secondSemesterCourses)
                )
            ),
            Stage(
                id = 2,
                name = "المرحلة الثانية (التأسيس في العلوم الأساسية للطب)",
                semesters = listOf(
                    Semester(3, "الفصل الدراسي الثالث", 34.0, 832, firstSemesterCourses.map { it.copy(id = "C3_" + it.id) }),
                    Semester(4, "الفصل الدراسي الرابع", 34.0, 832, secondSemesterCourses.map { it.copy(id = "C4_" + it.id) })
                )
            ),
            Stage(
                id = 3,
                name = "المرحلة الثالثة (التأسيسية بنظام الأجهزة)",
                semesters = listOf(
                    Semester(5, "الفصل الدراسي الخامس", 26.0, 1024, firstSemesterCourses.map { it.copy(id = "C5_" + it.id) }),
                    Semester(6, "الفصل الدراسي السادس", 26.0, 1024, secondSemesterCourses.map { it.copy(id = "C6_" + it.id) }),
                    Semester(7, "الفصل الدراسي السابع", 26.0, 1024, firstSemesterCourses.map { it.copy(id = "C7_" + it.id) })
                )
            ),
            Stage(
                id = 4,
                name = "المرحلة الرابعة (السريرية والامتياز)",
                semesters = listOf(
                    Semester(8, "الفصل الدراسي الثامن", 20.0, 960, secondSemesterCourses.map { it.copy(id = "C8_" + it.id) }),
                    Semester(9, "الفصل الدراسي التاسع", 20.0, 960, firstSemesterCourses.map { it.copy(id = "C9_" + it.id) }),
                    Semester(10, "الفصل الدراسي العاشر", 20.0, 960, secondSemesterCourses.map { it.copy(id = "C10_" + it.id) }),
                    Semester(11, "الفصل الدراسي الحادي عشر", 18.0, 768, firstSemesterCourses.map { it.copy(id = "C11_" + it.id) }),
                    Semester(12, "الفصل الدراسي الثاني عشر", 27.0, 1152, secondSemesterCourses.map { it.copy(id = "C12_" + it.id) }),
                    Semester(13, "الفصل الدراسي الثالث عشر", 21.0, 816, firstSemesterCourses.map { it.copy(id = "C13_" + it.id) })
                )
            )
        )
    }

    fun getMedicalSystems(): List<MedicalSystem> {
        return listOf(
            MedicalSystem("الجهاز الهيكلي العضلي", listOf("علم التشريح", "علم وظائف الأعضاء", "علم الأنسجة")),
            MedicalSystem("الجهاز القلبي والأوعية الدموية", listOf("علم وظائف الأعضاء", "علم الأمراض", "علم الأدوية")),
            MedicalSystem("الجهاز التنفسي", listOf("علم وظائف الأعضاء", "علم الأمراض", "العناية المركزة")),
            MedicalSystem("الجهاز الهضمي", listOf("الباطنية", "التشريح الجراحي", "علم الأمراض")),
            MedicalSystem("الجهاز البولي التناسلي", listOf("علم وظائف الأعضاء", "علم الأدوية")),
            MedicalSystem("الجهاز العصبي 1", listOf("علم التشريح", "الفسيولوجيا العصبية")),
            MedicalSystem("الجهاز العصبي 2", listOf("البثولوجيا العصبية", "العلاجات")),
            MedicalSystem("الجهاز الصمائي", listOf("الغدد الصماء", "كيمياء حيوية")),
            MedicalSystem("الفحوصات السريرية", listOf("العلامات الحيوية", "الفحوصات")),
            MedicalSystem("غازات الدم والأمراض", listOf("eGFR", "ABG"))
        )
    }
}
