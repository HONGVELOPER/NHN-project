package nhncommerce.project.option.domain

data class OptionStockDTO (
    val detailIdList : MutableList<Long>,
    val detailStockList : MutableList<Int>,
    val detailChargeList : MutableList<Int>
)