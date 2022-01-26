package com.e.myapplication.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitClass {
    private val retrofit = Retrofit.Builder().baseUrl("http://13.125.113.173:8080")
        .addConverterFactory(GsonConverterFactory.create())
        .addConverterFactory(ScalarsConverterFactory.create()).build()
    var api: UserApi = retrofit.create(UserApi::class.java)
}