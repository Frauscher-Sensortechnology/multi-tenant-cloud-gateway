package com.demo.gateway

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity

fun main(args: Array<String>) {
	SpringApplication.run(Application::class.java, *args)
}

@EnableWebFluxSecurity
@SpringBootApplication
class Application
