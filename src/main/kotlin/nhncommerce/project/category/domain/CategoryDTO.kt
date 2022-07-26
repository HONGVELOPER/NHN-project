package nhncommerce.project.category.domain

import nhncommerce.project.baseentity.Status
import javax.persistence.Column
import javax.persistence.OneToMany

class CategoryDTO (
    var categoryId : Long? = null,

    var name : String? = null,

    var status : Status? = Status.ACTIVE,

    var subCategoies : MutableList<SubCategory>? = ArrayList<SubCategory>()
)