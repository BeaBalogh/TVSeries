package hu.bme.aut.tvseries.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import hu.bme.aut.tvseries.dto.RatingDTO
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
@Table(name = "Rating")
data class Rating(

        @Column(nullable = false)
        var rate: @NotNull Double = 0.0,

        @Column(nullable = false)
        var comment: @NotBlank String = "",

        @Column(nullable = false)
        var date: @NotBlank String = "",

        @ManyToOne
        @JsonBackReference
        var user: @NotNull User = User(),

        @ManyToOne
        @JsonBackReference
        @JoinColumn(name = "series_id", nullable = false)
        var series: @NotNull Series = Series()

) : BaseEntity() {

    fun entityToDTO(): RatingDTO {
        return RatingDTO(
                id = this.id,
                rating = this.rate,
                comment = this.comment,
                userName = this.user.name,
                userID = this.user.email,
                date = this.date
        )
    }

    fun dtoToEntity(dto: RatingDTO, series: Series, user: User) {
        this.comment = dto.comment
        this.date = dto.date
        this.rate = dto.rating
        this.user = user
        this.series = series
    }
}
