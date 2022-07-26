package nhncommerce.project.baseentity

import lombok.Getter
import lombok.Setter
import org.hibernate.annotations.CreationTimestamp
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.EntityListeners
import javax.persistence.MappedSuperclass

@Getter
@Setter
@MappedSuperclass
@EntityListeners(value = [AuditingEntityListener::class])
abstract class BaseEntity {

    @CreationTimestamp
    var createdAt: LocalDateTime=LocalDateTime.now()

    @LastModifiedDate
    var updatedAt: LocalDateTime=LocalDateTime.now()

}