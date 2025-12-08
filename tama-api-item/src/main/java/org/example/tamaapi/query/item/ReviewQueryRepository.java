package org.example.tamaapi.query.item;

import org.example.tamaapi.domain.item.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface ReviewQueryRepository extends JpaRepository<Review, Long> {


    Optional<Review> findByOrderItemId(Long orderItemId);

}
