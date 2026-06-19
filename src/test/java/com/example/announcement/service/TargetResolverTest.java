package com.example.announcement.service;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * {@link TargetResolver} 的单元测试：使用 Mock Player 注入 PlayerProvider，
 * 不依赖 MockBukkit / Bukkit.getOnlinePlayers()。
 */
class TargetResolverTest {

    private Player steve;
    private Player alex;
    private TargetResolver resolver;

    @BeforeEach
    void setUp() {
        steve = mock(Player.class);
        when(steve.getName()).thenReturn("Steve");
        alex = mock(Player.class);
        when(alex.getName()).thenReturn("Alex");
        resolver = new TargetResolver(TargetResolver.of(steve, alex));
    }

    @Test
    @DisplayName("null 输入返回 EMPTY")
    void nullInput() {
        assertTrue(resolver.resolve(null).isEmpty());
    }

    @Test
    @DisplayName("空字符串返回 EMPTY")
    void blankInput() {
        assertTrue(resolver.resolve("   ").isEmpty());
    }

    @Test
    @DisplayName("'all'（任意大小写）解析为 ALL")
    void allKeyIsCaseInsensitive() {
        assertEquals(TargetResolver.Result.Type.ALL, resolver.resolve("all").type());
        assertEquals(TargetResolver.Result.Type.ALL, resolver.resolve("ALL").type());
        assertEquals(TargetResolver.Result.Type.ALL, resolver.resolve("All").type());
    }

    @Test
    @DisplayName("'@a' 同样解析为 ALL（类 Minecraft 选择器）")
    void atAResolvesToAll() {
        assertEquals(TargetResolver.Result.Type.ALL, resolver.resolve("@a").type());
        assertEquals(2, resolver.resolve("@a").size());
    }

    @Test
    @DisplayName("存在的玩家返回 SINGLE，包含该玩家")
    void existingPlayerReturnsSingle() {
        var result = resolver.resolve("Steve");
        assertEquals(TargetResolver.Result.Type.SINGLE, result.type());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("玩家名大小写不敏感")
    void playerNameCaseInsensitive() {
        var result = resolver.resolve("ALEX");
        assertEquals(TargetResolver.Result.Type.SINGLE, result.type());
    }

    @Test
    @DisplayName("不存在的玩家返回 EMPTY")
    void missingPlayerReturnsEmpty() {
        var result = resolver.resolve("NotARealPlayer");
        assertTrue(result.isEmpty());
        assertFalse(result.players().iterator().hasNext());
    }
}
