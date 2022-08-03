package nhncommerce.project.option.domain

import nhncommerce.project.baseentity.Status
import nhncommerce.project.product.domain.Product

data class OptionDetailDTO (
    var optionDetailId : Long?=null,
    var status : Status? = Status.ACTIVE,
    var extraCharge : Int? = null,
    var stock : Int? = null,
    var num : Int? = null,
    var name : String? = null,
    var product : Product? = null,
    var option1 : Option? = null,
    var option2 : Option? = null,
    var option3 : Option? = null,
) {
    fun toEntity() : OptionDetail {
        return OptionDetail(
            optionDetailId = optionDetailId,
            status = status,
            extraCharge = extraCharge,
            stock = stock,
            num = num,
            name = name,
            product = product,
            option1 = option1,
            option2 = option2,
            option3 = option3,
        )
    }

}