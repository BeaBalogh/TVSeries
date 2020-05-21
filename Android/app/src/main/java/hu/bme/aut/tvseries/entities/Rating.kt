package hu.bme.aut.tvseries.entities

data class Rating(
    var id: Long = 0,
    var userName: String,
    var userID: String,
    var comment: String,
    var date: String,
    var rating: Double

)