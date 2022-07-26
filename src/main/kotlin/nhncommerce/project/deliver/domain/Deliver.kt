package nhncommerce.project.deliver.domain

import javax.persistence.*

@Table(name = "deliver")
class Deliver (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deliver_id", nullable = false)
    val id: Long? = null,

//  enum status pull request 에 올라와 있어서 주석 처리 후 재 수정함. //
//    @Column(nullable = false)
//    open var status: Status,

    @Column(nullable = false)
    var address: String,

//    생성일, 수정일  base entity 가져와서 하기 위해 명세 안함. //
)