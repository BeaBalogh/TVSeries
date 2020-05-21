package hu.bme.aut.tvseries.entity

import com.fasterxml.jackson.annotation.JsonManagedReference
import hu.bme.aut.tvseries.dto.EpisodeDTO
import hu.bme.aut.tvseries.dto.SeasonDTO
import hu.bme.aut.tvseries.dto.SeriesDTO
import org.springframework.beans.factory.annotation.Autowired
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity

@Table(name = "Season")
data class Season(
        @Column(nullable = false)
        var number: @NotNull Int = 0,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "series_id")
        var series: Series? = null,


        @JsonManagedReference
        @OneToMany(mappedBy = "season", cascade = [CascadeType.ALL])
        var episodes: MutableList<Episode> = mutableListOf()
) : BaseEntity() {
    override fun toString(): String {
        return "season number $number"
    }

    fun addEpisode(episode: Episode) {
        episode.season = this
        episodes.add(episode)
    }

    fun entityToDTO(): SeasonDTO {
        return SeasonDTO(
                number = number,
                episodes = episodes.map { e -> e.entityToDTO() }
        )
    }

}
