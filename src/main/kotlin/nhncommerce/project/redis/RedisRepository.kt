package nhncommerce.project.redis

import nhncommerce.project.user.domain.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisRepository : CrudRepository<User, String> {
}