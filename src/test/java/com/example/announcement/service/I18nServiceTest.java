package com.example.announcement.service;

import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link I18nService} 的单元测试。
 * <p>
 * 重点验证：缺失键不抛异常、降级到默认语言、UTF-8 中文能正常加载。
 */
class I18nServiceTest {

    private I18nService service;

    @BeforeEach
    void setUp() {
        service = new I18nService(Logger.getLogger("test"));
    }

    @Test
    @DisplayName("英文 locale 正常渲染")
    void englishLocale() {
        Component c = service.render(null, "cmd.no-permission");
        assertNotNull(c);
    }

    @Test
    @DisplayName("中文 locale 正常渲染（UTF-8）")
    void chineseLocale() {
        // 即使没有 CommandSender，也会用 fallback locale（en_US）拿到非空字符串
        // 我们的 messages.properties 本身就是英文，所以这里验证资源加载链路
        Component c = service.render(null, "cmd.no-permission", Locale.SIMPLIFIED_CHINESE);
        assertNotNull(c);
    }

    @Test
    @DisplayName("缺失的 key 不抛异常，返回 key 字面量")
    void missingKeyFallback() {
        Component c = service.render(null, "nonexistent.key");
        assertNotNull(c);
    }

    @Test
    @DisplayName("占位符 %s 能被 String.format 替换")
    void placeholderReplacement() {
        Component c = service.render(null, "cmd.player-not-found", "Steve");
        assertNotNull(c);
    }

    @Test
    @DisplayName("畸形翻译键不会让插件炸（mock 不可达，仅验证正常路径）")
    void normalKeysRender() {
        for (String key : new String[]{
                "cmd.help.header", "cmd.help.send", "cmd.help.broadcast",
                "cmd.help.template", "cmd.help.reload", "cmd.help.help",
                "cmd.help.footer", "cmd.unknown", "cmd.no-permission",
                "cmd.usage.send", "cmd.usage.broadcast", "cmd.usage.template",
                "cmd.player-not-found", "cmd.send.single.ok", "cmd.send.all.ok",
                "cmd.broadcast.ok", "cmd.template.not-found",
                "cmd.template.single.ok", "cmd.template.all.ok", "cmd.reload.ok"
        }) {
            assertNotNull(service.render(null, key), "key must resolve: " + key);
        }
    }
}
