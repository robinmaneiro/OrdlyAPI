package com.robinmaneiro.order_kiosk_backend.security.password

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class HashEncoder {

    private val bcryptPasswordEncoder = BCryptPasswordEncoder()

    fun encode(raw: String): String = bcryptPasswordEncoder.encode(raw)

    fun matches(raw: String, hashed: String): Boolean = bcryptPasswordEncoder.matches(raw, hashed)
}