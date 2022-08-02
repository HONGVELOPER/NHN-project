package nhncommerce.project.category.domain

import nhncommerce.project.baseentity.BaseEntity
import nhncommerce.project.baseentity.Status
import javax.persistence.*

@Entity
class SubCategory (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var subCategoryId : Long? = null,

    @Column(nullable = false)
    var name : String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    var category : Category? = null,

    @Column(nullable = false)
    var status : Status? = Status.ACTIVE
): BaseEntity()