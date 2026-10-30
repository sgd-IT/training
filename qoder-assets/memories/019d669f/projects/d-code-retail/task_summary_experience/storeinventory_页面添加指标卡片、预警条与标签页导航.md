---
title: "store/inventory 页面添加指标卡片、预警条与标签页导航"
usage_scenario:
    - "When enhancing an existing inventory management page with visual metrics and overview capabilities"
    - "When integrating echarts into a Vue3 + Element Plus + UnoCSS frontend for time-series data visualization"
keywords:
    - "store/inventory"
    - "summary grid"
    - "metric cards"
    - "stock warning"
    - "tabs navigation"
    - "echarts"
    - "Vue3"
    - "Element Plus"
    - "UnoCSS"
source: "auto"
---

## Task description
- Core requirement: enhance the /store/inventory page UI by adding a summary grid (4 metric cards), a stock warning band below it, and tab navigation to switch between "进出货记录" and "库存概览"
- Core requirement (new): modify the toolbar buttons in the /store/inventory/records.vue page to match the style shown in the reference image, then merge the two separate buttons ('登记进货' and '登记出货') into a single '登记进出货' button.
- Task background: the inventory page existed but lacked visual metrics and overview; user provided HTML/CSS for the summary grid and warning band; echarts was already installed (6.1.0) and needed integration for the trend chart in the overview tab; the original toolbar had three buttons (新增/修改/删除) with Element Plus default styling; the user provided an image showing three new buttons with specific colors, icons, and labels; the goal was to replace the old buttons and then simplify the UI by merging the first two.

## Execution process
1. index.vue: add the summary grid HTML above the search-wrap div, using classes like summary-grid, metric-card, metric-top, etc.
2. index.vue: add CSS styles for all summary grid and metric card classes in a scoped <style> block, using Element Plus variables for theme compatibility
3. index.vue: replace Font Awesome <i class="fas ..."> icons with @iconify/vue <Icon> components (fa6-solid:* variants) since FA is not installed
4. index.vue: add the stock warning band HTML below the summary grid and above the search-wrap, with three state classes (warning/danger/safe)
5. index.vue: add tab navigation HTML below the stock band, with buttons for "进出货记录" and "库存概览"
6. index.vue: wrap the existing search-wrap + table card section with v-show="activeTab === 'records'"
7. index.vue: add the overview section (chart-grid with echarts canvas and stock facts sidebar) with v-show="activeTab === 'overview'"
8. index.vue: add script logic: import echarts, define activeTab ref, implement initChart() and switchTab() functions, and add onBeforeUnmount cleanup
9. index.vue: add CSS styles for tabs, chart-grid, chart-card, stock-facts, fact, and tag classes
10. Replaced the original 新增/修改/删除 buttons with three new buttons matching the image: '登记进货' (blue filled, download icon), '登记出货' (white outlined, upload icon), and '导出' (white outlined, download icon).
11. Added a delete button back to the toolbar after the export button, using the danger color and outlined style.
12. Merged the '登记进货' and '登记出货' buttons into a single '登记进出货' button (blue filled, plus icon), which opens the same dialog but with the '进出货类型' radio group pre-set to '进货' by default, allowing the user to select '进货' or '出货' within the dialog.
13. Updated the handleAdd function to remove the type parameter and simply open the dialog with the title '登记进出货'.
14. Verified that the form's type field defaults to 1 (进货) and is editable in the dialog.

## Related files
- d:\code\retail\anwei-fireworks-retail-admin\src\views\store\inventory\index.vue
- d:\code\retail\anwei-fireworks-retail-admin\src\views\store\inventory\records.vue

## Notes
- Font Awesome is not installed in the project, so all <i class="fas ..."> tags were replaced with @iconify/vue <Icon> components
- echarts@6.1.0 was already installed and used for the trend chart
- The chart currently displays placeholder random data (ponytail: note for future API integration)
- The '登记进出货' button uses a Plus icon instead of Download/Upload to represent the unified action.
- The dialog's '进出货类型' radio group allows the user to choose between '进货' and '出货' after opening the dialog.
- The handleAdd function no longer accepts a type parameter; it now directly opens the dialog with the title '登记进出货'.

## Task overview
Successfully enhanced the /store/inventory page UI: added 4-metric summary grid with proper styling and Iconify icons; added stock warning band with three state variants; implemented tab navigation to switch between records and overview; integrated echarts for the trend chart in the overview tab with proper initialization and cleanup logic; replaced the original toolbar buttons with image-matching styles; added a delete button; merged '登记进货'/'登记出货' into a single '登记进出货' button with unified dialog behavior. All changes compile and function correctly.
