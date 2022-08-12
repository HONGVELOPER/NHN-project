package nhncommerce.project.radis.domain

data class EventCheckDTO (
    var check : Boolean? = false,
    var order : Long? = null,
    var eventNow : Boolean? = false
)