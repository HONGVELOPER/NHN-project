package nhncommerce.project.option.domain

import nhncommerce.project.product.domain.Product

data class OptionListDTO (
    var product : Product?=null,
    var option1 : String?=null,
    var option2 : String?=null,
    var option3 : String?=null,
    var option1List: MutableList<String> = ArrayList<String>(),
    var option2List: MutableList<String> = ArrayList<String>(),
    var option3List: MutableList<String> = ArrayList<String>()
)