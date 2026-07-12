# CRM Axel — Design System

## 1. Tokens

### Color

```scss
// Brand
$color-primary:       #123499;  // Azul institucional
$color-primary-dark:  #0a2472;  // Gradiente / hover escuro
$color-accent:        #3b82f6;  // Azul claro (links, destaques)

// Surfaces
$color-bg:            #f8fafc;  // Fundo da página (slate-50)
$color-surface:       #ffffff;  // Cards, diálogos, tabelas
$color-sidenav:       #0a2472;  // Fundo da navegação lateral
$color-sidenav-header:#00072d;  // Topo da sidenav

// Text
$color-text-primary:  #00072d;  // Títulos, corpo principal
$color-text-secondary:#475569;  // Labels, descrições
$color-text-tertiary: #64748b;  // Placeholder, metadados
$color-text-muted:    #94a3b8;  // Desabilitado, bordas
$color-text-on-dark:  #ffffff;  // Texto sobre fundo escuro
$color-text-nav:      rgba(255,255,255,0.75);  // Itens da sidenav

// Semantic
$color-blue:    #3b82f6;  // Clientes
$color-purple:  #8b5cf6;  // Leads
$color-green:   #10b981;  // Receita, Convertido
$color-amber:   #f59e0b;  // Pipeline, Negociação
$color-cyan:    #06b6d4;  // Métricas secundárias
$color-red:     #ef4444;  // Despesas, Perdido, Erro

// Borders
$color-border:  rgba(18,52,153,0.06);  // Cards
$color-border-2: rgba(18,52,153,0.08); // Toolbar, menus
$color-border-3: rgba(18,52,153,0.10); // Diálogos
$color-border-4: rgba(18,52,153,0.15); // Form fields

// Shadows
$shadow-card:   0 4px 15px rgba(0,7,45,0.01);
$shadow-card-h: 0 12px 25px rgba(0,7,45,0.06);
$shadow-dialog: 0 20px 50px rgba(0,7,45,0.10);
$shadow-menu:   0 10px 30px rgba(0,7,45,0.10);
$shadow-toolbar:0 2px 10px rgba(0,7,45,0.03);
$shadow-button: 0 4px 12px rgba(18,52,153,0.15);
$shadow-button-h:0 6px 16px rgba(18,52,153,0.25);
```

### Typography

```scss
$font-heading: 'Outfit', sans-serif;  // Weights: 300-800
$font-body:    'Inter', sans-serif;   // Weights: 300-700
$font-icon:    'Material Icons Round', 'Material Icons';

// Sizes
$heading-1: 28px;
$heading-2: 22px;
$heading-3: 18px;
$heading-card: 16px;
$body: 14px;
$body-sm: 13px;
$caption: 12px;
$micro: 11px;
$label-section: 10px;  // Sidenav section labels (uppercase)
```

### Spacing

```scss
$space-xs:   4px;
$space-sm:   8px;
$space-md:   12px;
$space-lg:   16px;
$space-xl:   20px;
$space-xxl:  24px;
$page-padding: 24px;
$grid-gap: 20px;     // KPI grid
$bento-gap: 24px;    // Bento grid (dashboard)
```

### Borders & Radius

```scss
$radius-sm:   8px;    // Buttons, inputs, chips
$radius-md:   12px;   // Menus, selects
$radius-lg:   16px;   // Dialogs, KPI cards
$radius-xl:   20px;   // Bento cards

$border-card:   1px solid rgba(18,52,153,0.06);
$border-elevated:1px solid rgba(18,52,153,0.10);
```

---

## 2. Layout

### Shell

```
┌──────────────────────────────────────┐
│  Toolbar (white, 64px, sticky top)   │
│  ┌─── breadcrumbs ───── [user] [notif] │
├────────┬─────────────────────────────┤
│Sidenav │  Content / Page             │
│260px   │  padding: 24px              │
│#0a2472 │  max-width: 1280px          │
│        │  background: #f8fafc        │
│ CRM    │  min-height: calc(100vh-64) │
│───     │                             │
│ Oper.  │                             │
│───     │                             │
│ Fin.   │                             │
│───     │                             │
│ Admin  │                             │
└────────┴─────────────────────────────┘
```

- **Sidenav**: 4 sections (CRM, Operações, Financeiro, Admin). Section label: 10px, uppercase, 1.2px tracking, 35% opacity. Items: 40px height, 13.5px, circular hover with `padding-left` shift.
- **Toolbar**: white, bottom border, box-shadow subtle. Icon buttons `#475569`. Timer component in the toolbar.
- **Content**: `24px` padding, `#f8fafc` background, optional `max-width: 1280px`.
- **Breadcrumbs**: Links `#64748b`, separator chevron icon `#94a3b8`, current `#0a2472` weight 600.
- **Mobile** (<768px): Sidenav as overlay, triggered by hamburger.

### Page Structure

```
┌─────────────────────────────────────┐
│  page-title (Outfit 700, 28px)      │
│  [primary button "Novo"]            │
├─────────────────────────────────────┤
│  [loading] spinner                   │
│  [empty] icon + message             │
│  [error] icon + message + retry     │
│  [data]  mat-table + paginator      │
└─────────────────────────────────────┘
```

- Title + actions on same line, right-aligned "Novo" button.
- Loading: `mat-spinner diameter=40` centered.
- Empty: `inbox` icon (48px), message text, centered.
- Error: `error_outline` icon (warn), message + "Tentar novamente" button.

---

## 3. Components

### Buttons

| Type | Style |
|------|-------|
| Primary (raised/unelevated) | Gradient `#123499 → #0a2472`, white text, 600 weight, `8px` radius, hover lift `-1px` + stronger shadow |
| Stroked | Default `8px` radius, `#64748b` text |
| Icon | `#64748b`, hover: blue tint bg `rgba(18,52,153,0.05)` + `#123499` text |
| Menu items | `#334155`, hover `#f1f5f9`, selected `#123499` on `rgba(18,52,153,0.08)` |

### KPI Cards (Dashboard)

- Grid: `repeat(auto-fill, minmax(200px, 1fr))`, gap `20px`
- Card: white bg, `16px` radius, left border `4px` colored accent, `20px` padding
- Icon container: `48px` square, `12px` radius
- Value: `22px` bold `#00072d`
- Label: `12px` `#64748b`, 500 weight
- Hover: `translateY(-4px)` + elevated shadow

### Bento Cards (Dashboard charts/tables)

- Grid: 3 columns, gap `24px`
- Card: `20px` radius, `24px` padding, white bg
- Modifiers: `double-width` (span 2), `full-width` (span 3)
- Collapse at 1024px (→2 cols) and 768px (→1 col)

### Tables (list-page)

- Material `mat-table` with `matSort`
- Actions column: view (primary icon), edit (default icon), delete (warn icon)
- Paginator: footer, options `[5, 10, 25, 50]`, first/last buttons
- `mat-sort-header` on sortable columns

### Form Dialogs

- Generic `app-form-dialog`: receives `FieldDef[]` to build form dynamically
- Fields: text, email, number, date, select, textarea, checkbox, password
- Select options: `{value, label}[]
- Dynamic cascade: `pipelineId` → `stageId` loaded via API
- AI generate button: calls `POST /integrations/ai/generate` for text fields
- Buttons: "Cancelar" (stroked), "Salvar" (primary)
- Dialog: white bg, `16px` radius, `#00072d` border, elevated shadow
- Forms: outlined style, white bg, `#123499` focus/hover caret

### Charts (Chart.js)

- **Financial trend**: Line chart, revenue (green `#10b981`) vs expenses (red `#ef4444`), filled with 0.1 opacity, tension 0.3, `280px` min-height
- **Leads funnel**: Horizontal bar chart, per-stage counts, multi-colored with `borderRadius: 6`
- Responsive, maintain aspect ratio false
- Error state: centered icon + "Tentar novamente" button

---

## 4. Motion

| What | Easing | Duration | Note |
|------|--------|----------|------|
| KPI card hover | `cubic-bezier(0.25,0.8,0.25,1)` | 300ms | Lift + shadow |
| Bento card hover | Same | 300ms | Shadow only (no lift) |
| Primary button hover | `ease` | 200ms | Lift + shadow |
| Icon button hover | `ease` | 200ms | Tint background |
| Sidenav item hover | `cubic-bezier(0.4,0,0.2,1)` | 200ms | Brighten + slide |
| Scrollbar thumb | `ease` | 200ms | Darken on hover |
| Route transitions | View Transitions API | Browser native | Fade / crossfade |
| Card border transition | `ease` | 200ms | For interactive cards |

No reduce-motion equivalent yet — `prefers-reduced-motion` should be added.

---

## 5. Iconography

- **Set**: Material Icons Round (preferred), fallback Material Icons
- **Size**: `24px` default, `20px` in sidenav, `18px` notifications, `40px` error states
- All icons use `font-size` + `width` + `height` set explicitly (not just named size)

---

## 6. Background Pattern

```scss
background-image:
  radial-gradient(rgba(18, 52, 153, 0.03) 1px, transparent 0),
  radial-gradient(rgba(10, 36, 114, 0.02) 2px, transparent 0);
background-size: 32px 32px, 64px 64px;
```

Applied to `html, body` — subtle two-layer dot grid. First layer small dots, second layer larger dots at half frequency.
