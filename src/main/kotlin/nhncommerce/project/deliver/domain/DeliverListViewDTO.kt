package nhncommerce.project.deliver.domain

data class DeliverListViewDTO (
    val deliverId : Long,
    val fullAddress : String? = null
)