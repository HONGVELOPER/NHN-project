package nhncommerce.project.option.domain

import nhncommerce.project.product.domain.Product
import nhncommerce.project.product.domain.ProductDTO

data class OptionListDTO (
    var productDTO : ProductDTO?=null,
    val option1 : String?=null,
    val option2 : String?=null,
    val option3 : String?=null,
    val option1List: MutableList<String> = mutableListOf(),
    val option2List: MutableList<String> = mutableListOf(),
    val option3List: MutableList<String> = mutableListOf()
)