package hu.bme.aut.tvseries.entity


import hu.bme.aut.tvseries.dto.EpisodeDTO
import hu.bme.aut.tvseries.dto.SeriesDTO
import java.time.LocalDate
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull


@Entity

@Table(name = "Episode")
data class Episode(
        @ManyToOne
        var season: Season? = null,

        @Column(nullable = false)
        var apiId: @NotNull Long = 0,

        @Column(nullable = false)
        var number: @NotNull Int = 0,

        @Column(nullable = false)
        var title: @NotBlank String = "",

        @Column(nullable = false, columnDefinition="TEXT")
        var overview: @NotBlank String = "",

        @Column
        var releaseDate: LocalDate? = LocalDate.now()

) : BaseEntity() {
    override fun toString(): String {
        return "$number,$title"
    }

    fun dtoToEntity(dto: EpisodeDTO){
        this.title= dto.title
        this.overview = dto.overview
        this.releaseDate = LocalDate.parse(dto.date)
        this.number = dto.number
        this.apiId = dto.id
    }
    fun entityToDTO(): EpisodeDTO {
        return EpisodeDTO(
                id = apiId,
                title = title,
                overview = overview,
                number = number,
                date = releaseDate.toString()
        )
    }
}
