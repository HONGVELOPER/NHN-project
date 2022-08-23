package nhncommerce.project.sales.domain

import nhncommerce.project.exception.AlertException
import nhncommerce.project.exception.ErrorMessage
import nhncommerce.project.sales.jobconfig.SalesJobConfig
import org.slf4j.LoggerFactory
import org.springframework.batch.core.JobExecutionException
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.net.InetAddress
import java.time.LocalDateTime

@Component
class Scheduler(
    val jobLauncher: JobLauncher,
    val salesJob: SalesJobConfig
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${server.cloudIp}")
    private val cloudIp = ""

    @Scheduled(cron = "0 0 05 * * *") //매일 새벽 5시 실행
    fun executeJob() {
        val serverIp = InetAddress.getLocalHost().hostAddress
        if(serverIp == cloudIp){
            try {
                jobLauncher.run(
                    salesJob.salesJob(), JobParametersBuilder()
                        .addString("datetime", LocalDateTime.now().toString()) //job의 유일한 id로 같으면 안되기 때문
                        .toJobParameters() // job parameter 설정
                )
            } catch (ex: JobExecutionException) {
                log.info("일 매출 계산 스케줄 작업 실패")
            }
        }

    }

}