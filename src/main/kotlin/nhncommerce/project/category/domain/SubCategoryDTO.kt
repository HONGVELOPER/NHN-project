package nhncommerce.project.category.domain

import nhncommerce.project.baseentity.Status
import javax.persistence.Column
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne


class SubCategoryDTO (
    var subCategoryId : Long? = null,

    var name : String? = null,

    var category : Category? = null,

    var status : Status? = Status.ACTIVE
)