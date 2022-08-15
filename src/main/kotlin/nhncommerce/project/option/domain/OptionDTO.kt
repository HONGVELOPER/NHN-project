package nhncommerce.project.option.domain

import nhncommerce.project.baseentity.Status
import nhncommerce.project.product.domain.Product

data class OptionDTO (
    var optionId : Long? = null,
    var parentOption : Option? = null,
    var name : String? = null,
    var status : Status? = Status.ACTIVE,
    var product : Product?=null
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

