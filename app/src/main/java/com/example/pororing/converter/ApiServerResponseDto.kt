package com.example.pororing.converter

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