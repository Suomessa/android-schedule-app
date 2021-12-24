package com.hfad.retro

import com.hfad.retro.model.Faculty
import com.hfad.retro.model.Lesson
import java.lang.StringBuilder

fun getLessonsByDay(day: Int, allLessons: List<Lesson>): List<Lesson> {
    val result = mutableListOf<Lesson>()
    for (lesson in allLessons) {
        if(lesson.day == day) result.add(lesson)
    }
    return result
}

fun spinnerItemToFaculty(faculty: String)=
    when (faculty) {
         "МехМат"-> "mm"
         "Геофак"-> "gf"
         "Биофак"-> "bf"
         "Геол"-> "gl"
         "ИИ"-> "ii"
         "ИиМО"-> "imo"
         "ИФ"-> "ff"
         "Социо"-> "sf"
        else -> "No faculty"
    }

fun spinnerFacultyToPath(faculty: String, faculties: List<Faculty>): String {
    faculties.forEach { e -> if(e.name == faculty) return e.code }
    return "error";
}

fun getLessonTime(lesson: Int) = when (lesson) {
    1 ->  "08:20 - 09:50"
    2 ->  "10:00 - 11:35"
    3 ->  "12:05 - 13:40"
    4 ->  "13:50 - 15:25"
    5 ->  "15:35 - 17:10"
    6 ->  "17:20 - 18:40"
    7 ->  "18:45 - 20:05"
    8 ->  "Иди уже домой, а"
    else -> ""
}

fun formatOthers(parity: String, other: String, type: String): String {
    val resultString = StringBuilder()

    if(parity != "") resultString.append("$parity | ")

    if(other != "") resultString.append(other)

    if(type != "" && other != "") resultString.append(" | $type")
    else if (type != "") resultString.append(type)

    return resultString.toString()
}

fun dayChooser(day: Int): String {
    when (day) {
        1 -> {
            return "ПОНЕДЕЛЬНИК"
        }
        2 -> {
            return "ВТОРНИК"
        }
        3 -> {
            return "СРЕДА"
        }
        4 -> {
            return "ЧЕТВЕРГ"
        }
        5 -> {
            return "ПЯТНИЦА"
        }
        6 -> {
            return "СУББОТА"
        }
        7 -> {
            return "Воскресенье"
        }
    }
    return "Error"
}

fun setDayOfWeek(day: Int): Int {
    return if (day == 7) 1 else day
}