package hu.bme.aut.tvseries.restservice

import com.google.firebase.auth.FirebaseAuth
import hu.bme.aut.tvseries.dao.UserRepository
import hu.bme.aut.tvseries.entity.AuthorityName
import hu.bme.aut.tvseries.entity.User
import hu.bme.aut.tvseries.service.TokenService
import hu.bme.aut.tvseries.service.UserService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.ws.rs.HeaderParam


@CrossOrigin
@RestController
@Api(value = "Auth API", description = "Operations pertaining to Authentication")
@RequestMapping(value = ["/auth"])
class AuthRestService(val tokenService: TokenService,
                      val userService: UserService) {
    val unauthorized = ResponseEntity.status(HttpStatus.UNAUTHORIZED)

    @ApiOperation(value = "User authentication")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "Successfully authenticated user"),
        ApiResponse(code = 401, message = "Invalid token")
    ])
    @PostMapping(value = ["/login"])
    fun login(@RequestHeader("token") token: String?): ResponseEntity<Any> {
        if(token==null || token=="") return unauthorized.build()
        val decodedToken = tokenService.decode(token) ?: return unauthorized.build()
        val role = userService.getUserByEmail(decodedToken.email)?.authority
        val followed = userService.getIDsBy("followed", decodedToken.email).toSet()
        val watched = userService.getIDsBy("watched", decodedToken.email).toSet()
        return ResponseEntity.ok().body(mapOf("role" to role, "followed" to followed, "watched" to watched))
    }

}