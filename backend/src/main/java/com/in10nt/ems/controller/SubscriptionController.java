package com.in10nt.ems.controller;

import com.in10nt.ems.model.Subscription;
import com.in10nt.ems.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionRepository subscriptionRepository;
    
    @GetMapping
    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Subscription> getSubscriptionById(@PathVariable Long id) {
        return subscriptionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/user/{userId}")
    public List<Subscription> getSubscriptionsByUser(@PathVariable Long userId) {
        return subscriptionRepository.findByAssignedToId(userId);
    }
    
    @GetMapping("/expiring-soon")
    public List<Subscription> getExpiringSoonSubscriptions() {
        LocalDate now = LocalDate.now();
        LocalDate thirtyDaysFromNow = now.plusDays(30);
        return subscriptionRepository.findExpiringSoon(now, thirtyDaysFromNow);
    }
    
    @GetMapping("/expired")
    public List<Subscription> getExpiredSubscriptions() {
        return subscriptionRepository.findExpired(LocalDate.now());
    }
    
    @PostMapping
    public Subscription createSubscription(@RequestBody Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Subscription> updateSubscription(@PathVariable Long id, @RequestBody Subscription subscriptionDetails) {
        return subscriptionRepository.findById(id)
                .map(subscription -> {
                    subscription.setServiceName(subscriptionDetails.getServiceName());
                    subscription.setProvider(subscriptionDetails.getProvider());
                    subscription.setDescription(subscriptionDetails.getDescription());
                    subscription.setStartDate(subscriptionDetails.getStartDate());
                    subscription.setEndDate(subscriptionDetails.getEndDate());
                    subscription.setCost(subscriptionDetails.getCost());
                    subscription.setStatus(subscriptionDetails.getStatus());
                    subscription.setAssignedTo(subscriptionDetails.getAssignedTo());
                    subscription.setNotes(subscriptionDetails.getNotes());
                    subscription.setUpdatedAt(LocalDateTime.now());
                    return ResponseEntity.ok(subscriptionRepository.save(subscription));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubscription(@PathVariable Long id) {
        return subscriptionRepository.findById(id)
                .map(subscription -> {
                    subscriptionRepository.delete(subscription);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}