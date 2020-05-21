package hu.bme.aut.tvseries.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*


@MappedSuperclass
abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @JsonIgnore
    var id: Long = 0

    @Version
    @JsonIgnore
    var version: Int = 0
}