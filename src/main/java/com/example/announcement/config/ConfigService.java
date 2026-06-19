package com.example.announcement.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 类型安全的配置访问层。
 * <p>
 * 所有原始配置键的访问都收敛到此处，避免业务代码使用裸的 {@code plugin.getConfig().getString(...)}
 * 拼接路径带来的脆弱性。同时在初始化时对配置做基础校验。
 * <p>
 * 接受 {@link FileConfiguration} 而非 {@link Plugin}，便于在单元测试中传入 mock。
 */
public final class ConfigService {

    private static final String KEY_PREFIX = "prefix";
    private static final String KEY_TEMPLATES = "templates";
    private static final String KEY_AUTHME_ENABLED = "authme.enabled";
    private static final String KEY_AUTHME_UNREGISTERED = "authme.unregistered-message";
    private static final String KEY_AUTHME_REGISTER_BROADCAST = "authme.register-broadcast";

    private final Plugin plugin; // nullable in tests
    private FileConfiguration config;

    public ConfigService(@NotNull Plugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    /** 测试用：直接传入配置。 */
    public ConfigService(@NotNull FileConfiguration config) {
        this.plugin = null;
        this.config = config;
    }

    /** 重新加载并校验配置（仅在持有 plugin 时有效）。 */
    public void reload() {
        if (plugin != null) {
            plugin.reloadConfig();
            this.config = plugin.getConfig(); // 重新拿引用（关键修复）
        }
        validate();
    }

    /** 公告前缀。 */
    @NotNull
    public String getPrefix() {
        String prefix = config.getString(KEY_PREFIX, "");
        return prefix == null ? "" : prefix;
    }

    /**
     * 读取指定模板的文本。
     *
     * @param name 模板名
     * @return 模板文本；不存在返回 {@code null}
     */
    @Nullable
    public String getTemplate(@NotNull String name) {
        ConfigurationSection section = config.getConfigurationSection(KEY_TEMPLATES);
        if (section == null) {
            return null;
        }
        return section.getString(name + ".text");
    }

    /** 所有已定义的模板名（按配置文件顺序）。 */
    @NotNull
    public Set<String> getTemplateNames() {
        ConfigurationSection section = config.getConfigurationSection(KEY_TEMPLATES);
        if (section == null) {
            return Collections.emptySet();
        }
        return new LinkedHashSet<>(section.getKeys(false));
    }

    public boolean isAuthMeEnabled() {
        return config.getBoolean(KEY_AUTHME_ENABLED, true);
    }

    @NotNull
    public String getAuthMeUnregisteredMessage() {
        return config.getString(KEY_AUTHME_UNREGISTERED, "");
    }

    @NotNull
    public String getAuthMeRegisterBroadcast() {
        return config.getString(KEY_AUTHME_REGISTER_BROADCAST, "");
    }

    /** 基础校验：模板节点必须为 Section。 */
    private void validate() {
        Object templates = config.get(KEY_TEMPLATES);
        if (templates != null && !(templates instanceof ConfigurationSection)) {
            log("'templates' 节点类型错误，应为 Section");
        }
    }

    private void log(String message) {
        if (plugin != null) {
            plugin.getLogger().warning(message);
        }
    }
}
