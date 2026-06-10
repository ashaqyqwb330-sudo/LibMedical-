package com.example.model

data class CourseDescriptionJson(
    val levels: List<Level>
)

data class Level(
    val name: String,
    val semesters: List<Semester>
)

data class Semester(
    val name: String,
    val courses: List<CourseDescription>
)

data class CourseDescription(
    val id: String,
    val nameAr: String,
    val description: String = "",
    val descriptionHtml: String? = null
)
