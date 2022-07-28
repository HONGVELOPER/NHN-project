package nhncommerce.project.product

import nhncommerce.project.product.domain.Product
import nhncommerce.project.product.domain.ProductDTO
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

@Service
class ProductService(
    val productRepository: ProductRepository
) {

    /**
     * 상품 전제 조회
     */
    fun getProducts(): MutableList<Product> {
        return productRepository.findAll()
    }


    fun validate(productDTO: ProductDTO, response: HttpServletResponse,session : HttpSession) : Boolean{
        runCatching {
            productDTO.productValidate()
        }.onFailure {
            session.setAttribute("productName",productDTO.productName)
            session.setAttribute("price",productDTO.price)
            session.setAttribute("briefDescription",productDTO.briefDescription)
            session.setAttribute("detailDescription",productDTO.detailDescription)
        }
        return true
    }

}