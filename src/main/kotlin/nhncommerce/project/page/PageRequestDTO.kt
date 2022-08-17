package nhncommerce.project.page

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort


class PageRequestDTO(
    var page : Int = 1,
    var size : Int = 10,
){
    var type : String=""
    var keyword : String=""

    fun getPageable(sort : Sort) : Pageable{
        return PageRequest.of(page -1, size,sort)
    }

    fun changeSize(n : Int){
        size = n
    }
}
