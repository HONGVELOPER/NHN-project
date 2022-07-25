package nhncommerce.project.category.domain

import nhncommerce.project.config.Status
import nhncommerce.project.config.Status.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import javax.persistence.*


@Entity
@Table(name = "category")
class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var categoryId : Long? = null

    @Column(nullable = false)
    var name : String? = null

    @OneToMany(mappedBy = "category")
    var subCategoies : MutableList<SubCategory>? = ArrayList<SubCategory>()

    @Column(nullable = false)
    var status : Status? = ACTIVE

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
}