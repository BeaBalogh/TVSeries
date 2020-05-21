package hu.bme.aut.tvseries.service

import hu.bme.aut.tvseries.dao.EpisodeRepository
import hu.bme.aut.tvseries.dao.SeriesRepository
import hu.bme.aut.tvseries.dao.UserRepository
import hu.bme.aut.tvseries.entity.User
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserService(
        val userRepository: UserRepository
) {

    fun getUserByEmail(email: String): User? {
        return userRepository.findUserByEmail(email)
    }

    fun getIDsBy(getBy: String, email: String): List<Long> {
        val user = userRepository.findUserByEmail(email)
        if (user != null) {
            when (getBy) {
                "followed" -> return user.followed.toList()
                "watched" -> return user.watched.toList()
            }
        }
        return listOf()
    }

    fun postIDBy(getBy: String, email: String, id: Long): Boolean {
        val user = userRepository.findUserByEmail(email)
        if (user != null) {
            when (getBy) {
                "followed" -> {
                    user.followed.add(id)
                    userRepository.save(user)
                    return true
                }
                "watched" -> {
                    user.watched.add(id)
                    userRepository.save(user)
                    return true
                }
            }
        }
        return false
    }

    fun deleteIDBy(getBy: String, email: String, id: Long): Boolean {
        val user = userRepository.findUserByEmail(email)
        if (user != null) {
            when (getBy) {
                "followed" -> {
                    user.followed.remove(id)
                    userRepository.save(user)
                    return true
                }
                "watched" -> {
                    user.watched.remove(id)
                    userRepository.save(user)
                    return true
                }
            }
        }
        return false
    }
}