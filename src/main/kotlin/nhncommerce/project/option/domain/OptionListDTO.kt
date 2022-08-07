package nhncommerce.project.option.domain

import nhncommerce.project.product.domain.Product
import nhncommerce.project.product.domain.ProductDTO

data class OptionListDTO (
    var productDTO : ProductDTO?=null,
    var option1 : String?=null,
    var option2 : String?=null,
    var option3 : String?=null,
    var option1List: MutableList<String> = mutableListOf(),
    var option2List: MutableList<String> = mutableListOf(),
    var option3List: MutableList<String> = mutableListOf()
)