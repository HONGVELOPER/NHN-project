package nhncommerce.project.redis

import nhncommerce.project.redis.domain.Event
import org.springframework.data.jpa.repository.JpaRepository

interface EventRepository : JpaRepository<Event, Long> {
}