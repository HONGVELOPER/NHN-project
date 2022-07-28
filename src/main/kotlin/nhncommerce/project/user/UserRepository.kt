package nhncommerce.project.user

import nhncommerce.project.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.Id

interface UserRepository :JpaRepository<User,Long>{


    fun findByEmail(email:String):User?

//    fun findOne(id: Long):User= findById(id).orElse(null)
}