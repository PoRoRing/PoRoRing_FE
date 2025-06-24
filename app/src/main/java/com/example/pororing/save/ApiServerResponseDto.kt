package com.example.pororing.save

data class ApiServerResponseDto(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: GeminiResult
)

data class GeminiResult(
    val name: String,
    val geminiResult: String
)