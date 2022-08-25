package nhncommerce.project.option

import nhncommerce.project.option.domain.Option
import nhncommerce.project.option.domain.OptionDTO
import nhncommerce.project.product.domain.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface OptionRepository : JpaRepository<Option, Long> {


    //product에 해당하는 option 찾기 (부모 option not null, optionId로 정렬)
    @Query(value = "select o from Option as o where o.product = :product and o.parentOption is null and o.status = 'ACTIVE' order by o.optionId")
    fun findParentOptionsByProduct(@Param("product") product: Product) : MutableList<Option>

    //부모 option에 해당하는 option 조회
    @Query(value = "select o from Option as o where o.parentOption = :parentOption and o.status = 'ACTIVE'")
    fun findOptionsByParentOption(@Param("parentOption") parentOption : Option?) : MutableList<Option>

    //productId를 통해 부모 option 삭제
    @Modifying
    @Query(value = "delete from Option as o where o.parentOption is null and o.product.productId=:productId")
    fun deleteParentOptionsByProductId(@Param("productId") productId: Long)

    //productId를 통해 자식 option 삭제
    @Modifying
    @Query(value = "delete from Option as o where o.parentOption is not null and o.product.productId=:productId")
    fun deleteChildOptionsByProductId(@Param("productId") productId: Long)

}