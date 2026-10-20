# Contexto do Projeto — Auto Posto 01 (Clube 01)

Este documento estabelece as diretrizes obrigatórias de arquitetura, desenvolvimento, design, QA e governança do projeto **Auto Posto 01 / Clube 01**. Todo agente ou desenvolvedor deve ler e seguir rigorosamente estas diretrizes antes de qualquer alteração no código.

---

## 1. Visão do Produto & Negócio
- **Nome:** Auto Posto 01 (Clube 01)
- **Objetivo:** Aplicativo móvel para clientes da rede de postos Auto Posto 01, oferecendo consulta de preços de combustíveis atualizados em tempo real, localização e rotas dos postos, clube de vantagens/fidelidade (pontuação por abastecimento/compras na loja Petros), promoções exclusivas e pagamentos facilitados.
- **Plataforma Estratégica:** Iniciando pelo **iOS** e mantendo paridade com **Android** através de base de código única e compartilhada (Cross-Platform via React Native/Expo ou Flutter).

---

## 2. Governança e Processo de Entrega (GitHub Flow)

### 2.1 Issues no GitHub
Todas as tarefas devem possuir uma Issue correspondente antes do início da implementação, categorizadas obrigatoriamente com o prefixo no título e label:
- `[Nova função]` - Para desenvolvimento de telas, integrações ou recursos inéditos.
- `[Melhoria]` - Para refatoração, otimização de performance, refinamento de UI/UX ou acessibilidade.
- `[Correção]` - Para resolução de bugs, regressões ou falhas de contrato de dados.

### 2.2 Pull Requests e Branching
Nenhum código entra diretamente na branch principal (`main`). Todas as entregas devem seguir Pull Requests (PRs).
Em **todo Pull Request**, a descrição deve conter obrigatoriamente:
1. **Issue Relacionada:** Link para a issue (ex: `Closes #X` ou `Ref #X`).
2. **O que mudou:** Descrição clara e concisa das modificações implementadas.
3. **Como foi validado:** Evidências de testes manuais, testes automatizados e capturas de tela/vídeo da interface renderizada.
4. **Riscos, Limitações e Próximos Passos:** Impactos colaterais previstos, trade-offs assumidos e dependências futuras.

---

## 3. Padrões de UX, UI & Motion Design

O app deve transmitir uma sensação **premium, nativa, responsiva e fluida** (Human Interface Guidelines da Apple + Motion Principles).

### 3.1 Requisitos de Interface
- **Skeleton Screens:** Carregamento elegante com efeito shimmer para todas as listas, cards de preços e extratos de pontos (nada de spinners genéricos no meio da tela vazia).
- **Lazy Loading:** Paginação e carregamento sob demanda para listas extensas e imagens.
- **Animações Suaves de Entrada e Saída:** Duração entre 200ms e 350ms, utilizando curvas de desaceleração naturais (spring / ease-out).
- **Feedback Visual & Tátil (Haptic):** Todo toque em botão ou card interativo deve ter resposta imediata de pressão/opacidade e haptic feedback nativo (especialmente no iOS).
- **Estados de Progresso:** Botões de envio devem exibir estado de loading inline (spinner embutido e botão desabilitado para prevenir double-click).
- **Transições Consistentes:** Navegação entre telas, abertura de modais em formato *bottom sheet* (detent do iOS) e transição de cards com continuidade visual.
- **Revisão de Design de Produto Sênior:** Antes de considerar qualquer entrega finalizada, revisar a interface eliminando qualquer comportamento brusco, travado, genérico ou amador.

---

## 4. Esteira de Qualidade & Engenharia (QA & Reliability)

### 4.1 Observabilidade
- Monitoramento de erros e crashes em tempo real (Sentry / Datadog).
- Rastreamento de chamadas de API e tempos de resposta de rede (Firestore e Petros).

### 4.2 Qualidade de Código & Lint
- Linter e formatador estrito (Biome / ESLint / Prettier).
- Tipagem estrita TypeScript (sem uso indiscriminado de `any`).
- Pre-commit hooks com validação de lint e mensagens de commit semânticas (Commitlint).

### 4.3 Testes
- Testes unitários para regras de negócio, cálculos de pontos e parsers de dados.
- Testes de integração para repositórios de dados e serviços de autenticação.
- Testes End-to-End (E2E) com Playwright / Detox / Maestro para fluxos críticos (Login -> Home -> Exibição do QR Code de Fidelidade).

### 4.4 Segurança & Diretrizes Específicas do iOS / App Store
- **Sign in with Apple obrigatório:** Diretriz da Apple exige autenticação nativa da Apple se o app oferecer qualquer outro login de terceiros (Google, etc.).
- **Separação Frontend/Backend:** Nenhuma chave mestra ou credencial administrativa embutida no bundle do cliente móvel.
- **Controle de Sessão e Biometria:** Face ID / Touch ID nativo com fallback seguro de senha.
- **Rate Limit & Sanitização:** Validação estrita de entradas em formulários (CPF, e-mail, telefone).
- **Termos de Uso e Política de Privacidade:** Telas dedicadas acessíveis antes e depois do login.

### 4.5 Arquitetura
- **Evitar Overengineering:** Escolher sempre a solução mais simples, robusta e direta que resolve o problema.
- **Componentização Reutilizável:** Design System de componentes atômicos (Botões, Inputs, Cards de Preço, Sheets, Headers) evitando reinventar componentes existentes.
- **Evitar Bottlenecks:** Memoização consciente e evitar re-renderizações desnecessárias em listas de preços e postos.
