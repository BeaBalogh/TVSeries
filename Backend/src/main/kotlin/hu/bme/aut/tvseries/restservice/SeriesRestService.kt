package hu.bme.aut.tvseries.restservice

import hu.bme.aut.tvseries.dto.*
import hu.bme.aut.tvseries.entity.AuthorityName
import hu.bme.aut.tvseries.service.SeriesService
import hu.bme.aut.tvseries.service.TokenService
import hu.bme.aut.tvseries.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.ws.rs.HeaderParam

@CrossOrigin
@RestController
@RequestMapping(value = ["/series"])
class SeriesRestService(val seriesService: SeriesService,
                        val tokenService: TokenService,
                        val userService: UserService) {

    val unauthorized = ResponseEntity.status(HttpStatus.UNAUTHORIZED)

    @GetMapping
    fun getSeries(@RequestParam getBy: String): ResponseEntity<List<SeriesDTO>> {
        return ResponseEntity(seriesService.getSeries(getBy), HttpStatus.OK)
    }

    @PostMapping
    fun postSeries(@RequestBody series: SeriesDTO, @RequestHeader("token") token: String): ResponseEntity<SeriesDTO> {
        val decodedToken = tokenService.decode(token) ?: return unauthorized.build()
        return if (userService.getUserByEmail(decodedToken.email)?.authority == AuthorityName.ROLE_ADMIN.name)
            ResponseEntity(seriesService.postSeries(series), HttpStatus.OK)
        else
            unauthorized.build()
    }

    @GetMapping(value = ["/search"])
    fun searchSeries(@RequestParam title: String): ResponseEntity<List<SeriesDTO>> {
        return ResponseEntity(seriesService.searchSeries(title), HttpStatus.OK)
    }

    @GetMapping(value = ["/{id}"])
    fun getSeries(@PathVariable id: Long): ResponseEntity<SeriesDTO> {
        val dto = seriesService.getSeries(id)
        return if (dto == null)
            ResponseEntity(HttpStatus.NOT_FOUND)
        else
            ResponseEntity(dto, HttpStatus.OK)
    }

    @DeleteMapping(value = ["/{id}"])
    fun deleteSeries(@PathVariable id: Long, @RequestHeader("token") token: String?): ResponseEntity<Any> {
        token ?: return unauthorized.build()
        val decodedToken = tokenService.decode(token) ?: return unauthorized.build()
        if (userService.getUserByEmail(decodedToken.email)?.authority == AuthorityName.ROLE_ADMIN.name) {
            return ResponseEntity(mapOf( "succes" to seriesService.deleteSeries(id)), HttpStatus.OK)
        }
        return unauthorized.build()
    }

    @PostMapping(value = ["/{id}/cast"])
    fun postCast(@PathVariable id: Long, @RequestHeader("token") token: String, @RequestBody cast: CastDTO): ResponseEntity<Any> {
        val decodedToken = tokenService.decode(token) ?: return unauthorized.build()
        if (userService.getUserByEmail(decodedToken.email)?.authority == AuthorityName.ROLE_ADMIN.name) {
            return if (seriesService.addCast(id, cast))
                ResponseEntity(HttpStatus.OK)
            else
                ResponseEntity(HttpStatus.NOT_FOUND)
        }
        return unauthorized.build()
    }

    @GetMapping(value = ["/{id}/season/{season_id}"])
    fun getSeason(@PathVariable id: Long, @PathVariable season_id: Int): ResponseEntity<SeasonDTO> {
        val dto = seriesService.getSeason(id, season_id)
        return if (dto == null)
            ResponseEntity(HttpStatus.NOT_FOUND)
        else
            ResponseEntity(dto, HttpStatus.OK)

    }

    @PostMapping(value = ["/{id}/season/{season_id}/episode/{episode_id}"])
    fun postEpsiode(@PathVariable id: Long, @RequestHeader("token") token: String, @PathVariable season_id: Int, @PathVariable episode_id: Int, @RequestBody episode: EpisodeDTO): ResponseEntity<EpisodeDTO> {
        val decodedToken = tokenService.decode(token) ?: return unauthorized.build()
        if (userService.getUserByEmail(decodedToken.email)?.authority == AuthorityName.ROLE_ADMIN.name) {

            val dto = seriesService.postEpisode(id, season_id, episode_id, episode)
            return if (dto == null)
                ResponseEntity(HttpStatus.NOT_FOUND)
            else
                ResponseEntity(dto, HttpStatus.OK)
        }
        return unauthorized.build()
    }

    @DeleteMapping(value = ["/{id}/season/{season_id}/episode/{episode_id}"])
    fun deleteEpisode(@PathVariable id: Long, @RequestHeader("token") token: String, @PathVariable season_id: Int, @PathVariable episode_id: Int): ResponseEntity<Any> {
        val decodedToken = tokenService.decode(token) ?: return unauthorized.build()
        if (userService.getUserByEmail(decodedToken.email)?.authority == AuthorityName.ROLE_ADMIN.name) {
            return ResponseEntity(mapOf( "succes" to seriesService.deleteEpisode(id, season_id, episode_id)), HttpStatus.OK)
        }
        return unauthorized.build()
    }

    @GetMapping(value = ["/{id}/ratings"])
    fun getRatings(@PathVariable id: Long, @RequestHeader("token") token: String): ResponseEntity<List<RatingDTO>> {
        tokenService.decode(token) ?: return unauthorized.build()
        return ResponseEntity(seriesService.getRatings(id), HttpStatus.OK)
    }

    @PostMapping(value = ["/{id}/ratings"])
    fun postRating(@PathVariable id: Long, @RequestHeader("token") token: String, @RequestBody rating: RatingDTO): ResponseEntity<RatingDTO> {
        tokenService.decode(token) ?: return unauthorized.build()
        val dto = seriesService.postRating(id, rating)
        return if (dto == null)
            ResponseEntity(HttpStatus.NOT_FOUND)
        else
            ResponseEntity(dto, HttpStatus.OK)
    }

    @DeleteMapping(value = ["/{id}/ratings/{rating_id}"])
    fun deleteRating(@PathVariable id: Long, @RequestHeader("token") token: String, @PathVariable rating_id: Long): ResponseEntity<Any> {
        val decodedToken = tokenService.decode(token) ?: return unauthorized.build()
        if (userService.getUserByEmail(decodedToken.email)?.authority == AuthorityName.ROLE_ADMIN.name) {
            return ResponseEntity(mapOf( "succes" to seriesService.deleteRating(rating_id)), HttpStatus.OK)
        }
        return unauthorized.build()
    }

}