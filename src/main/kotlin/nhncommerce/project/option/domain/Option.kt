package nhncommerce.project.option.domain

import nhncommerce.project.baseentity.BaseEntity
import nhncommerce.project.baseentity.Status
import nhncommerce.project.product.domain.Product
import javax.persistence.*

@Entity
@Table(name="options")
class Option (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val optionId : Long=0L,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parent_id")
    val parentOption : Option? = null,

    @Column(nullable = false)
    val name : String,


    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val status : Status = Status.ACTIVE,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    val product : Product
//    var product : Product?=null

) : BaseEntity() {
    fun entityToDto() : OptionDTO {
        return OptionDTO(
            optionId = optionId,
            parentOption = parentOption,
            name = name,
            status = status,
            product = product
        )
    }
}