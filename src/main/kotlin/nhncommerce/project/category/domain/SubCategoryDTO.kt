package nhncommerce.project.category.domain

import nhncommerce.project.baseentity.Status


class SubCategoryDTO (
    var subCategoryId : Long? = null,

    var name : String? = null,

    var category : Category? = null,

    var status : Status? = Status.ACTIVE
)