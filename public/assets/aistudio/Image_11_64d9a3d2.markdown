---
name: Verdant Shield
colors:
  surface: '#f8faf9'
  surface-dim: '#d8dada'
  surface-bright: '#f8faf9'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f2f4f3'
  surface-container: '#eceeed'
  surface-container-high: '#e6e9e8'
  surface-container-highest: '#e1e3e2'
  on-surface: '#191c1c'
  on-surface-variant: '#3d4a3d'
  inverse-surface: '#2e3131'
  inverse-on-surface: '#eff1f0'
  outline: '#6d7b6c'
  outline-variant: '#bccbb9'
  surface-tint: '#006e2f'
  primary: '#006e2f'
  on-primary: '#ffffff'
  primary-container: '#22c55e'
  on-primary-container: '#004b1e'
  inverse-primary: '#4ae176'
  secondary: '#456554'
  on-secondary: '#ffffff'
  secondary-container: '#c7ebd4'
  on-secondary-container: '#4b6b59'
  tertiary: '#3b6925'
  on-tertiary: '#ffffff'
  tertiary-container: '#86ba6b'
  on-tertiary-container: '#1c4a07'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#6bff8f'
  primary-fixed-dim: '#4ae176'
  on-primary-fixed: '#002109'
  on-primary-fixed-variant: '#005321'
  secondary-fixed: '#c7ebd4'
  secondary-fixed-dim: '#abcfb9'
  on-secondary-fixed: '#002113'
  on-secondary-fixed-variant: '#2e4d3d'
  tertiary-fixed: '#bbf29d'
  tertiary-fixed-dim: '#a0d583'
  on-tertiary-fixed: '#062100'
  on-tertiary-fixed-variant: '#23510e'
  background: '#f8faf9'
  on-background: '#191c1c'
  surface-variant: '#e1e3e2'
typography:
  display-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 48px
    fontWeight: '800'
    lineHeight: 56px
    letterSpacing: -0.03em
  display-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 36px
    fontWeight: '700'
    lineHeight: 44px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 36px
    letterSpacing: -0.02em
  headline-lg-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 22px
    fontWeight: '700'
    lineHeight: 30px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
    letterSpacing: -0.01em
  headline-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '600'
    lineHeight: 24px
    letterSpacing: 0em
  body-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
    letterSpacing: -0.01em
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
    letterSpacing: 0em
  body-sm:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 16px
    letterSpacing: 0.01em
  label-lg:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
    letterSpacing: 0.01em
  label-md:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.02em
  label-sm:
    fontFamily: Inter
    fontSize: 10px
    fontWeight: '700'
    lineHeight: 14px
    letterSpacing: 0.05em
rounded:
  sm: 0.5rem
  DEFAULT: 1rem
  md: 1.5rem
  lg: 2rem
  xl: 3rem
  full: 9999px
spacing:
  gutter: 1rem
  gutter-mobile: 0.75rem
  margin: 1.5rem
  margin-mobile: 1.25rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
---

## Brand & Style

This design system expresses a calm, approachable, and transparent mobile security experience. Moving away from alarmist dark interfaces and aggressive neon grids, it establishes trust through clarity, breathing room, and soft natural vitality. The aesthetic balances modern minimalism with tactile reassurance: expansive crisp surfaces, vivid bio-luminescent emerald accents, and grounded deep forest typography that conveys steady authority without intimidation.

The emotional target is effortless peace of mind. Visual structures rely on friendly geometry, generous border radiuses, and clear concentric state indicators that instantly communicate system health and protection at a glance.

## Colors

The palette revolves around fresh, high-contrast greens on an immaculate off-white base:

- **Primary (`#22C55E`)**: Vibrant emerald green applied to critical status states, focus rings, and active toggle indicators.
- **Secondary (`#133324`)**: Deep forest slate green used for primary headlines, high-contrast copy, icon strokes, and prominent dark pill surfaces.
- **Tertiary (`#BBF29D`)**: Soft lime-mint accent deployed across secondary card highlights, contextual action banners, and ambient concentric glow rings.
- **Neutral (`#FBFDFC`)**: Pure crisp background canvas complemented by subtle sage-tinted borders (`#E2ECE6`) and soft container fills (`#F3F8F5`).
- **Support & Semantic**: Muted body text is rendered in deep sage-slate (`#4A6356`), while inactive or disabled elements use soft forest mist (`#D2DDD7`). Error and alert states maintain the soft organic tone with a bright coral-red (`#EF4444`).

## Typography

The type scale combines **Plus Jakarta Sans** for expressive, humanistic headings with **Inter** for crisp, highly readable data points and body text. 

- **Headlines & Status Indicators**: Plus Jakarta Sans brings rounded friendly terminal strokes that pair naturally with pill-shaped visual motifs. Large state text (such as toggle states like "ON" / "OFF") adopts heavy weights with tight negative letter-spacing for immediate glanceable impact.
- **Body & Labels**: Inter handles all descriptive and functional copy. Its systematic vertical metrics guarantee clean optical alignment alongside icons, badges, and inline status chips.
- **Hierarchy**: Major screen titles use `headline-lg` or `headline-lg-mobile`. Section headers (`headline-md`) establish distinct conceptual zones, while secondary guidance relies strictly on muted `body-md` and `body-sm`.

## Layout & Spacing

The layout model is driven by an airy, single-column fluid container on mobile devices, expanding into an anchored 4-to-8 column grid structure on wider viewports.

- **Vertical Cadence**: Ample vertical breathing room reinforces psychological calm. Focal security controls (e.g., hero status toggles) are isolated with generous margins (`space-xl` to `2.5rem`) to eliminate accidental taps and emphasize state clarity.
- **Card and Component Flow**: Actions and recommendations sit within stacked cards using `space-md` gaps. Internal card paddings stay uniform at `space-md` (vertical) and `space-lg` (horizontal) to preserve comfortable tap targets.
- **Responsive Adaptations**:
  - **Mobile (<640px)**: 1.25rem outer edge margin, centered hero actions, full-width pill action buttons.
  - **Tablet/Desktop (≥640px)**: Content max-width constrained to 480px–640px for utility dialogs and mobile-first screens, preserving touch ergonomics and visual focus.

## Elevation & Depth

Visual depth is achieved through delicate tinted tonal layering and multi-layered soft glows rather than harsh drop shadows.

- **Concentric Ambient Rings**: Primary state controls utilize concentric stroke tiers (`4px` to `8px` widths) combining solid vibrant greens with semi-transparent halos (`rgba(34, 197, 94, 0.2)` and `rgba(187, 242, 157, 0.4)`), generating depth through radial stacking.
- **Surfaces**: 
  - Level 0 (Base canvas): Solid `#FBFDFC`.
  - Level 1 (Interactive cards): Soft tinted sage-white (`#F3F8F5`) bounded by a subtle `1px` border in `#E2ECE6`.
  - Level 2 (Floating sheets / modals): Crisp `#FFFFFF` with an ambient diffused shadow: `0 12px 32px -8px rgba(19, 51, 36, 0.08)`.
- **Highlights**: Pill buttons use low-contrast internal rim borders (`1px solid rgba(255, 255, 255, 0.6)`) to convey tactile softness.

## Shapes

The geometric identity is defined by Level 3 pill roundedness (`1rem` base, scaling to `2rem` and full circles).

- **Buttons & Pills**: Fully rounded pill shapes (`border-radius: 9999px`) for action banners, tags, and navigation elements.
- **Containers & Cards**: Generously softened corners (`border-radius: 1.5rem` to `2rem`) that hug inner elements and reinforce the friendly protective aesthetic.
- **State Hero Control**: Concentric circular geometry containing bold typographic indicators.

## Components

### Status Hero Indicator
- **Structure**: A large circular toggle button (`160px` to `200px`) centered on the canvas.
- **Appearance**: Deep forest green core fill (`#133324`) housing bold display text ("ON" / "OFF") in crisp white or soft mint. Encased in concentric rings: an inner vibrant emerald band (`#22C55E`, 8px) and an outer soft translucent ring (`#BBF29D`, 6px).
- **Subtext**: Stacked immediately below the hero circle with `headline-sm` in deep forest and auxiliary tap prompts in muted sage (`#4A6356`).

### Action Banners & Cards
- **Primary Action Banner**: Full-width pill-shaped container with tertiary lime-mint fill (`#BBF29D`). Includes an icon on the left, clear action copy in deep forest (`#133324`), and a trailing arrow icon. Padding: `16px 20px`.
- **Standard Card**: Off-white tinted background (`#F3F8F5`), 1px solid border (`#E2ECE6`), `24px` border radius, containing contextual guidance, status metrics, or diagnostic lists.

### Buttons & Pills
- **Primary**: Solid emerald (`#22C55E`) or deep forest (`#133324`) pill with high-contrast text (`#FFFFFF`), `16px` vertical padding, `24px` horizontal padding.
- **Secondary / Soft**: Mint-tinted pill (`#BBF29D`) with `#133324` text for non-destructive, quick-access tools.
- **Ghost / Utility**: Borderless pill with subtle hover state (`#E2ECE6`), used for header navigation and circular back/settings icons.

### Inputs & Search Fields
- **Container**: Fully pill-shaped or rounded (`20px`) input container with `#F3F8F5` surface fill and `#E2ECE6` border.
- **Focus State**: Crisp `2px` ring in `#22C55E` with no harsh box-shadows. Placeholder styled in muted sage (`#4A6356`).

### Selection Controls
- **Switches**: Pill-shaped track (`#E2ECE6` inactive, `#22C55E` active) with a pure white circular thumb sliding seamlessly.
- **Checkmarks & Chips**: Filter chips use pill shapes with `#F3F8F5` base and a `#133324` text label, switching to active state with `#133324` background and `#BBF29D` text/icon accents.