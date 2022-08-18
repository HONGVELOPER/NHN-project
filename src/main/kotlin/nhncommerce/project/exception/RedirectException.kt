package nhncommerce.project.exception

import nhncommerce.project.util.alert.alertDTO

class RedirectException(
    val alertDTO: alertDTO
) : RuntimeException() {}