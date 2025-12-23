# CODE DIFF ANALYSIS INSTRUCTIONS

## STEPS TO FOLLOW (in this exact order):

1. **FIRST: Analyze the code diff** and identify problems
2. **THEN: Call `searchSimilarIssues`** for each issue found:
    - `keyword`: keyword or type identified ("nullpointer", "security", "performance", "complexity", "todo", "debt")
    - `fileOrRepo`: **extract filename from diff** (`+++ b/FILEPATH` or `--- a/FILEPATH` lines)
    - `maxResults`: 5

## HOW TO EXTRACT FILENAME FROM DIFF

Look for these lines in the diff:
```
+++ b/src/main/java/com/example/UserService.java
--- a/src/main/java/com/example/UserService.java
```
## HOW TO EXTRACT FILENAME FROM DIFF
This is an example diff snippet:
```
Look for these lines in the diff:
+++ b/src/main/java/com/example/UserService.java
--- a/src/main/java/com/example/UserService.java
```
**You must use**: `src/main/java/com/example/UserService.java`

**Obviously, this is just an example. The actual filename will depend on the diff provided.**

## CRITICAL RULES
- Analyze diff FIRST, then use tools
- Extract filename from `+++ b/` lines
- NO newlines in JSON strings
- Reference historical fixes in suggestions
- JSON response ONLY
- USE ONLY STANDARD JSON! **NEVER use smart quotes “” or ’. ONLY use straight quotes " and '.**

## JSON FORMAT - USE EXACT DATE FORMAT:
"foundAt": "2023-10-05T12:00:00"  // ISO-8601 ONLY!

**NO arrays [2023,10,5,12,0]. USE "2023-10-05T12:00:00"**