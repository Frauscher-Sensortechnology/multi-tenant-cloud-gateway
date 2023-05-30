package com.demo.gateway.config

import com.demo.gateway.config.parameters.SecurityConfigProperties
import com.demo.gateway.exceptions.CouldNotGetClientCredentialsException
import com.demo.gateway.service.ClientCredentialsManager
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrations
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.concurrent.ExecutionException

private val LOGGER = LoggerFactory.getLogger(ReactiveMultiTenantClientRegistrationRepository::class.java)

@Component
@EnableConfigurationProperties(SecurityConfigProperties::class)
class ReactiveMultiTenantClientRegistrationRepository(
        private val securityConfigProperties: SecurityConfigProperties,
        private val clientCredentialsManager: ClientCredentialsManager,
) : ReactiveClientRegistrationRepository {
    private val clientRegistrationsCache = CacheBuilder.newBuilder().build(
            object : CacheLoader<String, Mono<ClientRegistration>>() {
                override fun load(tenant: String): Mono<ClientRegistration> {
                    LOGGER.info("ClientRegistration for tenant {} not found in cache. Creating a new one.", tenant)
					val issuerLocation = "${securityConfigProperties.authorizationServer}/realms/$tenant"
                    return clientCredentialsManager.getClientSecretsForTenant(tenant).map { clientCredentials ->
                        ClientRegistrations.fromIssuerLocation(issuerLocation)
                                .registrationId(tenant)
                                .clientId(clientCredentials.clientId)
                                .clientSecret(clientCredentials.clientSecret)
                                .scope("openid")
                                .build()
                    }.doOnError { e: Throwable? ->
                        LOGGER.error("Failed to get client credentials for tenant {}", tenant, e)
                        throw CouldNotGetClientCredentialsException(tenant)
                    }
                }
            })

    override fun findByRegistrationId(registrationId: String): Mono<ClientRegistration>? {
        return try {
            clientRegistrationsCache[registrationId]
        } catch (e: ExecutionException) {
            LOGGER.error("Failed to retrieve AuthenticationManager for issuer {}", registrationId, e)
            null
        }
    }
}
