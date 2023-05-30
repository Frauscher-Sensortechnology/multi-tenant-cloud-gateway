package com.demo.gateway.filter

import com.demo.gateway.config.parameters.GatewayParameters
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import java.net.URI
import java.util.regex.Pattern

private val logger = LoggerFactory.getLogger(MultiTenantLoginFilter::class.java)

@Component
@Order(-101)
@EnableConfigurationProperties(GatewayParameters::class)
class MultiTenantLoginFilter(gatewayParameters: GatewayParameters) : WebFilter {
	private val tenantHostPattern: Pattern = Pattern.compile(gatewayParameters.tenantHostPattern)

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        if (exchange.request.path.value() == "/login") {
			return loginRequest(exchange)
		}
        return chain.filter(exchange)
    }

	private fun loginRequest(exchange: ServerWebExchange): Mono<Void> {
		val response = exchange.response
		val host = UriComponentsBuilder.fromUri(exchange.request.uri)
			.build().host
		if (host == null) {
			response.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
			logger.error("Parameter HOST was null in MultiTenantLoginFilter")
			return Mono.empty()
		}
		val matcher = tenantHostPattern.matcher(host)
		if (matcher.find()) {
			response.statusCode = HttpStatus.TEMPORARY_REDIRECT
			response.headers.location = URI.create("/oauth2/authorization/" + matcher.group(1))
			return Mono.empty()
		}
		response.statusCode = HttpStatus.BAD_REQUEST
		return Mono.empty()
	}
}
