# Testing Patterns

**Analysis Date:** 2026-03-31

## Test Framework

**Runner:**
- Java: JUnit 5 (Spring Boot Test)
- TypeScript: Not explicitly detected (Vite default)

**Assertion Library:**
- Java: Spring Boot Test assertions
- TypeScript: Not explicitly detected

**Mocking:**
- Java: Mockito 5.20.0

**Run Commands:**
```bash
# Java - Run all tests
./mvnw test

# Java - Run specific module tests
./mvnw test -pl bootstrap

# TypeScript - Run tests (not configured)
npm run test
```

## Test File Organization

**Location:**
- **Java:** Co-located with main code in `src/test/java/`
- **TypeScript:** Not explicitly detected

**Naming:**
- Java: `{ClassName}Tests.java` - `IntentTreeServiceTests.java`, `QueryRewriteTests.java`

**Structure:**
```
src/
├── main/
│   └── java/com/nageoffer/ai/ragent/
│       └── ingestion/service/
│           ├── IngestionTaskService.java
│           └── IngestionTaskServiceImpl.java
└── test/
    └── java/com/nageoffer/ai/ragent/
        └── ingestion/service/
            └── IngestionTaskServiceTests.java
```

## Test Structure

**Suite Organization:**
```java
@Slf4j
@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IntentTreeServiceTests {

    private final IntentTreeService intentTreeService;

    @Test
    public void initFromFactory() {
        intentTreeService.initFromFactory();
    }

    @Test
    public void getFullTree() {
        List<IntentNodeTreeVO> roots = intentTreeService.getFullTree();
        // 测试逻辑
    }
}
```

**Patterns:**
- **Setup:** @SpringBootTest annotation for integration tests, @RequiredArgsConstructor for dependency injection
- **Teardown:** Not explicitly observed
- **Assertion:** Using Spring Boot Test assertions

## Mocking

**Framework:** Mockito 5.20.0

**Patterns:**
```java
// 示例（未直接观察，但在 pom.xml 中配置）
@Mock
private IngestionEngine ingestionEngine;

@InjectMocks
private IngestionTaskServiceImpl ingestionTaskService;

@BeforeEach
void setUp() {
    MockitoAnnotations.openMocks(this);
}

@Test
void testExecute() {
    // 模拟行为
    when(ingestionEngine.process(any())).thenReturn(mock(IngestionResult.class));

    // 执行测试
    IngestionResult result = ingestionTaskService.execute(mockRequest);

    // 验证
    assertNotNull(result);
    verify(ingestionEngine).process(any());
}
```

**What to Mock:**
- External dependencies (APIs, databases, message queues)
- Complex services with side effects

**What NOT to Mock:**
- Value objects and DTOs
- Simple utility functions
- Domain logic that should be tested directly

## Fixtures and Factories

**Test Data:**
```java
// 直接在测试中创建对象
@Test
public void getFullTree() {
    List<IntentNodeTreeVO> roots = intentTreeService.getFullTree();
    // 处理结果
}
```

**Location:**
- Test data is typically created inline within test methods

## Coverage

**Requirements:** Not explicitly enforced

**View Coverage:**
```bash
# Java - Generate coverage report
./mvnw test -pl bootstrap -Dtest=IntentTreeServiceTests -Djacoco.skip=false

# TypeScript - Not configured
```

## Test Types

**Unit Tests:**
- Scope: Individual methods and classes
- Approach: Isolate the unit under test using mocks
- Example: `QueryRewriteTests.java`

**Integration Tests:**
- Scope: Multiple components working together
- Approach: Use @SpringBootTest to load the application context
- Example: `IntentTreeServiceTests.java`

**E2E Tests:**
- Framework or "Not used" - Not explicitly detected

## Common Patterns

**Async Testing:**
- Not explicitly observed

**Error Testing:**
```java
@Test
void testNullRequest() {
    assertThrows(ClientException.class, () -> {
        ingestionTaskService.execute(null);
    });
}
```

---

*Testing analysis: 2026-03-31*