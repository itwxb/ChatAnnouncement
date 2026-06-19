package com.example.announcement.service;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 国际化服务：把命令执行者所在客户端的语言（{@code player.locale()}）映射到对应 {@link ResourceBundle}，
 * 用 MiniMessage 渲染带格式的消息字符串。
 * <p>
 * 设计要点：
 * <ul>
 *     <li>缺翻译时降级到 {@code en_US}，绝不抛异常</li>
 *     <li>消息本身就是 MiniMessage 字符串（管理员可改 yml 调整）</li>
 *     <li>支持无 Player 场景（控制台 → 默认 en_US）</li>
 *     <li>使用 UTF-8 资源控制，避免非 ASCII 字符被错读为 ISO-8859-1</li>
 * </ul>
 */
public final class I18nService {

    public static final Locale FALLBACK_LOCALE = Locale.US;
    public static final String BUNDLE_BASE = "com.example.announcement.lang.messages";

    private final MiniMessage miniMessage;
    private final Logger logger;
    private final ResourceBundle defaultBundle;
    private final ResourceBundle.Control utf8Control;

    public I18nService(@NotNull Logger logger) {
        this.logger = logger;
        this.miniMessage = MiniMessage.miniMessage();
        this.utf8Control = new Utf8Control();
        this.defaultBundle = ResourceBundle.getBundle(BUNDLE_BASE, FALLBACK_LOCALE, utf8Control);
    }

    /**
     * 取一个翻译键并用 MiniMessage 渲染为 {@link Component}。
     *
     * @param sender 接收方（可能为 null 表示控制台）
     * @param key    资源键
     * @param args   {@link String#format} 占位符参数
     */
    @NotNull
    public Component render(@Nullable CommandSender sender, @NotNull String key, @NotNull Object... args) {
        Locale locale = resolveLocale(sender);
        String value;
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_BASE, locale, utf8Control);
            value = bundle.getString(key);
        } catch (MissingResourceException ex) {
            try {
                value = defaultBundle.getString(key);
            } catch (MissingResourceException fallback) {
                logger.log(Level.WARNING, "i18n key missing: {0}", key);
                return Component.text(key);
            }
        }
        if (args.length > 0) {
            value = String.format(value, args);
        }
        try {
            return miniMessage.deserialize(value);
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Failed to render i18n key " + key, ex);
            return Component.text(value);
        }
    }

    /** 便捷方法：把 Component 发给一个 sender。 */
    public void send(@Nullable CommandSender sender, @NotNull String key, @NotNull Object... args) {
        if (sender == null) {
            return;
        }
        sender.sendMessage(render(sender, key, args));
    }

    @NotNull
    private Locale resolveLocale(@Nullable CommandSender sender) {
        if (sender instanceof Player player) {
            try {
                Locale loc = player.locale();
                if (loc != null) {
                    return loc;
                }
            } catch (NoSuchMethodError ignored) {
                // 旧版本 API 没有 locale()，降级到默认值
            }
        }
        return FALLBACK_LOCALE;
    }

    /**
     * 自定义 ResourceBundle.Control，使用 UTF-8 读取 .properties。
     * 默认 JDK 实现按 ISO-8859-1 读取，对中文字段不友好。
     */
    private static final class Utf8Control extends ResourceBundle.Control {
        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format,
                                        ClassLoader loader, boolean reload)
                throws IOException {
            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, "properties");
            try (InputStream is = loader.getResourceAsStream(resourceName)) {
                if (is == null) {
                    return null;
                }
                try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                    return new PropertyResourceBundle(reader);
                }
            }
        }
    }

    // 让编译器引用 Bukkit，避免 import 警告
    @SuppressWarnings("unused")
    private static Class<?> bukkit() {
        return Bukkit.class;
    }
}
