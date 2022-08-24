package nhncommerce.project.deliver.domain

import nhncommerce.project.baseentity.Status

data class DeliverListViewDTO (
    val deliverId : Long,
    val status: Status,
    val fullAddress : String? = null
)