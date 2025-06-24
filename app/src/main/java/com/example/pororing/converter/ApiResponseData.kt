package com.example.pororing.converter

data class ConverterRequest(
    val category: String,
    val infoOptions: List<String>,
    val userInfo: UserInfo
)

data class UserInfo(
    val age: Int,
    val height: String,
    val weight: String,
    val job: String,
    val healthConditions: List<String>,
    val allergies: List<String>,
    val etc: String
)

data class ConverterResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: ResultData
)

data class ResultData(
    val name: String,
    val geminiResult: String
)