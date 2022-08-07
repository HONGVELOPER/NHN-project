package nhncommerce.project.category.domain

import nhncommerce.project.baseentity.BaseEntity
import nhncommerce.project.baseentity.Status
import javax.persistence.*

@Entity
@Table(name="category")
class Category (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var categoryId : Long? = null,

    @Column(nullable = false)
    var name : String? = null,

    @OneToOne
    @JoinColumn(name ="parent_id")
    var parentCategory : Category? = null,


    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status : Status? = Status.ACTIVE
): BaseEntity() {
    fun toCategoryDTO() : CategoryDTO {
        return CategoryDTO(
            categoryId = categoryId,
            name = name,
            parentCategory = parentCategory,
            status = status
        )
    }
}