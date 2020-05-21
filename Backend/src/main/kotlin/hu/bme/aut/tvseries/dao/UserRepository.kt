package hu.bme.aut.tvseries.dao

import hu.bme.aut.tvseries.entity.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository


@Repository
interface UserRepository : CrudRepository<User, Long> {
    fun findUserByEmail(email: String): User?
}