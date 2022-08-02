package nhncommerce.project.option.domain

data class OptionStockDTO (
    var detailIdList : ArrayList<Long>,
    var detailStockList : ArrayList<Int>,
    var detailChargeList : ArrayList<Int>
)