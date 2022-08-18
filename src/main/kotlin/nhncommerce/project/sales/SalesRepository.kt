package nhncommerce.project.sales

import nhncommerce.project.sales.domain.Sales
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface SalesRepository : JpaRepository<Sales, Long> {


    fun findAllByDateBetween(startDate: LocalDate, endDate: LocalDate): List<Sales>

}