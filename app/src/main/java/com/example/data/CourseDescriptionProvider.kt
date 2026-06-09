package com.example.data

import com.example.model.CourseDescription

object CourseDescriptionProvider {
    
    fun getCourseDescriptions(): List<CourseDescription> = listOf(
        // المستوى 1 - الفصل 1 (10 مقررات)
        CourseDescription("A21U111", "ثقافة إسلامية", "المستوى 1 - الفصل 1"),
        CourseDescription("A21U127", "ثقافة وطنية", "المستوى 1 - الفصل 1"),
        CourseDescription("A21U114", "لغة إنجليزية 1", "المستوى 1 - الفصل 1"),
        CourseDescription("A21U112", "لغة عربية 1", "المستوى 1 - الفصل 1"),
        CourseDescription("A21F110", "فيزياء طبية", "المستوى 1 - الفصل 1"),
        CourseDescription("A21F111", "كيمياء عامة وعضوية", "المستوى 1 - الفصل 1"),
        CourseDescription("A21P111", "طب مجتمع 1", "المستوى 1 - الفصل 1"),
        CourseDescription("A21F112", "أخلاقيات الطب", "المستوى 1 - الفصل 1"),
        CourseDescription("A21F113", "مهارات التواصل", "المستوى 1 - الفصل 1"),
        CourseDescription("A21P124_L1", "علم الخلية والأنسجة العام", "المستوى 1 - الفصل 1"),
        
        // المستوى 1 - الفصل 2 (9 مقررات)
        CourseDescription("A21U121", "لغة عربية 2", "المستوى 1 - الفصل 2"),
        CourseDescription("A21U126", "صراع عربي إسرائيلي", "المستوى 1 - الفصل 2"),
        CourseDescription("A21U122", "لغة إنجليزية 2", "المستوى 1 - الفصل 2"),
        CourseDescription("A21U113", "مهارات الحاسوب", "المستوى 1 - الفصل 2"),
        CourseDescription("A21P122", "علم التشريح والأجنة العام", "المستوى 1 - الفصل 2"),
        CourseDescription("A21P123", "علم وظائف الأعضاء العام", "المستوى 1 - الفصل 2"),
        CourseDescription("A21P124_L2", "علم الأمراض العام", "المستوى 1 - الفصل 2"),
        CourseDescription("A21P125", "كيمياء حيوية عامة", "المستوى 1 - الفصل 2"),
        CourseDescription("A21F125", "مصطلحات طبية", "المستوى 1 - الفصل 2"),
        
        // المستوى 2 - الفصل 1 (6 مقررات)
        CourseDescription("A21P216", "علم الأحياء الدقيقة الطبية العام", "المستوى 2 - الفصل 1"),
        CourseDescription("A21P218", "علم الأدوية العام", "المستوى 2 - الفصل 1"),
        CourseDescription("A21P2110", "علم الأحياء الجزيئية والوراثة", "المستوى 2 - الفصل 1"),
        CourseDescription("A21P2111", "علم الطفيليات الطبية", "المستوى 2 - الفصل 1"),
        CourseDescription("A21P2112", "التغذية", "المستوى 2 - الفصل 1"),
        CourseDescription("A21P2114", "الجهاز الهيكلي العضلي", "المستوى 2 - الفصل 1"),
        
        // المستوى 2 - الفصل 2 (5 مقررات)
        CourseDescription("A21P219", "إحصاء طبي (طب مج 2)", "المستوى 2 - الفصل 2"),
        CourseDescription("A21P2215", "جهاز الدم واللمف", "المستوى 2 - الفصل 2"),
        CourseDescription("A21P2216", "الجهاز التنفسي", "المستوى 2 - الفصل 2"),
        CourseDescription("A21P2218", "الجهاز الدوري القلبي", "المستوى 2 - الفصل 2"),
        CourseDescription("A21P2217", "علم المناعة", "المستوى 2 - الفصل 2"),
        
        // المستوى 3 - الفصل 1 (4 مقررات)
        CourseDescription("A21P2113", "رعاية صحية أولية (طب مج 3)", "المستوى 3 - الفصل 1"),
        CourseDescription("A21P3122", "الجهاز الهضمي", "المستوى 3 - الفصل 1"),
        CourseDescription("A21P3120", "الجهاز البولي التناسلي", "المستوى 3 - الفصل 1"),
        CourseDescription("A21P3121", "مناهج البحث العلمي (طب مج 4)", "المستوى 3 - الفصل 1"),
        
        // المستوى 3 - الفصل 2 (4 مقررات)
        CourseDescription("A21P3119", "جهاز الغدد الصماء", "المستوى 3 - الفصل 2"),
        CourseDescription("A21P3223", "الجهاز العصبي المركزي", "المستوى 3 - الفصل 2"),
        CourseDescription("A21P3224", "الحواس الخاصة وتشريح الرأس والرقبة", "المستوى 3 - الفصل 2"),
        CourseDescription("A21P3225", "مهارات سريرية", "المستوى 3 - الفصل 2"),
        
        // المستوى 4 - الفصل 1
        CourseDescription("A21P3226", "باطنة عامة 1", "المستوى 4 - الفصل 1"),
        CourseDescription("A21P4132", "جراحة عامة 1", "المستوى 4 - الفصل 1"),
        CourseDescription("A21P5143_L4", "نساء وتوليد 1", "المستوى 4 - الفصل 1"),
        CourseDescription("A21P4130", "طب الأطفال 1", "المستوى 4 - الفصل 1"),
        CourseDescription("A21P4129", "أشعة تشخيصية", "المستوى 4 - الفصل 1"),
        CourseDescription("A21P4131", "تخدير", "المستوى 4 - الفصل 1"),
        
        // المستوى 4 - الفصل 2
        CourseDescription("A21P4232", "باطنة عامة 2", "المستوى 4 - الفصل 2"),
        CourseDescription("A21P4233", "جراحة عامة 2", "المستوى 4 - الفصل 2"),
        CourseDescription("A21P4234", "نساء وتوليد 2", "المستوى 4 - الفصل 2"),
        CourseDescription("A21P4235", "طب الأطفال 2", "المستوى 4 - الفصل 2"),
        CourseDescription("A21P4236", "طب الطوارئ", "المستوى 4 - الفصل 2"),
        CourseDescription("A21P4237", "إدارة صحية (طب مج 5)", "المستوى 4 - الفصل 2"),
        
        // المستويات المتقدمة
        CourseDescription("A21P5138", "أنف، أذن وحنجرة", "المستوى المتقدم - الفصل 1"),
        CourseDescription("A21P5139", "الطب النفسي", "المستوى المتقدم - الفصل 1"),
        CourseDescription("A21P5140", "العيون", "المستوى المتقدم - الفصل 1"),
        CourseDescription("A21P4141", "جراحة المسالك البولية", "المستوى المتقدم - الفصل 1"),
        CourseDescription("A21P5142", "أمراض عصبية", "المستوى المتقدم - الفصل 1"),
        CourseDescription("A21P5143_ADV", "أمراض جلدية", "المستوى المتقدم - الفصل 1"),
        CourseDescription("A21P5144", "طب الأسرة (طب مج 6)", "المستوى المتقدم - الفصل 1"),
        CourseDescription("A21P5245", "باطنة عامة 3", "المستوى المتقدم - الفصل 2"),
        CourseDescription("A21P5246", "جراحة عامة 3", "المستوى المتقدم - الفصل 2"),
        CourseDescription("A21P5247", "نساء وتوليد 3", "المستوى المتقدم - الفصل 2"),
        CourseDescription("A21P4248", "طب الأطفال 3", "المستوى المتقدم - الفصل 2"),
        CourseDescription("A21P5249", "الطب الشرعي والسموم", "المستوى المتقدم - الفصل 2"),
        CourseDescription("A21P5250", "جراحة العظام", "المستوى المتقدم - الفصل 2"),
        CourseDescription("A21P5251", "بحث التخرج (طب مج 16)", "المستوى المتقدم - الفصل 2"),
        CourseDescription("A21P6152", "باطنية عامة 4", "المستوى المتقدم - الفصل 3"),
        CourseDescription("A21P6153", "جراحة عامة 4", "المستوى المتقدم - الفصل 3"),
        CourseDescription("A21P6154", "نساء وتوليد 4", "المستوى المتقدم - الفصل 3"),
        CourseDescription("A21P6155", "طب الأطفال 4", "المستوى المتقدم - الفصل 3"),
        CourseDescription("A21P6151", "بحث التخرج (طب مج 7)", "المستوى المتقدم - الفصل 3"),
        CourseDescription("A21P6256", "باطنية عامة 5", "المستوى المتقدم - الفصل 4"),
        CourseDescription("A21P6257", "جراحة عامة 5", "المستوى المتقدم - الفصل 4"),
        CourseDescription("A21P6258", "نساء وولادة 5", "المستوى المتقدم - الفصل 4"),
        CourseDescription("A21P6259", "طب أطفال 5", "المستوى المتقدم - الفصل 4")
    )
    
    fun getCoursesByLevel(): Map<String, List<CourseDescription>> {
        return getCourseDescriptions().groupBy { it.levelName }
    }
    
    fun getLevelNames(): List<String> {
        val order = listOf(
            "المستوى 1 - الفصل 1",
            "المستوى 1 - الفصل 2",
            "المستوى 2 - الفصل 1",
            "المستوى 2 - الفصل 2",
            "المستوى 3 - الفصل 1",
            "المستوى 3 - الفصل 2",
            "المستوى 4 - الفصل 1",
            "المستوى 4 - الفصل 2",
            "المستوى المتقدم - الفصل 1",
            "المستوى المتقدم - الفصل 2",
            "المستوى المتقدم - الفصل 3",
            "المستوى المتقدم - الفصل 4"
        )
        val available = getCoursesByLevel().keys
        return order.filter { it in available }
    }
}
