package com.fij.services;

import com.fij.models.Block;
import com.fij.models.User;
import com.fij.repositories.BlockRepository;
import com.fij.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlockService {

    private final UserRepository userRepository;
    private final BlockRepository blockRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void blockUser(Long blockedUserId) {
        User blocker = getCurrentUser();
        
        User blockedUser = userRepository.findById(blockedUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (blocker.getId().equals(blockedUserId)) {
            throw new RuntimeException("Cannot block yourself");
        }
        
        if (blockRepository.existsByBlockerIdAndBlockedId(blocker.getId(), blockedUserId)) {
            throw new RuntimeException("User already blocked");
        }
        
        Block block = new Block();
        block.setBlocker(blocker);
        block.setBlocked(blockedUser);
        
        blockRepository.save(block);
    }

    public void unblockUser(Long blockedUserId) {
        User blocker = getCurrentUser();
        
        Block block = blockRepository.findByBlockerIdAndBlockedId(blocker.getId(), blockedUserId)
                .orElseThrow(() -> new RuntimeException("Block not found"));
        
        blockRepository.delete(block);
    }

    public List<Block> getMyBlockedUsers() {
        return blockRepository.findByBlockerId(getCurrentUser().getId());
    }

    public boolean isBlocked(Long userId1, Long userId2) {
        return blockRepository.existsByBlockerIdAndBlockedId(userId1, userId2);
    }
}
