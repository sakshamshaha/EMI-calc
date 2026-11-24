---
name: Cyber-Luxe Fintech
colors:
  surface: '#12121e'
  surface-dim: '#12121e'
  surface-bright: '#383845'
  surface-container-lowest: '#0c0d18'
  surface-container-low: '#1a1b26'
  surface-container: '#1e1f2b'
  surface-container-high: '#282935'
  surface-container-highest: '#333440'
  on-surface: '#e2e1f1'
  on-surface-variant: '#cbc3d7'
  inverse-surface: '#e2e1f1'
  inverse-on-surface: '#2f2f3c'
  outline: '#958ea0'
  outline-variant: '#494454'
  surface-tint: '#d0bcff'
  primary: '#d0bcff'
  on-primary: '#3c0091'
  primary-container: '#a078ff'
  on-primary-container: '#340080'
  inverse-primary: '#6d3bd7'
  secondary: '#4cd7f6'
  on-secondary: '#003640'
  secondary-container: '#03b5d3'
  on-secondary-container: '#00424e'
  tertiary: '#ffb0cd'
  on-tertiary: '#640039'
  tertiary-container: '#f751a1'
  on-tertiary-container: '#570032'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#e9ddff'
  primary-fixed-dim: '#d0bcff'
  on-primary-fixed: '#23005c'
  on-primary-fixed-variant: '#5516be'
  secondary-fixed: '#acedff'
  secondary-fixed-dim: '#4cd7f6'
  on-secondary-fixed: '#001f26'
  on-secondary-fixed-variant: '#004e5c'
  tertiary-fixed: '#ffd9e4'
  tertiary-fixed-dim: '#ffb0cd'
  on-tertiary-fixed: '#3e0022'
  on-tertiary-fixed-variant: '#8c0053'
  background: '#12121e'
  on-background: '#e2e1f1'
  surface-variant: '#333440'
typography:
  display-hero:
    fontFamily: Space Grotesk
    fontSize: 48px
    fontWeight: '700'
    lineHeight: 56px
    letterSpacing: -0.03em
  display-hero-mobile:
    fontFamily: Space Grotesk
    fontSize: 36px
    fontWeight: '700'
    lineHeight: 44px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Space Grotesk
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-lg-mobile:
    fontFamily: Space Grotesk
    fontSize: 26px
    fontWeight: '700'
    lineHeight: 34px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Space Grotesk
    fontSize: 22px
    fontWeight: '600'
    lineHeight: 30px
    letterSpacing: -0.01em
  headline-sm:
    fontFamily: Space Grotesk
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 26px
  body-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  body-sm:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 18px
  label-lg:
    fontFamily: Space Grotesk
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
    letterSpacing: 0.04em
  label-md:
    fontFamily: Space Grotesk
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.06em
  label-caps:
    fontFamily: Space Grotesk
    fontSize: 10px
    fontWeight: '700'
    lineHeight: 14px
    letterSpacing: 0.12em
  numeric-metric:
    fontFamily: Space Grotesk
    fontSize: 40px
    fontWeight: '700'
    lineHeight: 44px
    letterSpacing: -0.02em
rounded:
  sm: 0.5rem
  DEFAULT: 1rem
  md: 1.5rem
  lg: 2rem
  xl: 3rem
  full: 9999px
spacing:
  gutter: 1rem
  gutter-tablet: 1.5rem
  gutter-desktop: 2rem
  margin: 1.25rem
  margin-tablet: 2rem
  margin-desktop: 3rem
  space-2xs: 0.25rem
  space-xs: 0.5rem
  space-sm: 0.75rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
  space-2xl: 3rem
  space-3xl: 4rem
---

## Brand & Style

This design system blends futuristic cyberpunk aesthetics with ultra-premium consumer fintech. Tailored for next-generation borrowers, digital natives, and tech-forward consumers calculating amortizations, loan tenures, and interest structures, it transforms traditionally dry financial modeling into an immersive, high-velocity digital experience.

The visual style combines high-contrast darkness with luminous glassmorphism. Deep obsidian voids meet luminous gas discharges—electric violet, hyper-cyan, and vivid magenta strokes that illuminate actionable controls. Elements feature multi-stop translucent backdrops, frosted depth filters, and tactile luminescence. The emotional intent is calculated mastery, playful dynamism, and unwavering computational precision.

## Colors

The palette operates on deep cosmic contrast:
- **Base Surfaces:** The primary canvas anchors on `#090A15`, with layered elevations stepping up through `#0D0F24` and `#141836`. Translucent container tiers utilize variable alpha channels (`rgba(13, 15, 36, 0.65)` to `rgba(20, 24, 54, 0.45)`).
- **Primary Light Source (`#8B5CF6` Electric Violet):** Dominant brand anchor, core keyframes, and primary call-to-actions.
- **Secondary Beam (`#06B6D4` Luminous Cyan):** Real-time interactive calculation feedback, principal repayment markers, and sliding track heads.
- **Tertiary Flare (`#EC4899` Hot Pink):** Interest cost visualizations, premium upgrade badges, and alerts.
- **Accent Highlight (`#10B981` Neo-Emerald):** Loan approvals, optimal EMI configurations, and net savings indicators.
- **Text & Borders:** Off-white highlights (`#F8FAFC`) ensure readability against deep voids, supported by muted cyber-gray labels (`#94A3B8`). Outlines use linear gradient borders transitioning from luminous tones to zero-opacity fades.

## Typography

The type system balances technical edge and legible financial clarity:
- **Headings & Metrics (Space Grotesk):** Provides engineered, geometric precision. Tabular figures must be enabled (`font-variant-numeric: tabular-nums`) across all numerical metrics, repayment schedules, and interactive slider indicators to avoid visual jitter during live updates.
- **Body & Microcopy (Inter):** Maximizes readability inside compact cards, analytical tables, and dense loan condition breakouts.
- **Labels & Micro-Tags:** Rendered in Space Grotesk with expanded letter spacing, creating an instrumentation-panel feel.

## Layout & Spacing

The layout is built around mobile-first utility with responsive expansion:
- **Grid Architecture:** A 4-column layout on mobile devices transitions into an 8-column layout on tablets and a 12-column layout on web views, maxing out at `1280px` centered width.
- **Mobile Safe Area & Padding:** Mobile interfaces preserve a compact `1.25rem` outer canvas padding to maximize screen space for calculators, sliders, and dynamic pie/arc charts.
- **Stacking Flow:** Dynamic calculator inputs and interactive dials stack vertically on narrow devices, reflowing into a split-screen matrix (inputs on the left, interactive repayment donut/schedules on the right) at `768px` breakpoints and above.

## Elevation & Depth

Visual hierarchy does not use murky grey drop shadows; it relies on multi-layer luminous glassmorphism and colored photon fields:
- **Base Layer (L0):** Solid obsidian/navy canvas (`#090A15`) treated with ambient static linear mesh gradients.
- **Frosted Panel (L1):** Background `rgba(13, 15, 36, 0.65)`, backdrop filter blur of `20px` (`blur-xl`), paired with a top-to-bottom border stroke gradient: `linear-gradient(180deg, rgba(139, 92, 246, 0.3) 0%, rgba(6, 182, 212, 0.05) 100%)`.
- **Raised Interactive Surfaces (L2):** Input nodes, segment pickers, and active calculator cards feature `rgba(20, 24, 54, 0.8)`, an inner rim highlight of `1px solid rgba(255, 255, 255, 0.12)`, and a diffused soft glow: `box-shadow: 0 8px 32px -4px rgba(139, 92, 246, 0.15)`.
- **Floating Controls & Modals (L3):** Sticky calculate triggers, loan breakdown sheets, and bottom navigation draw an intense atmospheric perimeter: `box-shadow: 0 20px 48px -8px rgba(6, 182, 212, 0.25), 0 0 1px 1px rgba(255, 255, 255, 0.2)`.

## Shapes

The interface embraces pill geometry and curved silhouettes:
- Outer panels and glass cards utilize continuous rounded corners (`24px` to `32px` on major surfaces, matching the pill-grade scale).
- Interactive buttons, filter pills, value chips, and numerical toggles are full stadium pills (`9999px` / `rounded-full`).
- Range slider thumbs, donut segments, and progress trackers avoid square terminations; rounded caps reinforce a fluid, futuristic aesthetic.

## Components

### Action Buttons
- **Primary Action (Luminous Pill):** Stadium profile, filled with a `135deg` gradient from `#8B5CF6` to `#06B6D4`. Includes an ambient neon bloom (`box-shadow: 0 0 24px rgba(139, 92, 246, 0.45)`). Font is Space Grotesk semi-bold with white text.
- **Secondary / Ghost:** Glass pill (`rgba(255, 255, 255, 0.05)`) with an outer border gradient (`#8B5CF6` to `#EC4899` at `40%` opacity). Expands to full neon illumination on hover or active tap.

### Interactive Loan Sliders
- **Track & Rail:** Unfilled track features a `6px` height with a dark background (`rgba(255, 255, 255, 0.08)`); filled dynamic track utilizes a continuous horizontal gradient (`#8B5CF6` to `#06B6D4`).
- **Thumb:** A `28px` floating orb clad in pure white, bounded by an electric cyan ring (`2px`) with dynamic glow flares while dragging. Real-time bubble tags float directly above the thumb.

### Financial Glass Cards
- Built with `28px` border radius, frosted translucency (`backdrop-blur-xl`), and micro-gradient borders. Internal spacing holds `space-lg` margins.
- Top-right corner reserves space for neon status badges (e.g., "Optimal Rate" in `#10B981`).

### Form Fields & Loan Amount Inputs
- Input containers feature inset dark backgrounds (`rgba(9, 10, 21, 0.6)`) with subtle inset shadows and `1px` border lines that transition to solid luminous cyan upon focus.
- Prefix symbols (`$`, `€`, `₹`) render in bold primary violet typography. Numbers use tabular figures to maintain layout stability.

### Choice Chips & Tenure Selectors
- Segmented tenure options (e.g., 1 Yr, 3 Yrs, 5 Yrs) feature horizontal pill strips.
- Unselected: Semi-transparent text on clean dark glass.
- Selected: Solid violet/cyan background glow with high-contrast white text and an instant scale bounce micro-interaction (`scale-105`).

### Visual Charts (The EMI Donut / Radar)
- Multi-color neon stroke gauge with glowing outer filters (`drop-shadow(0 0 8px rgba(139, 92, 246, 0.5))`).
- Segment A (Principal): Luminous Cyan (`#06B6D4`).
- Segment B (Total Interest): Hot Pink (`#EC4899`).
- Centered within the arc: Tabular dynamic monthly EMI counter in `numeric-metric` styling.