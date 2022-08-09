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

    fun findOptionDetailsByProduct(product: Product) : List<OptionDetail>

    //option에 해당하는 optionDetail 삭제
    @Modifying
    @Query(value = "delete from OptionDetail as d " +
            "where d.option1=:option or " +
            "d.option2=:option or d.option3=:option")
    fun deleteOptionDetailsByOption(@Param("option") option : Option)

    //productId의 optionDetail 삭제
    @Modifying
    @Query(value = "delete from OptionDetail as d where d.product.productId=:productId")
    fun deleteOptionDetailsByProductId(@Param("productId") productId: Long)

    fun findByOptionDetailId(optionDetailId: Long) : OptionDetail

//    fun findByProduct(product: Product): OptionDetail

}