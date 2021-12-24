package com.hfad.retro

import com.hfad.retro.model.Faculty
import com.hfad.retro.model.Group
import com.hfad.retro.model.Lesson
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("getGroupSchedule")
    fun getLessons(@Query("faculty") faculty: String,
                   @Query("group") group: String): Call<List<Lesson>>

    @GET("allFaculties")
    fun allFaculties(): Call<List<Faculty>>

    @GET("allGroupsOnFaculty")
    fun allGroupsOnFaculty(@Query("faculty") faculty: String): Call<List<Group>>

}