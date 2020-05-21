package hu.bme.aut.tvseries.entities

data class AuthResponse(
    val role: String,
    val followed: Set<Long>,
    val watched: Set<Long>
)