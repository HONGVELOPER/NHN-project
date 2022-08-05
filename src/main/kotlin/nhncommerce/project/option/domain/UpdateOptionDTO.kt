package nhncommerce.project.option.domain

import nhncommerce.project.product.domain.Product

data class UpdateOptionDTO (
    var product : Product?=null,
    var optionTypeList : Array<Option?> = Array<Option?>(3, {null}),
    var optionNameList : List<MutableList<Option>?> = ArrayList<ArrayList<Option>?>()
)