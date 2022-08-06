package nhncommerce.project.option

import nhncommerce.project.option.domain.Option
import nhncommerce.project.option.domain.OptionDetail
import nhncommerce.project.product.domain.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface OptionDetailRepository  : JpaRepository<OptionDetail, Long> {

    //fun findOptionDetailsByProductId(productId : Long) : List<OptionDetail>

    fun findOptionDetailsByProduct(product: Product) : List<OptionDetail>

    @Modifying
    @Query(value = "delete from OptionDetail as d " +
            "where d.option1=:option or " +
            "d.option2=:option or d.option3=:option")
    fun deleteOptionDetailsByOption(@Param("option") option : Option)

    @Modifying
    @Query(value = "delete from OptionDetail as d where d.product.productId=:productId")
    fun deleteOptionDetailsByProductId(@Param("productId") productId: Long)



}