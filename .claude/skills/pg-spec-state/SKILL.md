---
name: spec-state
description: 管理 Spec 工作流的阶段状态、持久化 PRD 与设计文档到文件系统、跨命令复用上下文，避免用户重复粘贴文档内容。适用于 /pg_prd、/pg_clarify、/pg_plan、/pg_task、/pg_code、/pg_done 等命令执行时调用。
---

# Spec State 状态管理技能

## 一、核心职责

1. **持久化文档**：每个命令完成产物后**立即写文件**，不等用户确认
2. **状态跟踪**：维护 `spec-status.json`，每个阶段完成后**强制更新**
3. **上下文复用**：后续命令直接读取文件，不要求用户重复粘贴
4. **进度恢复**：支持 `/pg_code` 断点续传，记录已完成文件列表
5. **阶段引导**：每个命令完成后状态栏显示下一步，只允许 `/pg_xxx` 命令触发，不允许自然语言触发

---

## 二、输入检查与引导规范（所有命令必须遵守）

**每个 `/pg_xxx` 命令在执行前，必须先判断输入是否明确。若不明确，禁止直接推进，应通过多轮对话收集完整信息。**

### 输入不明确的判断标准

以下情况均视为「输入不明确」，必须停下来引导用户澄清：

| 命令 | 典型的不明确输入 | 必须澄清的内容 |
|---|---|---|
| `/pg_prd` | 仅输入命令，无需求来源 | 需求来源（Confluence/YAPI/文字描述） |
| `/pg_clarify` | 仅输入命令，无澄清答案 | 逐条列出待澄清问题，要求用户一一回答 |
| `/pg_plan` | 仅输入命令，无系统文档背景 | 是否有已有系统设计文档 / 是否全新项目 |
| `/pg_fix` | 「修复一下」「有个bug」 | bug 的具体表现、文件位置、复现步骤 |
| `/pg_edit` | 「改一下文档」「更新一下」 | 修改哪个文档、哪个章节、改成什么内容 |
| `/pg_code` | 在缺少设计文档/任务清单时触发 | 先引导执行 `/pg_plan` 和 `/pg_task` |
| `/pg_review` | 仅输入命令，无指定范围 | 审查范围（全局 / 指定文件 / 最近变更） |
| `/pg_verify` | 仅输入命令 | 优先读取 spec-status.json，若无则询问验证范围 |

### 引导对话规范

1. **停止执行**：收到不明确指令时，不得推测补全后直接执行
2. **列出缺失项**：明确告知用户缺少什么信息，每次只问一个核心问题，避免信息轰炸
3. **保持上下文**：用户多轮回答期间，已确认的信息不需要重复确认
4. **收集完整后执行**：所有必要信息确认完毕后，再开始实际执行

**引导模板示例（`/pg_fix`）：**
```
要帮你修复代码，需要了解以下信息：

1. **问题描述**：具体报错信息或异常现象是什么？
   （例如：NullPointerException at XxxService.java:42）

请先告诉我第 1 点，我会根据回答继续询问。
```

### 引导过程中的产物更新规则

若引导对话过程中用户提供了**影响已有产物的新信息**，必须立即更新：

| 情况 | 需要更新的产物 |
|---|---|
| 用户澄清了需求范围，与 PRD 有出入 | 更新 PRD 文件，更新 spec-status.json 的 updatedAt |
| 用户确认了设计方案的修改意见 | 更新程序设计文档文件 |
| 用户调整了任务优先级或范围 | 更新 task.md |
| 用户在 /pg_code 引导中修改了文件清单 | 更新设计文档的「涉及文件清单」章节，更新 codeProgress.totalFiles |

> ⚠️ 产物更新后必须告知用户「已更新 [文件名]」，并展示更新了哪些内容（一句话）。

---


```
[项目根目录]/
  pg/
    doc/
      [需求名称]/
        spec-status.json             ← 状态文件（必须维护）
        PRD_[需求名称]_YYYYMMDD.md
        澄清记录_[需求名称]_YYYYMMDD.md
        程序设计_[需求名称]_YYYYMMDD.md
        task.md
      archive/
        [需求名称]_YYYYMMDD/         ← /pg_archive 归档目标
          README.md
          [PRD/设计文档等产物...]
```

### 项目根目录推断规则（所有产物命令遵守）

> ⚠️ `pg/doc/` 必须在项目内部，不得建在全局目录（`~`）下。

项目根推断顺序：

| 优先级 | 方法 | 处理 |
|---|---|---|
| 1 | 读取 `spec-status.json` 的 `projectRoot` 字段 | 直接使用，无需重新推断 |
| 2 | 当前目录向上递归查找 `.git` | 找到即为项目根 |
| 3 | IDE 当前活跃工作区根目录 | 直接使用 |
| 4 | 以上均无法确定 | **展示確认请求，等待用户回复** |

**无法自动确定时的提示模板：**

```
⚠️ 无法自动确定项目根目录

pg/doc/ 应存放在项目内部，请确认项目根目录：
1. 将当前目录作为项目根（目录将为 [pwd]/pg/doc/）
2. 手动指定：/pg_spec 需求名 path:/绝对路径/
```

> ⚠️ 禁止使用旧路径（`docs/specs/` 或 `~/.gemini/antigravity/specs/` 或 `~/pg/doc`）

---

## 三、`spec-status.json` 完整格式

```json
{
  "requirementName": "需求名称",
  "projectRoot": "/absolute/path/to/project/",
  "currentPhase": "待需求分析 | PRD生成 | 澄清中 | 技术设计 | 任务拆分 | 开发中 | 验证中 | 已完成",
  "specDir": "/absolute/path/to/project/pg/doc/需求名称/",
  "files": {
    "prd": "PRD_需求名称_20260330.md",
    "design": "程序设计_需求名称_20260330.md",
    "clarify": "澄清记录_需求名称_20260330.md",
    "task": "task.md",
    "report": "收尾报告_需求名称_20260330.md",
    "checklist": "上线Checklist_需求名称_20260330.md",
    "analyze": "现状设计_模块名称_20260330.md"
  },
  "codeProgress": {
    "totalFiles": 5,
    "completedFiles": 2,
    "currentFile": "XxxService.java",
    "completedList": ["XxxController.java", "XxxDO.java"]
  },
  "createdAt": "2026-03-30T10:00:00+08:00",
  "updatedAt": "2026-03-30T14:30:00+08:00"
}
```

### 字段说明

| 字段 | 说明 |
|---|---|
| `currentPhase` | 当前阶段，严格按枚举值设置；逆向流程时由 `/pg_edit` 回退 |
| `codeProgress` | 产码进度，`/pg_code` 每完成一个文件后更新 |
| `codeProgress.completedList` | 已完成文件列表，用于断点续传 |
| `files.prd` | PRD 文档路径，`/pg_verify` 和 `/pg_review` 会读取用于 PRD 兜底验证 |
| `files.design` | 程序设计文档路径，所有验证/审查的主要基准 |
| `files.report` | `/pg_done` 生成的收尾报告文件路径 |
| `files.checklist` | `/pg_done` 生成的稳定性上线 Checklist 路径（按需动态生成各维度章节）|
| `files.analyze` | `/pg_analyze` 生成的现状程序设计文档路径；`/pg_archive` 执行后用于判断现状文档是否需要更新 |
| `archiveDir` | `/pg_archive` 执行后的归档目录路径（`pg/doc/archive/[需求名称]_YYYYMMDD/`）|

---

## 四、各命令强制执行节点（不可跳过）

### `/pg_spec [需求名]` 执行时
1. 确定 specDir（按优先级）
2. 创建目录
3. 写入初始 `spec-status.json`（`currentPhase: "待需求分析"`）
4. 初始化 `codeProgress: null`

### `/pg_prd` 完成时（**立即**，不等确认）
1. 从 PRD 标题提取需求名称
2. 确定 specDir：读取 spec-status.json → 若无则按优先级自行推断并创建目录
3. 将 PRD 写入文件（顶部附加元信息头）
4. **立即**更新 `spec-status.json`：`currentPhase: "PRD生成"`，`files.prd` 指向新文件
5. PRD 中有 ⚠️ 标注项时，在文件路径提示**之前**列出待澄清问题

**元信息头格式：**
```markdown
---
归档时间: YYYY-MM-DD HH:mm
需求名称: [需求名称]
文档类型: PRD
版本: v1.0
specDir: [目录绝对路径]
---
```

### `/pg_clarify` 执行时
1. 读取 `spec-status.json` 获取 `specDir` 和 `files.prd`
2. 若文件存在，直接读取 PRD 文件内容（无需用户粘贴）
3. 合并后**立即**覆盖写入原 PRD 文件（更新版本号和归档时间）
4. 写入澄清记录：`澄清记录_[需求名称]_YYYYMMDD.md`
5. **立即**更新 `spec-status.json`：`currentPhase: "澄清中"`，`files.clarify` 指向澄清记录

### `/pg_plan` 完成时（**立即**，不等确认）
1. 设计方案输出完毕后立即写入：`程序设计_[需求名称]_YYYYMMDD.md`
2. **立即**更新 `spec-status.json`：`currentPhase: "技术设计"`，`files.design` 指向新文件
3. 方案确认时：**只能引导用户使用 `/pg_code` 命令**，禁止「自然语言确认后自动产码」

### `/pg_task` 完成时（**立即**，不等确认）
1. 将任务清单写入 `task.md`
2. **立即**更新 `spec-status.json`：`currentPhase: "任务拆分"`，初始化 `codeProgress`
3. 提示用户使用 `/pg_code` 开始产码

### `/pg_code` 执行时
1. 启动前读取 `codeProgress` 恢复断点（若有已完成文件，跳过不重复产出）
2. 每个文件产出并用户确认后：
   - 更新 `codeProgress.completedFiles`
   - 更新 `codeProgress.completedList`
   - 更新 `codeProgress.currentFile` 为下一个文件
   - 更新 `updatedAt`
3. 全部完成后：`currentPhase: "验证中"`，`codeProgress.currentFile: null`

### `/pg_verify` 完成时
1. 加载 `files.prd`（若存在）与 `files.design` 作为双基准
2. **立即**更新 `spec-status.json`：`currentPhase: "验证中"` → 验证通过后 `"已完成"`

### `/pg_analyze` 完成时
1. 现状程序设计文档写入文件后
2. **立即**更新 `spec-status.json`：`files.analyze` 指向新文件
   > 若状态文件不存在，尝试在项目根目录创建 `pg/doc/` 并初始化状态文件后再写入

### `/pg_done` 执行时
1. 读取所有 `files` 字段，汇总产物
2. **立即**更新 `currentPhase: "已完成"`

### `/pg_edit` 执行时（逆向流程状态回退规则）

> ⚠️ 这是**全流程最易被忽视的状态更新**：在晚期阶段修改文档必须回退状态，否则后续命令无法感知「有内容需要返工」。

**判断是否需要回退 `currentPhase`：**

| 当前阶段 | 修改内容 | 状态回退至 | 含义 |
|---|---|---|---|
| 技术设计 / 任务拆分 / 开发中 / 验证中 | PRD 功能点、接口、业务规则 | `技术设计` | 设计文档需重新对齐 PRD |
| 任务拆分 / 开发中 / 验证中 | PRD + 程序设计文档均有变 | `任务拆分` | task.md 需重新确认范围 |
| 开发中 / 验证中 | 仅程序设计文档（接口/逻辑变更） | 保持 `开发中` | `/pg_fix` 修复对应代码即可 |
| 任意阶段 | PRD 背景、非功能性描述 | 不回退 | 不影响设计和代码 |

**回退时必须做：**
1. 更新 `spec-status.json` 的 `currentPhase` 为回退值
2. 更新 `files.[prd 或 design]` 指向新版本文件
3. 更新 `updatedAt`
4. **在回复中明确告知用户**：「状态已回退至 [阶段]，建议下一步：[命令]」

### `/pg_archive` 完成时
1. 将所有产物复制到归档目录再删除原目录（已在 pg_archive.md 中确认）
2. 更新 `spec-status.json`：`specDir → null`，`archiveDir → 归档路径`
3. **第六步：检查 `files.analyze` 是否存在**：
   - 不存在：静默跳过
   - 存在：对比本次需求的涉及文件与现状文档分析范围，有交集时展示「现状文档失效提醒」，并提供两种更新路径：
     - 方式一：`/pg_analyze [目录]` 全量重分析（改动较大）
     - 方式二：`/pg_edit 现状设计 更新[感章节]：[本次修改摘要]` 增量更新（改动较小，省 token）

---

## 五、版本冲突处理

- 同一天同类型文件已存在时，自动追加版本号：`PRD_[需求名称]_20260330_v2.md`
- `spec-status.json` 的 `files.xxx` 始终指向最新版本
- 不删除旧版本文件（保留历史可追溯）

---

## 六、状态展示格式

每次命令完成后，在回复末尾附加状态栏：

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📋 需求：[需求名称]
📍 当前阶段：[当前阶段]
📁 文件目录：[specDir]
⏭  下一步：[具体的 /pg_xxx 命令]
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

> ⚠️ 「下一步」只能写 `/pg_xxx` 命令，**禁止写「开始产码」「继续」等自然语言**，避免用户绕过命令约束直接触发。
