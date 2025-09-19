package com.kotlinspring.controllers

import com.kotlinspring.dto.CourseDTO
import com.kotlinspring.entity.Course
import com.kotlinspring.services.CourseService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.reactive.server.WebTestClient
import com.kotlinspring.util.courseDTO
import io.mockk.just
import io.mockk.runs


@WebMvcTest(controllers = [CourseController::class])
@AutoConfigureWebTestClient
class CourseControllerUnitTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockkBean
    lateinit var courseServiceMockk: CourseService

    @Test
    internal fun addCourse() {
        val courseDTO = CourseDTO(
            id = null,
            name = "Kotlin Spring Boot",
            category = "Programming Course"
        )

        every { courseServiceMockk.addCourse(any()) } returns courseDTO(id = 1)

        val savedCourseDTO = webTestClient.post()
            .uri("/v1/courses")
            .bodyValue(courseDTO)
            .exchange()
            .expectStatus().isCreated
            .expectBody(CourseDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertTrue { savedCourseDTO!!.id != null }
    }

    @Test
    internal fun addCourse_validation() {
        val courseDTO = CourseDTO(
            id = null,
            name = "",
            category = ""
        )

        every { courseServiceMockk.addCourse(any()) } returns courseDTO(id = 1)

        val savedCourseDTO = webTestClient.post()
            .uri("/v1/courses")
            .bodyValue(courseDTO)
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun retrieveAllCourses() {
        every { courseServiceMockk.retrieveAllCourses() }.returnsMany(listOf(
            courseDTO(id = 1),
            courseDTO(id = 2, name = "Rest Spring Boot Course"),
            courseDTO(id = 3),
            courseDTO(id = 4)
        ))

        val courseDTOs = webTestClient.get()
            .uri("/v1/courses")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult()
            .responseBody

        println("Courses: $courseDTOs")
        Assertions.assertEquals(4, courseDTOs!!.size)
    }

    @Test
    fun updateCourse() {
        every { courseServiceMockk.updateCourse(any(), any()) } returns courseDTO(
            100,
            "Java is a great"
            )

        //COURSE ID
        //Updated CourseDTO
        val updatedCourseDTO = CourseDTO(
            id = null,
            name = "Java Spring Boot - Updated",
            category = "Programming Course - Updated"
        )

        val updatedCourse = webTestClient.put()
            .uri("/v1/courses/{course_id}", 100)
            .bodyValue(updatedCourseDTO)
            .exchange()
            .expectStatus().isOk
            .expectBody(CourseDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertEquals("Java is a great" , updatedCourse!!.name)
    }
//
    @Test
    fun deleteCourse() {
        every { courseServiceMockk.deleteCourse(any()) } just runs

        webTestClient.delete()
            .uri("/v1/courses/{course_id}", 100)
            .exchange()
            .expectStatus().isNoContent
    }
}