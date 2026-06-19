# 更新日志 (Changelog)

本项目的所有显著变更都会记录在此文件中。格式基于 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.1.0/)，
版本号遵循 [语义化版本 (Semantic Versioning)](https://semver.org/lang/zh-CN/)。

## [Unreleased]

## [1.2.0] - 2026-06-19

### ✨ Added（新增）
- **i18n 国际化**：自动按 `Player.locale()` 切换 zh_CN / en_US，UTF-8 资源控制
- **PlaceholderAPI 集成**：消息内支持 PAPI 占位符（可选依赖，反射调用）
- **单元测试**：28 个 JUnit 5 + Mockito 用例，覆盖 Service 层核心逻辑
- **JaCoCo 覆盖率报告**：JaCoCo 0.8.11 集成，Service/Config 包要求 ≥ 60%
- **CI/CD**：GitHub Actions 多 Job 流水线（构建 + 测试 + Codecov + 制品上传 + 覆盖率校验）
- **MIT 许可证**：补全 LICENSE 文件

### 🔄 Changed（变更）
- **架构重构**：拆出 Service 层（Config/Message/Target/Template/I18n），构造函数注入
- **错误隔离**：MiniMessage 解析 try/catch 包裹，畸形消息降级到纯文本
- **类型安全配置**：`ConfigService` 集中管理配置键路径，附带基础校验
- **可测试性**：`TargetResolver.PlayerProvider` 抽象 Bukkit 依赖
- **公共 API 收窄**：移除 `getMessageManager()` getter，暴露 Service 集合
- **包结构**：旧 `manager/MessageManager` 删除，业务代码迁至 `service/`
- **依赖**：新增 PAPI 仓库 (`repo.extendedclip.com`)

### 🗑️ Removed（移除）
- `MessageManager` 类（被 `MessageService` 替代）
- 硬编码中文消息字符串（已迁移至 i18n 资源包）
- `plugin.getConfig().getString(...)` 散落调用（8 处 → 0 处）

### 🐛 Fixed（修复）
- 玩家不存在时 `Bukkit.getPlayerExact()` 仍可调用导致的隐式 NPE 风险
- AuthMe 监听器对 AuthMe API 缺失场景下的 NPE（已加存在性检查）

## [1.1.0] - 2026-02-20

### ✨ Added
- AuthMe 集成：未注册玩家加入时自动发送注册提示
- 注册成功后全服广播欢迎消息
- 个性化欢迎消息（`%player%`、`%sender%` 变量）
- AuthMe 作为可选依赖，不影响其他功能

## [1.0.0] - 2026-02-20

### ✨ Added
- 初始版本发布
- 支持三种点击事件：`suggest_command` / `run_command` / `open_url`
- 支持 MiniMessage 富文本格式（颜色、渐变、悬停）
- 完整指令系统：`/announcement send|broadcast|template|reload|help`
- 模板系统（`templates.<name>.text`）
- 配置文件热重载
- 5 个权限节点（`announcement.use/send/broadcast/template/reload/admin`）

[Unreleased]: https://gitee.com/yourusername/chatannouncement/compare/v1.2.0...HEAD
[1.2.0]: https://gitee.com/yourusername/chatannouncement/compare/v1.1.0...v1.2.0
[1.1.0]: https://gitee.com/yourusername/chatannouncement/compare/v1.0.0...v1.1.0
[1.0.0]: https://gitee.com/yourusername/chatannouncement/releases/tag/v1.0.0
