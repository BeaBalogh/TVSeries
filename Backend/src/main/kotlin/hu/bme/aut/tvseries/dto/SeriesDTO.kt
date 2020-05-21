package hu.bme.aut.tvseries.dto

data class SeriesDTO(
        var id: Long = 0,
        val title: String = "",
        val year: Int = 0,
        val rating: Double = 0.0,
        val pegi: String = "",
        val overview: String = "",
        val seasonCount: Int = 0,
        val image: String = "",
        val imageLandscape: String = "",
        val cast: List<CastDTO> = listOf(),
        val seasons: List<SeasonDTO> = listOf(),
        val ratings: List<RatingDTO> = listOf()
)