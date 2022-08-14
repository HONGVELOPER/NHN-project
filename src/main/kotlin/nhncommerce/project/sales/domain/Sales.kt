package nhncommerce.project.sales.domain

import java.time.LocalDate
import javax.persistence.*

@Entity
class Sales(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val salesId: Long? = null,
    val date: LocalDate,
    val quantity: Int,
    val totalAmount: Long
){
    fun entityToDTO() : SalesDTO {
        return SalesDTO(
            date = date,
            totalAmount = totalAmount,
            quantity = quantity
        )
    }
}