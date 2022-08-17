package nhncommerce.project.option.domain

import nhncommerce.project.product.domain.ProductDTO

data class UpdateOptionDTO(
    val productDTO : ProductDTO?=null,
    val optionTypeList : MutableList<Option?> = mutableListOf(null, null, null),
    val optionNameList : MutableList<MutableList<Option>?> = mutableListOf(mutableListOf())
)