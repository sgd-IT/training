---
title: "Frontend visual craft precision guidelines"
usage_scenario:
    - "When implementing visual refinements for frontend components"
    - "When designing new UI components requiring precise visual control"
    - "When reviewing frontend visual implementation against precision standards"
keywords:
    - "visual precision"
    - "micro-luminance"
    - "font-weight compensation"
    - "typing animation"
    - "monospaced numbers"
source: "auto"
---

The frontend-visual-craft skill defines five core visual precision guidelines: 1) Micro-luminance color layers for UI depth, using precise brightness steps (e.g., +2.8% L) from a base color to create a consistent hierarchy without relying on borders; 2) Dark-mode font-weight compensation, reducing font weight by -40 for variable fonts at ≥20px to maintain readability; 3) Streaming typing animation with chunking (≤24 characters), total duration ≤3s, and Intl.Segmenter for correct Chinese/English word breaks; 4) Monospaced numbers and typography protection using tabular-nums and min-width for consistent digit alignment; 5) Personality-driven animations where motion curves and timing are chosen to match the component's purpose (e.g., elastic easing for interactive elements, linear for data displays).
