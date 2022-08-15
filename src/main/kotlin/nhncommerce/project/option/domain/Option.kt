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
    var optionId : Long?=null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parent_id")
    var parentOption : Option? = null,

    @Column(nullable = false)
    var name : String? = null,


    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status : Status? = Status.ACTIVE,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    var product : Product?=null

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