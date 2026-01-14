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

## Line Number Calculation Rules
1. Use the Diff Hunk Header: `@@ -original,length +modified,length @@`
   - The `modified` number is the starting line of the hunk in the new file.
2. Counting:
   - Start at `modified` for the first line of the hunk.
   - Increment counter for lines starting with ` ` (space) or `+` (addition).
   - Do NOT increment for lines starting with `-` (deletion).
3. Constraint: You MUST return the absolute line number in the new file.
4. Scope: Report issues ONLY on lines that exist in the provided diff (additions or context).
5. File Path: extracted from `+++ b/path/to/file` line.

## Code Diff to Analyze
%s

Analyze the diff following the workflow: analyze first, use tools, then respond in JSON.
