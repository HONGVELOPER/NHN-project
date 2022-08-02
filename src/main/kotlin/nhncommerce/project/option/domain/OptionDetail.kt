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
    var optionDetailId : Long?=null,

    @Column(nullable = false)
    var status : Status? = Status.ACTIVE,

    @Column(nullable = true)
    var extraCharge : Int? = null,

    @Column(nullable = false)
    var stock : Int? = null,

    @Column(nullable = false)
    var num : Int? = null,

    @Column(nullable = false)
    var name : String? = null,

    //product
    @ManyToOne
    @JoinColumn(name="product_id")
    var product : Product? = null,

    /**optionChild의 경우 각각을 갖는게 나은가? 아니면 List에 담아서 보관하는게 나은가?**/
    @OneToOne
    @JoinColumn(name="option_id1")
    var option1 : Option? = null,

    @OneToOne
    @JoinColumn(name="option_id2")
    var option2 : Option? = null,

    @OneToOne
    @JoinColumn(name="option_id3")
    var option3 : Option? = null

    ): BaseEntity() {
        fun toOptionDetailDTO() : OptionDetailDTO {
            return OptionDetailDTO(
                optionDetailId = optionDetailId,
                status = status,
                extraCharge = extraCharge,
                stock = stock,
                num = num,
                name = name,
                product = product,
                option1 = option1,
                option2 = option2,
                option3 = option3
            )
        }

}