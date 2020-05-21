package hu.bme.aut.tvseries.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import hu.bme.aut.tvseries.dto.CastDTO
import javax.persistence.*

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull


@Entity
@Table(name = "Cast")
data class Cast(
        @JsonBackReference
        @ManyToOne
        @JoinColumn(name = "series_id", nullable = false)
        var series: @NotNull Series = Series(),

        @ManyToOne
        @JoinColumn(name = "actor_id", nullable = false)
        var actor: @NotNull Actor = Actor(),

        @Column(nullable = false)
        var role: @NotBlank String = ""

) : BaseEntity() {
        override fun toString(): String {
                return "$actor - $role"
        }

        fun dtoToEntity(dto: CastDTO){

        }
}