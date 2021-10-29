package com.hfad.retro

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