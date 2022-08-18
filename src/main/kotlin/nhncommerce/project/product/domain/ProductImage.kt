package nhncommerce.project.product.domain

import nhncommerce.project.baseentity.BaseEntity
import nhncommerce.project.baseentity.Status
import javax.persistence.*

@Entity
class ProductImage (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var productImageId:Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: Status = Status.ACTIVE,

    @Column(nullable = false)
    var image:String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    var product: Product

):BaseEntity() {}