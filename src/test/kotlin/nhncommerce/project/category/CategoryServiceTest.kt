package nhncommerce.project.category

import nhncommerce.project.baseentity.Status
import nhncommerce.project.category.domain.Category
import nhncommerce.project.option.OptionDetailRepository
import nhncommerce.project.option.domain.OptionDetail
import nhncommerce.project.product.ProductRepository
import nhncommerce.project.product.ProductService
import nhncommerce.project.product.domain.Product
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback

@SpringBootTest
class CategoryServiceTest {
    @Autowired
    private lateinit var productService : ProductService
    @Autowired
    private lateinit var productRepository: ProductRepository
    @Autowired
    private lateinit var categoryService: CategoryService
    @Autowired
    private lateinit var categoryRepository: CategoryRepository
    @Autowired
    private lateinit var optionDetailRepository: OptionDetailRepository


    /**
     * 항상 카테고리 생성 먼저하고 상품 생성하기
     */

    @Test
    @Rollback(value = false)
    fun insertCategory() {
        val categoryList = listOf<String>(
                "Music",
                "Photo",
                "Concert",
                "Living",
                "Beauty",
                "Stationery",
                "Fashion"
        )

        val subCategoryList = listOf<List<String>>(
                listOf("CD", "DVD","etc"),
                listOf("Printed", "Photo Book", "etc"),
                listOf("official Fanlight", "Concert Goods", "etc"),
                listOf("Home", "F&B", "Tech"),
                listOf("Skin Care", "Make Up", "Perfume"),
                listOf("Note", "Office", "Pen"),
                listOf("Clothing", "Acc", "Jewelry")
        )


        for(i in 1..7){
            val category = Category(
                categoryId = i.toLong(),
                name = categoryList[i - 1],
                parentCategory = null,
                status = Status.ACTIVE
            )
            categoryRepository.save(category)
        }
        for(i in 1 .. 7){
            val category = categoryRepository.findById(i.toLong()).get()

            for(j in 1..3){
                val subCategory = Category(
                    categoryId = null,
                    name = "${subCategoryList[i-1][j-1]}",
                    parentCategory = category,
                    status = Status.ACTIVE
                )
                categoryRepository.save(subCategory)
            }
        }
    }
    @Test
    @Rollback(value = false)
    fun insertProduct() {
        val imgArr = listOf<String>(
            "https://cdn.pixabay.com/photo/2013/07/13/10/51/football-157930_1280.png",
            "https://cdn.pixabay.com/photo/2013/07/12/14/07/basketball-147794_1280.png",
            "https://cdn.pixabay.com/photo/2012/04/05/01/45/baseball-25761_1280.png"
        )
        for(k in 1 .. 100){
            var count = 8
            for(i in 1..7){
                for(j in 1 .. 3){
                    val category = categoryRepository.findById((count).toLong()).get()


                    val product = Product(
                        null,
                        Status.ACTIVE,
                        "test" + i,
                        i * 1000,
                        "simple description" + i,
                        "detail" + i,
                        imgArr[j - 1],
                        0,
                        0F,
                        category
                    )
                    productRepository.save(product)

                    val optionDetail = OptionDetail(
                        optionDetailId = null,
                        status = Status.ACTIVE,
                        extraCharge = 0,
                        stock = 0,
                        num = 3,
                        name = "옵션 없음",
                        product = product,
                        option1 = null,
                        option2 = null,
                        option3 = null
                    )

                    optionDetailRepository.save(optionDetail)

                    count++
                }
            }
        }
    }
}