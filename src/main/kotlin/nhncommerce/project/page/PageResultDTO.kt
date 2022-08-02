package nhncommerce.project.page

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

import java.util.function.Function
import java.util.stream.Collectors
import java.util.stream.IntStream

class PageResultDTO<DTO,EN> {

    var dtoList : MutableList<DTO>

    var pageList : MutableList<Int> = mutableListOf()

    var totalPage : Int
    var page : Int=0
    var size : Int=0
    var start : Int=0
    var end : Int =0
    var prev : Boolean = true
    var next : Boolean = true

    constructor(result: Page<EN>, fn : Function<EN,DTO>){
        this.dtoList=result.stream().map(fn).collect(Collectors.toList())
        this.totalPage=result.totalPages
        makePageList(result.pageable)
    }

    private fun makePageList(pageable: Pageable){
        this.page = pageable.pageNumber +1
        this.size = pageable.pageSize

        var tempEnd = (Math.ceil(page/10.0)*10).toInt()

        this.start = tempEnd - 9
        this.prev = start > 1
        if(totalPage > tempEnd){
            this.end = tempEnd
        }else{
            this.end = totalPage
        }

        this.next = totalPage > tempEnd
        this.pageList = IntStream.rangeClosed(start,end).boxed().collect(Collectors.toList())
    }

}
