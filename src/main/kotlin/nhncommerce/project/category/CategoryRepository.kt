package nhncommerce.project.category

import nhncommerce.project.category.domain.Category
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository: JpaRepository<Category, Long> {

}