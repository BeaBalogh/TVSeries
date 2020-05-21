package hu.bme.aut.tvseries.service

import hu.bme.aut.tvseries.dao.*
import hu.bme.aut.tvseries.dto.*
import hu.bme.aut.tvseries.entity.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Service
class SeriesService(val seriesRepository: SeriesRepository,
                    val seasonRepository: SeasonRepository,
                    val episodeRepository: EpisodeRepository,
                    val actorRepository: ActorRepository,
                    val ratingRepository: RatingRepository,
                    val userService: UserService,
                    val seriesAPI: TheMovieDBService
) {

    //TODO: Save images
    fun postSeries(dto: SeriesDTO): SeriesDTO {
        val series = Series(apiId = System.currentTimeMillis(), title = dto.title, year = dto.year, pegi = dto.pegi, overview = dto.overview)
        seriesRepository.save(series)
        return series.entityToDTO()
    }

    fun searchSeries(title: String): List<SeriesDTO> {
        val set = seriesRepository.findByTitleContains(title).toMutableSet()
        if (set.size < 20) {
            set.addAll(seriesAPI.searchSeries(title))
        }
        val list  = set.take(20)
        val seriesDTOList: MutableList<SeriesDTO> = mutableListOf()
        list.forEach { s -> seriesDTOList.add(s.entityToDTO()) }
        return seriesDTOList
    }

    fun getSeries(getBy: String): List<SeriesDTO> {
        return seriesAPI.getSeries(getBy).map { s -> s.entityToDTO() }
    }

    fun getSeries(id: Long): SeriesDTO? {
        var series = seriesRepository.findFirstByApiId(id)
        if (series == null) {
            series = createSeriesWithoutSave(id)
        }
        return series?.entityToDTO()
    }

    //TODO: Balcklist
    fun deleteSeries(id: Long): Boolean {
        seriesRepository.findFirstByApiId(id)?.let {
            seriesRepository.delete(it)
            return true
        }
        return false
    }

    fun addCast(id: Long, dto: CastDTO): Boolean {
        val series = seriesRepository.findFirstByApiId(id) ?: createSeries(id) ?: return false
        val actor = actorRepository.findByApiId(dto.actor.id)
                ?: Actor(apiId = dto.actor.id, image = dto.actor.image, name = dto.actor.name)
        val cast = Cast(role = dto.role, actor = actor)
        actor.casts.add(cast)
        series.cast.add(cast)
        actorRepository.save(actor)
        seriesRepository.save(series)
        return true
    }

    fun getSeason(id: Long, seasonId: Int): SeasonDTO? {
        val series = seriesRepository.findFirstByApiId(id)
        val season = series?.let { seasonRepository.findBySeriesAndNumber(it, seasonId) }
        if (season != null) {
            return season.entityToDTO()
        }
        return null
    }

    fun postEpisode(id: Long, season_number: Int, episode_number: Int, dto: EpisodeDTO): EpisodeDTO? {
        val series = seriesRepository.findFirstByApiId(id) ?: createSeries(id) ?: return null
        var season = seasonRepository.findBySeriesAndNumber(series, season_number)
        if (season == null) {
            season = Season(number = season_number)
            series.addSeason(season)
        }

        var episode = season.episodes.find { e -> e.number == episode_number }
        if (episode == null) {
            episode = Episode(apiId = System.currentTimeMillis(), number = episode_number, overview = dto.overview, title = dto.title, releaseDate = stringToDate(dto.date))
            season.addEpisode(episode)
        } else {
            episode.dtoToEntity(dto)
        }

        seriesRepository.save(series)
        return episode.entityToDTO()
    }

    fun deleteEpisode(id: Long, season_number: Int, episode_number: Int): Boolean {
        val series = seriesRepository.findFirstByApiId(id) ?: this.createSeries(id)
        series?.let{s ->
        seasonRepository.findBySeriesAndNumber(s, season_number)?.let { season ->
            episodeRepository.findBySeasonAndNumber(season, episode_number)?.let { episode ->
                episodeRepository.delete(episode)
                return true
            }
        }
    }
    return false
}

fun getRatings(id: Long): List<RatingDTO> {
    seriesRepository.findFirstByApiId(id)?.let { series ->
        return series.ratings.map { r ->
            r.entityToDTO()
        }
    }
    return listOf()
}

fun postRating(id: Long, dto: RatingDTO): RatingDTO? {
    userService.getUserByEmail(dto.userID)?.let { user ->
        val series = seriesRepository.findFirstByApiId(id) ?: this.createSeries(id)
        series?.let { s ->
            val rating = Rating()
            rating.dtoToEntity(dto, s, user)
            user.ratings.add(rating)
            s.ratings.add(rating)
            ratingRepository.save(rating)
            return rating.entityToDTO()
        }
    }
    return null

}

fun deleteRating(rating_id: Long): Boolean {
    ratingRepository.findByIdOrNull(rating_id)?.let { rating ->
        rating.series.ratings.remove(rating)
        rating.user.ratings.remove(rating)
        ratingRepository.delete(rating)
        return true
    }
    return false
}

private fun createSeries(id: Long): Series? {
    var series: Series = seriesAPI.getSeriesById(id) ?: return null
    series = seriesRepository.save(series)
    series.seasons.forEach { s ->
        s.series = series
        saveSeason(s.number, id, s)
    }
    val cast = seriesAPI.getCastBySeriesId(id)
    cast.forEach { c -> saveCast(c, series) }
    return seriesRepository.save(series)
}

private fun createSeriesWithoutSave(id: Long): Series? {
    val series: Series = seriesAPI.getSeriesById(id) ?: return null
    series.seasons.forEach { s ->
        s.series = series
        getSeasonWithoutSave(s.number, id, s)
    }
    val cast = seriesAPI.getCastBySeriesId(id)
    cast.forEach { c ->
        c.actor.casts.add(c)
        c.series = series
        series.cast.add(c)
    }
    return series
}

private fun saveCast(c: Cast, series: Series) {
    c.actor.casts.add(c)
    c.series = series
    c.actor = actorRepository.save(c.actor)
    series.cast.add(c)
}

private fun saveSeason(i: Int, id: Long, season: Season) {
    val episodes = seriesAPI.getSeasonById(id, i)
    if (episodes.episodes.size == 0) {
        return
    }
    episodes.episodes.forEach { e ->
        e.season = season
        season.episodes.add(e)
    }
    seasonRepository.save(season)
}

private fun getSeasonWithoutSave(i: Int, id: Long, season: Season) {
    val episodes = seriesAPI.getSeasonById(id, i)
    if (episodes.episodes.size == 0) {
        return
    }
    episodes.episodes.forEach { e ->
        e.season = season
        season.episodes.add(e)
    }
}

private fun stringToDate(date: String): LocalDate {
    return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
}

}