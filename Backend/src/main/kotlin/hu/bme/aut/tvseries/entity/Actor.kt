package hu.bme.aut.tvseries.entity

import com.fasterxml.jackson.annotation.JsonManagedReference
import hu.bme.aut.tvseries.dto.ActorDTO
import javax.persistence.*

@Entity
@Table(name = "Actor")
data class Actor(
        @Column(nullable = false)
        var name: String = "",

        @Column
        var image: String = "",

        @Column(nullable = false)
        var apiId: Long = 0,

        @JsonManagedReference
        @OneToMany(mappedBy = "actor", cascade = [CascadeType.ALL])
        var casts: MutableList<Cast> = mutableListOf()


        ) : BaseEntity() {
    override fun toString(): String {
        return name
    }
    fun entityToDTO(): ActorDTO {
        return ActorDTO(
                id = this.apiId,
                name = this.name,
                image = this.image
        )
    }

    fun dtoToEntity(dto: ActorDTO) {
        this.apiId = dto.id
        this.name = dto.name
        this.image = dto.image
    }
}