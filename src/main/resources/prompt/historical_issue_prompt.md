# TOOL USAGE INSTRUCTIONS

You have access to `searchSimilarIssues(keyword, fileOrRepo, maxResults)`.

## When to use it
After identifying each issue in the code diff, call this tool to find similar historical issues.

## How to extract filename from diff
Git diffs show filenames like this:
```text
+++ b/src/main/java/com/example/UserService.java
--- a/src/main/java/com/example/UserService.java
```
**Use the path after `b/`**: `src/main/java/com/example/UserService.java`

## Parameters
- `keyword`: Issue type - one of: "nullpointer", "security", "performance", "complexity", "todo", "debt"
- `fileOrRepo`: Extracted filename from diff
- `maxResults`: Always use 5

## Workflow
1. FIRST: Analyze the diff completely
2. THEN: For each issue found, call `searchSimilarIssues`
3. FINALLY: Incorporate historical context in your suggestions

