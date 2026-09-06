package com.example.order.repository;

import com.example.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    boolean existsByOrderNumber(String orderNumber);

    @Query("""
            SELECT o FROM Order o
            WHERE (:userId IS NULL OR o.userId = :userId)
              AND (:status IS NULL OR o.status = :status)
            """)
    Page<Order> findByFilters(@Param("userId") Long userId,
                              @Param("status") String status,
                              Pageable pageable);
}