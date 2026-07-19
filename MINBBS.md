# 📢 ChatAnnouncement - 高级聊天公告插件

> 支持点击事件、i18n、PlaceholderAPI、AuthMe 集成 的 Minecraft 聊天公告插件  
> 适用于 **Minecraft 1.21.x** (Paper / Spigot)

![Java](https://img.shields.io/badge/Java-21-orange) ![Minecraft](https://img.shields.io/badge/Minecraft-1.21.x-green) ![License](https://img.shields.io/badge/License-MIT-blue) ![Tests](https://img.shields.io/badge/Tests-26_passing-brightgreen) ![Coverage](https://img.shields.io/badge/Coverage-60%25+-yellow)

---

## ✨ 插件简介

**ChatAnnouncement** 是一款为 Minecraft 服务器设计的聊天公告插件。

使用 **MiniMessage 富文本**、**点击事件**、**悬停提示** 替代传统的 `&6&l[公告]&f` 文字公告。

---

## 🎯 核心功能

### 🖱️ 三大点击事件

| 类型 | 效果 | 适用场景 |
|------|------|----------|
| `suggest_command` | 点击后**填充到聊天框**，玩家按回车执行 | 注册、领礼包等需要确认的操作 |
| `run_command` | 点击后**直接执行** | 传送到主城、打开菜单等快捷操作 |
| `open_url` | 点击后**打开网页** | 官网、投票、Wiki 链接 |

### 🎨 MiniMessage 富文本

- ✅ **16 种基础颜色** + HEX 自定义色（`#FF0000`）
- ✅ **RGB 渐变色**（`#FFD700 → #FFA500`）
- ✅ **粗体 / 斜体 / 下划线 / 删除线**
- ✅ **悬停提示**（hover）
- ✅ **嵌套组合**无限制

### 📦 完整功能

- ✅ **指令系统**：`/announcement send` / `broadcast` / `template` / `reload`
- ✅ **模板系统**：预设常用公告，一键调用
- ✅ **配置热重载**：修改后 `/announcement reload` 立即生效
- ✅ **Tab 补全**：命令和参数自动补全
- ✅ **权限分级**：5 个权限节点，精细控制
- ✅ **i18n 国际化**：自动按玩家客户端语言切换（中文 / 英文）
- ✅ **AuthMe 集成**：未注册玩家自动提示 + 注册成功全服广播
- ✅ **PlaceholderAPI 集成**：消息中可使用 PAPI 占位符
- ✅ **bStats 遥测**：匿名统计，便于改进（可关闭）
- ✅ **错误降级**：畸形消息自动降级为纯文本，绝不崩溃

---

## 🖼️ 效果预览

> 📷 **建议截图位置（发布时自己截）**：
> 1. `/announcement broadcast` 实际聊天效果
> 2. 鼠标悬停显示提示
> 3. 玩家点击后聊天框出现命令
> 4. `/announcement help` 渐变标题
> 5. AuthMe 集成的注册提示

---

## 📦 安装方法

### 1. 直接安装

1. 下载 `ChatAnnouncement-1.2.0.jar`
2. 放入服务器的 `plugins` 文件夹
3. **重启服务器**（首次安装）
4. 编辑 `plugins/ChatAnnouncement/config.yml` 自定义内容
5. 执行 `/announcement reload` 重载配置

### 2. 从源码构建

```bash
git clone <你的仓库地址>
cd chatannouncement
./gradlew shadowJar
# 产物在 build/libs/ChatAnnouncement-1.2.0.jar
```

### 3. 依赖要求

| 项目 | 要求 |
|------|------|
| 服务端 | Paper / Spigot 1.21+ |
| Java | 21+ |
| 可选插件 | AuthMe（注册集成）/ PlaceholderAPI（占位符） |

---

## 🎮 命令一览

| 命令 | 描述 | 权限 |
|------|------|------|
| `/announcement send <玩家\|all> <消息>` | 发送公告给指定玩家或所有人 | `announcement.send` |
| `/announcement broadcast <消息>` | 广播公告给所有在线玩家 | `announcement.broadcast` |
| `/announcement template <模板名> <玩家\|all>` | 使用预设模板发送公告 | `announcement.template` |
| `/announcement reload` | 重新加载配置 | `announcement.reload` |
| `/announcement help` | 显示帮助信息 | 所有玩家 |

**快捷别名**：`/ann`、`/notice`

### 使用示例

```bash
# 发送给特定玩家（带颜色）
/ann send Steve <yellow>欢迎回到服务器！</yellow>

# 广播给所有人（带渐变）
/ann broadcast <gradient:#FF6B6B:#FFE66D>周末双倍经验开启！</gradient>

# 使用模板
/ann template welcome all

# 重新加载配置（修改 config.yml 后）
/announcement reload
```

---

## ⚙️ 配置说明

配置文件位置：`plugins/ChatAnnouncement/config.yml`

### 完整默认配置

```yaml
# 公告前缀
prefix: "<gradient:#FF0000:#FF5555><bold>[公告]</bold></gradient> "

# 预设模板
templates:
  welcome:
    text: "<yellow>欢迎来到梦想之都服务器！<click:suggest_command:'/register'><hover:show_text:'<green>点击注册账号'><gold>[点击注册]</gold></click></yellow>"

  welcome_new:
    text: "<light_purple><bold><click:suggest_command:'欢迎新的小伙伴来到梦想之都！'><hover:show_text:'<green>点击发送欢迎语'><yellow>欢迎新的小伙伴来到梦想之都！</yellow><gold>[发送欢迎语]</gold></click></bold></light_purple>"

  spawn:
    text: "<green>点击 <click:run_command:'/spawn'><hover:show_text:'<aqua>传送到主城'><gold>[传送]</gold></click> 返回主城</green>"

  help:
    text: "<white>需要帮助？<click:suggest_command:'/help'><hover:show_text:'<yellow>点击获取帮助'><gold>[帮助]</gold></click> <click:run_command:'/rules'><hover:show_text:'<yellow>查看服务器规则'><gold>[规则]</gold></click></white>"

  vote:
    text: "<light_purple>支持服务器！<click:open_url:'https://example.com/vote'><hover:show_text:'<green>点击前往投票'><gold>[投票]</gold></click></light_purple>"

  donation:
    text: "<gradient:#FFD700:#FFA500>[赞助]</gradient> <white>感谢 <gold>大佬</gold> 赞助服务器！点击 <click:suggest_command:'感谢大佬赞助服务器！'><hover:show_text:'<green>点击发送感谢语'><gold>[感谢大佬]</gold></click> 送上祝福！</white>"

# 三种点击事件参考示例
example:
  suggest_command: "<white>点击 <click:suggest_command:'/help'><hover:show_text:'点击后聊天框会出现 /help'><gold>[填充命令]</gold></click> 测试</white>"
  run_command: "<white>点击 <click:run_command:'/spawn'><hover:show_text:'点击后执行 /spawn'><gold>[执行命令]</gold></click> 传送到主城</white>"
  open_url: "<white>点击 <click:open_url:'https://www.baidu.com'><hover:show_text:'点击打开百度'><gold>[打开网页]</gold></click> 访问官网</white>"

# AuthMe 集成（需同时安装 AuthMe）
authme:
  enabled: true
  unregistered-message: "<yellow>欢迎来到梦想之都服务器！请点击 <click:suggest_command:'/register 密码 在输入一次密码'><hover:show_text:'<green>点击注册账号'><gold>[注册]</gold></click> 完成注册</yellow>"
  register-broadcast: "<green>新成员</green> <white><gold>%player%</gold> 加入了梦想之都服务器！点击 <click:suggest_command:'欢迎 %player%，我是 %sender%，一起游玩吧！'><hover:show_text:'<green>点击发送欢迎消息'><gold>[打招呼]</gold></click> 欢迎新玩家！</white>"
```

### 配置热重载

修改 `config.yml` 后，执行 `/announcement reload` 立即生效，**无需重启服务器**。

### 如何新增自定义模板

```yaml
templates:
  my_custom:  # 模板名（自定义）
    text: "<green>这是我的自定义公告！</green>"  # 模板内容
```

然后使用：
```bash
/announcement template my_custom all
```

---

## 🔐 权限节点

| 权限 | 描述 | 默认 |
|------|------|------|
| `announcement.use` | 基础使用权限（查看帮助） | 所有玩家 |
| `announcement.send` | 发送公告 | OP |
| `announcement.broadcast` | 广播公告 | OP |
| `announcement.template` | 使用模板 | OP |
| `announcement.reload` | 重载配置 | OP |
| `announcement.admin` | 管理员权限（含以上所有） | OP |

**推荐用法**：
- 服主：`announcement.admin`
- 子管理员：`announcement.send + announcement.broadcast`
- 普通玩家：`announcement.use`（默认已有）

---

## 🎨 配色速查

### 基础颜色名

| 标签 | 颜色 | 标签 | 颜色 |
|------|------|------|------|
| `<black>` | 黑色 | `<red>` | 红色 |
| `<dark_red>` | 深红 | `<gold>` | 金色 |
| `<yellow>` | 黄色 | `<green>` | 绿色 |
| `<dark_green>` | 深绿 | `<aqua>` | 青色 |
| `<dark_aqua>` | 深青 | `<blue>` | 蓝色 |
| `<light_purple>` | 浅紫 | `<dark_purple>` | 深紫 |
| `<white>` | 白色 | `<gray>` | 灰色 |

### HEX 自定义色

```yaml
<#FF6B6B>珊瑚红</#FF6B6B>
<color:#4ECDC4>薄荷绿</color>
```

### 渐变色

```yaml
<gradient:#FF6B6B:#FFE66D>红→黄渐变</gradient>
<gradient:#4ECDC4:#556270>绿→灰渐变</gradient>
```

### 实用配色参考

| 场景 | 配色 |
|------|------|
| 重要通知 | `<gradient:#FF0000:#FF5555><bold>[公告]</bold></gradient>` |
| 活动宣传 | `<gradient:#FFD700:#FFA500>[活动]</gradient>` |
| 系统消息 | `<gray>[系统]</gray>` |
| 玩家高亮 | `<gold>玩家名</gold>` |
| 成功提示 | `<green>成功</green>` |
| 警告提示 | `<yellow>警告</yellow>` |
| 错误提示 | `<red>错误</red>` |

---

## ❓ 常见问题

### Q1: 修改配置后没生效？

执行 `/announcement reload` 即可。**无需重启服务器**。

### Q2: 中文显示乱码？

确保客户端使用最新版（1.21+）。  
本插件使用 UTF-8 编码资源文件，正常情况不会出现乱码。

### Q3: 装了 AuthMe 但没看到欢迎消息？

1. 检查 `config.yml` 中 `authme.enabled: true`
2. 检查 AuthMe 是否已加载（控制台无 AuthMe 报错）
3. 检查玩家是否已注册（已注册玩家不会收到提示）

### Q4: 装了 PlaceholderAPI 但占位符没展开？

本插件会在 PAPI 加载失败时**安全降级**到原文（不崩溃）。  
确认 PAPI 已成功加载（控制台无报错），重启服务器使插件重新检测。

### Q5: 如何关闭 bStats 遥测？

编辑 `plugins/bStats/config.yml`，设置 `enabled: false`。

### Q6: MiniMessage 标签写错了会怎样？

**自动降级为纯文本**，控制台无报错，**绝不崩溃**。

---

## 🔧 开发与构建

### 环境要求

- JDK 21+
- Gradle 8.8+

### 构建命令

```bash
./gradlew shadowJar          # 打包
./gradlew test               # 跑测试
./gradlew jacocoTestReport   # 生成覆盖率报告
```

### 单元测试

| 测试类 | 用例数 |
|--------|--------|
| `ConfigServiceTest` | 7 |
| `MessageServiceTest` | 4 |
| `TemplateServiceTest` | 5 |
| `TargetResolverTest` | 7 |
| `I18nServiceTest` | 5 |
| **合计** | **26** |

---

## 📋 更新日志

### v1.2.0 (2026-06-19) — 工程化重构

**业务层**
- ✨ 架构重构：Service 层拆分（Config / Message / Target / Template / I18n）
- ✨ 错误隔离：MiniMessage 解析失败自动降级
- ✨ i18n 国际化：自动按客户端语言切换
- ✨ PlaceholderAPI 集成（反射调用，失败降级）
- ✨ 类型安全配置：ConfigService 集中管理
- ✨ 26 个单元测试 + Mockito

**工程层**
- ✨ JaCoCo 覆盖率门禁（Service ≥ 60%）
- ✨ GitHub Actions CI/CD
- ✨ MIT 许可证
- ✨ bStats 遥测
- ✨ CHANGELOG 规范化

### v1.1.0 (2026-02-20)

- ✨ AuthMe 集成
- ✨ 未注册玩家提示
- ✨ 注册成功全服广播
- ✨ `%player%` / `%sender%` 变量支持

### v1.0.0 (2026-02-20)

- 🎉 初始版本发布
- ✨ 点击事件（3 种）
- ✨ MiniMessage 富文本
- ✨ 完整指令系统
- ✨ 模板系统
- ✨ 配置文件热重载

---

## 📥 下载

> ⚠️ **发布前请替换为你的真实下载链接**

- **Gitee**：[<你的仓库地址>]()
- **GitHub**：[<你的仓库地址>]()

---

## 📞 联系方式

> ⚠️ **发布前请替换为你的真实联系方式**

- **作者**：<你的名字>
- **QQ群**：<你的群号>
- **邮箱**：<你的邮箱>
- **问题反馈**：[Issues]()

---

## ⭐ 支持作者

如果觉得插件好用，欢迎：
- 给个 **免费的 Star** ⭐
- 分享给其他服主
- 在评论区留下你的服务器名（我会收录到"用户列表"）

---

## 📄 许可证

本项目采用 **MIT 许可证**。

你可以自由使用、修改、分发本插件，**包括商用**，只需保留版权声明。

---

**🎉 感谢使用 ChatAnnouncement！**  
**如果有任何问题或建议，欢迎在评论区留言！**
