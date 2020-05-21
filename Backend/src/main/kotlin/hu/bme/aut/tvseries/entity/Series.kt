package hu.bme.aut.tvseries.entity

import com.fasterxml.jackson.annotation.JsonManagedReference
import hu.bme.aut.tvseries.dto.ActorDTO
import hu.bme.aut.tvseries.dto.CastDTO
import hu.bme.aut.tvseries.dto.SeriesDTO
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull


@Entity
@Table(name = "Series")
data class Series(
        @Column(nullable = false)
        @NotBlank
        var title: String = "",

        @Column(nullable = false)
        @NotBlank
        var apiId: Long = 0,

        @Column(nullable = false)
        @NotNull
        var year: Int = 0,

        @Column(nullable = false)
        @NotNull
        var rating: Double = 0.0,

        @Column(nullable = false)
        @NotBlank
        var pegi: String = "",

        @Column(nullable = false, columnDefinition="TEXT")
        @NotBlank
        var overview: String = "",

        @Column(nullable = false)
        @NotBlank
        var seasonCount: Int = 0,

        @Column
        var image: String = "",

        @Column
        var imageLandscape: String = "",

        @JsonManagedReference
        @OneToMany(mappedBy = "series", cascade = [CascadeType.ALL])
        var seasons: MutableList<Season> = mutableListOf(),

        @JsonManagedReference
        @OneToMany(mappedBy = "series", cascade = [CascadeType.ALL])
        var cast: MutableList<Cast> = mutableListOf(),

        @JsonManagedReference
        @OneToMany(mappedBy = "series", cascade = [CascadeType.ALL])
        var ratings: MutableList<Rating> = mutableListOf()

) : BaseEntity() {
    override fun toString(): String {
        return "title: $title, apiid: $apiId, year: $year"
    }

    override fun hashCode(): Int {
        return apiId.toInt()
    }

    override fun equals(other: Any?): Boolean {
        return apiId == (other as Series).apiId
    }

    fun addSeason(season: Season) {
        season.series = this
        seasons.add(season)
    }

    fun dtoToEntity(dto: SeriesDTO) {
        this.apiId = dto.id
        this.title = dto.title
        this.year = dto.year
        this.rating = dto.rating
        this.pegi = dto.pegi
        this.overview = dto.overview
        this.image = dto.image
        this.imageLandscape = dto.imageLandscape
        dto.cast.forEach { (role, actorDTO) ->
            val actor = Actor()
            actor.dtoToEntity(actorDTO)
            this.cast.plus(Cast(series = this, actor = actor, role = role))
        }
    }

    fun entityToDTO(): SeriesDTO {
        val castList: MutableList<CastDTO> = mutableListOf()
        this.cast.forEach { c -> castList.add(CastDTO(c.role, c.actor.entityToDTO())) }
        return SeriesDTO(id = apiId,
                title = title,
                year = year,
                rating = rating,
                pegi = pegi,
                seasonCount = seasonCount,
                overview = overview,
                cast = castList,
                seasons = seasons.map { s-> s.entityToDTO() },
                image = image,
                imageLandscape = imageLandscape,
                ratings = ratings.map { r -> r.entityToDTO() }
        )
    }
}