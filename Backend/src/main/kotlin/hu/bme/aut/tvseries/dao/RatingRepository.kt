package hu.bme.aut.tvseries.dao

import hu.bme.aut.tvseries.entity.Rating
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository


@Repository
interface RatingRepository : CrudRepository<Rating, Long> {

}