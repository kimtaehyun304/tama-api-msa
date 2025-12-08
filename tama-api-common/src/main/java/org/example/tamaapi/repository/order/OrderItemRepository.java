package org.example.tamaapi.repository.order;

import org.example.tamaapi.domain.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("select oi from OrderItem oi join fetch oi.order o where oi.id =:orderItemId")
    Optional<OrderItem> findWithOrderById(Long orderItemId);

    @Query("select oi from OrderItem oi join fetch oi.order o where o.paymentId =:paymentId")
    List<OrderItem> findAllWithOrderByPaymentId(String paymentId);

}
