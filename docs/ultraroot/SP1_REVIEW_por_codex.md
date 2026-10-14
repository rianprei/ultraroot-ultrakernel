# Revisão adversarial SP1 — Tasks 1–6

Data: 2026-10-11. Revisão estática/read-only. Não rodei testes, adb, fastboot real ou device. O commit da Task 2 é c8e78121d602a1b4bf8b00318d2d3acec78c965a.

## Veredito

**CORRIGIR** antes de tratar o pacote SP1 como pronto para o device: completar o aceite KSU real, fechar a etapa de reboot do recovery e retirar o pacote C0.2/C0.1 obsoleto do caminho operacional.

## Achados

### 1. [P1] accept_ksu_root.sh não valida o device e pode aprovar sem provar root

**Evidência:** O plano da Task 3 define accept_ksu_root.sh <serial> como exit 0 só quando root foi concedido pelo KSU e pede coleta via adb -s da presença de ksud ou /data/adb/ksu, UAPI do supercall e contexto de su ([fora do repo] docs/superpowers/plans/2026-10-11-ultraroot-sp1.md:76-86). O script atual exige ACCEPT_KSU_FIXTURE; sem ela, sai 2 antes de chamar adb (docs/ultraroot/accept_ksu_root.sh:11-17). Com fixture, aceita qualquer KSU_SU_CONTEXT não vazio que não contenha a substring minúscula magisk, junto com KSU_UAPI=5 e pelo menos uma flag de presença (accept_ksu_root.sh:19-46). Não verifica status/saída do comando su, uid=0 ou que esses valores vieram do serial informado. O teste cobre Magisk com contexto literal u:r:magisk:s0, KSU com u:r:su:s0 e ausência completa de dados; não cobre KSU instalado sem root concedido (scratch/sp1/test_accept_ksu.sh:19-40). No pin local do KSU, UAPI 5 é versão da interface e um campo de get_info, não uma prova de concessão de root (scratch/grok/src/KernelSU/uapi/supercall.h:9-13,34-38).

**Impacto:** O passo de aceite do DEVICE_PLAN_SP1.md:28 nunca pode concluir no uso documentado sem fixture. No único caminho que retorna 0, valores de fixture podem produzir falso positivo: por exemplo, metadados KSU presentes mais qualquer contexto não vazio, mesmo sem prova de uid=0. O fixture Magisk específico reprova, mas isso não prova que a execução de su veio do KSU.

**Correção:** Implementar a coleta real por adb -s "$serial" e falhar se qualquer comando falhar; exigir uma execução su que demonstre uid=0 e a evidência KSU especificada pelo plano. Manter fixtures restritas aos testes e adicionar um caso de KSU instalado com root negado, além de erro de serial/comando.

### 2. [P2] O procedimento de rollback termina em fastboot, embora a suite diga que o backup boota

**Evidência:** Depois do flash, o guard executa set_active b e termina com exit "$rc" ([fora do repo] tools/fastboot_guard.sh:184-193); não envia reboot. A suite descreve como sucesso que o aparelho boota o backup (docs/ultraroot/RECOVERY_SUITE.md:11-13), e o comando de ensaio no plano termina em rollback <backup> (docs/ultraroot/DEVICE_PLAN_SP1.md:31-37). A documentação AOSP especifica que set_active define o slot ativo e limpa o estado unbootable; reboot é um comando separado ([AOSP, implementação A/B](https://source.android.com/docs/core/ota/ab/ab_implement#Set_partitions_slots), [comandos fastboot do AOSP](https://android.googlesource.com/platform/system/core/%2B/e323976e747c28b78b5e1be565317f500e0f5c06/fastboot/fastboot.cpp#314)).

**Impacto:** Executar apenas o rollback restaura e reativa B, mas deixa o aparelho na tela de fastboot. O critério “backup boota” não acontece sem outra ação.

**Correção:** Acrescentar um passo explícito de fastboot_guard.sh reboot após rollback (ou instrução equivalente pelas teclas) e só declarar o cenário concluído após observar o boot. O reboot já é uma operação allowlisted e tem interlock próprio no guard.

### 3. [P2] Há instruções operacionais C0.2/C0.1 ainda acionáveis junto do plano C0.3

**Evidência:** research/MASTER_PLAN.md:46-58 ainda se intitula “PACOTE DE RETORNO” e contém comandos para C0.2 e para o APK debug; o plano C0.3 aparece mais abaixo como “PACOTE DE RETORNO FINAL” (MASTER_PLAN.md:60-70). Além disso, o guard_hashes.txt atualmente presente na raiz do checkout do kernel não rastreado contém o hash do backup e o de C0.1 (poco-c75-lake-kernel/guard_hashes.txt:1-2); o relatório confirma que esse arquivo não entrou no commit (SP1_IMPL_REPORT.md:5). A allowlist específica SP1 e os hashes dos arquivos de imagem estão corretos: backup 3890fb96…29fc7, C0.3 2977e1dc…304fe; C0.1 não consta em guard_hashes_sp1.txt.

**Impacto:** Seguir o bloco antigo ou apontar FASTBOOT_GUARD_HASHES para o arquivo solto pode aceitar C0.1/C0.2 em vez da imagem e do manager release C0.3. Isso pode deixar o kernel bootado sem conceder root ao manager pretendido, apesar do bloco final correto existir.

**Correção:** Retirar ou marcar claramente o bloco C0.2 como obsoleto no pacote de retorno ativo; deixar uma única sequência C0.3 e exigir guard_hashes_sp1.txt (ou regenerar/verificar o arquivo operacional antes do primeiro comando).

## Itens sem achado

- **Task 1:** guard_hashes_sp1.txt contém exatamente os hashes do backup e C0.3, e sha256sum dos dois artefatos confere com esses valores. Não contém o prefixo/hash C0.1.
- **Task 2 — alvo de set_active:** **não há caminho no código revisado que envie set_active a**. A rota rollback exige um arquivo, aplica os gates de tamanho, magic e hash, reescreve a operação para flash boot_b, passa pelo gate current-slot == b/is-userspace != yes antes do flash e só após retorno 0 envia o literal set_active b ([fora do repo] tools/fastboot_guard.sh:101-169,184-192). R1 exige set_active b e ausência de set_active a; R2 nega slot a; R3 nega o pedido direto set_active a; R4 nega hash incorreto ([fora do repo] tools/selftest_fastboot_guard.sh:425-476). O código e os logs arquivados reportam 44/44; não os executei nesta revisão. Sem device test, o efeito real do bootloader ao reativar B permanece NOT_TESTED.
- **Task 4:** a suite marca corretamente os cenários de device como NOT_TESTED; o problema no procedimento de sucesso está no Achado 2.
- **Task 5:** estados distinguem implementação de validação. KSU-core é limitado ao boot C0.1; manager-grant/C0.3 está NOT_TESTED/IMPLEMENTED_UNVALIDATED; F01–F30 permanecem UNKNOWN/NOT_STARTED. Não encontrei alegação de que C0.3 já foi validado no device.
- **Task 6:** os dois ZIPs têm module.prop com id, name, version, versionCode e author. A matriz declara mount como NOT_TESTED, proíbe magic mount e overlayfs simultâneos e não anuncia compatibilidade universal.

## Bloco final

STATUS: DONE
VEREDITO: CORRIGIR(Task 3; Task 4 reboot; retirar instruções/hash operacional C0.2/C0.1)
SET_ACTIVE_A_POSSIVEL: não
ACCEPT_KSU_FALSO_POSITIVO: sim(fixture: flags KSU + qualquer contexto não vazio sem “magisk”; não comprova uid=0)
ACHADOS: 3
