---
title: "store/inventory 页面记录列表工具栏按钮样式与行为重构"
usage_scenario:
    - "When refactoring toolbar buttons to match a design specification image"
    - "When simplifying UI by merging similar actions into a single entry point with in-dialog selection"
keywords:
    - "records.vue"
    - "toolbar"
    - "button styling"
    - "dialog"
    - "进出货类型"
source: "auto"
---

## Task description
- Core requirement: modify the toolbar buttons in the /store/inventory/records.vue page to match the style shown in the reference image, then merge the two separate buttons ('登记进货' and '登记出货') into a single '登记进出货' button.
- Task background: the original toolbar had three buttons (新增/修改/删除) with Element Plus default styling; the user provided an image showing three new buttons with specific colors, icons, and labels; the goal was to replace the old buttons and then simplify the UI by merging the first two.

## Execution process
1. Replaced the original 新增/修改/删除 buttons with three new buttons matching the image: '登记进货' (blue filled, download icon), '登记出货' (white outlined, upload icon), and '导出' (white outlined, download icon).
2. Added a delete button back to the toolbar after the export button, using the danger color and outlined style.
3. Merged the '登记进货' and '登记出货' buttons into a single '登记进出货' button (blue filled, plus icon), which opens the same dialog but with the '进出货类型' radio group pre-set to '进货' by default, allowing the user to select '进货' or '出货' within the dialog.
4. Updated the handleAdd function to remove the type parameter and simply open the dialog with the title '登记进出货'.
5. Verified that the form's type field defaults to 1 (进货) and is editable in the dialog.

## Related files
- d:\code\retail\anwei-fireworks-retail-admin\src\views\store\inventory\records.vue

## Notes
- The '登记进出货' button uses a Plus icon instead of Download/Upload to represent the unified action.
- The dialog's '进出货类型' radio group allows the user to choose between '进货' and '出货' after opening the dialog.
- The handleAdd function no longer accepts a type parameter; it now directly opens the dialog with the title '登记进出货'.

## Task overview
Successfully completed the toolbar button refactor: replaced the original buttons with image-matching styles, added a delete button, and merged '登记进货'/'登记出货' into a single '登记进出货' button with unified dialog behavior. All changes compile and function correctly.
