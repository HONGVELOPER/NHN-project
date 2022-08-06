package nhncommerce.project.deliver.domain

import nhncommerce.project.baseentity.BaseEntity
import nhncommerce.project.baseentity.Status
import nhncommerce.project.user.domain.User
import javax.persistence.*

@Table(name = "deliver")
@Entity
class Deliver(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    val deliverId: Long? = null,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: Status,

    @Column(nullable = false)
    var name: String, // 등록한 사람

    @Column(nullable = false)
    var addressName: String, // 자취방, 집, 회사

    @Column(nullable = false)
    var address: String,

    @Column(length = 13)
    var phone: String? = null,

    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User

): BaseEntity() {

    fun update(deliverDTO: DeliverDTO) {
        name = deliverDTO.name
        addressName = deliverDTO.addressName
        address = deliverDTO.address
        phone = deliverDTO.phone
    }
}