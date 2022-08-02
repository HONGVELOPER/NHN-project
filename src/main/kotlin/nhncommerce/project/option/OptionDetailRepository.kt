package nhncommerce.project.option

import nhncommerce.project.option.domain.OptionDetail
import nhncommerce.project.product.domain.Product
import org.springframework.data.jpa.repository.JpaRepository

interface OptionDetailRepository  : JpaRepository<OptionDetail, Long> {

    //fun findOptionDetailsByProductId(productId : Long) : List<OptionDetail>

    fun findOptionDetailsByProduct(product: Product) : List<OptionDetail>


}