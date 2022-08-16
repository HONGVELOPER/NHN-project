package nhncommerce.project.page

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort


class PageRequestDTO(){

    var page : Int
    var size : Int
    var type : String=""
    var keyword : String=""

    init {
        this.page=1
        this.size=10
    }

    fun getPageable(sort : Sort) : Pageable{
        return PageRequest.of(page -1, size,sort)
    }

    fun changeSize(n : Int){
        size = n
    }
}
