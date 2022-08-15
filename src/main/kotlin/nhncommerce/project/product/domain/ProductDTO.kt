package nhncommerce.project.product.domain

import nhncommerce.project.baseentity.Status
import nhncommerce.project.category.domain.Category
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class ProductDTO(

    var productId : Long?=null,

    var status: Status = Status.ACTIVE,

    @field:NotBlank(message = "상품명을 입력해주세요.")
    @field:Size(max = 15, message = "상품명을 15자 이내로 입력하세요.")
    var productName:String="",

    @field:Min(0, message = "상품 가격이 0원 보다 작을수 없습니다.")
    var price:Int=0,

    @field:NotBlank(message = "상품명을 입력해주세요.")
    @field:Size(max = 30, message = "간략 설명을 30자 이내로 입력하세요")
    var briefDescription:String?="",

    @field:NotBlank(message = "상품명을 입력해주세요.")
    @field:Size(max = 300, message = "상세 설명을 300자 이내로 입력하세요.")
    var detailDescription:String?="",

    var thumbnail:String="",

    var viewCount:Int=0,

    var totalStar:Float=0F,

    //카테고리
    var category: Category?=null

){

    fun String.intOrString(): Any {
        val v = toIntOrNull()
        return when(v) {
            null -> this
            else -> v
        }
    }

    fun productValidate():Boolean{

        if(price < 0){
            throw error("가격이 음수가 될 수 없습니다.")
        }

        try{
            price.toInt()
        }catch (e: NumberFormatException) {
            throw error("가격에 숫자가 아닌 문자열이 들어올 수 없습니다.")
        }

        return true
    }

    fun dtoToEntity() : Product {
        return Product(
            productId = productId,
            status = status,
            productName = productName,
            price = price,
            briefDescription = briefDescription,
            detailDescription = detailDescription,
            thumbnail = thumbnail,
            viewCount = viewCount,
            totalStar = totalStar,
            category = category
        )
    }
}