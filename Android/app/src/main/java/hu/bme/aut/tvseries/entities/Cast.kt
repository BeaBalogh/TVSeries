package hu.bme.aut.tvseries.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Cast(
    @SerializedName("actor")
    @Expose
    var actor: Actor,
    var role: String
)