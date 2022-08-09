package nhncommerce.project.option.domain

import nhncommerce.project.product.domain.ProductDTO

data class UpdateOptionDTO(
    var productDTO : ProductDTO?=null,
    var optionTypeList : MutableList<Option?> = mutableListOf(null, null, null),
    var optionNameList : MutableList<MutableList<Option>?> = mutableListOf(mutableListOf())
)