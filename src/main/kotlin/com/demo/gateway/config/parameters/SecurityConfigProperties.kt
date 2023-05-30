package com.demo.gateway.config.parameters

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("security")
class SecurityConfigProperties(val authorizationServer: String)
