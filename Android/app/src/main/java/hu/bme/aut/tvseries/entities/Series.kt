package hu.bme.aut.tvseries.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Series(
    var id: Long = 0,
    var title: String,
    var rating: String,
    @SerializedName("ratings")
    @Expose
    var ratings: MutableList<Rating>,
    var year: Int,
    var pegi: String,
    var overview: String,
    @SerializedName("cast")
    @Expose
    var cast: MutableList<Cast>,
    var follow: Boolean,
    var image: String,
    var imageLandscape: String,
    @SerializedName("seasons")
    @Expose
    var seasons: MutableList<Season>,
    var seasonCount: Int
)