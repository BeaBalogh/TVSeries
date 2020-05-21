package hu.bme.aut.tvseries.service

import hu.bme.aut.tvseries.dto.ActorDTO
import org.springframework.stereotype.Service

@Service
class ActorService(
        val seriesAPI: TheMovieDBService
) {
    fun searchByName(name: String): List<ActorDTO> {
        return seriesAPI.searchActors(name).map { a -> a.entityToDTO() }
    }

}