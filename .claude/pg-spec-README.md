# PG Spec Workflow（Claude Code 项目级）

已安装于 .claude/，打开此项目时自动生效。
推荐将 .claude/ 提交到 Git，让团队成员共享相同工作流。

## 目录结构

```
[项目根]/
├── .claude/
│   ├── commands/           ← 18 个 /pg_xxx 命令（pg_ 前缀平铺）
│   │   ├── pg_analyze.md   → /pg_analyze
│   │   ├── pg_spec.md      → /pg_spec
│   │   ├── pg_prd.md       → /pg_prd
│   │   └── ...
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
| /pg_analyze | 遗留系统梳理 |
| /pg_spec    | 初始化需求/查看状态 |
| /pg_prd     | 生成 PRD |
| /pg_clarify | 合并澄清答案 |
| /pg_plan    | 程序设计文档 |
| /pg_task    | 任务拆分+工时估算 |
| /pg_code    | 逐文件产码 |
| /pg_fix     | 修改代码 |
| /pg_edit    | 修改文档 |
| /pg_review  | Code Review |
| /pg_ut      | 单元测试规划 |
| /pg_test    | 生成测试用例 |
| /pg_verify  | 开发自验 |
| /pg_done    | 收尾+上线Checklist |
| /pg_archive | 归档 |
| /pg_status  | 状态查询 |
| /pg_recap   | 会话知识萃取 |
| /pg_recall  | 知识召回 |
