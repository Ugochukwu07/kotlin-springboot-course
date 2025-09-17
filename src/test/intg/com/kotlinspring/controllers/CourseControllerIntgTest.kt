package com.kotlinspring.controllers

import com.kotlinspring.dto.CourseDTO
import com.kotlinspring.entity.Course
import com.kotlinspring.repository.CourseRepository
import com.kotlinspring.util.courseEntityList
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class CourseControllerIntgTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var courseRepository: CourseRepository

    @BeforeEach
    fun setUp() {
        courseRepository.deleteAll()
        val courses = courseEntityList()
        courseRepository.saveAll(courses)
    }

    @Test
    internal fun addCourse() {
        val courseDTO = CourseDTO(
            id = null,
            name = "Kotlin Spring Boot",
            category = "Programming Course"
        )

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
    fun retrieveAllCourses() {
        val courseDTOs = webTestClient.get()
            .uri("/v1/courses")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult()
            .responseBody

        println("Courses: $courseDTOs")
        Assertions.assertEquals(3, courseDTOs!!.size)
    }

    @Test
    fun updateCourse() {
        //existing course
        val course = Course(
            id = null,
            name = "Java Spring Boot",
            category = "Programming Course"
        )
        val savedCourse = courseRepository.save(course)

        //COURSE ID
        //Updated CourseDTO
        val newCourseDTO = CourseDTO(
            id = null,
            name = "Java Spring Boot - Updated",
            category = "Programming Course - Updated"
        )

        val updatedCourse = webTestClient.put()
            .uri("/v1/courses/{course_id}", savedCourse.id)
            .bodyValue(newCourseDTO)
            .exchange()
            .expectStatus().isOk
            .expectBody(CourseDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertEquals(newCourseDTO.name , updatedCourse!!.name)
    }

    @Test
    fun deleteCourse() {
        //existing course
        val course = Course(
            id = null,
            name = "Java Spring Boot",
            category = "Programming Course"
        )
        val savedCourse = courseRepository.save(course)

        webTestClient.delete()
            .uri("/v1/courses/{course_id}", savedCourse.id)
            .exchange()
            .expectStatus().isNoContent

        Assertions.assertFalse { courseRepository.findById(savedCourse.id!!).isPresent }
    }
}