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
/** 애매 */
//    @Column(nullable = false)
//    var parentId : Long? = null,

    @OneToOne
    @JoinColumn(name ="parent_id")
    var parentCategory : Category? = null,

    @Column(nullable = false)
    var status : Status? = Status.ACTIVE
): BaseEntity() {
    fun toCategoryDTO() : CategoryDTO {
        return CategoryDTO(
            name = this.name,
            parentCategory = parentCategory,
            status = this.status
        )
    }
}