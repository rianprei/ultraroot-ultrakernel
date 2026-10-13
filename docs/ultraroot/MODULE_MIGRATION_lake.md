# Migração dos 8 módulos Magisk do lake para o KSU

Data: 2026-10-11. Leitura só. Serial `adb -s [redigido: serial]`. Nada foi instalado, ativado, desativado ou flashado. O Magisk não foi alterado.

`su -c id` devolveu `uid=0(root) gid=0(root) groups=0(root) context=u:r:magisk:s0`. A lista abaixo é a árvore que esse `su` enxerga em `/data/adb/modules`. Não é evidência de que o KSU já monta esses módulos.

Cópias locais dos textos lidos: `[artefato local, fora do repo]/grok/modmig/<id>/`.

Classes: NATIVO (o runtime de módulo do KSU executa o que já está aí), PRECISA-ZYGISK (SP2 / ZygiskNext), PRECISA-ADAPTER (mount ou nome de script que o runtime não executa sozinho), INCOMPATÍVEL, UNKNOWN.

| Módulo | O que faz | Mecanismo | Zygisk | Classe | Risco | Evidência |
|---|---|---|---|---|---|---|
| hosts | Hosts systemless. O `module.prop` diz "Magisk app built-in systemless hosts module". O arquivo tem cabeçalho AdAway de 2026-09-23 e só as linhas `127.0.0.1 localhost` e `::1 localhost`. | `system/etc/hosts` (magic mount de `system/`). Sem script. Sem arquivo `disable`. | não | NATIVO | Baixo. O conteúdo não traz lista de bloqueio além do localhost. | `module.prop` 126 bytes; `system/etc/hosts` 306 bytes |
| yt-noads | Bind do APK `yt-patched.apk` (111085263 bytes, 2026-09-23) sobre `base.apk` e cada `split_config*.apk` do YouTube, se `versionName` for 20.40.41. Depois apaga `oat` e faz `am force-stop`. | `service.sh`. `MODDIR=${0%/*}`. Sem `system/`. Sem arquivo `disable`. | não | NATIVO | O mount só ocorre nessa versão. Se `$0` chegar vazio, o APK não é achado. | `module.prop`; `service.sh` 1035 bytes |
| poco_perfgov | Depois de `sys.boot_completed`, escreve governor `performance` nos cpu0–7 e no Mali, cpuset, VM, TCP, IO, props, thermal, doze, fpsgo, EEM e GED. | `service.sh` (6624 bytes). O `module.prop` fala em post-fs-data; não existe `post-fs-data.sh`. Chama `resetprop`. Sem arquivo `disable`. | não | NATIVO | Props `persist.*` sobrevivem ao reboot. `resetprop` é o applet do Magisk; se o KSU não o tiver no PATH, esses props falham e o sysfs continua. A linha do EEM escreve em `/proc/eem//eem_offset`. | `module.prop`; `service.sh` |
| termuxadb_rootport | No post-fs-data grava `persist.adb.tcp.port=5555`. | `post-fs-data.sh`. `setprop` e, se o getprop não voltar 5555, `resetprop`. Sem arquivo `disable`. | não | NATIVO | Se o SELinux negar `setprop` e não houver `resetprop`, a porta não fixa. | `module.prop`; `post-fs-data.sh` 1032 bytes |
| always-trust-user-certs | Um certificado PEM em `system/etc/security/cacerts/c8750f0d.0` (1172 bytes). O subject em base64 no PEM contém `bWl0bXByb3h5` (mitmproxy). | Magic mount de `system/`. Não há `module.prop` nem script. Há arquivo `disable` (vazio, 2026-10-05) e `skip_mount_placeholder` (não é `skip_mount`). | não | PRECISA-ADAPTER | Sem `module.prop` o runtime não reconhece o módulo. O store que o `tabs-ca-inject` documenta é `/apex/com.android.conscrypt/cacerts`, não este path de system. Está desativado. | `ls -laR`; o PEM |
| tabs-ca-inject | Quer injetar a CA mitmproxy no trust store do APEX antes do zygote, e no service reaplicar o bloqueio do UID 10361. | Os scripts estão em `post-fs-data.d/10-apex-ca.sh` e `service.d/99-offline.sh`. Não há `post-fs-data.sh` nem `service.sh`. O primeiro faz `mount --bind` de `/data/local/tmp/apexcerts` sobre o APEX. O segundo chama `/data/local/tmp/tabs-offline/tabs-offline-block-uid.sh`. Arquivo `disable` vazio (2026-10-05). | não | PRECISA-ADAPTER | O runtime de módulo executa `post-fs-data.sh` e `service.sh`, não esses diretórios `.d`. Depende de arquivos fora do módulo. Está desativado. | `module.prop` id `tabs.ca-inject`; os dois scripts |
| zygisk_vector | Vector v2.2 (3080), framework Xposed. `updateJson` aponta para `JingMatrix/Vector` em `zygisk/update.json`. | `zygisk/arm64-v8a.so` (1243240) e `zygisk/armeabi-v7a.so` (890612). `service.sh` sobe `daemon` com `unshare -m`. `sepolicy.rule` cria `xposed_file` e `xposed_data`. Arquivo `disable` vazio (2026-10-05). | sim | PRECISA-ZYGISK | Sem ZygiskNext o `.so` não entra no zygote. Está desativado. O `service.sh` usa `MODDIR="${0%/*}"`. | `module.prop`; `ls` de `zygisk/`; `service.sh`; `sepolicy.rule` |
| bc-poc | O `module.prop` descreve carga de `.so` em jogos por Zygisk, "Magisk ou KernelSU+ZygiskNext". O `post-fs-data.sh` cria `/data/adb/bepinex` e chama `migrate-mods-tree.sh`. | `zygisk/arm64-v8a.so` (377544) e `armeabi-v7a.so` (199092). `sepolicy.rule` no formato magiskpolicy; o próprio arquivo diz que Magisk e KernelSU aplicam essa sintaxe. Sem arquivo `disable`. | sim | PRECISA-ZYGISK | A árvore e o sepolicy cabem no runtime comum. A injeção do `.so` não. Está ativo na árvore Magisk. | `module.prop` id `bc-poc`, name `bepInEx-termux`; `post-fs-data.sh`; `sepolicy.rule`; `ls` de `zygisk/` |

## Contagem

- NATIVO: 4 — hosts, yt-noads, poco_perfgov, termuxadb_rootport.
- PRECISA-ZYGISK (SP2): 2 — zygisk_vector, bc-poc.
- PRECISA-ADAPTER: 2 — always-trust-user-certs, tabs-ca-inject.
- INCOMPATÍVEL: nenhum.
- UNKNOWN: nenhum nesta leitura. O que não foi aberto (APK de 111 MB, `.so`, `.dex`) não muda a classe: a classe sai do `module.prop`, dos scripts e da presença do diretório `zygisk/`.

## Aberto

- `su` ainda responde com contexto `u:r:magisk:s0`. Nenhum destes módulos foi executado sob o KSU.
- Não medi se o binário `resetprop` existe fora do Magisk.
- `yt-noads` e `zygisk_vector` dependem de `$0` apontar para o script. Isso foi lido no texto; não foi provado no boot do KSU.
