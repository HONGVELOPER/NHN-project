package nhncommerce.project.sales

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import nhncommerce.project.page.PageRequestDTO
import nhncommerce.project.sales.domain.Sales
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Controller
class SalesController(
    @Autowired
    val salesService: SalesService

) {

    @GetMapping("/admin")
    fun adminPage(model: Model, pageRequestDTO: PageRequestDTO):String{
        model.addAttribute("type",pageRequestDTO.type)
        model.addAttribute("total",salesService.getSalesList(pageRequestDTO))
        return "admin/index"
    }

    @GetMapping("/admin/dailyChartData")
    @ResponseBody
    fun getDataFromDate(
        @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate,
        @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate
    ): String {
        val dataList: List<Sales> = salesService.getDailySales(startDate, endDate)
        val jsonDate = JsonArray()
        val jsonQuantity = JsonArray()
        val jsonTotalAmount = JsonArray()
        val json = JsonObject()

        for (sales in dataList) {
            val formattedDate = sales.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            jsonDate.add(formattedDate)
            jsonQuantity.add(sales.quantity)
            jsonTotalAmount.add(sales.totalAmount)
        }

        json.add("date", jsonDate)
        json.add("quantity", jsonQuantity)
        json.add("totalAmount", jsonTotalAmount)
        return json.toString()
    }
}