package com.hfad.retro

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("api/schedule/get")
    fun getLessons(@Query("faculty") faculty: String,
                   @Query("group") group: String): Call<List<Lesson>>

}