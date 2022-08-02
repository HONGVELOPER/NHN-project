package nhncommerce.project.option.domain

import nhncommerce.project.product.domain.Product

data class UpdateOptionDTO (
    var product : Product?=null,
    var optionTypeList : Array<Option?> = Array<Option?>(3, {null}),
//    var optionType1List : MutableList<Option> = ArrayList<Option>(),
//    var optionType2List : MutableList<Option> = ArrayList<Option>(),
//    var optionType3List : MutableList<Option> = ArrayList<Option>(),
    var optionNameList : List<MutableList<Option>?> = ArrayList<ArrayList<Option>?>()
)