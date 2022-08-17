package nhncommerce.project.category.domain

import nhncommerce.project.baseentity.BaseEntity
import nhncommerce.project.baseentity.Status
import javax.persistence.*

@Entity
@Table(name="category")
class Category (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val categoryId : Long = 0L,

    @Column(nullable = false)
    val name : String,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="parent_id")
    val parentCategory : Category? = null,


    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val status : Status = Status.ACTIVE
): BaseEntity() {
    fun entityToDto() : CategoryDTO {
        return CategoryDTO(
            categoryId = categoryId,
            name = name,
            parentCategory = parentCategory,
            status = status
        )
    }
}