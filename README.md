# Cloud Gateway

## Scope

This service acts as a central entry point / gateway into a microservice architecture. It handles the routing of requests to the corresponding downstream services, rewrites the requests (e.g. removes headers) and enforces authentication of the requests. It uses the token relay filter to authenticate the client via a cookie and exchanges to cookie for a JWT which is used for downstream services.

## Authentication

Each request must have a valid cookie to be authenticated. A request that does not have a valid cookie is automatically forwarded to the login. If a request is authenticated, it is forwarded to the downstream services with a valid JWT. JWT handling (request, renewal) is handled entirely by this service, the client does not take care of this (token relay filter).

## Multi tenancy
The implementation of this service is capable of identifying different requests against different IdPs and thus allowing a multi-tenant architecture.

## How the authentication works

The implementation uses Keycloak as IdP.

From an unauthenticated request, the associated tenant is extracted. This is required to determine the associated IdP and is done using a subdomain. For the extraction a tenant host pattern is used, which can be defined under `gateway.tenantHostPattern` and which is a regex used to extract the tenant from the request domain. For example, the `tenantHostPattern` `^(.*).your-domain.com` extracts the tenant `foo` from the domain `foo.your-domain.com`.
Then the client is forwarded to the associated IdP for authentication. In the current implementation, this is a realm with the same name as the tenant name in a keycloak. In the above example, the request is forwarded to the `foo` realm of the configuring keycloak. The URL of the keycloak to be used is configured at `security.authorizationServer`.

