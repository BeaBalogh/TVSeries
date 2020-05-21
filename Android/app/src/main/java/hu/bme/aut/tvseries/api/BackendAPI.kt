package hu.bme.aut.tvseries.api

import hu.bme.aut.tvseries.entities.*
import retrofit2.Call
import retrofit2.http.*


interface BackendAPI {
    companion object {
        //TODO: Change the link to the host server
        const val ENDPOINT_URL = "http://localhost:8080"
        const val IMAGE_URL = "https://image.tmdb.org/t/p/w600_and_h900_bestv2"
        const val BACKGROUND_IMAGE_URL = "https://image.tmdb.org/t/p/w1920_and_h800_multi_faces"
    }

    @GET("/series")
    fun getSeries(@Query("getBy") getBy: String): Call<List<Series>>

    @GET("/series/search")
    fun searchSeries(@Query("title") title: String): Call<List<Series>>

    @POST("/series")
    fun postSeries(@Body series: Series): Call<Series>

    @GET("/series/{id}")
    fun getSeriesById(@Path("id") id: Long): Call<Series>

    @DELETE("/series/{id}")
    fun deleteSeriesById(@Path("id") id: Long): Call<Success>

    @GET("/series/{id}/ratings")
    fun getRatings(@Path("id") id: Long): Call<List<Rating>>

    @POST("/series/{id}/ratings")
    fun postRating(@Path("id") id: Long, @Body rating: Rating): Call<Rating>

    @DELETE("/series/{id}/ratings/{rating_id}")
    fun deleteRating(@Path("id") id: Long, @Path("rating_id") ratingID: Long): Call<Success>

//    @GET("/series/{id}/season/{season_id}")
//    fun getSeason(@Path("id") id: Long, @Path("season_id") seasonId: Int): Call<Season>

    @POST("/series/{id}/season/{season_id}/episode/{episode_id}")
    fun postEpisode(
        @Path("id") id: Long,
        @Path("season_id") seasonId: Int,
        @Path("episode_id") episdoeId: Int,
        @Body episode: Episode
    ): Call<Episode>

    @DELETE("/series/{id}/season/{season_id}/episode/{episode_id}")
    fun deleteEpisode(
        @Path("id") id: Long,
        @Path("season_id") seasonId: Int,
        @Path("episode_id") episdoeId: Int
    ): Call<Success>

    @GET("/actor")
    fun searchActor(@Query("name") name: String): Call<List<Actor>>

//    @GET("/user")
//    fun getIDs(@Query("getby") getby: String): Call<List<Long>>

    @POST("/user")
    fun postID(@Query("getby") getby: String, @Body id: Long): Call<Success>

    @DELETE("/user")
    fun deleteID(@Query("getby") getby: String, @Query("id") id: Long): Call<Success>

    @POST("/auth/login")
    fun login(): Call<AuthResponse>

}
