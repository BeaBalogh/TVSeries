package hu.bme.aut.tvseries

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import java.io.InputStream

@EnableJpaRepositories(basePackages = ["hu.bme.aut.tvseries.dao"])
@SpringBootApplication(scanBasePackages = ["hu.bme.aut.tvseries"])
class TvSeriesApplication {
    companion object {
        fun getResource(): InputStream? {
            return this::class.java.classLoader.getResourceAsStream("google/serviceAccountKey.json")
        }
    }
}

fun main(args: Array<String>) {
    runApplication<TvSeriesApplication>(*args)

    val serviceAccount = TvSeriesApplication.getResource()
    val options = FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setDatabaseUrl("https://tvseries-374ba.firebaseio.com")
            .build()
    FirebaseApp.initializeApp(options)

}
