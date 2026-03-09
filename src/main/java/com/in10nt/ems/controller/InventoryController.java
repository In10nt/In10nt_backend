package com.in10nt.ems.controller;

import com.in10nt.ems.model.Inventory;
import com.in10nt.ems.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryRepository inventoryRepository;
    
    @GetMapping
    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }
    
    @PostMapping
    public Inventory createInventory(@RequestBody Inventory inventory) {
        return inventoryRepository.save(inventory);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Inventory> updateInventory(@PathVariable Long id, @RequestBody Inventory details) {
        return inventoryRepository.findById(id)
                .map(inventory -> {
                    inventory.setItemName(details.getItemName());
                    inventory.setCategory(details.getCategory());
                    inventory.setQuantity(details.getQuantity());
                    inventory.setUnitPrice(details.getUnitPrice());
                    inventory.setDescription(details.getDescription());
                    inventory.setUpdatedAt(LocalDateTime.now());
                    return ResponseEntity.ok(inventoryRepository.save(inventory));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInventory(@PathVariable Long id) {
        return inventoryRepository.findById(id)
                .map(inventory -> {
                    inventoryRepository.delete(inventory);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
