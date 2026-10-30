---
title: "Vue Options API imported function must be exposed in methods for template use"
usage_scenario:
    - "When debugging 'Property X was accessed during render but is not defined on instance' in Vue Options API components"
    - "When writing new Vue Options API components that need to use utility functions in templates"
keywords:
    - "Vue Options API"
    - "template function"
    - "methods exposure"
    - "import scope"
source: "auto"
---

Bug class: Vue template function resolution failure in Options API
Root cause: In Vue Options API components, imported functions (e.g., `import { fmtTime } from '@/utils/card'`) exist only in the module scope, not on the component instance; the template resolves against `this`, so `fmtTime(...)` fails with 'not defined on instance'
Fix pattern: Add the imported function to the `methods` object (e.g., `methods: { fmtTime }`) to bind it to the instance
Reusable lesson: Don't assume imported utility functions are automatically available in Vue Options API templates because they reside in module scope, not instance scope; instead explicitly expose them in `methods`. Applies when using Vue Options API and referencing imported functions in the template; does not apply to `<script setup>` where imports are auto-exposed.
