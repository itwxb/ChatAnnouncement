package com.example.announcement.service;

import com.example.announcement.config.ConfigService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * {@link TemplateService} 的单元测试。
 */
class TemplateServiceTest {

    private TemplateService service;

    @BeforeEach
    void setUp() {
        ConfigService config = mock(ConfigService.class);
        when(config.getTemplate("welcome")).thenReturn("欢迎 %player% 加入！");
        when(config.getTemplate("empty")).thenReturn(null);
        when(config.getTemplateNames()).thenReturn(Set.of("welcome", "help"));
        service = new TemplateService(config);
    }

    @Test
    @DisplayName("resolve 替换 %player% 占位符")
    void resolveReplacesPlayer() {
        String result = service.resolve("welcome", "Steve");
        assertEquals("欢迎 Steve 加入！", result);
    }

    @Test
    @DisplayName("resolve 在 playerName=null 时保留占位符")
    void resolveNullPlayerKeepsPlaceholder() {
        String result = service.resolve("welcome", null);
        assertEquals("欢迎 %player% 加入！", result);
    }

    @Test
    @DisplayName("不存在的模板返回 null")
    void resolveMissingReturnsNull() {
        assertNull(service.resolve("does-not-exist", "Steve"));
    }

    @Test
    @DisplayName("exists 检查存在性")
    void exists() {
        assertTrue(service.exists("welcome"));
        assertFalse(service.exists("does-not-exist"));
    }

    @Test
    @DisplayName("list 列出所有模板")
    void list() {
        Set<String> names = service.list();
        assertNotNull(names);
        assertTrue(names.contains("welcome"));
        assertTrue(names.contains("help"));
    }
}
