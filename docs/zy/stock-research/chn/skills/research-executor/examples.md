# 研究执行器技能 - 示例

## 示例1：AI医疗研究 - 完整执行流程

### 输入：结构化提示词

```markdown
### TASK
分析AI在临床诊断中的应用现状、技术能力和实施挑战

### SPECIFIC QUESTIONS
1. AI诊断准确率 vs 人类医生？
2. 市场规模和增长预测？
3. 主要供应商和竞争格局？
4. 实施成本和时间线？
5. 监管要求和合规？
```

### 阶段2：检索规划

```markdown
## 子主题分解

1. **市场和增长**
   - 查询："AI healthcare market size 2023 2024"
   - 查询："AI clinical diagnosis market CAGR forecast"
   - 来源：行业报告（Grand View Research, MarketsandMarkets）

2. **技术准确率**
   - 查询："AI vs human diagnosis accuracy radiology"
   - 查询："AI pathology accuracy peer reviewed study"
   - 来源：学术论文（Nature Medicine, Lancet Digital Health）

3. **供应商格局**
   - 查询："AI healthcare vendors market share 2024"
   - 查询："Gartner magic quadrant healthcare AI"
   - 来源：分析师报告（Gartner, Forrester）

4. **实施挑战**
   - 查询："AI healthcare implementation cost timeline"
   - 查询："hospital AI adoption barriers case study"
   - 来源：咨询报告、案例研究

## 智能体部署计划

| 智能体 | 子主题 | 查询数 | 预计时间 |
|--------|--------|--------|---------|
| Agent A | 市场和增长 | 3 | 5分钟 |
| Agent B | 技术准确率 | 3 | 5分钟 |
| Agent C | 供应商格局 | 2 | 5分钟 |
| Agent D | 实施挑战 | 3 | 5分钟 |

**部署方式**：并行（单条消息4个Task调用）
**预计总时间**：约5分钟（并行化）
```

### 阶段3：执行

```
[同时启动4个研究智能体]

Agent A 完成：
  发现：市场224亿美元，CAGR 37.5%
  来源：3个（1个A级、2个B级）
  置信度：高

Agent B 完成：
  发现：AI准确率91-96% vs 人类87-90%
  来源：4个（3个A级、1个C级）
  置信度：高

Agent C 完成：
  发现：IBM 18%、Google 15%、Microsoft 12%
  来源：2个（2个B级）
  置信度：中

Agent D 完成：
  发现：成本50万-500万美元，实施6-18个月
  来源：3个（1个B级、2个C级）
  置信度：中
```

### 阶段5：综合

```markdown
# AI在临床诊断中的应用：综合研究报告

## 执行摘要

全球AI healthcare市场在2023年达到224亿美元...
[综合4个智能体的发现...]

## 来源统计
- 总来源：12个
- A级：4个（33%）
- B级：5个（42%）
- C级：3个（25%）

## 质量评分：8.5/10
```

---

## 示例2：并行 vs 顺序部署

### ❌ 错误：顺序部署

```
第1分钟：启动Agent A → 等待完成
第6分钟：启动Agent B → 等待完成
第11分钟：启动Agent C → 等待完成
第16分钟：启动Agent D → 等待完成
第21分钟：开始综合

总时间：~25分钟
```

### ✅ 正确：并行部署

```
第1分钟：同时启动Agent A、B、C、D
第6分钟：所有Agent完成
第7分钟：开始综合

总时间：~10分钟
```

**效率提升**：2.5倍

---

## 示例3：智能体提示词实例

### 实际的Task调用

```markdown
[Task Agent A]
你是一名研究智能体，专注于AI医疗市场分析。

**研究目标**：确定2023-2024年全球AI医疗市场的规模、
增长率和关键细分领域。

**搜索查询**：
1. "AI healthcare market size 2023 2024"
2. "artificial intelligence clinical diagnosis market CAGR"
3. "AI medical imaging market forecast 2030"

**研究问题**：
1. 2023年全球AI医疗市场规模是多少？
2. 2024-2030年的CAGR预测是多少？
3. 最大的细分领域是什么（影像、药物发现、决策支持等）？
4. 增长的主要驱动因素是什么？

**输出要求**：
- 关键发现（3-5个要点）
- 市场数据表（细分领域、规模、增长率）
- 来源引用（每个来源：作者、日期、标题、URL）
- 来源质量评级（A-E级）

**重要**：
- 对比至少2个市场研究机构的数据
- 标注数据差异和原因
- 区分估计值和实际值
```

---

## 示例4：来源质量评级实操

### 来源评级示例

```markdown
## 来源归档

### A级来源
1. Smith, J. et al. (2023). "AI vs Human Diagnostic Accuracy 
   in Radiology." Nature Medicine, 29(4), 890-901. 
   https://doi.org/10.1038/s41591-023-xxxx
   质量：A - 同行评审，顶级期刊，大样本RCT

2. Jones, K. et al. (2024). "Deep Learning in Digital Pathology." 
   Lancet Digital Health, 6(2), e112-e125. 
   https://doi.org/10.1016/S2589-7500(24)xxxxx
   质量：A - 同行评审，高影响因子期刊

### B级来源
3. Grand View Research. (2024). "AI in Healthcare Market Report." 
   https://www.grandviewresearch.com/industry-analysis/...
   质量：B - 知名市场研究机构，方法论透明

4. Gartner. (2024). "Magic Quadrant for Healthcare AI." 
   https://www.gartner.com/...
   质量：B - 知名分析机构，行业基准

### C级来源
5. Davis, R. (2024). "Real-World AI Deployment Challenges." 
   Healthcare IT News, March 2024. 
   https://www.healthcareitnews.com/...
   质量：C - 行业新闻，非同行评审但信息有价值
```

---

## 示例5：处理研究空白

### 发现空白时

```markdown
## 研究空白

### 已识别的空白

1. **长期结果数据**
   - 状态：❌ 无法找到
   - 原因：AI诊断工具部署时间不足5年
   - 建议：标注为"待未来研究"

2. **中国市场特定数据**
   - 状态：⚠️ 有限
   - 已找到：整体市场数据
   - 缺失：中国三甲医院的具体采用率
   - 建议：部署额外智能体搜索中文来源

3. **成本效益分析**
   - 状态：⚠️ 部分
   - 已找到：初始投资数据
   - 缺失：5年TCO和ROI数据
   - 建议：在报告中标注"初步估计"
```

### 行动

```
发现空白后：
1. 对中国市场数据 → 部署额外智能体搜索中文来源
2. 对长期结果 → 报告中标注"研究前沿，数据不足"
3. 对TCO/ROI → 使用现有数据计算估算值，标注为"估计"
```

---

## 关键原则总结

1. **验证输入** → 确保结构化提示词完整
2. **规划充分** → 好的搜索查询决定研究质量
3. **并行部署** → 始终并行，永不顺序
4. **质量控制** → 每个来源都有评级
5. **诚实面对空白** → 不知道的就标注不知道
6. **结构化输出** → 统一的报告格式
7. **可重复性** → 保留所有来源和查询记录
