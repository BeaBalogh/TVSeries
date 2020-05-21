package hu.bme.aut.tvseries.dao

import hu.bme.aut.tvseries.entity.Cast
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CastRepository: CrudRepository<Cast, Long> {
}