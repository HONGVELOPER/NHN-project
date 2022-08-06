package nhncommerce.project.option.domain

import nhncommerce.project.product.domain.ProductDTO

data class UpdateOptionDTO(
    var productDTO : ProductDTO?=null,
    var optionTypeList : Array<Option?> = Array<Option?>(3, {null}),
    var optionNameList : List<MutableList<Option>?> = ArrayList<ArrayList<Option>?>()
)