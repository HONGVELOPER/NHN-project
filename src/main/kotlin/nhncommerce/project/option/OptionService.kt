package nhncommerce.project.option

import nhncommerce.project.baseentity.Status
import nhncommerce.project.option.domain.*
import nhncommerce.project.product.ProductRepository
import nhncommerce.project.product.domain.Product
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class OptionService (
    private val optionRepository: OptionRepository,
    private val optionDetailRepository: OptionDetailRepository,
    private val productRepository: ProductRepository) {

    fun getOptions() : List<OptionDTO>{
        val options = optionRepository.findAll()
        return options.map{it.toOptionDTO()}
    }

    fun getOption(id : Long) : OptionDTO  = optionRepository.findById(id).get().toOptionDTO()


    //옵션명 생성
    fun createOption(optionDTO: OptionDTO) : OptionDTO {
        val option = optionRepository.save(optionDTO.toEntity())
        return option.toOptionDTO()
    }

    //옵션명 수정
    /**
     *
     */
    fun updateOptionTitle(id : Long, optionDTO: OptionDTO) : OptionDTO{
        val optionTitle : Option = optionRepository.findById(id).get()
        optionTitle.name = optionDTO.name

        optionRepository.save(optionTitle)
        return optionTitle.toOptionDTO()
    }

    //옵션 수정
//    fun updateOption(id : Long, optionDTO : OptionDTO) : OptionDTO {
//        val option = optionRepository.findById(id).get()
//        option.name = optionDTO.name
//        option.parentOption = optionDTO.parentOption
//
//        optionRepository.save(option)
//        return option.toOptionDTO()
//    }

    //옵션 삭제
    fun deleteOption(id : Long) {
        optionRepository.deleteById(id)
    }

    //상품 가져오기 (임시)
    fun getProductList() : List<Product> {
        val products = productRepository.findAll()
        return products
    }

    //상품 옵션(재고) 가져오기
    fun getProductOptionDetails(productId : Long) : List<OptionDetailDTO> {
        val product = productRepository.findById(productId).get()
        val optionDetailList = optionDetailRepository.findOptionDetailsByProduct(product)
        return optionDetailList.map { it.toOptionDetailDTO() };
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
        var optionTypeList = Array<Option?>(3, {null})
        for(i in 0 until parentOptionList.size)
            optionTypeList[i] = parentOptionList[i]
        var optionNameList = ArrayList<MutableList<Option>?>()

        for(i in 0..2){
            if (optionTypeList[i] != null){
                var temp = optionRepository.findOptionsByParentOption(optionTypeList[i])
                optionNameList.add(temp)
            } else {
                optionNameList.add(null)
            }
        }
        var updateOptionDTO = UpdateOptionDTO(product, optionTypeList, optionNameList)
        return updateOptionDTO
    }

    //옵션 상세 생성
    fun createOptionDetail(optionListDTO: OptionListDTO) {
        //상품 생성
        val product = optionListDTO.product
        //옵션 생성
        val option1List = ArrayList<Option?>()
        val option2List = ArrayList<Option?>()
        val option3List = ArrayList<Option?>()

        if (!(optionListDTO.option1.isNullOrEmpty())) {
            //옵션 종류 생성
            val option1 = optionRepository.save(Option(null, null, optionListDTO.option1, Status.ACTIVE, product))
            for(s in optionListDTO.option1List) {
                //옵션명 생성
                val option = optionRepository.save(Option(null, option1, s, Status.ACTIVE, product))
                option1List.add(option)
            }
        }
        if (!(optionListDTO.option2.isNullOrEmpty())) {
            val option2 = optionRepository.save(Option(null, null, optionListDTO.option2, Status.ACTIVE, product))
            for(s in optionListDTO.option2List) {
                val option = optionRepository.save(Option(null, option2, s, Status.ACTIVE, product))
                option2List.add(option)
            }
        }
        if (!optionListDTO.option3.isNullOrEmpty()) {
            val option3 = optionRepository.save(Option(null, null, optionListDTO.option3, Status.ACTIVE, product))
            for(s in optionListDTO.option3List){
                val option = optionRepository.save(Option(null, option3, s, Status.ACTIVE, product))
                option3List.add(option)
            }
        }
        //옵션 디테일 생성
        if (option1List.size == 0)
            option1List.add(null)
        if (option2List.size == 0)
            option2List.add(null)
        if (option3List.size == 0)
            option3List.add(null)

        for(o1 in 0 until option1List.size){
            for(o2 in 0 until (if (option2List.size > 0) option2List.size else 1)){
                for(o3 in 0 until (if (option3List.size > 0) option3List.size else 1)){
                    val num = option1List.size + option2List.size + option3List.size
                    val name = "${option1List[o1]?.name?:""} / ${option2List[o2]?.name?:""} / ${option3List[o3]?.name?:""}"
                    val optionDetail = OptionDetail(
                        null,
                        Status.ACTIVE,
                        0,
                        0,
                        num,
                        name,
                        product,
                        option1List[o1],
                        option2List[o2],
                        option3List[o3]
                    )
                    optionDetailRepository.save(optionDetail)
                }
            }
        }

    }


}