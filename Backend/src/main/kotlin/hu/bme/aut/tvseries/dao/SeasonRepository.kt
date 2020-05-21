package hu.bme.aut.tvseries.dao

import hu.bme.aut.tvseries.entity.Season
import hu.bme.aut.tvseries.entity.Series
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository


@Repository
interface SeasonRepository: CrudRepository<Season, Long> {
    fun findBySeriesAndNumber(series: Series, number: Int): Season?
}