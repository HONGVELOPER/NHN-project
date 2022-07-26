package nhncommerce.project.product.domin

import nhncommerce.project.baseentity.Status
import javax.persistence.Column
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

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