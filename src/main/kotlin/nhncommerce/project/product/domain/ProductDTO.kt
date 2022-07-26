package nhncommerce.project.product.domain

import nhncommerce.project.baseentity.Status

data class ProductDTO(

    var productId:Long,

    var status: Status = Status.ACTIVE,

    var productName:String,

    var price:Int,

    var briefDescription:String?,

    var detailDescription:String?,

    var thumbnail:String?,

    var viewCount:Int,

    var totalStar:Float,

)