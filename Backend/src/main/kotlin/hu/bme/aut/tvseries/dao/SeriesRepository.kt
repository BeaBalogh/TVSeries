package hu.bme.aut.tvseries.dao

import hu.bme.aut.tvseries.entity.Series
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SeriesRepository : CrudRepository<Series, Long> {
    fun findByTitleContains(title: String): List<Series>
    fun findFirstByApiId(apiId: Long): Series?
}