package nhncommerce.project.deliver.domain

import java.time.LocalDateTime

class DeliverListDTO(
    val deliverId: Long?,

    val addressName: String,

    val address: String,

    val name: String,

    val phone: String? = null,

    val createdAt: LocalDateTime,

) { }