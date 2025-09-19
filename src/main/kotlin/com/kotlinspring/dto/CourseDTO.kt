package com.kotlinspring.dto

import jakarta.validation.constraints.NotBlank

data class CourseDTO(
    val id : Int?,
    @get:NotBlank(message = "courseDTO.name should not be empty/blank")
    val name: String,
    @get:NotBlank(message = "courseDTO.category should not be blank/empty")
    val category: String
)
