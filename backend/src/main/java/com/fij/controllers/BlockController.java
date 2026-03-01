package com.fij.controllers;

import com.fij.models.Block;
import com.fij.services.BlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blocks")
@RequiredArgsConstructor
public class BlockController {

    private final BlockService blockService;

    @PostMapping("/{userId}")
    public ResponseEntity<Void> blockUser(@PathVariable Long userId) {
        blockService.blockUser(userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> unblockUser(@PathVariable Long userId) {
        blockService.unblockUser(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Block>> getMyBlockedUsers() {
        return ResponseEntity.ok(blockService.getMyBlockedUsers());
    }
}
