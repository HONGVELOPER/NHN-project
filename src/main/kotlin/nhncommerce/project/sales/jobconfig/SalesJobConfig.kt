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

    var quantity : Int = 0
    var totalAmount : Long = 0

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
                val sales = Sales(null, LocalDate.now().minusDays(1),quantity,totalAmount)
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
        return object : ItemProcessor<Order, Order> {
            override fun process(orders: Order): Order {
                totalAmount+=orders.price!!
                quantity+=1
                return orders
            }
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
        var now = LocalDate.now()
        var start = LocalDateTime.of(now.year,now.month,now.dayOfMonth,0,0).minusDays(1)
        var end = LocalDateTime.of(now.year,now.month,now.dayOfMonth,0,0)
        parameterValues.put("start",start)
        parameterValues.put("end",end)
        return JpaPagingItemReaderBuilder<Order>()
            .pageSize(10)
            .parameterValues(parameterValues)
            .queryString("SELECT o FROM Order o WHERE o.updatedAt >= :start and o.updatedAt <= :end and o.status = 'ACTIVE' ORDER BY order_id ASC")
            .entityManagerFactory(entityManagerFactory)
            .name("JpaPagingItemReader")
            .build()
    }

}