package com.demo.gateway.filter

import org.springframework.http.ResponseCookie
import org.springframework.security.web.server.csrf.CsrfToken
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.time.Duration

private const val COOKIE_NAME = "XSRF-TOKEN"

/*
 * This Filter is needed to set the XSRF-TOKEN cookie to every request
 * https://github.com/spring-projects/spring-security/issues/5766
 */
@Component
class CsrfFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val key = CsrfToken::class.java.name
        val csrfToken = if (null != exchange.getAttribute(key)) exchange.getAttribute(key) else Mono.empty<CsrfToken>()
        return csrfToken.doOnSuccess { token: CsrfToken ->
            val cookie = ResponseCookie.from(COOKIE_NAME, token.token).maxAge(Duration.ofHours(1))
                    .httpOnly(false).path("/").build()
            exchange.response.cookies.add(COOKIE_NAME, cookie)
        }.then(chain.filter(exchange))
    }
}
