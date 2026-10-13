# HANDOFF — UltraRoot (2026-10-12 v3)

Responder em pt-BR. Projeto: [fora do repo]. App: ultraroot (pacote me.weishu.kernelsu, label UltraRoot). Handoff anterior: docs/history/handoffs/HANDOFF-ultraroot-2026-10-11-v2.md.

## Estado do aparelho ([redigido: ip privado]:5555, 2410FPCC5G, lake, kernel 6.6.89-android15-8-4k)
- App instalado: ui8 (versionCode 40008, 0.2.0-ui8), instalado com `pm install -r` a partir de /data/local/tmp (sha conferido no aparelho). Pasta do app F2FS.
- Manager coroado pelo kernel (uid 10296). Root Access lê a allowlist: "Nenhum app com root" (vazia).
- Grant: NÃO testado. A UI atual é só leitura; conceder e revogar é da inc3.
- Aceite (accept_ksu_root.sh): REPROVA contexto magisk. O su do adb ainda passa pelo Magisk. Coroação pendente.
- Causa do "backend indisponível" (resolvida): instalação incremental do adb cria a pasta do app em incfs; a busca do manager do KernelSU pula essa pasta. Ver docs/ultraroot/DIAG_BACKEND_INDISPONIVEL.md e docs/history/evidence/2026-10-12_*.

## Artefatos (todos em ultraroot/out/)
- Instalado: UltraRoot-own-ui8-vc40008.apk (sha256 5861465c14a99e4bade3b6efa5885bc31eae7904b694c993bef0ac19ca7a1529).
- Último build, NÃO instalado: UltraRoot-own-ui9-vc40009.apk (sha256 671d0c9ce4d14bfc910870ff5273a3a65990d179294e0f864f4ac041fc97f48a). Ícone do robô recalibrado (trim e área de segurança).
- Assinatura: v2 apenas, cert efb8d49f0bb8022e55e5ec89db26c5131bf4a6c3878496fa583d74fbc8d99ad9. Chave em [chaves locais do dono, fora do repo]/ (NÃO abrir nem imprimir o arquivo de senha).
- Ícone-fonte: ultraroot/assets/app-icon-source.png (imagem enviada pelo dono).
- Ícone no launcher ainda mostrava o antigo após o ui8: cache do launcher. Reinício do launcher é opção, só com "sim".

## Auditorias e documentos (ler por caminho)
- docs/ultraroot/DESIGN_M3_SELECAO.md (seleção M3, auditoria design-review seção 9).
- docs/ultraroot/AUDIT_UI4_codex4.md (nota 2/4, seis itens; aplicados os que valiam).
- docs/ultraroot/LOOP_AUDITORIA_ui5_ui6.md (ciclo de auditoria e recusas com motivo).
- docs/ultraroot/MAGISK_KSU_UI_INVENTORY.md (inventário de Magisk e KernelSU; prioridade alta: busca/filtro no Root Access, grant com confirmação, perfil de menor privilégio, histórico de root, inventário de módulos, instalação de ZIP, estado honesto do backend).
- docs/ultraroot/CAPABILITIES.md, TODOs.md, COMPAT_MATRIX.md (mapa de capacidades e status).
- Extração do site M3: parcial. 92 páginas do CMS com texto em [artefato local, fora do repo]/m3-extract/pages/; 27 batem com o sitemap de 375 URLs.

## Gates pendentes (device só com "sim, <passo>")
1. "sim, instalar ui9" (pm install -r, sem incremental). Depois, captura e design-review.
2. "sim, reiniciar o launcher" (opcional, se o ícone antigo persistir).
3. "sim, grant teste" só depois da inc3. Termux como app de teste; depois rodar accept_ksu_root.sh com ACCEPT_KSU_ALLOW_ADB=1.
4. Coroação: desligar o Magisk só depois de migrar os 8 módulos (lista em HANDOFF-…-v2.md). Módulos com Zygisk dependem de SP2; com adapter, de SP3.

## Regras invioláveis
- Device só com "sim, <passo>" explícito. Não fazer instalação, grant nem reinício sem ele.
- Boot: slot B, nunca A (se houver flash).
- Legítimo: sem ocultação de root, sem bypass de atestação.
- Handoffs e artefatos no projeto, nunca em /tmp.
- Cross-review: autor ≠ revisor. Prompts para Codex ou outro agente: usar /prompt-techniques antes.
- Subagentes: os últimos estouraram o limite pedido (108k e 194k tokens contra 100k). Só com autorização explícita por uso, e com teto de tokens no prompt.
- Sessão atual: orçamento de contexto quase esgotado. Retomar numa sessão nova.

## Suggested skills
- prompt-techniques (antes de qualquer prompt a agente).
- design-review (rubrica 0-4 para auditoria com captura).
- material-3, android-compose-design, frontend-design, ui-ux-pro-max (instalados; skills de projeto em .claude/skills/ do app).
- maestri (orquestrar Codex e grok).

## Próximos passos (em ordem)
1. Aguardar "sim, instalar ui9". Instalar, capturar o launcher e a tela, rodar design-review.
2. Implementar, no código, os itens de prioridade alta do inventário que não exigem backend novo: busca e filtro no Root Access; estado honesto do backend (ativo, ausente, incompatível, com motivo).
3. Inc3: conceder e revogar com confirmação e estado por app. Depois "sim, grant teste".
4. Accept só passa com coroação (Magisk desligado). Não tentar antes.
