package hu.bme.aut.tvseries.restservice

import hu.bme.aut.tvseries.dto.ActorDTO
import hu.bme.aut.tvseries.service.ActorService
import hu.bme.aut.tvseries.service.TheMovieDBService
import hu.bme.aut.tvseries.service.TokenService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.ws.rs.HeaderParam
import javax.ws.rs.PathParam

@CrossOrigin
@RestController
@RequestMapping(value = ["/actor"])
class ActorRestService(val actorService: ActorService, val tokenService: TokenService) {

    val unauthorized = ResponseEntity.status(HttpStatus.UNAUTHORIZED)

    @GetMapping
    fun searchByName(@RequestParam name: String, @RequestHeader("token") token: String?): ResponseEntity<List<ActorDTO>> {
        val decodedToken = token?.let { tokenService.decode(it) } ?: return unauthorized.build()
        return if (decodedToken.claims["role"] == "admin")
            ResponseEntity(actorService.searchByName(name), HttpStatus.OK)
        else return unauthorized.build()
    }
}