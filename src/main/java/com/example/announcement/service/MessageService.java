package com.example.announcement.service;

import com.example.announcement.config.ConfigService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

/**
 * 负责将字符串解析为 Adventure {@link Component}，并附加前缀与占位符展开。
 * <p>
 * 关键设计：
 * <ul>
 *     <li>所有 MiniMessage 解析都包在 try/catch 中，畸形输入不会让服务器炸</li>
 *     <li>失败时回退到安全字符串而非抛异常</li>
 *     <li>支持 PlaceholderAPI 在线占位符展开（可选依赖）</li>
 * </ul>
 */
public final class MessageService {

    /** 标识"发送给所有人"的关键字（大小写不敏感）。 */
    public static final String TARGET_ALL = "all";

    private final ConfigService config;
    private final Logger logger;
    private final MiniMessage miniMessage;

    public MessageService(@NotNull ConfigService config, @NotNull Logger logger) {
        this.config = config;
        this.logger = logger;
        this.miniMessage = MiniMessage.miniMessage();
    }

    /**
     * 解析一条带前缀的公告消息。
     *
     * @param raw 原始 MiniMessage 字符串
     * @return 解析后的 Component，解析失败时返回纯文本
     */
    @NotNull
    public Component parseWithPrefix(@NotNull String raw) {
        return parseWithPrefix(raw, null);
    }

    /**
     * 解析一条带前缀的公告消息，并对指定玩家展开 PAPI 占位符。
     */
    @NotNull
    public Component parseWithPrefix(@NotNull String raw, @Nullable Player target) {
        String expanded = expandPlaceholders(raw, target);
        String prefixed = config.getPrefix() + expanded;
        return parseSafe(prefixed);
    }

    /** 不带前缀的解析（用于模板原样展示等）。 */
    @NotNull
    public Component parseRaw(@NotNull String raw) {
        return parseRaw(raw, null);
    }

    /** 不带前缀的解析，并对指定玩家展开 PAPI 占位符。 */
    @NotNull
    public Component parseRaw(@NotNull String raw, @Nullable Player target) {
        String expanded = expandPlaceholders(raw, target);
        return parseSafe(expanded);
    }

    /**
     * 安全解析：失败时降级为纯文本 + 警告日志，绝不向上抛。
     */
    @NotNull
    private Component parseSafe(@NotNull String text) {
        try {
            return miniMessage.deserialize(text);
        } catch (Exception ex) {
            logger.warning(() -> "MiniMessage 解析失败，已降级为纯文本。原始输入: " + text);
            logger.log(java.util.logging.Level.FINE, "MiniMessage parse error", ex);
            return Component.text(text);
        }
    }

    /**
     * 展开 PlaceholderAPI 占位符。无 PAPI 或未注册占位符时原样返回。
     */
    @NotNull
    private String expandPlaceholders(@NotNull String text, @Nullable Player target) {
        if (text.indexOf('%') < 0) {
            return text; // 快速路径：没有 % 就不调用 PAPI
        }
        try {
            // 通过反射调用 PlaceholderAPI.setPlaceholders，避免硬依赖
            Class<?> papiClass = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            if (target != null) {
                Object result = papiClass.getMethod("setPlaceholders", Player.class, String.class)
                        .invoke(null, target, text);
                if (result instanceof String s) {
                    return s;
                }
            } else {
                Object result = papiClass.getMethod("setPlaceholders", OfflinePlayer.class, String.class)
                        .invoke(null, (OfflinePlayer) null, text);
                if (result instanceof String s) {
                    return s;
                }
            }
        } catch (ClassNotFoundException notInstalled) {
            // PAPI 未安装，原样返回
        } catch (Throwable t) {
            logger.warning(() -> "PlaceholderAPI 展开失败: " + t.getMessage());
        }
        return text;
    }

    /** 仅用于测试：暴露 MiniMessage 解析器。 */
    MiniMessage miniMessage() {
        return miniMessage;
    }

    // 避免 Bukkit.getOnlinePlayers 编译警告（保留供未来扩展）
    @SuppressWarnings("unused")
    private static int onlineCount() {
        return Bukkit.getOnlinePlayers().size();
    }
}
