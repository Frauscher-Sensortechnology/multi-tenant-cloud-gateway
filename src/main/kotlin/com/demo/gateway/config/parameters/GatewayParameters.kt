package com.demo.gateway.config.parameters

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("gateway")
class GatewayParameters(val tenantHostPattern: String, val clientId: String, val vaultClientSecretPrefix: String)
