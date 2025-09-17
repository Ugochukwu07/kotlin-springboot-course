package com.kotlinspring.controllers

import com.kotlinspring.services.GreetingService
import mu.KLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/greetings")
class GreetingController(
    val greetingService: GreetingService
) {

    companion object : KLogging()

    @GetMapping("/{name}")
    fun greet(@PathVariable("name") name: String): String {
        logger.info { "greet() invoked with name: $name" }
        return greetingService.retrieveGreeting(name)
    }
}