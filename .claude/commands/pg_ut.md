---
description: PG - 单元测试规划命令 - 基于本次会话生成的代码，规划单元测试任务清单并逐轮生成测试代码 (PG Unit Test Command)
---

当用户输入 `/pg_ut` 命令时，你必须严格按照以下步骤执行：

> 📌 **命令定位**：独立质量工具，不绑定 spec 阶段，不修改 `spec-status.json`，可在开发任意阶段触发。

---

## ⛔ 前置：项目类型检测（最先执行）

扫描当前工作区判断项目类型：
- 存在 `pom.xml` / `build.gradle` / `src/main/java/` → 后端项目，继续执行
- 存在 `package.json` / `vue.config.js` / `vite.config.*` / `.vue` / `.tsx` 文件 → **前端项目，拒绝执行**

**前端项目输出以下提示并终止：**

```
⛔ /pg_ut 不适用于前端项目

本命令基于 Java/Mockito 单测框架设计，不支持前端项目（Vue/React/TS 等）。

前端单测建议：
→ 使用 Vitest / Jest 为工具函数和 composable/hooks 编写单测
→ 使用 Cypress / Playwright 编写 E2E 测试
→ 直接向我描述「需要测试的函数/组件」，我会根据项目已有测试框架生成对应测试代码
```

> 确认为后端项目后，从下方「前置检查」步骤继续。

---

## 前置检查：确认代码上下文

**优先**尝试从以下来源获取本次生成的代码列表：
1. 本次会话的产码记录（`spec-status.json` 的 `codeProgress.completedList`）
2. 会话上下文中已输出的代码文件

**若无任何代码上下文，输出提示并停止：**

```
⚠️ 未检测到本次会话生成的代码。

请提供（任选一种）：
1. 将需要单测的代码文件粘贴到当前对话
2. 指定文件路径（如：`/pg_ut src/main/java/.../XxxService.java`）
3. 先执行 `/pg_code` 产出代码后再规划单测
```

---

## 第一步：扫描与识别

从本次会话生成/提供的代码中，识别所有需要单元测试的类：

**✅ 纳入测试范围：**
- Service 实现类（含核心业务逻辑）
- 有条件分支的 Manager / Helper / Component 类
- 核心工具类（含判断/转换逻辑）
- 带有复杂计算或状态流转的类

**❌ 排除测试范围：**
- Controller 类（仅参数转发，无业务逻辑）
- 纯数据类（DO / DTO / VO / Request / Response）
- Mapper 接口
- 枚举类、常量类
- 配置类（Apollo / Spring Config）

扫描完毕后，在输出任务清单前，先确认：

```
📋 扫描完成，识别到以下 [N] 个需要单测的类：
[列出类名和文件路径]

排除以下 [M] 个类（无需单测）：
[列出排除的类名和原因]

是否调整范围？（直接回复「确认」或说明调整项）
```

---

## 第二步：输出任务拆分清单

用户确认范围后，逐任务输出，每个任务格式如下（Markdown 格式，非代码块）：

---

### 任务 N：【被测类名】

**文件路径**：`src/main/java/.../XxxService.java`
**测试类路径**：`src/test/java/.../XxxServiceTest.java`

**被测方法**：
| 方法名 | 参数 | 返回值 | 测试优先级 |
|---|---|---|---|
| `doCreate(req)` | XxxCreateReq | Long | 🔴 核心 |
| `getDetail(id)` | Long | XxxVO | 🟡 次要 |

**需要 Mock 的依赖**：
- `XxxMapper` — 数据库操作
- `UserService` — 外部服务调用
- ⚠️ `XxxConverter`（MapStruct）— **用 `new XxxConverterImpl()` 真实实例，禁止 Mock**

**重点覆盖场景**：
- ✅ 正常流程：[具体描述主链路]
- ❌ 异常分支：[具体描述触发条件和预期异常]
- 🔲 边界条件：[null 值、空列表、极值等]

**执行指令**（回复以下内容触发生成）：

> 「请生成任务 N 的单元测试，仅输出 `XxxServiceTest.java` 完整代码（含所有 import），按以下约束和示例风格生成：
>
> 【约束】
> 1. `@RunWith(MockitoJUnitRunner.class)`，禁止 Spring 测试环境
> 2. `@Mock` 声明依赖，`@InjectMocks` 声明被测类
> 3. MapStruct Converter 用 `new XxxConverterImpl()` 真实实例，禁止 Mock
> 4. 禁止 Mock final 类 / static 方法（需要时说明替代方案）
> 5. 每个测试方法只验证单一行为，命名：`should_预期结果_when_触发条件`
> 6. `verify(mock, times(n))` 次数必须与被测逻辑实际调用次数严格一致
> 7. 异常分支必须加 `verify(mock, never())` 验证未调用的方法
> 8. 使用 `given/when/then` 注释三段式结构
>
> 【示例风格】
> ```java
> @RunWith(MockitoJUnitRunner.class)
> public class XxxServiceTest {
>
>     @Mock private XxxMapper xxxMapper;
>     @Mock private UserService userService;
>     private XxxConverter converter = new XxxConverterImpl(); // 真实实例，不 Mock
>     @InjectMocks private XxxServiceImpl xxxService;
>
>     @Test
>     public void should_createRecord_when_requestIsValid() {
>         // given
>         when(userService.getById(1L)).thenReturn(mockUser());
>         when(xxxMapper.insert(any())).thenReturn(1);
>         // when
>         Long id = xxxService.doCreate(buildRequest());
>         // then
>         assertNotNull(id);
>         verify(userService, times(1)).getById(1L);
>         verify(xxxMapper, times(1)).insert(any());
>     }
>
>     @Test(expected = BizException.class)
>     public void should_throwException_when_userNotFound() {
>         // given
>         when(userService.getById(anyLong())).thenReturn(null);
>         // when
>         xxxService.doCreate(buildRequest());
>         // then（异常分支：不该调用 Mapper）
>         verify(xxxMapper, never()).insert(any());
>     }
> }
> ```」

---

## 第三步：输出执行顺序建议

所有任务列出后，附上优先级建议：

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📋 单测任务规划完成，共 [N] 个任务

建议执行顺序（优先级：分支最多 → 被依赖多 → 边缘辅助）：
🔴 任务 2 — XxxCoreService（核心业务，分支最多）
🟡 任务 1 — XxxHelperService（被多处依赖）
🟢 任务 3 — XxxValidateUtil（辅助工具）

执行方式：
→ 回复「执行任务 N」开始生成第 N 个测试类
→ 回复「执行任务 all」按推荐顺序自动逐个生成（每个完成后等待确认）
→ 回复「跳过任务 N」跳过某个任务
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

---

## 第四步：逐任务生成测试代码

用户触发「执行任务 N」后：

1. 输出完整的 `XxxServiceTest.java`（含所有 import，可直接复制粘贴）
2. 代码输出完毕后，展示确认菜单：

```
✅ 任务 N「XxxServiceTest」已生成（第 N/Total）

→ 回复「继续」生成下一个：[下一个类名]
→ 回复「修改 [描述]」对当前测试提出调整
→ 回复「跳过」跳过下一个，继续后续
→ 回复「执行任务 all」剩余全部自动生成
```

---

## 禁止行为

- ❌ 禁止在测试中使用 `@SpringBootTest` 或启动 Spring 容器
- ❌ 禁止 Mock `final` 类 / `static` 方法（应标注并建议重构）
- ❌ 禁止一次性生成所有测试（除非用户明确输入「执行任务 all」）
- ❌ 禁止英文注释
- ❌ 禁止 `verify` 次数与实际调用次数不一致
