package com.demo.gateway.exceptions

class CouldNotGetClientCredentialsException(val tenant: String) : RuntimeException()
