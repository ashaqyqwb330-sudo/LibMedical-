package com.example.model

enum class ContentType {
    TEXT,
    VIDEO,
    PDF,
    PRESENTATION,
    INTERACTIVE,
    VR_SIMULATION
}

data class CourseContent(
    val titleAr: String,
    val type: ContentType,
    val descriptionAr: String
)

data class Course(
    val id: String,
    val nameAr: String,
    val totalCreditHours: Double,
    val totalActualHours: Int,
    val contents: List<CourseContent>
)

data class BookEntry(
    val chapter: Int,
    val title: String,
    val type: String,
    val file: String,
    val cover_path: String,
    val directPdf: String? = null
)

data class Chapter(
    val id: String,
    val name: String,
    val bookCount: Int,
    val icon: String
)

data class ChapterContent(
    val books: List<BookEntry>,
    val generals: List<BookEntry>,
    val devices: Map<String, List<BookEntry>>
)

