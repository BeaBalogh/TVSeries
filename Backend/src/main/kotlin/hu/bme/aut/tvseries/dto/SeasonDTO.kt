package hu.bme.aut.tvseries.dto

data class SeasonDTO(
        val number: Int = 0,
        val episodes: List<EpisodeDTO> = listOf()
)
