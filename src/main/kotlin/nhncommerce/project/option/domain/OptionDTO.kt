package nhncommerce.project.option.domain

import nhncommerce.project.baseentity.Status
import nhncommerce.project.product.domain.Product

data class OptionDTO (
    val optionId : Long=0L,
    val parentOption : Option? = null,
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

