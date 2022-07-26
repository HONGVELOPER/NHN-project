package nhncommerce.project.option

import nhncommerce.project.baseentity.Status
import nhncommerce.project.exception.AlertException
import nhncommerce.project.exception.ErrorMessage
import nhncommerce.project.exception.RedirectException
import nhncommerce.project.option.domain.*
import nhncommerce.project.product.ProductRepository
import nhncommerce.project.util.alert.alertDTO
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class OptionService (
    private val optionRepository: OptionRepository,
    private val optionDetailRepository: OptionDetailRepository,
    private val productRepository: ProductRepository) {

    //상품 옵션(재고) 가져오기
    fun getProductOptionDetails(productId : Long) : List<OptionDetailDTO> {
        val product = productRepository.findById(productId).get()
        val optionDetailList = optionDetailRepository.findOptionDetailsByProduct(product)
        return optionDetailList.map { it.entityToDto() };
    }

    //옵션 초기화
    @Transactional
    fun deleteOptions(productId: Long){
        val product = productRepository.findById(productId).get()
        optionRepository.findParentOptionsByProduct(product).map { it.makeOptionInActive() }
        optionDetailRepository.findOptionDetailsByProduct(product).map { it.makeDetailInActive() }
    }

    //상품 재고, 추가금액 수정
    @Transactional
    fun updateOptionDetail(optionStockDTO: OptionStockDTO) {
        for(i in 0 until optionStockDTO.detailIdList.size){
            val detailId = optionStockDTO.detailIdList[i]
            val detailStock = optionStockDTO.detailStockList[i]
            val detailCharge = optionStockDTO.detailChargeList[i]
            val optionDetail = optionDetailRepository.findById(detailId).get()

            optionDetail.changeStockAndCharge(detailStock, detailCharge)
        }
    }

    // 상품 , 옵션 리스트 가져오기
    fun getProductOptionList(productId : Long) : UpdateOptionDTO{
        val product = productRepository.findById(productId).get()
        val parentOptionList =optionRepository.findParentOptionsByProduct(product)
        val optionTypeList = mutableListOf<Option?>(null, null, null)
        val optionNameList = ArrayList<MutableList<Option>?>()

        //옵션 타입
        for(i in 0 until parentOptionList.size)
            optionTypeList[i] = parentOptionList[i]

        //옵션 명
        for(i in 0..2){
            if (optionTypeList[i] != null){
                val temp = optionRepository.findOptionsByParentOption(optionTypeList[i])
                optionNameList.add(temp)
            } else {
                optionNameList.add(null)
            }
        }
        return UpdateOptionDTO(product.entityToDto(), optionTypeList, optionNameList)
    }


    //옵션 상세 생성
    @Transactional
    fun createOptionDetail(optionListDTO: OptionListDTO) {
        val product = optionListDTO.productDTO?.let { it.dtoToEntity() } ?: throw AlertException(ErrorMessage.EMPTY_PRODUCTDTO)
        val optionList = mutableListOf(mutableListOf<Option?>(),mutableListOf<Option?>(),mutableListOf<Option?>())
        //옵션 종류
        val optionTypeList = mutableListOf<String?>(optionListDTO.option1, optionListDTO.option2, optionListDTO.option3)
        //옵션 명
        val optionNameList = mutableListOf(optionListDTO.option1List, optionListDTO.option2List, optionListDTO.option3List)

        //option 생성
        for(i in 0..2){
            if (!optionTypeList[i].isNullOrEmpty()){
                val optionType = optionRepository.save(Option(parentOption = null, name = optionTypeList[i]!!, status = Status.ACTIVE, product = product))
                for(optionName in optionNameList[i]){
                    val option = optionRepository.save(Option(parentOption = optionType, name = optionName, status = Status.ACTIVE, product = product))
                    optionList[i].add(option)
                }
            }
            if (optionList[i].size == 0)
                optionList[i].add(null)
        }

        //옵션들의 경우의수에 맞게 optionDetail 생성
        for(o1 in 0 until optionList[0].size){
            for(o2 in 0 until optionList[1].size){
                for(o3 in 0 until optionList[2].size){
                    val name = generateDetailName(listOf(optionList[0][o1]?.name, optionList[1][o2]?.name, optionList[2][o3]?.name))
                    val optionDetail = OptionDetail(
                        status = Status.ACTIVE, extraCharge = 0, stock = 0, name = name, product = product,
                        option1 = optionList[0][o1], option2 = optionList[1][o2], option3 = optionList[2][o3]
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
        return if (name == "") "옵션 없음" else name
    }

    fun getOptionDetail(optionDetailId: Long):OptionDetailDTO{
        val optionDetail = optionDetailRepository.findByOptionDetailId(optionDetailId)
        return optionDetail.entityToDto();

    }
}