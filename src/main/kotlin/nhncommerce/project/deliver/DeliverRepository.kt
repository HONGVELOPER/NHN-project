package nhncommerce.project.deliver

import nhncommerce.project.deliver.domain.Deliver

data class DeliverRepository (
    var id: Long? = null,
    var address: String,
) {
    fun toEntity(): Deliver {
        return Deliver(address = address)
    }

    fun toUpdateEntity(): Deliver {
        return Deliver(id = id, address = address)
    }
}