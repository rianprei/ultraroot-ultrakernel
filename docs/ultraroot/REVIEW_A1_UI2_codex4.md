# Revisão independente — A1 + UI ui2

Revisor: Codex #4. Autor das mudanças revisadas: orquestrador Claude.
Escopo: revisão estática e stubs host; nenhum adb/fastboot real, instalação ou reboot.

## REVISÃO 1 (A1): OK

- Achados: nenhum.
- Diff: diff -u [artefato local, fora do repo]/device-grant/before/accept_ksu_root.before-quote-fix.sh docs/ultraroot/accept_ksu_root.sh mostra:
  - substituição das coletas simples por captura de id_raw/probe_raw, seus rc e normalização nas linhas 31–39 atuais;
  - rejeições id_rc != 0 e probe_rc != 0 nas linhas 40–47;
  - troca do parse head -n 1 por sed -n '1p' na linha 59 atual.
- rg -n -F '|| true' docs/ultraroot/accept_ksu_root.sh não imprime correspondências (rc 1). bash -n no script atual e no backup: PASS.
- RC e precedência: accept_ksu_root.sh:33–36 captura separadamente rc de adb ... su -c id e da coleta ksud. A normalização CR fica nas linhas 38–39; antes de qualquer teste semântico de contexto, marcadores KSU, UAPI ou uid, as linhas 40–47 reprovam rc diferente de zero com exit 1. As validações de conteúdo começam nas linhas 49–96.
- Aspas do probe: accept_ksu_root.sh:35 passa um argumento remoto delimitado por aspas duplas ao adb shell. O Bash local remove as aspas duplas externas e mantém as aspas simples internas no argumento único su -c 'if ...; then ...; fi; ...'. O adb junta/passa a string de comando remoto e o sh remoto interpreta as aspas simples como um único argumento de comando para su -c. Os stubs indicados juntam os argumentos e executam sh -c; a bateria abaixo exercita esse caminho simulado.
- Bateria executada com stubs e /usr/bin/timeout 25, stdin /dev/null; variáveis-base usadas: SCRIPT=docs/ultraroot/accept_ksu_root.sh e STUB=docs/history/reports/a1/stub.log.
  - (a) Comando: env STUB_MODE=ksu STUB_LOG="$STUB" ACCEPT_KSU_ALLOW_ADB=1 PATH=[artefato local, fora do repo]/device-grant/stubs-fixed/ksu:docs/history/reports/sp1-audit/stubs/ksu:/usr/bin:/bin /usr/bin/timeout 25 /bin/bash "$SCRIPT" SERIALTEST </dev/null. Saída literal: APROVA ksu serial=SERIALTEST uid=0 uapi=5; rc=0.
  - (b) Mesmo comando com STUB_MODE=magisk e PATH=[artefato local, fora do repo]/device-grant/stubs-fixed/magisk:docs/history/reports/sp1-audit/stubs/magisk:/usr/bin:/bin. Saída literal: REPROVA contexto magisk: uid=0(root) gid=0(root) context=u:r:magisk:s0; rc=1.
  - (c) Comando: env -u ACCEPT_KSU_ALLOW_ADB -u ACCEPT_KSU_FIXTURE STUB_LOG="$STUB" PATH=[artefato local, fora do repo]/device-grant/stubs-fixed/ksu:/usr/bin:/bin /usr/bin/timeout 25 /bin/bash "$SCRIPT" SERIALTEST </dev/null. Saída literal: sem fixture: coleta adb exige ACCEPT_KSU_ALLOW_ADB=1; rc=2; stub log vazio, logo adb não foi chamado.
  - (d) Comando: env STUB_LOG="$STUB" PATH=[artefato local, fora do repo]/device-grant/stubs-partial/id:/usr/bin:/bin ACCEPT_KSU_ALLOW_ADB=1 /usr/bin/timeout 25 /bin/bash "$SCRIPT" SERIALTEST </dev/null. Saída literal: REPROVA su -c id falhou (rc=3): uid=0(root) gid=0(root) context=u:r:su:s0; rc=1 apesar da saída parcial com uid=0.
  - (e) Comando: env STUB_LOG="$STUB" PATH=[artefato local, fora do repo]/device-grant/stubs-partial/probe:/usr/bin:/bin ACCEPT_KSU_ALLOW_ADB=1 /usr/bin/timeout 25 /bin/bash "$SCRIPT" SERIALTEST </dev/null. Saída literal: REPROVA coleta do ksud falhou (rc=3); rc=1 apesar da saída parcial conter HAS_KSUD/UAPI 5.
- Comparação literal: cmp entre cada saída salva em docs/history/reports/a1/ e R_ksu.txt, R_magisk.txt, R_noopt.txt, R_part_id.txt, R_part_probe.txt retornou MATCH em todos os cinco casos. R_shim.txt (rc=127) e R_device.txt são evidências adicionais de cenários diferentes; não foram usados como substitutos da bateria, e nenhum teste em aparelho foi executado nesta revisão.

## REVISÃO 2 (UI ui2): FALHA

- Achados por item: (a) nenhum; (b) nenhum; (c) nenhum; (d) FALHA — pares de contraste sem teste; (e) nenhum; (f) nenhum. Valores de espaçamento fora da grade de 8 dp ficam listados para decisão de design.

- (a) Tipografia, forma e movimento: sem divergências nos valores pedidos. Type.kt:12–26 contém a escala completa e os pesos esperados; comparação com TypeScaleTokens.kt (por exemplo, pesos Regular nas linhas 37–157/217 e Medium nas 172–202/232–247). Shape.kt:9–13 = 4/8/12/16/28 dp, conforme ShapeTokens.kt:29,38,47,70,72. Motion.kt:9–13 = 200/300 ms e as três curvas esperadas, conforme MotionTokens.kt:33,39,61,64,67.
- (b) Texto visível e identidade:
  - Recursos (strings.xml:3–26): “UltraRoot”; “Abrir seções”; “0.2.0-ui2”; “Em construção”; “Lendo a allowlist do kernel…”; “Leitura da allowlist indisponível”; “Nenhum app com root”; “Nenhum app recebeu acesso root. Conceder acesso chega num incremento futuro.”; “Pacote não identificado”; “UID”; “Acesso root”; “Permitido”; “Negado”; “Perfil indisponível”; “Dados lidos do kernel. Se o backend não responder, o motivo aparece aqui.”; “Lendo o backend do kernel…”; “Leitura do kernel indisponível”; “Versão do root no kernel”; “UAPI do kernel”; “UAPI deste app”; “Verifique se o backend de root está ativo e se o kernel reconhece este app.”; “Sem resposta do kernel não há valores para mostrar. Verifique o backend de root e tente de novo.”
  - Seções (Section.kt:20–53): “Root Access”; “Allowlist de root concedido pelo kernel. Conceder e revogar acesso chegam num incremento futuro.”; “Module Center”; “Módulos de root instalados e seus estados. Disponível em um incremento futuro.”; “System Toolkit”; “Ferramentas de sistema: processos, propriedades e SELinux. Disponível em um incremento futuro.”; “Kernel & Performance”; “Boot & Recovery”; “Leitura do slot ativo e das imagens de boot. Disponível em um incremento futuro.”; “Extensions Hub”; “Extensões e integrações com os backends. Disponível em um incremento futuro.” Os índices 01–06 também são renderizados. O body de Root é declarado em Section.kt:23, mas o roteamento atual usa RootAccessScreen para Root (UltraRootApp.kt:109–110), não o placeholder.
  - Presenter/dados (RootAccessState.kt:6–8,22–40; RootAccessScreen.kt:53–81): constantes do modelo incluem “nenhum app com root concedido”, “perfil indisponível” e o sentinela “—”; o render usa recursos localizados. O motivo indisponível é passado pelo backend, atualmente “backend indisponível” (KsuBackend.kt:30, exibido por RootAccessScreen.kt:56). Linha de dados exibe nome/pacote, UID e “Permitido”, “Negado” ou “Perfil indisponível”.
  - Não há KSU, KernelSU, Magisk, APatch ou weishu em texto visível nos recursos ou textos de seção. StringsIdentityTest.kt:10–26 define e testa essa lista; XML registra 2 testes, 0 falhas. Os hits de rg no Kotlin de UI são identificadores de implementação (KsuBackend/KsuRead), não strings de interface. O package do APK continua me.weishu.kernelsu, conforme badging.
- (c) Estados: sem achado. RootAccessState.kt:24 preserva read.reason; RootAccessScreen.kt:53–57 mostra ui.message. RootAccessState.kt:31–35 mantém allowSu=null para perfil indisponível, e RootAccessScreen.kt:78–81 mapeia null para “Perfil indisponível”, deixando “Negado” exclusivamente para false. RootAccessState.kt:25–28 produz Empty apenas quando read.value.isEmpty(). Os casos correspondentes constam em RootAccessPresenterTest.kt:13–26,69–86.
- (d) Contraste e espaçamentos: FALHA de cobertura do teste. ContrastTest.kt:20–32,43–47 testa os pares listados e outline/surface. Pares de texto usados pelos componentes que não são testados:
  - onSurfaceVariant/background: LoadingState usa esse par em LoadingState.kt:34; introdução do Kernel Performance em KernelPerformanceScreen.kt:49–52; EmptyState body em EmptyState.kt:24–27. O teste cobre onSurfaceVariant sobre surface/high/variant, não sobre background (ContrastTest.kt:24–26).
  - primary/background: índice do placeholder em SectionPlaceholder.kt:19–22; o teste usa primary/surface, não primary/background (ContrastTest.kt:28).
  - onErrorContainer/errorContainer: texto do StatusCard Error em StatusCard.kt:40,55–60, usado por Root Access e Kernel Performance (RootAccessScreen.kt:53–57; KernelPerformanceScreen.kt:56–60). Não aparece na lista ContrastTest.kt:20–32; o tema deixa os papéis de erro nos defaults M3 (UltraTheme.kt:7–8).
  - Pares equivalentes por cor são cobertos: surface e surfaceContainer mapeiam a mesma Panel, e onSurface e onBackground mapeiam Paper (UltraTheme.kt:22–25,32); o outline/surface é testado em ContrastTest.kt:43–47. Não há uso de outline em componente nesta UI.
  - Espaçamentos estáticos (arquivo:linha): UltraRootApp.kt:51 padding 28/24/28/12; :74 12; :106 16/12; SectionPlaceholder.kt:18, KernelPerformanceScreen.kt:48, RootAccessScreen.kt:49 spacedBy 16; KernelPerformanceScreen.kt:73, RootAccessScreen.kt:88, StatusCard.kt:49, LoadingState.kt:25 padding 16; KeyValueRow.kt:22 padding 8; EmptyState.kt:17 padding 24; EmptyState.kt:23 Spacer 4; LoadingState.kt:30 Spacer 16; StatusCard.kt:53 Spacer 16, :56 Spacer 4, :59 Spacer 8. UltraRootApp.kt:104 usa padding dinâmico do Scaffold (sem valor dp literal). Fora de múltiplos de 8: 28 e 12 dp em UltraRootApp.kt:51; 12 dp em UltraRootApp.kt:74,106; 4 dp em EmptyState.kt:23 e StatusCard.kt:56. O padding 24 dp é múltiplo de 8. LoadingState.kt:29 size 24 e strokeWidth 3 são dimensões do indicador, não padding/espaçamento.
- (e) Regressão: os 5 casos passam nos artefatos existentes. TEST-dev.superroot.ultraroot.ui.RootAccessPresenterTest.xml:2–8: tests=5, skipped=0, failures=0, errors=0; inclui perfil nulo, unavailable, lista vazia e lista populada. gradle_ui2b.log:122 diz BUILD SUCCESSFUL, embora o task testDebugUnitTest apareça UP-TO-DATE; essa evidência XML é de execução anterior, não de um rerun desta revisão.
- (f) APK: confirmado. sha256sum → a4a0403772bb12c207c608508831a2f1d82a79d5d2b54aa87d05b764905c632b. apksigner verify --verbose --print-certs: V1 false, V2 true, V3/V3.1/V3.2/V4 false, 1 signer, SHA-256 efb8d49f0bb8022e55e5ec89db26c5131bf4a6c3878496fa583d74fbc8d99ad9. aapt2 dump badging: package me.weishu.kernelsu, versionCode 40002, versionName 0.2.0-ui2, activity dev.superroot.ultraroot.MainActivity.
- Espaçamentos fora de 8 dp: 28 dp (UltraRootApp.kt:51); 12 dp (UltraRootApp.kt:51,74,106); 4 dp (EmptyState.kt:23; StatusCard.kt:56).
- Pares de contraste sem teste: onSurfaceVariant/background; primary/background; onErrorContainer/errorContainer.

## SABOTAGEM

Comandos executados só na cópia scratch, sem editar o projeto original:
cp -r ultraroot docs/history/reports/sabotage-app
find docs/history/reports/sabotage-app -type d -name build -prune -exec rm -rf -- {} +
find docs/history/reports/sabotage-app -type d -name .gradle -prune -exec rm -rf -- {} +
sed -i 's/allowSu = null,/allowSu = false,/' docs/history/reports/sabotage-app/app/src/main/java/dev/superroot/ultraroot/ui/RootAccessState.kt
diff mostrou somente essa linha: allowSu = null → allowSu = false.

Comando executado (cwd docs/history/reports/sabotage-app):
env PATH=[artefato local, fora do repo]/device-grant/stubs-fixed/ksu:docs/history/reports/sp1-audit/stubs/ksu:/usr/bin:/bin STUB_LOG=docs/history/reports/gradle-stub.log TMPDIR=docs/history/reports/tmp /usr/bin/timeout 600 ./gradlew :app:testDebugUnitTest --no-daemon </dev/null >docs/history/reports/sabotage-gradle.log 2>&1
Retorno:
gradle_rc=1
RootAccessPresenterTest > failedProfileDoesNotBecomeAllowSuFalse FAILED
java.lang.AssertionError at RootAccessPresenterTest.kt:76
BUILD FAILED in 18s
XML da cópia: expected null, but was:<false> no teste failedProfileDoesNotBecomeAllowSuFalse. O log dos stubs ficou vazio. A checagem acusou a sabotagem: sim.

## Resultado

A1 = OK. UI ui2 = FALHA pela cobertura incompleta dos pares de contraste usados. Os espaçamentos 4/12/28 dp estão fora da grade de 8 e ficam destacados para decisão de design. Nenhum dispositivo foi acessado.

RELATÓRIO: docs/ultraroot/REVIEW_A1_UI2_codex4.md
