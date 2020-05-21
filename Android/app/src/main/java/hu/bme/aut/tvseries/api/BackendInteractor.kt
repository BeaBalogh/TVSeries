package hu.bme.aut.tvseries.api

import android.content.Context
import android.os.Handler
import android.util.Log
import com.google.gson.GsonBuilder
import hu.bme.aut.tvseries.entities.*
import hu.bme.aut.tvseries.utils.PrefUtils
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class BackendInteractor(context: Context) {
    private val backendAPI: BackendAPI

    init {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(TokenInterceptor(PrefUtils.getToken(context) ?: ""))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BackendAPI.ENDPOINT_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        this.backendAPI = retrofit.create(BackendAPI::class.java)
    }


    private fun <T> runCallOnBackgroundThread(
        call: Call<T>,
        onSuccess: (T) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val handler = Handler()
        Thread {
            try {
                val execute = call.execute()
                val response = execute.body()!!
                Log.d("mylog API", response.toString())
                handler.post { onSuccess(response) }

            } catch (e: Exception) {
                e.printStackTrace()
                handler.post { onError(e) }
            }
        }.start()
    }

    fun getSeries(
        getBy: String,
        onSuccess: (List<Series>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val getSeriesRequest = backendAPI.getSeries(getBy)
        runCallOnBackgroundThread(getSeriesRequest, onSuccess, onError)
    }

    fun searchSeries(
        title: String,
        onSuccess: (List<Series>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val getSeriesRequest = backendAPI.searchSeries(title)
        runCallOnBackgroundThread(getSeriesRequest, onSuccess, onError)
    }

    fun postSeries(
        series: Series,
        onSuccess: (Series) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val getSeriesRequest = backendAPI.postSeries(series)
        runCallOnBackgroundThread(getSeriesRequest, onSuccess, onError)
    }

    fun deleteSeries(
        id: Long,
        onSuccess: (Success) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val getSeriesRequest = backendAPI.deleteSeriesById(id)
        runCallOnBackgroundThread(getSeriesRequest, onSuccess, onError)
    }

    fun getSeriesById(
        id: Long,
        onSuccess: (Series) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val getSeriesRequest = backendAPI.getSeriesById(id)
        runCallOnBackgroundThread(getSeriesRequest, onSuccess, onError)
    }

    fun getRatings(
        id: Long,
        onSuccess: (List<Rating>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val getSeriesRequest = backendAPI.getRatings(id)
        runCallOnBackgroundThread(getSeriesRequest, onSuccess, onError)
    }

    fun postRating(
        id: Long,
        rating: Rating,
        onSuccess: (Rating) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val getSeriesRequest = backendAPI.postRating(id, rating)
        runCallOnBackgroundThread(getSeriesRequest, onSuccess, onError)
    }

    fun deleteRating(
        id: Long,
        ratingID: Long,
        onSuccess: (Success) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val getSeriesRequest = backendAPI.deleteRating(id, ratingID)
        runCallOnBackgroundThread(getSeriesRequest, onSuccess, onError)
    }

    fun postEpisode(
        id: Long,
        seasonID: Int,
        episodeID: Int,
        episode: Episode,
        onSuccess: (Episode) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val getSeriesRequest = backendAPI.postEpisode(id, seasonID, episodeID, episode)
        runCallOnBackgroundThread(getSeriesRequest, onSuccess, onError)
    }

    fun deleteEpisode(
        id: Long,
        seasonID: Int,
        episodeID: Int,
        onSuccess: (Success) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val getSeriesRequest = backendAPI.deleteEpisode(id, seasonID, episodeID)
        runCallOnBackgroundThread(getSeriesRequest, onSuccess, onError)
    }

    fun searchActor(
        name: String,
        onSuccess: (List<Actor>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val getSeriesRequest = backendAPI.searchActor(name)
        runCallOnBackgroundThread(getSeriesRequest, onSuccess, onError)
    }

    fun postID(
        getby: String,
        id: Long,
        onSuccess: (Success) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val getSeriesRequest = backendAPI.postID(getby, id)
        runCallOnBackgroundThread(getSeriesRequest, onSuccess, onError)
    }

    fun deleteID(
        getby: String,
        id: Long,
        onSuccess: (Success) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val getSeriesRequest = backendAPI.deleteID(getby, id)
        runCallOnBackgroundThread(getSeriesRequest, onSuccess, onError)
    }

    fun login(
        onSuccess: (AuthResponse) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val getSeriesRequest = backendAPI.login()
        runCallOnBackgroundThread(getSeriesRequest, onSuccess, onError)
    }

}