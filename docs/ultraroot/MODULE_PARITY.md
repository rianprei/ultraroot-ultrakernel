# Paridade modular por etapa

Data: 2026-10-11. Montar de verdade é device-gated (Task 7). Aqui só a estrutura no host.

Regra: não anunciar compatibilidade universal. Não ativar magic mount e overlayfs ao mesmo tempo. Um módulo Magisk que depende de magic mount pode não montar sob o KSU; isso se declara, não se promete.

Fixtures:

- `[artefato local, fora do repo]/sp1/fixtures/sp1-ksu-min.zip`
- `[artefato local, fora do repo]/sp1/fixtures/sp1-magisk-min.zip`

| Etapa | KSU fixture | Magisk fixture | Status |
|---|---|---|---|
| `module.prop` (id, name, version, versionCode, author) | presente | presente | host: `test_module_props.sh` |
| `post-fs-data.sh` | escreve marcador | ausente de propósito | NOT_TESTED no boot |
| `service.sh` | ausente | presente | NOT_TESTED no boot |
| `system.prop` | ausente | ausente | NOT_TESTED |
| `sepolicy.rule` | ausente | ausente | NOT_TESTED |
| mount systemless (`system/`) | ausente | `system/etc/sp1-magisk-marker` | NOT_TESTED; depende de magic mount |
| `.replace` | ausente | ausente | NOT_TESTED |
| init-stages | não medido | não medido | NOT_TESTED |

O teste de host falha se `id`, `name`, `version`, `versionCode` ou `author` faltar no `module.prop` do zip.
