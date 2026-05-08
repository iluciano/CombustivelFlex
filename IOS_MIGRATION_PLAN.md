# Plano de Migração iOS - Combustível Flex

## 1. Resumo do app atual

O Combustível Flex é um app Android nativo em Java para ajudar o usuário a decidir entre gasolina e etanol. A regra principal compara os preços informados e, quando o usuário informa consumo real do veículo, compara custo por quilômetro.

O app atual possui uma interface nova baseada em múltiplas `Activity` Java com layouts XML, navegação por abas inferiores simuladas, persistência simples em `SharedPreferences`, histórico local dos últimos 25 cálculos, configurações de consumo padrão, AdMob e fluxo de atualização pelo Google Play Core.

Regras de negócio principais:

- Se houver consumo de gasolina e etanol, o resultado usa custo por km: etanol vence quando `precoEtanol * consumoGasolina < precoGasolina * consumoEtanol`.
- Se não houver consumo, usa a regra clássica de 70%: etanol vence quando `precoEtanol / precoGasolina < 0.7`.
- Preços e consumos devem ser maiores que zero.
- Consumo deve ser informado para os dois combustíveis ou para nenhum.
- Quando há consumo padrão salvo, a tela inicial preenche os campos de consumo automaticamente.
- Se o usuário informar consumos diferentes dos padrões salvos, o app pergunta se deve atualizar os padrões.
- Cada resultado válido é salvo no histórico local.

## 2. Lista de telas existentes

- `NewStartActivity`: tela inicial/atalhos, atualmente a `LAUNCHER Activity`.
- `NewHomeActivity`: tela principal de cálculo com campos de preço e consumo.
- `NewResultActivity`: resultado, economia estimada e comparativo de custo.
- `NewHistoryActivity`: histórico dos últimos 25 cálculos e ação para limpar histórico.
- `NewStationsActivity`: tela "Postos próximos" marcada como em breve.
- `NewTipsActivity`: dicas de economia de combustível.
- `NewMoreActivity`: tela "Mais", com atalhos para dicas e configurações, além de anúncio nativo.
- `NewSettingsActivity`: configurações de unidade, consumo padrão, notificações, lembrete, avaliação, compartilhamento e sobre.
- `MainActivity` e `ResultActivity`: fluxo legado de cálculo/resultado, ainda no projeto, mas a entrada principal usa o fluxo novo.

## 3. Fluxo de navegação

Fluxo principal novo:

1. App abre em `NewStartActivity`.
2. "Calcular combustível" abre `NewHomeActivity` limpando entradas.
3. `NewHomeActivity` valida os campos e abre `NewResultActivity`.
4. `NewResultActivity` permite recalcular, voltando para `NewHomeActivity` com entradas limpas.
5. Abas inferiores dão acesso a Início, Histórico, Postos e Mais em quase todas as telas.
6. `NewMoreActivity` abre `NewTipsActivity` e `NewSettingsActivity`.
7. `NewHistoryActivity` lista cálculos salvos e pode limpar o histórico com confirmação.
8. `NewStationsActivity` é uma página informativa de funcionalidade futura.

Observações:

- `NewStartActivity` também verifica atualização via Play Core.
- `MainActivity` possui um atalho oculto: se o preço da gasolina for `99.00`, abre `NewStartActivity`.
- No iOS, a navegação deve ser recriada com `TabView` e `NavigationStack`, sem imitar Activities.

## 4. Models/classes principais

- `CalculationHistoryItem`: model local com `createdAt`, preços, consumos, resultado, economia e flag `usedConsumption`.
- `CalculationHistoryStore`: salva, lista e limpa histórico em `SharedPreferences` como JSON; limite de 25 itens.
- `NewSettingsStore`: guarda unidade (`liter` ou `km`), consumo padrão de gasolina/etanol, notificações e lembrete de preço em `SharedPreferences`.
- `AdMobBanner`: centraliza banners adaptativos e anúncio nativo do AdMob.
- `InAppUpdateHelper`: usa Google Play Core para atualização in-app e fallback para Play Store.
- `MaskedDecimalTextWatcher`: máscara simples para entradas decimais com até 4 dígitos e preenchimento de 2 casas decimais.
- `EdgeToEdgeHelper`: ajustes de janela/insets Android.

Sugestão iOS equivalente:

- `FuelCalculation`: entrada e resultado do cálculo.
- `CalculationHistoryItem: Identifiable, Codable`.
- `CalculationService`: regras de cálculo, validação e formatação independente de UI.
- `HistoryStore`: persistência local com `UserDefaults` usando `Codable`, mantendo limite de 25 itens.
- `SettingsStore: ObservableObject`: preferências em `UserDefaults` ou `@AppStorage`.
- `AdService` ou `AdMobBannerView`: wrapper opcional para Google Mobile Ads SDK no iOS.

## 5. APIs, endpoints ou services utilizados

Não há chamadas HTTP de backend próprio, REST, Firebase, Retrofit, OkHttp, Room, Maps ou localização implementadas.

Services/dependências externas atuais:

- Google Mobile Ads/AdMob:
  - App ID: `ca-app-pub-1199102836233471~8079530547`
  - Banner principal: `ca-app-pub-1199102836233471/9002359835`
  - Banner resultado: `ca-app-pub-1199102836233471/5072740189`
  - Native ad "Mais": `ca-app-pub-1199102836233471/3525523095`
  - Banner dicas: `ca-app-pub-1199102836233471/3058619235`
  - Banner configurações: `ca-app-pub-1199102836233471/2035295102`
- Google Play In-App Updates:
  - `com.google.android.play:app-update:2.1.0`
  - Sem equivalente direto no iOS; atualizações ficam sob a App Store.
- Links externos:
  - Play Store para avaliar/atualizar/compartilhar o app.
  - No iOS, substituir por App Store URL quando o app tiver Bundle ID/App Store ID.

## 6. Permissões Android usadas e equivalentes iOS

- `android.permission.INTERNET`
  - iOS: não exige permissão do usuário; necessário apenas configurar ATS se houver HTTP não seguro. Para AdMob, integrar Google Mobile Ads SDK e configurar `Info.plist` conforme documentação do SDK.
- `android.permission.POST_NOTIFICATIONS`
  - iOS: usar `UserNotifications` (`UNUserNotificationCenter.requestAuthorization`) quando a funcionalidade de notificação/lembrete for realmente implementada.

Não há permissão Android de localização no manifesto atual. A tela de postos é "em breve" e ainda não usa mapa/localização.

Se a tela de postos for implementada no iOS:

- Usar `CoreLocation` para permissão de localização.
- Usar `MapKit` para mapa, postos próximos e rotas.
- Incluir chaves de privacidade no `Info.plist`, como `NSLocationWhenInUseUsageDescription`.

## 7. Assets necessários para iOS

Assets bitmap existentes a migrar/adaptar:

- `ic_launcher.png` em densidades mipmap.
- `new_header_road.png`.
- `new_stations_hero.png`.
- `car.png`.
- `desertroad4k.png`.
- `ic_local_gas_station_black_48dp.png`.

Assets vetoriais/drawables XML a recriar em SwiftUI ou como SF Symbols/custom vectors:

- Ícones de navegação: home, histórico, postos, mais, voltar, chevron.
- Ícones de features: relógio, localização, rota, etiqueta, engrenagem, lâmpada.
- Ícones de dicas: pneu, cones/aceleração, manutenção, ar-condicionado.
- Fundos/cartões/botões: cards, badges, botões primário/secundário, cartões verdes/laranjas, painel de comparação, onda da tela de postos.

Cores principais:

- Background: `#F6F8FC`
- Surface: `#FFFFFF`
- Texto primário: `#101828`
- Texto secundário: `#475467`
- Texto muted: `#98A2B3`
- Divider: `#E4E7EC`
- Azul: `#1473F8`
- Laranja: `#FF7A00`
- Verde: `#0F9F5A`
- Verde claro/resultado: `#ECFDF3`, `#D1FADF`

Também será necessário gerar App Icons e Launch Screen próprios no padrão iOS.

## 8. Funcionalidades já prontas

- Cálculo gasolina vs etanol pela regra de 70%.
- Cálculo por consumo real e custo por km.
- Validação de entradas.
- Máscara/formatação decimal básica.
- Resultado com combustível recomendado.
- Economia estimada.
- Comparativo gasolina/etanol.
- Histórico local dos últimos 25 cálculos.
- Limpeza de histórico com confirmação.
- Consumo padrão por combustível.
- Preenchimento automático dos consumos padrão.
- Confirmação para atualizar consumo padrão.
- Configuração de unidade (`R$/L` ou `R$/km`), embora a unidade ainda tenha efeito limitado na UI atual.
- Toggle de notificações e lembrete de preços em persistência local.
- Tela de dicas de economia.
- Compartilhamento do app.
- Avaliação do app via loja.
- AdMob em telas principais.
- Atualização in-app no Android via Play Core.

## 9. Funcionalidades incompletas ou "em breve"

- `Postos próximos`: tela visual/informativa, sem mapa, localização, busca real de postos, comparação de preços ou rotas.
- Notificações/lembrete de revisar preços: preferência é salva, mas não há agendamento de notificações locais.
- Unidade `R$/km`: preferência existe, mas a experiência ainda não parece mudar todos os cálculos/labels de forma abrangente.
- Tela "Sobre": mostra apenas um toast com o nome do app.
- AdMob iOS ainda exigirá configuração específica, IDs iOS próprios e revisão de privacidade.
- Links de Play Store devem ser substituídos por links App Store.
- Não há Firebase ou backend configurado.

## 10. Riscos técnicos para portar para iOS

- A regra de máscara decimal Android é simples e pode não ser natural no teclado iOS; convém adaptar para `Decimal`, `NumberFormatter` e locale `pt_BR`.
- O uso de `Double` pode causar pequenas diferenças de arredondamento; no iOS, preferir `Decimal` para preços.
- O histórico atual é JSON manual em `SharedPreferences`; no iOS, `Codable` em `UserDefaults` é suficiente, mas precisa preservar ordenação e limite de 25 itens.
- AdMob no iOS exige SDK, App ID próprio no `Info.plist`, consentimento/privacidade e possivelmente SKAdNetwork IDs.
- Play Core In-App Update não existe no iOS; a atualização deve ser deixada para a App Store ou virar um aviso manual com endpoint próprio no futuro.
- Funcionalidades de postos exigirão decisão de produto: MapKit puro, Apple Maps, Google Places ou backend próprio de preços.
- Textos do Android aparecem com encoding corrompido em alguns arquivos (`CombustÃ­vel` etc.); antes de migrar strings, revisar a codificação e restaurar acentuação correta.
- O projeto tem telas legadas e novas; a versão iOS deve priorizar o fluxo novo para evitar portar dívida desnecessária.

## 11. Sugestão de arquitetura iOS usando SwiftUI

Arquitetura simples, sem dependências externas no começo:

- `CombustivelFlexApp`: entrada do app.
- `RootTabView`: `TabView` com Início, Histórico, Postos e Mais.
- `StartView`: atalhos principais e identidade visual.
- `CalculatorView`: formulário de preços/consumos.
- `ResultView`: resultado e comparação.
- `HistoryView`: lista e limpeza do histórico.
- `StationsComingSoonView`: versão inicial da tela de postos.
- `TipsView`: lista estática de dicas.
- `MoreView`: atalhos para dicas/configurações.
- `SettingsView`: unidade, consumos padrão, notificações, compartilhar, avaliar e sobre.
- `FuelCalculatorViewModel`: estado e validação da tela de cálculo.
- `SettingsViewModel`: estado das preferências.
- `FuelCalculationService`: regras puras de negócio.
- `HistoryStore`: persistência de histórico com `UserDefaults`.
- `SettingsStore`: `@AppStorage` ou `UserDefaults`.
- `NotificationService`: somente quando implementar lembretes reais.
- `AdMobService`/`BannerAdView`: adicionar em etapa posterior, após MVP sem anúncios.

Persistência recomendada:

- `UserDefaults` para configurações simples e histórico pequeno.
- Avaliar `SwiftData` apenas se o histórico evoluir para filtros, busca, métricas ou sincronização.

## 12. Ordem recomendada de implementação

1. Criar projeto iOS SwiftUI, configurar assets, cores, tipografia base e strings corrigidas em português.
2. Implementar `FuelCalculationService` com testes unitários para regra de 70%, cálculo por consumo, economia e validações.
3. Implementar `SettingsStore` e `HistoryStore` com `Codable`/`UserDefaults`.
4. Criar `RootTabView` e navegação base com `TabView`/`NavigationStack`.
5. Implementar `StartView` e `CalculatorView`.
6. Implementar `ResultView` salvando histórico.
7. Implementar `HistoryView` com lista, vazio e limpeza com confirmação.
8. Implementar `SettingsView`, incluindo consumo padrão, unidade, compartilhar, avaliar e sobre.
9. Implementar `TipsView` e `MoreView`.
10. Implementar `StationsComingSoonView` preservando a promessa visual atual, sem localização ainda.
11. Adicionar notificações locais somente se o lembrete de preços for priorizado.
12. Integrar AdMob iOS em etapa separada, com IDs iOS e ajustes de privacidade.
13. Planejar versão real de postos próximos com MapKit/CoreLocation e definição de fonte de dados.

## Dúvidas e arquivos ausentes antes de implementar

- Qual será o Bundle ID iOS?
- O app já possui conta/App Store ID para links de avaliação e compartilhamento?
- Existem assets em alta resolução/originais fora do projeto Android?
- Haverá IDs AdMob específicos para iOS ou a primeira versão deve sair sem anúncios?
- A tela de postos próximos deve permanecer "em breve" no MVP iOS ou já deve usar MapKit/CoreLocation?
- O lembrete de revisar preços deve agendar notificações locais ou permanecer apenas como preferência visual?
- A unidade `R$/km` deve alterar a tela principal, o resultado, ou apenas ser uma configuração futura?
