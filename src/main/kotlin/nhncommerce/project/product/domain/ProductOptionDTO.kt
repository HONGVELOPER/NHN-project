package nhncommerce.project.product.domain

import nhncommerce.project.baseentity.Status
import nhncommerce.project.category.domain.Category
import nhncommerce.project.option.domain.OptionListDTO
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class ProductOptionDTO (

    var productId : Long = 0L,

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
    @field:Size(max = 250, message = "상세 설명을 250자 이내로 입력하세요.")
    var detailDescription:String?="",

    var thumbnail:String="",

    var totalStar:Float=0F,

    var categoryId: String?=null,

    var option1 : String?=null,
    var option2 : String?=null,
    var option3 : String?=null,
    var option1List: MutableList<String> = mutableListOf(),
    var option2List: MutableList<String> = mutableListOf(),
    var option3List: MutableList<String> = mutableListOf()

    )