package com.example.announcement.service;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 将 {@code all} / 玩家名解析为玩家集合。
 * <p>
 * 单独的解析器便于测试和未来扩展（多目标、组、权限过滤等）。
 * <p>
 * {@link PlayerProvider} 抽象掉对 {@link Bukkit} 的直接依赖，让单元测试不依赖 MockBukkit。
 */
public final class TargetResolver {

    /** 玩家查询回调。 */
    public interface PlayerProvider {
        /** 返回所有在线玩家。 */
        @NotNull Collection<Player> onlinePlayers();

        /** 按精确名字查询单个玩家。 */
        @Nullable Player playerExact(@NotNull String name);
    }

    /** 解析结果：可能是单个玩家、在线所有玩家，也可能为空（玩家不在线）。 */
    public record Result(@NotNull Type type, @NotNull Collection<Player> players) {
        public enum Type { ALL, SINGLE, EMPTY }

        public boolean isEmpty() {
            return type == Type.EMPTY;
        }

        public int size() {
            return players.size();
        }
    }

    private static final PlayerProvider DEFAULT = new PlayerProvider() {
        @Override public @NotNull Collection<Player> onlinePlayers() {
            return List.copyOf(Bukkit.getOnlinePlayers());
        }
        @Override public Player playerExact(@NotNull String name) {
            return Bukkit.getPlayerExact(name);
        }
    };

    private final PlayerProvider provider;

    public TargetResolver() {
        this(DEFAULT);
    }

    public TargetResolver(@NotNull PlayerProvider provider) {
        this.provider = provider;
    }

    /**
     * 解析一个目标字符串。
     *
     * @param input 用户输入：{@code all} / {@code @a} / 玩家名（大小写不敏感）
     * @return 解析结果
     */
    @NotNull
    public Result resolve(@Nullable String input) {
        if (input == null || input.isBlank()) {
            return new Result(Result.Type.EMPTY, Collections.emptyList());
        }
        if (MessageService.TARGET_ALL.equalsIgnoreCase(input) || "@a".equalsIgnoreCase(input)) {
            return new Result(Result.Type.ALL, List.copyOf(provider.onlinePlayers()));
        }
        Player player = provider.playerExact(input);
        if (player == null) {
            return new Result(Result.Type.EMPTY, Collections.emptyList());
        }
        return new Result(Result.Type.SINGLE, List.of(player));
    }

    /** 解析多个目标字符串。 */
    @NotNull
    public Result resolveAll(@NotNull Collection<String> inputs) {
        List<Player> players = inputs.stream()
                .map(this::resolve)
                .map(Result::players)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toUnmodifiableList());
        int online = provider.onlinePlayers().size();
        Result.Type type = players.size() == online
                ? Result.Type.ALL
                : (players.isEmpty() ? Result.Type.EMPTY : Result.Type.SINGLE);
        return new Result(type, players);
    }

    // 工具方法：生成一个空 provider
    public static PlayerProvider empty() {
        return new PlayerProvider() {
            @Override public @NotNull Collection<Player> onlinePlayers() {
                return Collections.emptyList();
            }
            @Override public Player playerExact(@NotNull String name) {
                return null;
            }
        };
    }

    // 工具方法：生成一个固定玩家的 provider（用于测试）
    public static PlayerProvider of(Player... players) {
        List<Player> list = List.of(players);
        return new PlayerProvider() {
            @Override public @NotNull Collection<Player> onlinePlayers() {
                return list;
            }
            @Override public Player playerExact(@NotNull String name) {
                for (Player p : list) {
                    if (p.getName().equalsIgnoreCase(name)) {
                        return p;
                    }
                }
                return null;
            }
        };
    }

    @SuppressWarnings("unused")
    private static Supplier<Player> unused() { return null; }

    @SuppressWarnings("unused")
    private static <T> Function<T, T> unusedFn() { return t -> t; }
}
