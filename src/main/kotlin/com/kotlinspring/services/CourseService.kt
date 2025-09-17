package com.kotlinspring.services

import com.kotlinspring.dto.CourseDTO
import com.kotlinspring.entity.Course
import com.kotlinspring.exceptions.CourseNotFoundException
import com.kotlinspring.repository.CourseRepository
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class CourseService(val courseRepository: CourseRepository) {

    companion object : KLogging()

    fun addCourse(courseDTO: CourseDTO): CourseDTO {
        //convert DTO to entity
        val courseEntity = courseDTO.let {
            Course(id=null, name = it.name, category = it.category)
        }

        //save the entity
        courseRepository.save(courseEntity)

        logger.info("Saved Course is: $courseEntity")

        //convert entity to DTO
        // return
        return courseEntity.let{
            CourseDTO(id=it.id, name=it.name, category=it.category)
        }
    }

    fun retrieveAllCourses(): List<CourseDTO> {
        return courseRepository.findAll().map {
            CourseDTO(id=it.id, name=it.name, category=it.category)
        }
    }

    fun updateCourse(courseDTO: CourseDTO, courseId: Int): CourseDTO {
        val course = courseRepository.findById(courseId)

        return if(course.isPresent){
            course.get()
                .let {
                    it.name = courseDTO.name
                    it.category = courseDTO.category
                    courseRepository.save(it)
                    CourseDTO(id=it.id, name=it.name, category=it.category)
                }
        }else {
            throw CourseNotFoundException("Course with id $courseId not found")
        }
    }

    fun deleteCourse(courseId: Int) {
        val course = courseRepository.findById(courseId)
        if(course.isPresent){
            courseRepository.deleteById(courseId)
        }else {
            throw CourseNotFoundException("Course with id $courseId not found")
        }
    }
}
