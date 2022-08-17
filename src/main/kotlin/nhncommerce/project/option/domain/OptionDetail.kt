package nhncommerce.project.option.domain

import nhncommerce.project.baseentity.BaseEntity
import nhncommerce.project.baseentity.Status
import nhncommerce.project.product.domain.Product
import javax.persistence.*

@Entity
@Table(name="option_detail")
class OptionDetail (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val optionDetailId : Long=0L,

    @Column(nullable = false)
    val status : Status = Status.ACTIVE,

    @Column(nullable = false)
    var extraCharge : Int = 0,

    @Column(nullable = false)
    var stock : Int = 0,

    @Column(nullable = false)
    val name : String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="product_id")
    val product : Product,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="option_id1")
    val option1 : Option? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="option_id2")
    val option2 : Option? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="option_id3")
    val option3 : Option? = null

    ): BaseEntity() {
        fun entityToDto() : OptionDetailDTO {
            return OptionDetailDTO(
                optionDetailId = optionDetailId,
                status = status,
                extraCharge = extraCharge,
                stock = stock,
                name = name,
                product = product,
                option1 = option1,
                option2 = option2,
                option3 = option3
            )
        }

}