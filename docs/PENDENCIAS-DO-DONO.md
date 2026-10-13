# Pendências do dono

Cada item diz o que falta e a frase exata que libera o passo. Nenhum agente executa um passo destes sem a frase.

## Aparelho (D0 a D7)

- **D1, instalar o ui10:** "sim, instalar ui10" (`docs/ultraroot/plan.md`, linha 92). Antes de instalar, conferir o sha256 do APK (`out/`, fora do repo).
- **D2, diagnóstico do fd (leitura):** "sim, diagnóstico do fd" (`docs/ultraroot/plan.md`, linha 93). Libera a decisão E2.
- **D3, log de boot do C0.3:** frase na linha 94 de `docs/ultraroot/plan.md`.
- **D4, grant de teste:** "sim, grant teste" (`docs/ultraroot/plan.md`, linha 95).
- **D5, migrar módulos nativos:** "sim, migrar <módulo>" (`docs/ultraroot/plan.md`, linha 96), um por vez.
- **D6 e D7 (flash do SP1 e drill de rollback):** linhas 97 e 98 de `docs/ultraroot/plan.md`. Só com backup conferido, slot B e guard.

## Decisões (E1 a E6)

- **E1, copy e layout dos cartões e da busca:** escolher a opção A ou B em `docs/ultraroot/E1-OPCOES-COPY-LAYOUT.md`. Libera H7.
- **E2, origem do fd:** decidir depois do D2 (`docs/ultraroot/plan.md`, linha 105). Libera a chamada de `setAppProfile` na tela.
- **E3, ordem com o Magisk:** `docs/ultraroot/plan.md`, linha 106.
- **E4, aceite do grant sem o Magisk desligado:** `docs/ultraroot/plan.md`, linha 107. Até decidir, "su via KSU" continua não provado.
- **E5 e E6:** `docs/ultraroot/plan.md`, linhas 108 e 109.

## Lake

- **Commit do lake:** "sim, commitar" para `c8e7812` (hoje só local).
- **Push do lake:** "sim, push", depois de revisão brutal do diff.
- **Diff de trabalho:** `kernel/lake/pending/diff-nao-commitado-2026-10-13.diff` não está commitado. Decidir se entra.

## Repo

- **Licença:** nenhuma definida. Todos os direitos reservados. Opções: MIT ou Apache-2.0 para o código próprio. Código do KernelSU em `kernel/upstream/` mantém a licença original.
- **Capturas de tela do aparelho:** duas PNG ficaram fora do repo (ver `docs/history/evidence/NAO-INCLUIDO.txt`). Podem conter notificações ou dados pessoais. Se quiser incluir, depois de você olhar.
