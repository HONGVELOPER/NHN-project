package nhncommerce.project.product.domain

import nhncommerce.project.baseentity.BaseEntity
import nhncommerce.project.baseentity.Status
import nhncommerce.project.category.domain.Category
import javax.persistence.*

@Entity
class Product(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val productId:Long? = null,

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
    
    //카테고리 추가
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    var category: Category?=null

):BaseEntity(){
    fun updateProduct(productDTO: ProductDTO){
        productName = productDTO.productName
        price = productDTO.price
        status = productDTO.status
        briefDescription = productDTO.briefDescription
        detailDescription=  productDTO.detailDescription
        thumbnail = productDTO.thumbnail
        category = productDTO.category
    }
    
    fun toProductDTO() : ProductDTO {
        return ProductDTO(
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
