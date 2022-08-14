package nhncommerce.project.sales


import nhncommerce.project.page.PageRequestDTO
import nhncommerce.project.page.PageResultDTO
import nhncommerce.project.sales.domain.Sales
import nhncommerce.project.sales.domain.SalesDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.function.Function

@Service
class SalesService(
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

    fun entityToDTO(sales: Sales) : SalesDTO {
        val salesDTO = SalesDTO(sales.date, sales.totalAmount, sales.quantity)
        return salesDTO
    }

    fun getSalesList(requestDTO : PageRequestDTO) : PageResultDTO<SalesDTO, Sales> {
        var sort = getSort(requestDTO)
        val pageable = requestDTO.getPageable(sort)
        val result = salesRepository.findAll(pageable)
        val fn: Function<Sales, SalesDTO> =
            Function<Sales, SalesDTO> { entity: Sales? -> entityToDTO(entity!!) }

        return PageResultDTO<SalesDTO,Sales>(result,fn)
    }

    fun getSort(pageRequestDTO: PageRequestDTO) : Sort {
        var sort : Sort = Sort.by("date").descending()

        val type = pageRequestDTO.type

        if(type.contains("quantity")){
            sort = Sort.by("quantity").descending()
        }
        if(type.contains("totalAmount")){
            sort = Sort.by("totalAmount").descending()
        }
        return sort
    }


}