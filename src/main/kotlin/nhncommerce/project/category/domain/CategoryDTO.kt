package nhncommerce.project.category.domain

import nhncommerce.project.baseentity.Status

class CategoryDTO (
    var categoryId : Long? = null,

    var name : String? = null,

    var status : Status? = Status.ACTIVE,

    var subCategories : MutableList<SubCategory>? = ArrayList<SubCategory>()
)