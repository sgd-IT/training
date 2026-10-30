---
title: "store/inventory 页面拆分为 records 与 overview 子页面并水平布局"
usage_scenario:
    - "When refactoring a monolithic page into independently routable sub-pages"
    - "When implementing responsive horizontal layouts for dashboard components"
    - "When injecting child routes into dynamically loaded menu systems"
keywords:
    - "inventory"
    - "records"
    - "overview"
    - "nested routing"
    - "CSS Grid"
    - "ECharts"
source: "auto"
---

## Task description
- Core requirement: split the /store/inventory page into two independent sub-page components: `records.vue` (进出货记录 list) and `overview.vue` (库存概览 dashboard), and display them horizontally in one row.
- Task background: the original index.vue contained both tabs and inline content; the goal was to separate concerns, improve maintainability, and support independent routing for each view.

## Execution process
1. Created `records.vue`: contains search filters, data table, and CRUD dialogues for inventory records.
2. Created `overview.vue`: contains two ECharts charts (30-day stock trend + 30-day in/out statistics) and a right-side stats panel, all horizontally aligned in a responsive grid.
3. Refactored `index.vue`: removed inline content, kept shared UI elements (summary grid, warning band, tab navigation), and added `<router-view>` to render child routes.
4. Modified `permission.ts`: injected child routes (`/records`, `/overview`) into the dynamic inventory route and set default redirect to `records`.
5. Updated `overview.vue` CSS: changed `.chart-grid` from 2-column to 3-column grid (`1fr 1fr 240px`) with responsive breakpoints (≤1200px → 2 columns, ≤640px → 1 column).
6. Adjusted legend positioning in `overview.vue`: increased grid bottom margin to create space for the '进货/出货' legend labels.

## Related files
- /src/views/store/inventory/records.vue
- /src/views/store/inventory/overview.vue
- /src/views/store/inventory/index.vue
- /src/store/modules/permission.ts

## Notes
- The inventory page now uses nested routing: `/store/inventory` renders index.vue, then redirects to `/store/inventory/records` or `/store/inventory/overview`.
- All three elements (trend chart, bar chart, stats panel) are horizontally aligned in `overview.vue` using CSS Grid with responsive behavior.
- The legend labels for the bar chart were moved down by increasing the grid's bottom margin to provide visual spacing.

## Task overview
Successfully split the inventory page into two independent sub-pages with horizontal layout: created records.vue and overview.vue as standalone components; refactored index.vue to serve as a parent layout with router-view; injected child routes via permission.ts; implemented responsive 3-column grid in overview.vue; adjusted legend positioning for better readability. All changes compile and function correctly.
