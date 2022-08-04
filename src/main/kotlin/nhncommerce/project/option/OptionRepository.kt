package nhncommerce.project.option

import nhncommerce.project.option.domain.Option
import nhncommerce.project.option.domain.OptionDTO
import nhncommerce.project.product.domain.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface OptionRepository : JpaRepository<Option, Long> {

    fun findOptionsByProduct(product : Product) : MutableList<Option>

    fun findOptionsByProductAndParentOptionIsNullOrderByOptionId(product: Product) : MutableList<Option>

    fun findOptionsByProductAndParentOptionIsNotNullOrderByOptionId(product : Product) : MutableList<Option>

    fun findOptionsByParentOption(parentOption : Option?) : MutableList<Option>

    @Modifying
    @Query(value = "delete from Option as o where o.parentOption is null and o.product.productId=:productId")
    fun deleteParentOptionsByProductId(@Param("productId") productId: Long)

    @Modifying
    @Query(value = "delete from Option as o where o.parentOption is not null and o.product.productId=:productId")
    fun deleteChildOptionsByProductId(@Param("productId") productId: Long)

}