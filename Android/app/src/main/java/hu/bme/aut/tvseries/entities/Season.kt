package hu.bme.aut.tvseries.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Season(
    val number: Int,
    @SerializedName("episodes")
    @Expose
    val episodes: MutableList<Episode>
)
