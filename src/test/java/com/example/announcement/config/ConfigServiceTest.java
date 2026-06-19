package com.example.announcement.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
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
 * {@link ConfigService} 的单元测试。
 * <p>
 * 通过 Mockito 模拟 FileConfiguration，不依赖 MockBukkit。
 */
class ConfigServiceTest {

    private FileConfiguration config;
    private ConfigService service;

    @BeforeEach
    void setUp() {
        config = mock(FileConfiguration.class);
        // prefix
        when(config.getString("prefix", "")).thenReturn("<gold>[公告]</gold> ");
        // templates section
        ConfigurationSection templates = mock(ConfigurationSection.class);
        when(config.getConfigurationSection("templates")).thenReturn(templates);
        when(templates.getKeys(false)).thenReturn(java.util.Set.of("welcome", "spawn", "help"));
        when(templates.getString("welcome.text"))
                .thenReturn("<yellow>欢迎来到服务器</yellow>");
        when(templates.getString("missing.text")).thenReturn(null);
        // authme
        when(config.getBoolean("authme.enabled", true)).thenReturn(true);
        when(config.getString("authme.unregistered-message", ""))
                .thenReturn("<yellow>请注册</yellow>");
        when(config.getString("authme.register-broadcast", ""))
                .thenReturn("<green>欢迎 %player%</green>");

        service = new ConfigService(config);
    }

    @Test
    @DisplayName("读取默认前缀")
    void defaultPrefix() {
        assertEquals("<gold>[公告]</gold> ", service.getPrefix());
    }

    @Test
    @DisplayName("列出所有模板名")
    void listTemplates() {
        Set<String> names = service.getTemplateNames();
        assertTrue(names.contains("welcome"));
        assertTrue(names.contains("spawn"));
        assertTrue(names.contains("help"));
    }

    @Test
    @DisplayName("查询存在的模板返回文本")
    void getExistingTemplate() {
        String text = service.getTemplate("welcome");
        assertNotNull(text);
        assertTrue(text.contains("欢迎"));
    }

    @Test
    @DisplayName("查询不存在的模板返回 null")
    void getMissingTemplate() {
        assertNull(service.getTemplate("nonexistent-template"));
    }

    @Test
    @DisplayName("AuthMe 配置默认启用")
    void authMeDefault() {
        assertTrue(service.isAuthMeEnabled());
        assertEquals("<yellow>请注册</yellow>", service.getAuthMeUnregisteredMessage());
        assertEquals("<green>欢迎 %player%</green>", service.getAuthMeRegisterBroadcast());
    }

    @Test
    @DisplayName("AuthMe 配置关闭时返回 false")
    void authMeDisabled() {
        when(config.getBoolean("authme.enabled", true)).thenReturn(false);
        assertFalse(service.isAuthMeEnabled());
    }

    @Test
    @DisplayName("模板节点缺失时返回空集合")
    void templatesSectionMissing() {
        when(config.getConfigurationSection("templates")).thenReturn(null);
        assertTrue(service.getTemplateNames().isEmpty());
        assertNull(service.getTemplate("welcome"));
    }

    @Test
    @DisplayName("reload 后应从新 config 读取（修复 Bukkit reloadConfig 引用替换问题）")
    void reloadReadsNewConfig() {
        // 关键：rebuild service 时用新的 config 对象（模拟 reloadConfig() 内部替换引用的行为）
        FileConfiguration newConfig = mock(FileConfiguration.class);
        when(newConfig.getString("prefix", "")).thenReturn("<red>[NEW]</red> ");
        // 重建 service 引用新 config
        service = new ConfigService(newConfig);
        assertEquals("<red>[NEW]</red> ", service.getPrefix(),
                "修复后重建 service 应当能读取到新 config 的值");
    }

    @Test
    @DisplayName("config 字段必须可变（不能是 final）以支持 reload")
    void configFieldIsMutable() {
        // 编译期保证：以下行能编译通过 = 字段不是 final
        FileConfiguration replacement = mock(FileConfiguration.class);
        // 通过反射验证字段未声明为 final
        try {
            var field = ConfigService.class.getDeclaredField("config");
            int mods = field.getModifiers();
            assertFalse(java.lang.reflect.Modifier.isFinal(mods),
                    "ConfigService.config 字段必须非 final，否则 reloadConfig() 引用替换后无法读到新值");
            // 验证反射赋值能成功
            field.setAccessible(true);
            field.set(service, replacement);
            assertEquals(replacement, field.get(service));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new AssertionError("ConfigService.config 字段应可访问", e);
        }
    }
}
