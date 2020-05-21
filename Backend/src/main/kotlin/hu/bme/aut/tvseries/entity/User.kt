package hu.bme.aut.tvseries.entity

import com.fasterxml.jackson.annotation.JsonManagedReference
import javax.persistence.*
import javax.validation.constraints.NotNull


@Entity
@Table(name = "User")
data class User(

        @Column(nullable = false)
        @NotNull
        var authority: String = AuthorityName.ROLE_USER.name,

        @Column(nullable = false)
        var name: String = "",

        @Column(nullable = false)
        var email: String = "",

        @JsonManagedReference
        @OneToMany(mappedBy = "user")
        var ratings: MutableList<Rating> = mutableListOf(),

        @ElementCollection
        var watched: MutableSet<Long> = mutableSetOf(),

        @ElementCollection
        var followed: MutableSet<Long> = mutableSetOf()

) : BaseEntity() {
}