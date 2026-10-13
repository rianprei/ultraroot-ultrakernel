# Ciclo de auditoria UI/UX: ui4 -> ui5 -> ui6 (2026-10-12)

## Auditoria 1: Codex #4, todas as skills de design (AUDIT_UI4_codex4.md)
Nota 2/4 (needs work). Seis itens:
- APLICADO: #2 "Tentar novamente" nos estados de erro (RetryButton; estado de carregamento durante a nova leitura). Nielsen #9.
- APLICADO: #6 lista de acessos em ListItem com divisores, dentro de um só cartão (M3 - List).
- RECUSADO: #1 remover destinos vazios da navegação. A especificação define as seis seções; as telas vazias passam a dizer "Em construção" e o roteiro.
- RECUSADO: #3 e #4 traduzir/renomear títulos. Os nomes são os da especificação (Root Access, Module Center, System Toolkit, Kernel & Performance, Boot & Recovery, Extensions Hub).
- ADIADO: #5 NavigationRail para largura >= 600 dp. O aparelho é compacto; entra no próximo incremento.

## Auditoria 2: subagente Haiku, conformidade M3 (M3_AUDIT_ui4.md)
Dez lacunas. Relatório usou ~194 mil tokens (limite pedido: 100 mil).
- APLICADO (ui6): tracking (letterSpacing) dos 15 papéis tipográficos, com os valores oficiais de TypeScaleTokens.kt (BodyLargeTracking = 0.5 sp, etc.).
- APLICADO (ui6): cantos de card em shapes.medium (12 dp), como no catálogo de componentes.
- APLICADO (ui6): papéis inversos (inverseSurface, inverseOnSurface, inversePrimary).
- RECUSADO: "curva Standard é EasingEmphasized". Não procede: Standard (0,2; 0; 0; 1) é EasingStandardCubicBezier (linha 62 de MotionTokens.kt). Emphasized tem o mesmo número por coincidência de valores, mas nome diferente.
- RECUSADO: tema só escuro. Identidade da marca; o M3 não exige claro.
- ADIADO: tween fixo vs motionScheme e NavigationRail adaptativo. Próximo incremento.
- RECUSADO: rótulos em inglês em Title Case. São os nomes da especificação (item acima).

## Estado
- ui5 (vc40005) e ui6 (vc40006) construídos, testes 9/9, assinados v2 com cert efb8d49f, identidade limpa.
- ui6 sha256 12765c9689dc647c01c1587bb5c2908213ddf6c42b04bdb76d577d94be2182ae. Não instalado no aparelho.
- Próximo: instalar ui6 com pm install (sem incremental) e capturar a navegação inferior para uma auditoria com captura real.

## Auditoria 3: design-review com captura real do ui6 (2026-10-12)
Captura: `[artefato local, fora do repo]/device-grant/screen_ui6.png` (Root Access, estado vazio, barra inferior visível).

## Root Access (ui6, captura do aparelho) - nota 3/4
Nota 3/4: hierarquia e acabamento estão corretos; sobram dois ajustes de texto e de cor.

### 1. Texto repetido no estado vazio
- Antes: "0" + "apps com root concedido" e, logo abaixo, "Nenhum app com root" / "Nenhum app recebeu acesso root." dizem a mesma coisa.
- Depois: título "Nada concedido ainda" e corpo "Conceder e revogar acesso chega num incremento futuro."
- Por quê: Nielsen heuristic #8 - Aesthetic and minimalist design.

### 2. Número zero com cor de texto secundário
- Antes: o ponto focal "0" usa onSurfaceVariant (cinza), que lê como desativado, não como resultado.
- Depois: usar onSurface para o número, em qualquer valor.
- Por quê: Material Design 3 - color roles (onSurface é o papel do conteúdo principal).

Fazer primeiro: o item 1 (texto repetido). Os dois itens entram no próximo ciclo, com nova instalação sob "sim".
