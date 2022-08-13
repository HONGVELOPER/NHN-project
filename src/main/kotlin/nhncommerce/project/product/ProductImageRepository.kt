package nhncommerce.project.product

import nhncommerce.project.product.domain.Product
import nhncommerce.project.product.domain.ProductImage
import org.springframework.data.jpa.repository.JpaRepository

interface ProductImageRepository : JpaRepository<ProductImage,Long>{

    fun findByProduct(product: Product) : List<ProductImage>?

}