package nhncommerce.project.category

import nhncommerce.project.category.domain.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface CategoryRepository: JpaRepository<Category, Long>, QuerydslPredicateExecutor<Category> {

    fun findCategoriesByParentCategory(category: Category?) : List<Category>

    fun findAllByParentCategoryIsNotNull() : List<Category>

}