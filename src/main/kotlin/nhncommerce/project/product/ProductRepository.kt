package nhncommerce.project.product


import nhncommerce.project.product.domain.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository :JpaRepository<Product,Long>, QuerydslPredicateExecutor<Product> {

    fun findByProductId(productId: Long): Product

    @Query(value = "select p from Product as p where p.category.categoryId =:categoryId")
    fun findProductsByCategoryId(@Param("categoryId") categoryId: Long) : List<Product>
}