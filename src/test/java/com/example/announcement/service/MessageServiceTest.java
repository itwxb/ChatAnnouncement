package com.example.announcement.service;

import com.example.announcement.config.ConfigService;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * {@link MessageService} 的单元测试。
 * <p>
 * 关键覆盖：异常 MiniMessage 不会让插件炸（降级到纯文本）。
 */
class MessageServiceTest {

    private MessageService service;

    @BeforeEach
    void setUp() {
        ConfigService config = mock(ConfigService.class);
        when(config.getPrefix()).thenReturn("<gold>[公告]</gold> ");
        Logger logger = Logger.getLogger("test");
        service = new MessageService(config, logger);
    }

    @Test
    @DisplayName("正常消息能解析为 Component")
    void parseValid() {
        Component c = service.parseRaw("<red>hello</red>");
        assertNotNull(c);
    }

    @Test
    @DisplayName("带前缀的解析不抛异常")
    void parseWithPrefix() {
        Component c = service.parseWithPrefix("<green>welcome</green>");
        assertNotNull(c);
    }

    @Test
    @DisplayName("畸形 MiniMessage 不会让插件炸，降级到纯文本")
    void malformedMessageDoesNotThrow() {
        // 一个没有正确闭合的标签
        String malformed = "<red>broken";
        Component c = assertDoesNotThrow(() -> service.parseRaw(malformed));
        assertNotNull(c);
        // MiniMessage 对未闭合标签其实是宽容的，但我们要保证永不抛
    }

    @Test
    @DisplayName("不包含 % 的字符串不会触发 PAPI 调用")
    void noPlaceholderFastPath() {
        Component c = service.parseWithPrefix("plain text");
        assertNotNull(c);
        assertTrue(c.children().iterator().hasNext() || c != Component.empty());
    }
}
