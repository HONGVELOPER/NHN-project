package nhncommerce.project.category.domain

import nhncommerce.project.baseentity.BaseEntity
import nhncommerce.project.baseentity.Status
import javax.persistence.*

@Entity
@Table(name="category")
class Category (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var categoryId : Long? = null, //todo : id는 null일 필요없다. var 사용을 줄이자, 수정같은건 메서드로수정을해야 찾기가 편하다.

    @Column(nullable = false)
    var name : String? = null, //todo : 수정해야함 nullable 과 null

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="parent_id")
    var parentCategory : Category? = null,


    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status : Status? = Status.ACTIVE
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