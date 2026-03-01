package com.fij.services;

import com.fij.models.*;
import com.fij.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlockServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BlockRepository blockRepository;

    @InjectMocks
    private BlockService blockService;

    private User blocker;
    private User blocked;

    @BeforeEach
    void setUp() {
        blocker = new User();
        blocker.setId(1L);
        blocker.setEmail("blocker@test.com");
        blocker.setRoles(Set.of(Role.ROLE_CANDIDATE));

        blocked = new User();
        blocked.setId(2L);
        blocked.setEmail("blocked@test.com");
        blocked.setRoles(Set.of(Role.ROLE_RECRUITER));

        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("blocker@test.com", null, List.of())
        );
    }

    @Test
    void blockUser_Success_CreatesBlock() {
        when(userRepository.findByEmail("blocker@test.com")).thenReturn(Optional.of(blocker));
        when(userRepository.findById(2L)).thenReturn(Optional.of(blocked));
        when(blockRepository.existsByBlockerIdAndBlockedId(1L, 2L)).thenReturn(false);
        when(blockRepository.save(any(Block.class))).thenAnswer(i -> i.getArgument(0));

        assertDoesNotThrow(() -> blockService.blockUser(2L));

        verify(blockRepository).save(any(Block.class));
    }

    @Test
    void blockUser_SelfBlock_ThrowsException() {
        when(userRepository.findByEmail("blocker@test.com")).thenReturn(Optional.of(blocker));

        assertThrows(RuntimeException.class, () -> blockService.blockUser(1L));
    }

    @Test
    void blockUser_AlreadyBlocked_ThrowsException() {
        when(userRepository.findByEmail("blocker@test.com")).thenReturn(Optional.of(blocker));
        when(userRepository.findById(2L)).thenReturn(Optional.of(blocked));
        when(blockRepository.existsByBlockerIdAndBlockedId(1L, 2L)).thenReturn(true);

        assertThrows(RuntimeException.class, () -> blockService.blockUser(2L));
    }

    @Test
    void unblockUser_Success_DeletesBlock() {
        Block block = new Block();
        block.setBlocker(blocker);
        block.setBlocked(blocked);

        when(userRepository.findByEmail("blocker@test.com")).thenReturn(Optional.of(blocker));
        when(blockRepository.findByBlockerIdAndBlockedId(1L, 2L)).thenReturn(Optional.of(block));

        blockService.unblockUser(2L);

        verify(blockRepository).delete(block);
    }

    @Test
    void getMyBlockedUsers_ReturnsList() {
        Block block = new Block();
        block.setBlocker(blocker);
        block.setBlocked(blocked);

        when(userRepository.findByEmail("blocker@test.com")).thenReturn(Optional.of(blocker));
        when(blockRepository.findByBlockerId(1L)).thenReturn(List.of(block));

        var result = blockService.getMyBlockedUsers();

        assertEquals(1, result.size());
    }

    @Test
    void isBlocked_ReturnsTrue() {
        when(blockRepository.existsByBlockerIdAndBlockedId(1L, 2L)).thenReturn(true);

        assertTrue(blockService.isBlocked(1L, 2L));
    }

    @Test
    void isBlocked_ReturnsFalse() {
        when(blockRepository.existsByBlockerIdAndBlockedId(1L, 2L)).thenReturn(false);

        assertFalse(blockService.isBlocked(1L, 2L));
    }
}
