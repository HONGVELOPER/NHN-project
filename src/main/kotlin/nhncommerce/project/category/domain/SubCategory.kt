package nhncommerce.project.category.domain

import nhncommerce.project.config.Status
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "sub_category")
class SubCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var subCategoryId : Long? = null

    @Column(nullable = false)
    var name : String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    var category : Category? = null

    @Column(nullable = false)
    var status : Status? = Status.ACTIVE

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
}