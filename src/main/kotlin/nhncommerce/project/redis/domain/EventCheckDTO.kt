package nhncommerce.project.redis.domain

data class EventCheckDTO (
    val check : Boolean? = false,
    val order : Long? = null,
    val eventNow : Boolean? = false
)