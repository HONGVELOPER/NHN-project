package nhncommerce.project.util.token.domain

import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class StorageToken (

    @Id @GeneratedValue
    val Id : Long?=null,

    var tokenId : String,

    var expires : LocalDateTime
)
