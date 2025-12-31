# ANALYSIS TASK

## Language
Code is written in: %s

## Enabled Analysis Flags
Analyze ONLY categories where flag = true:

- NULL POINTER: %b
- TECHNICAL DEBT : %b
- DETECT TODOS: %b
- PREDICT REGRET: %b
- CHECK COMPLEXITY: %b
- DETECT DUPLICATION: %b
- CHECK TEST COVERAGE: %b

**Rules:**
- Skip categories where flag = false
- Don't mention disabled categories in output
- For enabled categories with no issues, state "No issues found"

## Code Diff to Analyze
%s

Analyze the diff following the workflow: analyze first, use tools, then respond in JSON.
