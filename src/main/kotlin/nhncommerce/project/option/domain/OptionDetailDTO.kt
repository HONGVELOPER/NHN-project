package nhncommerce.project.option.domain

import nhncommerce.project.baseentity.Status
import nhncommerce.project.product.domain.Product

data class OptionDetailDTO (
    val optionDetailId : Long=0L,
    val status : Status = Status.ACTIVE,
    val extraCharge : Int=0,
    val stock : Int=0,
    val name : String,
    val product : Product,
    val option1 : Option? = null,
    val option2 : Option? = null,
    val option3 : Option? = null,
) {
    fun dtoToEntity() : OptionDetail {
        return OptionDetail(
            optionDetailId = optionDetailId,
            status = status,
            extraCharge = extraCharge,
            stock = stock,
            name = name,
            product = product,
            option1 = option1,
            option2 = option2,
            option3 = option3,
        )
    }

}