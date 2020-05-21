package hu.bme.aut.tvseries.dao

import hu.bme.aut.tvseries.entity.Actor
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ActorRepository : CrudRepository<Actor, Long> {
    fun findByApiId(apiId: Long): Actor?
}