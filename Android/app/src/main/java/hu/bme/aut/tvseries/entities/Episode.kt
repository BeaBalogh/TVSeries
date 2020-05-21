package hu.bme.aut.tvseries.entities

data class Episode(
    var id: Long = 0,
    var number: Int,
    var title: String,
    var overview: String,
    var date: String,
    var watched: Boolean
) {
}
