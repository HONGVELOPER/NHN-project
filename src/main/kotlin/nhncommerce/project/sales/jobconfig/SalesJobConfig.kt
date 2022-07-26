package nhncommerce.project.sales.jobconfig

import nhncommerce.project.order.domain.Order
import nhncommerce.project.sales.SalesRepository
import nhncommerce.project.sales.domain.Sales
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.EntityManagerFactory

@Configuration
class SalesJobConfig(
    val jobBuilderFactory: JobBuilderFactory,
    val stepBuilderFactory: StepBuilderFactory,
    val entityManagerFactory: EntityManagerFactory,
    val salesRepository: SalesRepository
) {

    private var quantity : Int = 0
    private var totalAmount : Long = 0

    @Bean
    fun salesJob() : Job {
        return jobBuilderFactory["salesJob"]
            .start(salesStep1())
            .next(salesStep2())
            .build()
    }

    @Bean
    fun salesStep1(): Step {
        return stepBuilderFactory["salesStep1"]
            .chunk<Order, Order>(10)
            .reader(reader())
            .processor(processor())
            .writer(writer())
            .build()
    }

    @Bean
    fun salesStep2() : Step {
        return stepBuilderFactory["salesStep2"]
            .tasklet{ contribution, chunkContext ->
                val sales = Sales(0L, LocalDate.now().minusDays(1),quantity,totalAmount)
                salesRepository.save(sales)
                quantity = 0
                totalAmount = 0
                RepeatStatus.FINISHED
            }
            .build()
    }

    @Bean
    @StepScope
    fun processor(): ItemProcessor<Order, Order> {
        return ItemProcessor<Order, Order> { orders ->
            totalAmount+=orders.price
            quantity+=orders.count
            orders
        }
    }

    private fun writer(): ItemWriter<Order> {
        return ItemWriter<Order> {
        }
    }

    @Bean
    @StepScope
    fun reader(): JpaPagingItemReader<Order> {
        val parameterValues: MutableMap<String, Any> = HashMap()
        val yesterday = LocalDate.now().yesterday()
        val midNight = LocalDate.now().midNight()
        parameterValues["yesterday"] = yesterday
        parameterValues["midNight"] = midNight
        return JpaPagingItemReaderBuilder<Order>()
            .pageSize(10)
            .parameterValues(parameterValues)
            .queryString("SELECT o FROM Order o WHERE o.updatedAt >= :yesterday and o.updatedAt <= :midNight and o.status = 'ACTIVE' ORDER BY order_id ASC")
            .entityManagerFactory(entityManagerFactory)
            .name("JpaPagingItemReader")
            .build()
    }

    fun LocalDate.yesterday() : LocalDateTime{
        val now = LocalDate.now()
        return LocalDateTime.of(now.year,now.month,now.dayOfMonth,0,0).minusDays(1)
    }
    fun LocalDate.midNight() : LocalDateTime{
        val now = LocalDate.now()
        return LocalDateTime.of(now.year,now.month,now.dayOfMonth,0,0)
    }

}