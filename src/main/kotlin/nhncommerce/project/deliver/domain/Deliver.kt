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
    val deliverId: Long = 0L,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: Status,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var addressName: String,

    @Column(nullable = false)
    var address: String,

    @Column(length = 13, nullable = false)
    var phone: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User

): BaseEntity() {

    fun entityToDeliverDto(): DeliverDTO {
        return DeliverDTO(
            addressName = addressName,
            name = name,
            address = address,
            phone = phone,
        )
    }

    fun entityToDeliverListDto(): DeliverListDTO {
        return DeliverListDTO(
            deliverId = deliverId,
            addressName = addressName,
            address = address,
            name = name,
            phone = phone,
            createdAt = createdAt,
        )
    }

    fun update(deliverDTO: DeliverDTO) {
        name = deliverDTO.name
        addressName = deliverDTO.addressName
        address = deliverDTO.address
        phone = deliverDTO.phone
    }

    fun updateStatus(newStatus: Status) {
         status = newStatus
    }
}