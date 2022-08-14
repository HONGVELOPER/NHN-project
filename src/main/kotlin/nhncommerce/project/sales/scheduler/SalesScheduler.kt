package nhncommerce.project.sales.domain

import nhncommerce.project.sales.jobconfig.SalesJobConfig
import org.springframework.batch.core.JobExecutionException
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class Scheduler(
    val jobLauncher: JobLauncher,
    val salesJob: SalesJobConfig
) {
//        @Scheduled(cron = "0 00 05 * * *") //매일 새벽 5시 실행
    @Scheduled(fixedDelay = 30 * 1000L) // 30초 마다 실행
    fun executeJob() {
        try {
            jobLauncher.run(
                salesJob.salesJob(), JobParametersBuilder()
                    .addString("datetime", LocalDateTime.now().toString()) //job의 유일한 id로 같으면 안되기 때문
                    .toJobParameters() // job parameter 설정
            )
        } catch (ex: JobExecutionException) {
            System.out.println(ex.message)
            ex.printStackTrace()
        }
    }

}