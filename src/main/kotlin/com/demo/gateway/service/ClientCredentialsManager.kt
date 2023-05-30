package com.demo.gateway.service

import com.azure.security.keyvault.secrets.SecretAsyncClient
import com.demo.gateway.config.parameters.GatewayParameters
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
@EnableConfigurationProperties(GatewayParameters::class)
class ClientCredentialsManager(
        private val secretAsyncClient: SecretAsyncClient,
        private val gatewayParameters: GatewayParameters,
) {
    fun getClientSecretsForTenant(tenant: String): Mono<ClientCredentials> {
        return secretAsyncClient.getSecret(gatewayParameters.vaultClientSecretPrefix + tenant)
                .map { ClientCredentials(gatewayParameters.clientId, it.value) }
    }
}
