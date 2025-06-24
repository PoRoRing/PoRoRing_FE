package com.example.pororing.converter

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @Multipart
    @POST("/api/converter")
    suspend fun uploadImageWithJson(
        @Part file: MultipartBody.Part,
        @Part preInformation: MultipartBody.Part
    ): Response<ApiServerResponseDto>
}