package hu.bme.aut.tvseries.service

import hu.bme.aut.tvseries.entity.*
import org.json.JSONArray
import org.springframework.stereotype.Service
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.validation.constraints.NotNull

@Service
class TheMovieDBService {

    val baseURL = "https://api.themoviedb.org/3"
    val apiKey = "ee1cea63c037abbee9eff96a83b23646"
    val basicParams = mutableMapOf(
            Pair("api_key", apiKey),
            Pair("language", "en-US")
    )

    fun getSeries(getBy: String): List<Series> {
        val responseSeries = khttp.get("$baseURL/tv/$getBy", params = basicParams)
        val seriesObj: JSONObject = responseSeries.jsonObject
        val list = mutableListOf<Series>()
        (seriesObj["results"] as JSONArray).forEach { s ->
            jsonToSeries(s as JSONObject, null)?.let { list.add(it) }
        }
        return list

    }

    fun searchActors(query: String): List<Actor> {
        val params = basicParams
        params["query"] = query
        params["include_adult"] = "false"
        val responseActors = khttp.get("$baseURL/search/person", params = params)
        val actorsObj: JSONObject = responseActors.jsonObject
        val list = mutableListOf<Actor>()
        (actorsObj["results"] as JSONArray).map { actorObj ->
            val actor = jsonToActor(actorObj as JSONObject)
            if (actor != null)
                list.add(actor)
        }
        return list.toList()
    }

    fun searchSeries(query: String): List<Series> {
        val params = basicParams
        params["query"] = query
        params["include_adult"] = "false"
        val responseSeries = khttp.get("$baseURL/search/tv", params = params)
        val seriesObj: JSONObject = responseSeries.jsonObject
        val list = mutableListOf<Series>()
        (seriesObj["results"] as JSONArray).forEach { s ->
            jsonToSeries(s as JSONObject, null)?.let { list.add(it) }
        }
        return list
    }

    fun getSeriesById(id: Long): Series? {
        val responseSeries = khttp.get("$baseURL/tv/$id", params = basicParams)
        val seriesObj: JSONObject = responseSeries.jsonObject
        val responsePegi = khttp.get("$baseURL/tv/$id/content_ratings", params = basicParams)
        val pegiObj: JSONObject = responsePegi.jsonObject
        return jsonToSeries(seriesObj, pegiObj)
    }

    fun getEpisodeById(id: Long, season_id: Int, episode_id: Int): Episode? {
        val responseEpisode = khttp.get("$baseURL/tv/$id/season/$season_id/episode/$episode_id", params = basicParams)
        val episodeObj: JSONObject = responseEpisode.jsonObject
        return jsonToEpisode(episodeObj)
    }

    fun getSeasonById(series_id: Long, season_id: Int): Season {
        val response = khttp.get("$baseURL/tv/$series_id/season/$season_id", params = basicParams)
        val obj: JSONObject = response.jsonObject
        return jsonToSeason(obj)
    }

    fun getCastBySeriesId(series_id: Long): MutableList<Cast> {
        val response = khttp.get("$baseURL/tv/$series_id/credits", params = basicParams)
        val obj: JSONObject = response.jsonObject
        return jsonToCast(obj)
    }

    private fun jsonToCast(obj: JSONObject): MutableList<Cast> {
        val cast = obj["cast"] as JSONArray
        val castList = mutableListOf<Cast>()
        cast.forEach { c ->
            c as JSONObject
            val name = c["name"] as String
            val image = if (c.has("profile_path") && c["profile_path"] != JSONObject.NULL) {
                c["profile_path"] as String
            } else ""
            val apiId = c["id"].toString().toLong()
            val actor = Actor(name, image, apiId)

            val role = c["character"] as String
            castList.add(Cast(actor = actor, role = role))
        }
        return castList
    }

    private fun jsonToSeason(obj: JSONObject): Season {
        val number: Int
        if (obj.has("season_number")) {
            number = (obj["season_number"] as Int) + 1
        } else return Season()

        val season = Season(number = number)
        val episodes = (obj["episodes"] as JSONArray).map { e ->
            jsonToEpisode(e as JSONObject)
        }
        season.episodes = episodes as MutableList<Episode>
        return season
    }

    private fun jsonToEpisode(obj: JSONObject): Episode? {
        if (!obj.has("name"))
            return null
        val number = obj["episode_number"] as Int
        val title = obj["name"] as String
        val overview = obj["overview"] as String
        val id = obj["id"].toString().toLong()
        var releaseDate: LocalDate? = null
        if (obj.has("air_date") && obj["air_date"] != JSONObject.NULL)
            releaseDate = stringToDate(obj["air_date"] as String)
        return Episode(apiId = id, number = number, title = title, overview = overview, releaseDate = releaseDate)
    }

    private fun jsonToSeries(seriesObj: JSONObject, pegiObj: JSONObject?): Series? {
        if (!seriesObj.has("name")) {
            return null
        }
        val title = seriesObj["name"] as String
        var year = 0
        if (seriesObj.has("first_air_date"))
            year = stringToDate(seriesObj["first_air_date"] as String)?.year?: 0
        val rating = seriesObj["vote_average"].toString().toDouble()
        val overview = seriesObj["overview"] as String
        var image = ""
        if (seriesObj.has("poster_path") && seriesObj["poster_path"] != JSONObject.NULL) {
            image = seriesObj["poster_path"] as String
        }
        var imageLandscape = ""
        if (seriesObj.has("backdrop_path") && seriesObj["backdrop_path"] != JSONObject.NULL) {
            imageLandscape = seriesObj["backdrop_path"] as String
        }
        var seasonCount = 0
        if (seriesObj.has("number_of_seasons")) {
            seasonCount = (seriesObj["number_of_seasons"] as Int) + 1
        }
        val list: MutableList<Season> = mutableListOf()
        if (seriesObj.has("seasons")) {
            (seriesObj["seasons"] as JSONArray).forEach { s ->
                s as JSONObject
                list.add(Season(s["season_number"] as Int))
            }
        }
        val pegi = jsonToPegi(pegiObj)
        val id = seriesObj["id"].toString().toLong()
        val series = Series(apiId = id, title = title, year = year, rating = rating, overview = overview, pegi = pegi, seasons = list, seasonCount = seasonCount, image = image, imageLandscape = imageLandscape)
        series.id = seriesObj["id"].toString().toLong()
        return series

    }

    private fun jsonToPegi(obj: JSONObject?): String {
        if (obj == null)
            return ""
        (obj.get("results") as JSONArray).forEach { p ->
            if ((p as JSONObject)["iso_3166_1"] == "DE") {
                return p["rating"] as String
            }
        }
        return ""
    }

    private fun jsonToActor(obj: JSONObject?): Actor? {
        if (obj == null)
            return null
        val name = obj["name"] as String
        val id = obj["id"].toString().toLong()
        val image = if (obj["profile_path"] != JSONObject.NULL) obj["profile_path"] as String else ""
        return Actor(name, image, id)
    }

    fun stringToDate(date: String): LocalDate? {
        if (date == "")
            return null
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }
}
