package nhncommerce.project.category

import nhncommerce.project.category.domain.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface CategoryRepository: JpaRepository<Category, Long>, QuerydslPredicateExecutor<Category> {

    /**
     * 부모 카테고리로 자식 카테고리 리스트 검색
     */
    fun findCategoriesByParentCategory(category: Category?) : List<Category>

    /**
     * 부모 카테고리가 NOT NULL인 카테고리 리스트 조회
     */
    fun findAllByParentCategoryIsNotNull() : List<Category>

}