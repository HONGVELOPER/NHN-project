package nhncommerce.project.option.domain

import nhncommerce.project.baseentity.Status
import nhncommerce.project.product.domain.Product

data class OptionDTO (
    var optionId : Long=0L,
    var parentOption : Option? = null,
    val name : String,
    val status : Status = Status.ACTIVE,
    val product : Product
) {
    fun dtoToEntity() : Option{
        return Option(
            optionId = optionId,
            name = name,
            status = status,
            parentOption = parentOption,
            product = product
        )
    }
}

