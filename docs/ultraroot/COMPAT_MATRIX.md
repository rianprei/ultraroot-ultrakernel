# Matriz de compatibilidade — seed SP1

Data-base: 2026-10-11; atualização de flags: 2026-10-13. VALIDATED nos flags vale só para host; aparelho NÃO VERIFICADO até D2. Registros de aparelho antigos são históricos, sem medida hoje; demais células NÃO VERIFICADAS nesta rodada. Célula: VALIDATED, IMPLEMENTED_UNVALIDATED, ARCHITECTED, INCOMPATIBLE ou UNKNOWN. Sem evidência nova, a célula não sobe.

Eixos: capacidade × backend × Android × kernel × ABI × device × deps.

| Capacidade | Backend | Android | Kernel | ABI | Device | Deps | Célula | Evidência |
|---|---|---|---|---|---|---|---|---|
| KSU-core | KSU tiann (modalidade no lake NÃO VERIFICADA) `6e386044` | 15 | 6.6.89-android15-8 | arm64 | POCO C75 lake, slot B | boot_b 67108864 | VALIDATED (histórico da base 2026-10-11); aparelho hoje NÃO VERIFICADO | boot C0.1 no aparelho, citado no plano-mestre |
| manager-grant | mesmo KSU + APK release | 15 | C0.3 desta árvore | arm64 | lake | cert `efb8d49f`, UAPI 5 | IMPLEMENTED_UNVALIDATED | hash da imagem `2977e1dc`; Task 7 não rodou |
| module.prop (F14) | KSU e Magisk, contrato de arquivos | 15 | — | arm64 | lake | zip com id/name/version/versionCode/author | ARCHITECTED | fixtures de host em `scratch/sp1/fixtures`; mount NOT_TESTED |
| magic mount (F16) | Magisk | — | — | — | lake | tmpfs+bind | ARCHITECTED | matriz marca ACOPLADA ao Magisk; não rodar junto com overlayfs |
| overlayfs de módulo | KSU | — | — | — | lake | post-fs-data | UNKNOWN | a matriz marca overlayfs AUSENTE nos repos lidos; o lake não foi medido |
| F01–F13, F15, F17–F27, F29–F30 | ver matriz fase 1 | — | — | — | — | — | UNKNOWN | sem artefato de port neste SP1 |
| GET_INFO: LKM | KSU, decoder puro | — (host JVM) | configuração lake NÃO VERIFICADA | — | host; aparelho NÃO VERIFICADO (D2) | KsuMode.kt:18-29; readMode em KsuBackend.kt:70-76 | VALIDATED (host); UNKNOWN (aparelho) | 2026-10-13: KsuModeTest.kt:14-15,28-36,47-69; 10/0 no XML. Bit 1; não prova modalidade do kernel do aparelho |
| GET_INFO: MANAGER | KSU, decoder puro | — (host JVM) | — | — | host; aparelho NÃO VERIFICADO (D2) | KsuMode.kt:18-29; readMode em KsuBackend.kt:70-76 | VALIDATED (host); UNKNOWN (aparelho) | 2026-10-13: KsuModeTest.kt:19-20,28-36,47-69; 10/0 no XML. Bit 2; refere-se ao processo que chama (KsuMode.kt:7-9) |
| GET_INFO: LATE_LOAD | KSU, decoder puro | — (host JVM) | — | — | host; aparelho NÃO VERIFICADO (D2) | KsuMode.kt:18-29; readMode em KsuBackend.kt:70-76 | VALIDATED (host); UNKNOWN (aparelho) | 2026-10-13: KsuModeTest.kt:24-25,28-36,47-69; 10/0 no XML. Bit 4; sem medida do kernel do aparelho |

Não há compatibilidade universal entre módulo Magisk e módulo KSU.

## Inventário dos 8 módulos reais (leitura, 2026-10-11)

Fonte: `docs/ultraroot/MODULE_MIGRATION_lake.md`. `adb -s [redigido: serial]`, só `/data/adb/modules`. O `su` dessa leitura estava em `u:r:magisk:s0`. Célula de "roda sob KSU" permanece UNKNOWN: ninguém executou esses módulos no runtime KSU.

| Módulo | Classe de migração |
|---|---|
| hosts, yt-noads, poco_perfgov, termuxadb_rootport | NATIVO |
| zygisk_vector, bc-poc | PRECISA-ZYGISK |
| always-trust-user-certs, tabs-ca-inject | PRECISA-ADAPTER |
