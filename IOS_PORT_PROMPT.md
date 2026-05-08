# Objetivo

Quero criar uma versão iOS nativa em Swift/SwiftUI com base no app Android existente neste repositório.

## Tarefa inicial

Analise todo o projeto Android atual e gere um plano de migração para iOS.

Não altere arquivos ainda nesta primeira etapa.

Crie um arquivo chamado `IOS_MIGRATION_PLAN.md` contendo:

1. Resumo do app atual.
2. Lista de telas existentes.
3. Fluxo de navegação.
4. Models/classes principais.
5. APIs, endpoints ou services utilizados.
6. Permissões Android usadas e equivalentes iOS.
7. Assets necessários para iOS.
8. Funcionalidades já prontas.
9. Funcionalidades incompletas ou "em breve".
10. Riscos técnicos para portar para iOS.
11. Sugestão de arquitetura iOS usando SwiftUI.
12. Ordem recomendada de implementação.

## Regras

- Não faça conversão automática linha por linha.
- Recrie a versão iOS usando boas práticas de SwiftUI.
- Preserve as regras de negócio do app Android.
- Preserve nomes, cores, textos e identidade visual quando fizer sentido.
- Adapte a experiência para padrões naturais do iOS.
- Use arquitetura simples com Views, ViewModels, Models e Services.
- Evite dependências externas no começo.
- Se houver chamadas HTTP, use URLSession.
- Se houver persistência local simples, sugira UserDefaults ou SwiftData conforme o caso.
- Se houver mapa/localização, indique o equivalente usando MapKit/CoreLocation.
- Se houver Firebase, indique quais SDKs iOS seriam necessários.
- Liste dúvidas ou arquivos ausentes antes de implementar.

## Resultado esperado

Ao final desta primeira etapa, quero apenas o arquivo `IOS_MIGRATION_PLAN.md`.

Depois eu vou pedir para você implementar a versão iOS em etapas.