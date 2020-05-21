package hu.bme.aut.tvseries.dao

import hu.bme.aut.tvseries.entity.Episode
import hu.bme.aut.tvseries.entity.Season
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository


@Repository
interface EpisodeRepository : CrudRepository<Episode, Long> {
    fun findBySeasonAndNumber(season: Season, number: Int): Episode?
    fun findByApiId(apiId: Long): Episode?
}