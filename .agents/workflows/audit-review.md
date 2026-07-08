---
description: Audit
---

## Target Agent Role
Act as an Android security auditor and reverse engineer.

## Objective
Conduct a comprehensive security audit of the Android APK file located in the workspace 'nodaysidle-werkstein-v2' and present findings along with actionable recommendations.

## Context
The APK file is available in the designated workspace. Assume the file is named 'app.apk' unless specified otherwise. The audit should cover static analysis, permission evaluation, code review for vulnerabilities, and assessment of third-party libraries.

## Instructions
1. Decompile the APK using tools like APKTool or JADX (in your environment).
2. List all requested permissions and assess their necessity and risk (e.g., over-privilege).
3. Examine the AndroidManifest.xml for security misconfigurations (e.g., exported components, debug flag).
4. Analyze the decompiled code for common vulnerabilities: insecure data storage, hardcoded secrets, improper SSL/TLS handling, WebView risks, and code obfuscation quality.
5. Identify third-party libraries and check for known vulnerabilities (CVEs).
6. Check for dynamic code loading, misuse of reflection, and intent handling issues.
7. Summarize findings in a structured report.

## Constraints
- Do not execute or install the APK; perform only static analysis.
- Respect ethical guidelines; do not modify or distribute the APK or its contents.
- If any identified vulnerability requires dynamic analysis to confirm, note it as a recommendation.
- Ensure the report is clear for a non-technical audience but includes technical details.

## Output Format
Provide a structured report with the following sections:
- Executive Summary
- Permissions Analysis
- Manifest Misconfigurations
- Code Vulnerabilities
- Third-Party Library Risks
- Overall Security Score (e.g., Low/Medium/High Risk)
- Actionable Recommendations (prioritized by severity)

## Acceptance Criteria
- The report must cover all the major security aspects listed above.
- Each finding must include severity (Critical/High/Medium/Low) and potential impact.
- Recommendations must be specific and actionable (e.g., "Remove unused permission '...'" or "Use HTTPS instead of HTTP in network calls").
- The analysis should be reproducible; list the exact tools and commands used.