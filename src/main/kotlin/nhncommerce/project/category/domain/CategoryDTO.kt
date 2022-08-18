package nhncommerce.project.category.domain

import nhncommerce.project.baseentity.Status

data class CategoryDTO (
    val categoryId : Long = 0L,
    val name : String,
    val status : Status = Status.ACTIVE,
    val parentCategory : Category? = null
    ) {
    fun dtoToEntity() : Category {
        return Category(
            categoryId = categoryId,
            name = name,
            status = status,
            parentCategory = parentCategory
        )
    }
}