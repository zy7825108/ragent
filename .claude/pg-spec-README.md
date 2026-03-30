# PG Spec Workflow（Claude Code 项目级）

已安装于 .claude/，打开此项目时自动生效。
推荐将 .claude/ 提交到 Git，让团队成员共享相同工作流。

## 目录结构

```
[项目根]/
├── .claude/
│   ├── commands/
│   │   └── pg/             ← 14 个 /pg: 命令（子目录命名空间）
│   │       ├── analyze.md  → /pg:analyze
│   │       ├── spec.md     → /pg:spec
│   │       ├── prd.md      → /pg:prd
│   │       └── ...
│   ├── skills/             ← pg 工作流 skills（pg- 前缀区分）
│   │   ├── pg-spec-state/
│   │   │   └── SKILL.md
│   │   ├── pg-code-executor/
│   │   │   └── SKILL.md
│   │   ├── pg-code-verifier/
│   │   │   └── SKILL.md
│   │   └── pg-doc-editor/
│   │       └── SKILL.md
│   └── pg-spec-README.md   ← 本说明文件
└── pg/
    └── doc/                ← PG Spec 产物（PRD、设计文档等）
        └── [需求名称]/
            ├── spec-status.json
            ├── PRD_xxx_YYYYMMDD.md
            ├── 程序设计_xxx_YYYYMMDD.md
            ├── 测试用例_xxx_YYYYMMDD.md
            └── task.md
```

## 命令索引

| 命令 | 说明 |
|---|---|
| /pg:analyze | 遗留系统梳理 |
| /pg:spec    | 初始化需求/查看状态 |
| /pg:prd     | 生成 PRD |
| /pg:clarify | 合并澄清答案 |
| /pg:plan    | 程序设计文档 |
| /pg:task    | 任务拆分+工时估算 |
| /pg:code    | 逐文件产码 |
| /pg:fix     | 修改代码 |
| /pg:edit    | 修改文档 |
| /pg:review  | Code Review |
| /pg:test    | 生成测试用例 |
| /pg:verify  | 开发自验 |
| /pg:done    | 收尾+上线Checklist |
| /pg:archive | 归档 |
