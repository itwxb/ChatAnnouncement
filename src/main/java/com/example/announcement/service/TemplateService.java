package com.example.announcement.service;

import com.example.announcement.config.ConfigService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * 模板服务：统一处理模板的查询、占位符替换与存在性检查。
 */
public final class TemplateService {

    private final ConfigService config;

    public TemplateService(@NotNull ConfigService config) {
        this.config = config;
    }

    /**
     * 解析一个模板，将 {@code %player%} 替换为实际玩家名。
     *
     * @param name       模板名
     * @param playerName 玩家名（可为 null）
     * @return 解析后的模板文本；模板不存在返回 null
     */
    @Nullable
    public String resolve(@NotNull String name, @Nullable String playerName) {
        String template = config.getTemplate(name);
        if (template == null) {
            return null;
        }
        if (playerName != null) {
            return template.replace("%player%", playerName);
        }
        return template;
    }

    /** 模板是否存在。 */
    public boolean exists(@NotNull String name) {
        return config.getTemplate(name) != null;
    }

    /** 列出所有模板名。 */
    @NotNull
    public Set<String> list() {
        return config.getTemplateNames();
    }
}
