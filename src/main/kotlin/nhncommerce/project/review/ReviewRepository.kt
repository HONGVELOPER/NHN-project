package nhncommerce.project.review

import nhncommerce.project.review.domain.Review
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface ReviewRepository: JpaRepository<Review, Long>, QuerydslPredicateExecutor<Review> {
}