package hu.bme.aut.tvseries.restservice

import hu.bme.aut.tvseries.service.TokenService
import hu.bme.aut.tvseries.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.ws.rs.HeaderParam


@CrossOrigin
@RestController
@RequestMapping(value = ["/user"])
class UserRestService(val userService: UserService,
                      val tokenService: TokenService) {
    val unauthorized = ResponseEntity.status(HttpStatus.UNAUTHORIZED)

    @GetMapping
    fun getIDsBy(@RequestParam getby: String, @RequestHeader("token") token: String?): ResponseEntity<List<Long>> {
        token ?: return unauthorized.build()
        val decodedToken = tokenService.decode(token) ?: return unauthorized.build()
        val email = decodedToken.email
        return ResponseEntity(userService.getIDsBy(getby, email), HttpStatus.OK)
    }

    @PostMapping
    fun postIDBy(@RequestParam getby: String, @RequestHeader("token") token: String?, @RequestBody id: Long): ResponseEntity<Any> {
        token ?: return unauthorized.build()
        val decodedToken = tokenService.decode(token) ?: return unauthorized.build()
        val email = decodedToken.email
        return ResponseEntity(mapOf("success" to userService.postIDBy(getby, email, id)), HttpStatus.OK)
    }

    @DeleteMapping
    fun deleteIDBy(@RequestParam getby: String, @RequestHeader("token") token: String?, @RequestParam id: Long): ResponseEntity<Any> {
        token ?: return unauthorized.build()
        val decodedToken = tokenService.decode(token) ?: return unauthorized.build()
        val email = decodedToken.email
        return ResponseEntity(mapOf("success" to userService.deleteIDBy(getby, email, id)), HttpStatus.OK)
    }

}