package nhncommerce.project.option.domain

data class OptionStockDTO (
    var detailIdList : MutableList<Long>,
    var detailStockList : MutableList<Int>,
    var detailChargeList : MutableList<Int>
)