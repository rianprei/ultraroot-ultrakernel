# Direção de design do UltraRoot: seleção do Material 3 (incremento ui1)

Data: 2026-10-11. Artefato: `ultraroot/out/UltraRoot-own-ui1-vc40001.apk`
(versionCode 40001, versionName 0.2.0-ui1, sha256 bf83214c9ad5013f2385ef31a9b0bc417c3f784cb9e7851a50b903050cea8a6d,
assinado v2 com cert efb8d49f). Não instalado no aparelho.

## 1. Fontes e status de verificação

| Fonte | O que cobre | Status |
|---|---|---|
| Tokens oficiais do Compose Material3 (androidx, `androidx-main`, `compose/material3/material3/.../tokens/`): `TypeScaleTokens.kt`, `ShapeTokens.kt`, `MotionTokens.kt`, `ColorSchemeKeyTokens.kt` | escala tipográfica, cantos, durações e easings, nomes dos papéis de cor | **Verificado** (arquivos baixados e lidos em 2026-10-11) |
| Site `https://m3.material.io/` | regras narrativas (quando usar cada componente, diálogos, layout, acessibilidade, Expressive) | **Não verificado nesta rodada**: o site é renderizado no cliente; WebFetch e curl retornaram só o título. Extração completa em andamento por subagente Haiku (índice: `[artefato local, fora do repo]/m3-extract/index.tsv`) |
| Decisões próprias | adaptações para o produto | Marcadas como "decisão própria" |

Regra deste documento: toda afirmação sobre o M3 aponta para o token oficial ou fica marcada como não verificada.

## 2. Diagnóstico do produto (antes da mudança)

Evidência: código da versão 40000 e captura `docs/history/evidence/2026-10-11_manager_grant_tela_root_access.png`.

- Estados reais aparecem como texto puro ("backend indisponível", "perfil indisponível"), sem hierarquia de severidade nem explicação.
- Textos de desenvolvimento visíveis ao usuário: "Este texto não é mais o conteúdo da seção", "Sem a lib, a tela fica em backend indisponível", rótulo "incremento 1".
- Tema com 12 papéis de cor. Tipografia, formas e movimento eram os padrões da biblioteca.
- Sem componentes compartilhados: cada tela montava seu próprio texto e seus próprios estados.

## 3. Seleção: o que entrou, por quê e de onde

| # | Item | Origem | Decisão | Código |
|---|---|---|---|---|
| 1 | Papéis de cor completos: superfícies de `surfaceContainerLowest` a `surfaceContainerHighest`, `outline` e `outlineVariant`, pares `on…` | Tokens oficiais (`ColorSchemeKeyTokens`) | Entrou | `theme/Color.kt`, `theme/UltraTheme.kt` |
| 2 | Papéis de erro (`error`, `errorContainer`, `onError`, `onErrorContainer`) | Valores padrão da biblioteca Material3 | Entrou (sem sobrescrever) | `theme/UltraTheme.kt` |
| 3 | Escala tipográfica (57 a 11 sp, com line height) | Tokens oficiais (`TypeScaleTokens`) | Entrou | `theme/Type.kt` |
| 4 | Pesos: Regular em textos longos; Medium em título médio/pequeno e em labels | Tokens oficiais (`TypeScaleTokens`) | Entrou | `theme/Type.kt` |
| 5 | Escala de cantos: 4, 8, 12, 16 e 28 dp | Tokens oficiais (`ShapeTokens`) | Entrou | `theme/Shape.kt` |
| 6 | Movimento: durações de 200 e 300 ms; easings Standard, StandardDecelerate e StandardAccelerate | Tokens oficiais (`MotionTokens`) | Entrou (troca de estado, `Crossfade`) | `theme/Motion.kt`, telas |
| 7 | Progresso indeterminado para espera de duração desconhecida | Componente M3 (`CircularProgressIndicator`) | Entrou (`LoadingState`) | `ui/components/LoadingState.kt` |
| 8 | Cartão de status com cor, ícone **e** texto | Decisão própria, por acessibilidade (a cor não é o único indicador) | Entrou (`StatusCard`) | `ui/components/StatusCard.kt` |
| 9 | Estado vazio com título e explicação, sem ilustração | Decisão própria (clareza operacional) | Entrou (`EmptyState`) | `ui/components/EmptyState.kt` |
| 10 | Valores técnicos (UID, versões, UAPI) em monoespaçada | Decisão própria (leitura de números e identificadores) | Entrou (`UltraTechnicalText`) | `theme/Type.kt`, `KeyValueRow` |
| 11 | Margem de 16 dp e espaçamento em múltiplos de 4 dp | Decisão própria, usando grade de 4 dp | Entrou | telas |
| 12 | Rolagem vertical na área de conteúdo (fontes grandes e telas pequenas) | Decisão própria | Entrou | `UltraRootApp.kt` |
| 13 | Drawer com versão em vez de "incremento 1" | Decisão própria (jargão interno fora da UI) | Entrou | `UltraRootApp.kt`, `strings.xml` |
| 14 | Textos de desenvolvimento removidos da UI | Decisão própria | Entrou | `Section.kt`, `strings.xml` |
| 15 | Texto visível sem nome de backend (identidade própria do app) | Requisito do produto | Entrou, com teste | `StringsIdentityTest` |

## 4. Não selecionado (e motivo)

- **Cores dinâmicas (Material You):** trocaria a identidade da marca pela cor do papel de parede. Fora.
- **Ilustrações em estados vazios:** não há conteúdo real a ilustrar; desvia a atenção do dado. Fora.
- **Efeitos de terminal, verde neon, glitch:** a direção pede equilíbrio entre identidade hacker e legibilidade. Nenhum foi aplicado nesta rodada, para não sacrificar clareza. Pode voltar como decisão do dono.
- **Expressive (formas e movimento expressivos):** não verificado no site nesta rodada. Fora até a extração confirmar o que é específico do Expressive.
- **Diálogos de confirmação:** ainda não há ação sensível (conceder ou revogar root é do incremento 3). O padrão entra junto com essa ação.
- **Barra de navegação inferior:** a especificação define navegação por drawer com seis seções. Mantido.

## 5. Contrato de estados da UI

| Estado | Componente | Texto | Movimento |
|---|---|---|---|
| Carregando | `LoadingState` (indeterminado) | "Lendo a allowlist do kernel…" / "Lendo o backend do kernel…" | — |
| Indisponível | `StatusCard` tom Error | Título fixo + motivo técnico devolvido pelo backend | Crossfade 300 ms |
| Vazio | `EmptyState` | Título e explicação | Crossfade 300 ms |
| Dados | `Card` com `KeyValueRow` | Rótulo + valor técnico | Crossfade 300 ms |
| Em construção | `StatusCard` tom Info | "Em construção" + descrição da seção | — |

Nenhum estado é mascarado: "backend indisponível" continua visível, com o motivo.

## 6. Acessibilidade (verificado por teste)

- Contraste: 12 pares de texto com mínimo de 4,5:1 e contorno de componente com mínimo de 3:1 (`ContrastTest`, valores calculados pela fórmula WCAG).
- Ícones decorativos têm `contentDescription = null` quando há texto ao lado; o ícone do menu tem descrição "Abrir seções".
- Estado de carregamento expõe sua mensagem como descrição semântica.
- Todos os tamanhos de texto em sp, respeitando a escala de fonte do sistema.

## 7. Validação

- Testes unitários: 12 de 12 passam (`ContrastTest` 2, `StringsIdentityTest` 2, `RootAccessPresenterTest` 5, `KsuBackendHostTest` 3).
- Build de release: `assembleRelease` concluído; assinatura v2 com cert `efb8d49f`; badging versionCode 40001, versionName 0.2.0-ui1, label UltraRoot; grep de identidade nos recursos só encontra o nome do pacote.
- **Não validado:** aparência no aparelho. O APK não foi instalado; a captura da nova UI não existe ainda.

## 8. Pendências

1. Extração do site M3 (subagente Haiku): confirmar as regras narrativas marcadas como não verificadas e atualizar este documento.
2. Revisão cruzada do código de UI pelo Codex #4 (autor ≠ revisor).
3. Instalação no aparelho: depende de "sim, instalar".
4. Incremento 3: diálogo de confirmação para conceder e revogar root.

## 9. Auditoria (skill design-review, formato e rubrica do próprio skill)

Artefato: captura real do aparelho com a versão 40000 (`evidence/2026-10-11_manager_grant_tela_root_access.png`).

## Root Access (versão 40000, captura do aparelho) - nota 2/4

Nota 2/4 (precisa de trabalho): a estrutura existe (título na barra, conteúdo abaixo), mas o acabamento é fraco. Pelo custo do conserto (uma passada de acabamento, não reconstrução), a faixa é 2/4.

### 1. Estado de erro sem destaque visual
- **Antes:** a única linha da tela é "backend indisponível" em texto corrido, sem ícone, sem cor de alerta e sem contorno.
- **Depois:** cartão de status com tom de erro (cor, ícone e texto), título e motivo separados.
- **Por quê:** Nielsen heuristic #1 - Visibility of system status.

### 2. Sem próximo passo para o usuário
- **Antes:** a tela diz que o backend está indisponível e não sugere nada a fazer.
- **Depois:** uma linha de orientação no cartão de erro: "Verifique se o backend de root está ativo e se o kernel reconhece este app."
- **Por quê:** Nielsen heuristic #9 - Help users recognize, diagnose, and recover from errors.

### 3. Jargão interno na interface
- **Antes:** "backend indisponível" e o rótulo "incremento 1" no menu lateral.
- **Depois:** título "Leitura da allowlist indisponível"; versão no lugar do rótulo de incremento.
- **Por quê:** Nielsen heuristic #2 - Match between system and the real world.

### 4. Texto de desenvolvedor visível ao usuário
- **Antes:** "Este texto não é mais o conteúdo da seção" (Root Access) e "Sem a lib, a tela fica em backend indisponível" (Kernel & Performance).
- **Depois:** textos de produto sem notas de implementação.
- **Por quê:** Nielsen heuristic #8 - Aesthetic and minimalist design.

### 5. Espaçamento fora de uma grade
- **Antes:** espaçamentos de 12 dp entre elementos, que não é múltiplo de 8.
- **Depois:** espaçamentos de 8 e 16 dp.
- **Por quê:** Material 3 - Spacing (sistema de 8 dp citado pelo skill material-3).

### 6. Estado de carregamento só em texto
- **Antes:** "Lendo a allowlist do kernel…" aparece como texto, sem indicador de progresso.
- **Depois:** progresso indeterminado do M3 ao lado do texto, com descrição semântica.
- **Por quê:** Material Design 3 - Progress indicators (indeterminado quando a duração é desconhecida).

**Fazer primeiro:** o estado de erro com cartão de status e próximo passo (itens 1 e 2). É o único estado que este aparelho mostra hoje, e hoje ele é ilegível como problema.

## 10. Skills de design instaladas (ordem pedida)

| # | Skill | Escopo | Verificação antes da instalação |
|---|---|---|---|
| 1 | frontend-design (anthropics, marketplace oficial) | usuário (`claude plugin install`) | instalado sem comandos a aceitar |
| 2 | material-3 (hamen/material-3-skill) | usuário (marketplace + plugin) | 16 arquivos; sem hooks, MCP nem comandos de rede |
| 3 | ui-ux-pro-max (ui-ux-pro-max-cli 2.15.0, fixada) | usuário (`~/.agents/skills/ui-ux-pro-max`, 76 arquivos) | sem scripts de ciclo de vida no npm; teste em sandbox: 173 arquivos, sem MCP nem hooks. O `stack/.mcp.json` do repositório não é copiado pelo instalador |
| 4 | android-compose-design (NI3singh/android-compose-design-skill) | projeto (`.claude/skills/android-compose-design`) | 6 arquivos; sem padrões suspeitos |
| 5 | design-review (humbleteam/design-review) | projeto (`.claude/skills/design-review`) | 6 arquivos; sem padrões suspeitos |

Clones de git copiados sem `.git`, para não criar repositório aninhado no projeto. Em ambos, o `.claude/` do projeto não tem `mcpServers` nem `hooks`.

Os skills de plugin e de usuário só entram na sessão nova do Claude Code. Nesta sessão, usei as rubricas lidas diretamente (design-review e android-compose-design) para a auditoria da seção 9.

## 11. Extração do site M3 (status honesto)

- Sitemap: 375 URLs. Casca Angular: o HTML estático não traz o texto.
- Conteúdo obtido pelo endpoint de exportação do CMS (`/_dsm/content/m3/<versão>/<arquivo>.json`, versão lida do bundle): 92 páginas com texto extraído em `[artefato local, fora do repo]/m3-extract/pages/`.
- Dessas, 27 batem com URLs do sitemap; as outras 65 são páginas do CMS fora do sitemap (ex.: `develop/...`, `blog`).
- 348 URLs do sitemap sem texto: os dados de componentes e estilos (`/_dsm/data/dsdb-m3/...`) estão no bundle, mas o formato exato da URL não foi confirmado (34 tentativas, 0 com 200).
- Subagente Haiku usado na tentativa inicial: 108.532 tokens, acima do limite de 100k pedido. A estimativa dele dizia ~75k; o número real é o do registro de uso.
- Páginas com texto verificado que casam com decisões do app: `styles/color/roles`, `styles/color/choosing-a-scheme`, `foundations/content-design/notifications`, `foundations/interaction/inputs`, `foundations/interaction/selection`, `foundations/customization`, `m3-expressive-motion-theming`, `building-with-m3-expressive`.

## 12. Versão ui2

- `UltraRoot-own-ui2-vc40002.apk`, sha256 `a4a0403772bb12c207c608508831a2f1d82a79d5d2b54aa87d05b764905c632b`, versionName 0.2.0-ui2, assinado v2 com cert efb8d49f.
- Mudanças da auditoria (seção 9): cartão de erro com próximo passo, espaçamento de 8/16 dp, textos sem nota de implementação, progresso indeterminado no carregamento.
- Testes unitários do app: 9 de 9 (StringsIdentity 2, ContrastTest 2, RootAccessPresenter 5); core-backend: 3 de 3 (inalterado).
- Não instalado no aparelho. Captura da nova tela não existe.
