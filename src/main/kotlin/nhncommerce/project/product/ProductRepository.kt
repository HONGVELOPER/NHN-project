package nhncommerce.project.product

import nhncommerce.project.product.domain.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository :JpaRepository<Product,Long>{

}