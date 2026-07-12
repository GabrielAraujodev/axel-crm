# CRM Axel

> Sistema de gestão empresarial completo — CRM, financeiro, operações e administrativo em um único painel.

## Register

**Product** — Single-page application (Angular 18), authenticated shell for internal teams.

## Audience

Equipe interna de vendas/administração. Desktop-first, uso diário em tela cheia. Perfis variam de operadores de CRM a administradores financeiros.

## Personality

Confident & professional. Transmite segurança e autoridade sem peso. A interface deve inspirar confiança — dados precisos, ações previsíveis, navegação clara.

## Voice & Tone

| Atributo | Diretriz |
|----------|----------|
| Idioma | Português brasileiro (pt-BR), terminologia de negócios |
| Tom | Direto, profissional, sem rodeios |
| Pessoa | 3ª pessoa ("Cliente", "Usuário") |
| Verbos | "Cadastrar", "Editar", "Visualizar", "Excluir" — ação explícita |
| Números | Formato BRL (R$ 1.234,56), datas dd/mm/aaaa |
| Erros | Neutros e informativos ("Falha ao carregar dados", não "Ops!") |
| Confirmações | "Tem certeza que deseja excluir?" — evitar excesso de modais |

## Design Principles

1. **Dados em primeiro lugar** — tabelas, cards de KPI, gráficos. Toda tela deve responder à pergunta "qual é o número?"
2. **Hierarquia clara** — título da página, breadcrumbs, ações principais no canto superior direito
3. **Consistência sobre novidade** — cada feature usa os mesmos padrões: `list-page`, `form-dialog`, botões, tipografia
4. **Mínimo atrito** — formulários diretos, validação inline, teclado navegável, feedback imediato
5. **Limpo sem ser frio** — bastante whitespace, cores contidas, micro-interações sutis (hover, transição de rota)

## Product Surface

### Shell (Layout Principal)

- Sidenav fixa à esquerda com seções colapsáveis: CRM, Operações, Financeiro, Admin
- Topbar com nome do usuário, dropdown de notificações (polling a cada 15s), timer global, botão de logout
- Breadcrumbs no topo do conteúdo
- Responsivo: em mobile (< 768px), sidenav vira overlay

### Autenticação

- `/login` e `/register` — fora do shell, fundo limpo, formulário centralizado
- Rotas públicas com guarda `authGuard` redirecionando para `/login`

### Dashboard

- 6 KPI cards com ícone + cor distinta: Clientes Ativos (azul), Leads (roxo), Negócios Fechados (verde), Pipeline (âmbar), Receitas (ciano), Despesas (vermelho)
- Gráfico de tendência financeira (linha, receitas vs despesas)
- Gráfico de funil de leads (barras horizontais por estágio)
- Tabelas de tarefas e comissões recentes

### CRUD Features (20+ módulos)

Todos os módulos seguem o mesmo padrão:
- **Lista** — `list-page.component` com tabela Material, ordenação por coluna, paginação (5/10/25/50)
- **Criação/Edição** — `form-dialog.component` com campos dinâmicos definidos por `FieldDef[]`, validação por `required`, suporte a texto/email/número/data/select/textarea/checkbox
- **Ações** — botões de visualizar, editar, excluir por linha
- **Estados** — loading (spinner), empty (ícone + mensagem), error (mensagem + retry)
- Camada de staged pipeline: campo `pipelineId` carrega `stageId` dinamicamente via API

### Admin

- Gestão de usuários, integrações, LGPD
- Timer de ponto global no shell

## Visual Identity

| Token | Valor |
|-------|-------|
| Cor primária | `#123499` |
| Gradiente primário | `linear-gradient(135deg, #123499, #0a2472)` |
| Fundo página | `#f8fafc` |
| Fundo padrão elementos | `#ffffff` |
| Texto principal | `#00072d` |
| Texto secundário | `#475569` / `#64748b` |
| Texto terciário / bordas | `#94a3b8` |
| Borda sutil | `rgba(18, 52, 153, 0.1)` |
| Sombras | `rgba(0, 7, 45, 0.08)`, `rgba(0, 7, 45, 0.1)` |
| Heading | **Outfit 700**, `-0.02em` tracking |
| Body | Inter 400/500/600 |
| Ícones | Material Icons Round |
| Cantos | `8px` (botões, inputs), `12-16px` (menus, diálogos) |
| Transições | `0.2s ease` (hover), View Transitions API (rotas) |
| Scrollbar | `6px`, `#f1f5f9` / `#cbd5e1` |

### Cores semânticas (gráficos e indicadores)

| Uso | Cor |
|-----|-----|
| Azul / Clientes | `#3b82f6` |
| Roxo / Leads | `#8b5cf6` |
| Verde / Receita, Convertido | `#10b981` |
| Âmbar / Pipeline, Negociação | `#f59e0b` |
| Ciano / Métricas secundárias | `#06b6d4` |
| Vermelho / Despesas, Perdido | `#ef4444` |

### Tipografia

- **Headings**: Outfit 700, `-0.02em` tracking, `#00072d`
- **Body**: Inter 400/500/600, `#00072d` / cinzas escuros
- **Tabelas e labels de campo**: Inter 400, `#475569` / `#64748b`
- Títulos de página e diálogos herdam Outfit 700 via seletor global
