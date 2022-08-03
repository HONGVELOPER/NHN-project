package nhncommerce.project.category.domain

import nhncommerce.project.baseentity.Status

data class CategoryDTO (
    var categoryId : Long? = null,
    var name : String? = null,
    var status : Status? = Status.ACTIVE,
    var parentCategory : Category? = null
    ) {
    fun toEntity() : Category {
        return Category(
            categoryId = categoryId,
            name = name,
            status = status,
            parentCategory = parentCategory
        )
    }
}