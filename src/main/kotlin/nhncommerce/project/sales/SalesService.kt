package nhncommerce.project.sales


import nhncommerce.project.sales.domain.Sales
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class SalesService(
    @Autowired
    val salesRepository: SalesRepository
) {
    fun getDailySales(startDate: LocalDate, endDate: LocalDate): List<Sales> {
        val list = mutableListOf<Sales>()
        val dailySalesList = salesRepository.findAllByDateBetween(startDate, endDate)
        dailySalesList.map {
            val salesListDTO = Sales(it.salesId, it.date, it.quantity, it.totalAmount)
            list.add(salesListDTO)
        }
        return list.toList()
    }


}