package com.demo.gateway.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec
import org.springframework.security.config.web.server.ServerHttpSecurity.LogoutSpec
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository
import org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository

@Configuration
class SecurityConfig(private val clients: ReactiveClientRegistrationRepository) {
    //	https://stackoverflow.com/questions/71684950/spring-session-redis-oauth2-spring-cloud-gateway-fails-when-restarting-app
    @Bean
	fun authorizedClientRepository(): ServerOAuth2AuthorizedClientRepository {
        return WebSessionServerOAuth2AuthorizedClientRepository()
    }

    @Bean
    fun filterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        val handler = OidcClientInitiatedServerLogoutSuccessHandler(clients)
        handler.setPostLogoutRedirectUri("{baseUrl}")
        http
                .authorizeExchange { exchange: AuthorizeExchangeSpec -> exchange.pathMatchers("/actuator/**").permitAll().anyExchange().authenticated() }
                .oauth2Login(Customizer.withDefaults())
                .logout { logout: LogoutSpec -> logout.logoutSuccessHandler(handler) }
                .csrf()
                .csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
        return http.build()
    }
}
