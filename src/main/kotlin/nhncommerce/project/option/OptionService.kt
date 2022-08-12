package nhncommerce.project.option

import nhncommerce.project.baseentity.Status
import nhncommerce.project.option.domain.*
import nhncommerce.project.product.ProductRepository
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class OptionService (
    private val optionRepository: OptionRepository,
    private val optionDetailRepository: OptionDetailRepository,
    private val productRepository: ProductRepository) {

    //상품 옵션(재고) 가져오기
    fun getProductOptionDetails(productId : Long) : List<OptionDetailDTO> {
        val product = productRepository.findById(productId).get()
        val optionDetailList = optionDetailRepository.findOptionDetailsByProduct(product)
        return optionDetailList.map { it.toOptionDetailDTO() };
    }


    //옵션 초기화
    fun deleteOptions(productId: Long){
        //외래키의 연관관계로 자식 옵션 부터 삭제
        optionDetailRepository.deleteOptionDetailsByProductId(productId)
        optionRepository.deleteChildOptionsByProductId(productId)
        optionRepository.deleteParentOptionsByProductId(productId)
    }

    //상품 재고, 추가금액 수정
    fun updateOptionDetail(optionStockDTO: OptionStockDTO) {
        for(i in 0 until optionStockDTO.detailIdList.size){
            val detailId = optionStockDTO.detailIdList[i]
            val detailStock = optionStockDTO.detailStockList[i]
            val detailCharge = optionStockDTO.detailChargeList[i]

            var optionDetail = optionDetailRepository.findById(detailId).get()

            optionDetail.stock = detailStock
            optionDetail.extraCharge = detailCharge
            optionDetailRepository.save(optionDetail)
        }
    }

    // 상품 , 옵션 리스트 가져오기
    fun getProductOptionList(productId : Long) : UpdateOptionDTO{
        val product = productRepository.findById(productId).get()
        var parentOptionList = optionRepository.findOptionsByProductAndParentOptionIsNullOrderByOptionId(product)
        var optionTypeList = mutableListOf<Option?>(null, null, null)
        var optionNameList = ArrayList<MutableList<Option>?>()
        //옵션 타입
        for(i in 0 until parentOptionList.size)
            optionTypeList[i] = parentOptionList[i]

        //옵션 명
        for(i in 0..2){
            if (optionTypeList[i] != null){
                var temp = optionRepository.findOptionsByParentOption(optionTypeList[i])
                optionNameList.add(temp)
            } else {
                optionNameList.add(null)
            }
        }

        return UpdateOptionDTO(product.toProductDTO(), optionTypeList, optionNameList)
    }


    //옵션 상세 생성
    fun createOptionDetail(optionListDTO: OptionListDTO) {
        val product = optionListDTO.productDTO!!.toEntity()
        val optionList = mutableListOf(mutableListOf<Option?>(),mutableListOf<Option?>(),mutableListOf<Option?>())
        //옵션 종류
        val optionTypeList = mutableListOf<String?>(optionListDTO.option1, optionListDTO.option2, optionListDTO.option3)
        //옵션 명
        val optionNameList = mutableListOf(optionListDTO.option1List, optionListDTO.option2List, optionListDTO.option3List)

        //option 생성
        for(i in 0..2){
            if (!optionTypeList[i].isNullOrEmpty()){
                val optionType = optionRepository.save(Option(null, null, optionTypeList[i], Status.ACTIVE, product))
                for(optionName in optionNameList[i]){
                    val option = optionRepository.save(Option(null, optionType, optionName, Status.ACTIVE, product))
                    optionList[i].add(option)
                }
            }
        }

        //옵션이 비어있을 경우 null 넣어주기
        for(i in 0..2){
            if (optionList[i].size == 0)
                optionList[i].add(null)
        }

        //옵션들의 경우의수에 맞게 optionDetail 생성
        for(o1 in 0 until optionList[0].size){
            for(o2 in 0 until (if (optionList[1].size > 0) optionList[1].size else 1)){
                for(o3 in 0 until (if (optionList[2].size > 0) optionList[2].size else 1)){
                    val num = optionList[0].size + optionList[1].size + optionList[2].size
                    val name = generateDetailName(listOf(optionList[0][o1]?.name, optionList[1][o2]?.name, optionList[2][o3]?.name))
                    val optionDetail = OptionDetail(
                        null, Status.ACTIVE, 0, 0, num, name, product,
                        optionList[0][o1], optionList[1][o2], optionList[2][o3]
                    )
                    optionDetailRepository.save(optionDetail)
                }
            }
        }
    }

    fun generateDetailName(optionList : List<String?>) : String{
        var name = ""
        for(i in 0..2){
            if (optionList[i] != null){
                if (i > 0 && optionList[i-1] != null)
                    name += " / "
                name += optionList[i]
            }
        }
        if (name.equals(""))
            return "옵션 없음"
        else
            return name
    }

    fun getOptionDetail(optionDetailId: Long):OptionDetailDTO{
        val optionDetail = optionDetailRepository.findByOptionDetailId(optionDetailId)
        return optionDetail.toOptionDetailDTO();

    }
}