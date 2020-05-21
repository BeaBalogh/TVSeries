package hu.bme.aut.tvseries.api

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(private val token: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder().addHeader("token", token).build()
        Log.d(
            "Sending request %s on %s%n%s",
            "${request.headers}"
        )
        return chain.proceed(request)
    }
}