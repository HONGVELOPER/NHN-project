package nhncommerce.project.product.domain

import nhncommerce.project.baseentity.Status
import nhncommerce.project.option.domain.OptionListDTO
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class ProductOptionDTO (

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

    var option1 : String?=null,
    var option2 : String?=null,
    var option3 : String?=null,
    var option1List: MutableList<String> = ArrayList<String>(),
    var option2List: MutableList<String> = ArrayList<String>(),
    var option3List: MutableList<String> = ArrayList<String>()

    )