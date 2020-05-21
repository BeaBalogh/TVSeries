package hu.bme.aut.tvseries.service

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import hu.bme.aut.tvseries.dao.UserRepository
import hu.bme.aut.tvseries.entity.AuthorityName
import hu.bme.aut.tvseries.entity.User
import org.springframework.stereotype.Service


@Service
class TokenService(val userRepository: UserRepository) {

    fun decode(token: String): FirebaseToken? {
        val decoded =  FirebaseAuth.getInstance().verifyIdToken(token)
        val user = userRepository.findUserByEmail(decoded.email)
                ?: User(authority = AuthorityName.ROLE_ADMIN.name, name = decoded.name, email = decoded.email)
        userRepository.save(user)
        return decoded
    }

}