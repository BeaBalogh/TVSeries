package hu.bme.aut.tvseries.dto

data class RatingDTO(
        val id: Long = 0,
        val rating: Double = 0.0,
        val comment: String = "",
        val userName: String = "",
        val userID: String = "",
        val date: String = ""
)