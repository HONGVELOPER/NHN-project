package nhncommerce.project.option

import nhncommerce.project.option.domain.Option
import nhncommerce.project.option.domain.OptionDetail
import nhncommerce.project.product.domain.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

interface OptionDetailRepository  : JpaRepository<OptionDetail, Long> {

    @Query(value = "select d from OptionDetail as d where d.product = :product and d.status = 'ACTIVE'")
    fun findOptionDetailsByProduct(@Param("product") product: Product) : List<OptionDetail>

    //productId의 optionDetail 삭제
    @Modifying
    @Query(value = "delete from OptionDetail as d where d.product.productId=:productId")
    fun deleteOptionDetailsByProductId(@Param("productId") productId: Long)

    @Query(value = "select d from OptionDetail as d where d.optionDetailId = :optionDetailId")
    fun findByOptionDetailId(@Param("optionDetailId") optionDetailId: Long) : OptionDetail

}