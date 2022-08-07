package nhncommerce.project.util.token

import nhncommerce.project.util.token.domain.StorageToken
import org.springframework.data.jpa.repository.JpaRepository

interface StorageTokenRepository : JpaRepository<StorageToken,Long>{
}