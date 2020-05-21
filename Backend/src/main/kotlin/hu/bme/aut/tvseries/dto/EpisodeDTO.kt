package hu.bme.aut.tvseries.dto

data class EpisodeDTO(
        val id: Long = 0,
        val title: String = "",
        val overview: String = "",
        val number: Int = 0,
        val date: String = ""
)