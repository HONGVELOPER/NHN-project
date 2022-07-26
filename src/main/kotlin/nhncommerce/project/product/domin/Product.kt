package nhncommerce.project.product.domin

import nhncommerce.project.baseentity.BaseEntity
import nhncommerce.project.baseentity.Status
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Product(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var productId:Long? = null,

    @Column(nullable = false)
    var status:Status = Status.ACTIVE,

    @Column(nullable = false)
    var productName:String,

    @Column(nullable = false)
    var price:Int,

    @Column(nullable = true)
    var briefDescription:String?,

    @Column(nullable = true)
    var detailDescription:String?,

    @Column(nullable = true)
    var thumbnail:String?,

    @Column(nullable = false)
    var viewCount:Int,

    @Column(nullable = false)
    var totalStar:Float,

):BaseEntity()