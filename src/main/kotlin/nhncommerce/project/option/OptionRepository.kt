package nhncommerce.project.option

import nhncommerce.project.option.domain.Option
import nhncommerce.project.option.domain.OptionDTO
import nhncommerce.project.product.domain.Product
import org.springframework.data.jpa.repository.JpaRepository

interface OptionRepository : JpaRepository<Option, Long> {

    fun findOptionsByProduct(product : Product) : MutableList<Option>

    fun findOptionsByProductAndParentOptionIsNullOrderByOptionId(product: Product) : MutableList<Option>

    fun findOptionsByProductAndParentOptionIsNotNullOrderByOptionId(product : Product) : MutableList<Option>

    fun findOptionsByParentOption(parentOption : Option?) : MutableList<Option>

}