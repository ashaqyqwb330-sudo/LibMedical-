package com.example.data

import android.content.Context
import com.example.model.CourseDescriptionJson
import com.google.gson.Gson
import java.io.IOException

object CourseDescriptionProvider {
    fun load(context: Context): CourseDescriptionJson? {
        return try {
            val json = context.assets.open("data/course_descriptions.json")
                .bufferedReader().use { it.readText() }
            Gson().fromJson(json, CourseDescriptionJson::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
