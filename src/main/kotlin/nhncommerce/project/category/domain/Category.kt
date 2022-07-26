package nhncommerce.project.category.domain

import nhncommerce.project.baseentity.BaseEntity
import nhncommerce.project.baseentity.Status
import javax.persistence.*

@Entity
class Category (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var categoryId : Long? = null,

    @Column(nullable = false)
    var name : String? = null,

    @OneToMany(mappedBy = "category")
    var subCategoies : MutableList<SubCategory>? = ArrayList<SubCategory>(),

    @Column(nullable = false)
    var status : Status? = Status.ACTIVE
): BaseEntity()