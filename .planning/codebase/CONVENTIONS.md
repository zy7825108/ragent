# Coding Conventions

**Analysis Date:** 2026-03-31

## Naming Patterns

**Files:**
- Java: PascalCase - `IngestionTaskService.java`, `IngestionTaskServiceImpl.java`
- TypeScript/TSX: PascalCase for components, camelCase for utilities - `App.tsx`, `error.ts`, `authStore.ts`
- Configuration: kebab-case or snake_case - `eslintrc.cjs`, `prettierrc`, `tailwind.config.cjs`

**Functions:**
- Java: PascalCase for methods - `execute()`, `getFullTree()`
- TypeScript: camelCase - `getErrorMessage()`, `safe()`

**Variables:**
- Java: camelCase - `ingestionEngine`, `taskMapper`
- TypeScript: camelCase - `error`, `fallback`, `userContext`

**Types:**
- Java: PascalCase - `IngestionResult`, `DocumentSource`
- TypeScript: PascalCase - `IngestionTaskVO`, `UserContext`

**Packages/Modules:**
- Java: Reverse domain name - `com.nageoffer.ai.ragent.ingestion.service`
- TypeScript: Path aliases with `@/` prefix - `@/components/ui/button`, `@/utils/error`

## Code Style

**Formatting:**
- **Java:** Spotless Maven Plugin with Apache License header
- **TypeScript/TSX:** Prettier
  - singleQuote: false
  - semi: true
  - tabWidth: 2
  - trailingComma: none
  - printWidth: 100

**Linting:**
- **Java:** Not explicitly detected (Spotless handles formatting)
- **TypeScript/TSX:** ESLint
  - extends: eslint:recommended, @typescript-eslint/recommended, react/recommended, react-hooks/recommended, react-refresh/recommended, prettier
  - parser: @typescript-eslint/parser
  - plugins: @typescript-eslint
  - rules: react/react-in-jsx-scope: off, react/prop-types: off

## Import Organization

**Java:**
1. Standard Java libraries
2. Third-party libraries (cn.hutool, com.baomidou, etc.)
3. Project-specific imports

**TypeScript:**
1. React and React DOM
2. Third-party libraries (axios, lucide-react, etc.)
3. Project-specific modules with `@/` alias

**Path Aliases:**
- TypeScript: `@/` maps to `src/` directory
- Examples: `@/components/ui/button`, `@/stores/authStore`, `@/utils/error`

## Error Handling

**Patterns:**

**Java:**
```java
// 使用自定义异常类
throw new ClientException("请求不能为空");

// 使用断言
Assert.notNull(request, () -> new ClientException("请求不能为空"));

// 事务回滚
@Transactional(rollbackFor = Exception.class)
```

**TypeScript:**
```typescript
// 错误信息提取
export function getErrorMessage(error: unknown, fallback: string) {
  if (typeof error === "string" && error.trim()) {
    return error;
  }
  if (error && typeof error === "object") {
    const maybeMessage = (error as { message?: unknown }).message;
    if (typeof maybeMessage === "string" && maybeMessage.trim()) {
      return maybeMessage;
    }
  }
  return fallback;
}
```

## Logging

**Framework:** SLF4J + Logback (Java), console (TypeScript)

**Patterns:**
```java
// Java - 使用 @Slf4j 注解
@Slf4j
public class IntentTreeServiceTests {
    // 日志记录
    log.info("Intent tree initialized");
}
```

## Comments

**When to Comment:**
- Explain why, not what (代码的意图和设计思路)
- Complex business logic
- Edge cases and assumptions
- Public API documentation

**JSDoc/TSDoc:**
```java
/**
 * 数据摄入任务服务接口
 */
public interface IngestionTaskService {

    /**
     * 执行数据摄入任务
     *
     * @param request 创建请求
     * @return 摄入结果
     */
    IngestionResult execute(IngestionTaskCreateRequest request);
}
```

**License Headers:**
- All Java files include Apache License 2.0 header
- Example in `/Users/zhongyang/Desktop/个人工作/study/ragent/bootstrap/src/main/java/com/nageoffer/ai/ragent/ingestion/service/IngestionTaskService.java`

## Function Design

**Size:**
- Prefer small, focused functions
- Complex logic should be split into smaller methods

**Parameters:**
- Use meaningful parameter names
- Avoid excessive parameters (consider DTOs for multiple parameters)

**Return Values:**
- Prefer specific types over generic objects
- Use Optional for nullable returns

## Module Design

**Exports:**
- Java: Public classes/interfaces with @Service, @Component, etc.
- TypeScript: Named exports for utilities, default exports for components

**Barrel Files:** Not explicitly detected

---

*Convention analysis: 2026-03-31*