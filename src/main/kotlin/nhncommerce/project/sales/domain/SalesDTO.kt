package nhncommerce.project.sales.domain

import java.time.LocalDate

class SalesDTO(

    val date: LocalDate,

    val totalAmount: Long,

    val quantity: Int

)