---
title: "Vue Options API component template function exposure requirement"
usage_scenario:
    - "When developing or modifying a Vue Options API component that needs to use imported utility functions in its template"
    - "When debugging 'function not defined on instance' errors in Vue Options API templates"
keywords:
    - "Vue Options API"
    - "template scope"
    - "methods exposure"
    - "utility function"
source: "auto"
---

In Vue Options API components (e.g., `export default { methods }`), imported utility functions are NOT automatically available in the template scope. To use them in template expressions (e.g., `fmtTime(item.time)`), they must be explicitly exposed by adding them to the `methods` object (e.g., `methods: { fmtTime }`). This is a project-specific structural constraint verified during execution.
