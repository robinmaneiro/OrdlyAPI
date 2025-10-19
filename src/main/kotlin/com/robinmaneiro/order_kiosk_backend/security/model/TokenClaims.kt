package com.robinmaneiro.order_kiosk_backend.security.model

data class TokenClaims(
    val userId: String,
    val variant: Variant
)

sealed interface Variant {
    val role: String
    val type: String

    sealed class Guest(override val role: String = "GUEST") : Variant {
        data object AccessToken : Guest() {
            override val type: String = "guest_access"
        }
        data object RefreshToken : Guest() {
            override val type: String = "guest_refresh"
        }
    }

    sealed class User(override val role: String = "USER") : Variant {
        data object AccessToken : User() {
            override val type: String = "auth_access"
        }
        data object RefreshToken : User() {
            override val type: String = "auth_refresh"
        }
    }
}
