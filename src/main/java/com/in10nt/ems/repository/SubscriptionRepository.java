package com.in10nt.ems.repository;

import com.in10nt.ems.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    
    List<Subscription> findByStatus(Subscription.Status status);
    
    List<Subscription> findByAssignedToId(Long userId);
    
    @Query("SELECT s FROM Subscription s WHERE s.endDate BETWEEN :startDate AND :endDate AND s.status = 'ACTIVE'")
    List<Subscription> findExpiringSoon(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT s FROM Subscription s WHERE s.endDate < :currentDate AND s.status = 'ACTIVE'")
    List<Subscription> findExpired(LocalDate currentDate);
}