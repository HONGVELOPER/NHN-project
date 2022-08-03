package nhncommerce.project.product.domain

import nhncommerce.project.baseentity.BaseEntity
import nhncommerce.project.baseentity.Status
import javax.persistence.*

@Entity
class Product(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var productId:Long? = null,

    @Enumerated(EnumType.STRING)
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
    var thumbnail:String,

    @Column(nullable = false)
    var viewCount:Int=0,

    @Column(nullable = false)
    var totalStar:Float=0F,

):BaseEntity(){
    fun updateProduct(productDTO: ProductDTO){
        productName = productDTO.productName
        price = productDTO.price
        status = productDTO.status
        briefDescription = productDTO.briefDescription
        detailDescription=  productDTO.detailDescription
        thumbnail = productDTO.thumbnail
    }
}