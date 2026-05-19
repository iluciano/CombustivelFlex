# CLAUDE.md — CombustivelFlex (Android)

> Contexto completo do projeto para o Claude Code. Leia este arquivo antes de
> qualquer alteração no código.

---

## Visão Geral

App Android nativo Java. Calculadora de combustível flex (gasolina vs etanol)
com tela de postos próximos com preços da ANP.

**Pacote:** `igorluciano.com.br.combustivelflex`
**Versão atual:** versionCode 30, versionName 1.2.0
**Branch principal:** `master`

---

## Stack

| Camada | Tecnologia |
|---|---|
| Linguagem | Java (Android nativo) |
| minSdk / targetSdk | 23 / 35 |
| Banco de dados | Cloud Firestore (Firebase) |
| Localização | FusedLocationProviderClient (Google Play Services) |
| UI da lista | RecyclerView + adapter customizado |
| Anúncios | AdMob — Native Advanced (`AdLoader` + `forNativeAd()`) |
| Build | Gradle Wrapper (`DEFAULT_WRAPPED`) |

---

## Estrutura de Telas

| Activity | Layout | Descrição |
|---|---|---|
| NewStartActivity | activity_new_start.xml | Calculadora flex (tela inicial) |
| NewHistoryActivity | activity_new_history.xml | Histórico de cálculos |
| NewStationsActivity | activity_new_stations.xml | Lista de postos próximos |
| NewStationDetailActivity | activity_new_station_detail.xml | Detalhe do posto |
| NewMoreActivity | activity_new_more.xml | Aba "Mais" |
| NewSettingsActivity | activity_new_settings.xml | Configurações |
| NewTipsActivity | activity_new_tips.xml | Dicas de economia |

---

## Estrutura do Firestore

**Coleção:** `postos` (6.431 documentos)

| Campo | Tipo | Observação |
|---|---|---|
| `nome` | String | Nome do posto |
| `bandeira` | String | `ipiranga`, `shell`, `br`, `ale`, `totalenergies` |
| `latitude` | Number | Latitude geográfica |
| `longitude` | Number | Longitude geográfica |
| `preco_gasolina` | Number | Preço da gasolina comum (R$) |
| `preco_etanol` | Number | Preço do etanol (R$) |
| `rua` | String | — |
| `numero` | String | — |
| `bairro` | String | — |
| `cidade` | String | — |
| `estado` | String | UF |
| `data_ultima_coleta` | String | Formato `YYYY-MM-DD` — data da coleta ANP |

---

## Query de Postos — Bounding Box

**NÃO fazer** `.collection("postos").get()` sem filtro — leria 6.431 documentos
a cada acesso, estourando o limite gratuito do Firestore com ~8 usuários/dia.

**Implementação correta** em `NewStationsActivity.java`:

```java
double latDelta = 100.0 / 111.0;
double lonDelta = 100.0 / (111.0 * Math.cos(Math.toRadians(userLat)));
double minLat = userLat - latDelta;
double maxLat = userLat + latDelta;
double minLon = userLon - lonDelta;
double maxLon = userLon + lonDelta;

FirebaseFirestore.getInstance()
    .collection("postos")
    .whereGreaterThan("latitude", minLat)
    .whereLessThan("latitude", maxLat)
    .get()
    .addOnSuccessListener(snapshot -> {
        // filtrar longitude no cliente:
        if (lon < minLon || lon > maxLon) continue;
        // calcular distância, ordenar, pegar top 10
    });
```

**Regras após receber os dados:**
1. Ignorar documentos sem `latitude` ou `longitude`
2. Filtrar `longitude` client-side (Firestore não suporta range em dois campos)
3. Calcular distância exata com `Location.distanceBetween()`
4. Ordenar do mais próximo ao mais distante
5. Exibir apenas os **10 primeiros**

---

## Data de Coleta ANP

- Campo Firestore: `data_ultima_coleta` (formato `YYYY-MM-DD`)
- Exibir como: `"Data de coleta: DD/MM/YYYY"`
- String resource: `stations_coleta_label = "Data de coleta: %1$s"`
- Fallback quando ausente: `"08/05/2026"`
- Acompanha ícone `ic_info.xml` que abre AlertDialog:
  - Título: `info_anp_title`
  - Mensagem: `info_anp_message`
- **Atenção de layout:** usar `layout_width="wrap_content"` no TextView da data —
  nunca `0dp` + `layout_weight`, pois empurra o ícone para longe do texto

---

## EdgeToEdge

`EdgeToEdge.enable()` está ativo em todas as Activities. Todas as telas precisam
de `android:fitsSystemWindows="true"` na raiz do layout para evitar sobreposição
com a status bar.

---

## Padrão Visual dos Layouts

- Títulos de seção ficam **fora** do card branco, em um header acima dele
- O card contém apenas o conteúdo funcional
- Anúncios nativos: container `FrameLayout` + divider acima ficam `visibility="gone"`
  até o ad carregar

---

## Anúncios AdMob

Formato: **Native Advanced** — usar `AdLoader.forNativeAd()`, nunca `AdView`.

| Tela | Ad Unit ID |
|---|---|
| Mais | `ca-app-pub-1199102836233471/3525523095` |
| Detalhe do posto | (ver código) |

---

## Formatações

**Distância:**
- < 1000 m → `"850 m"`
- ≥ 1000 m → `"1,2 km"` (1 casa decimal, vírgula como separador)

**Preço:**
- Padrão brasileiro: `"R$ 5,79"` (locale pt_BR)

**Data de coleta:**
- Raw: `"2026-05-08"` → Exibido: `"08/05/2026"`

---

## Permissões (AndroidManifest.xml)

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />
```

Localização precisa ser solicitada em runtime (API 23+).

---

## Modelo Posto.java (Parcelable)

Campos: `id`, `nome`, `bandeira`, `latitude`, `longitude`, `precoGasolinaComum`,
`precoEtanol`, `distanciaMetros`, `rua`, `numero`, `bairro`, `cidade`, `estado`,
`dataUltimaColeta`.

Ao adicionar campos: atualizar `writeToParcel` e o construtor `Posto(Parcel in)`
**na mesma ordem**.

---

## Como Gerar Release

Use o comando `/release` — ele obriga a atualizar a versão antes de gerar o AAB.

**Nunca** rodar `./gradlew bundleRelease` diretamente sem antes bumpar
`versionCode` e `versionName` em `app/build.gradle`.

AAB de saída: `app/build/outputs/bundle/release/app-release.aab`

---

## Observações Importantes

- **`google-services.json`** não deve ser criado pelo Claude Code — é gerado pelo
  Firebase Console e contém chaves privadas
- **`keystore.properties`** não deve ser commitado — contém credenciais de assinatura
- O app lê dados apenas do Firestore, nunca de CSV diretamente
- Para referência da lógica de postos no iOS: ver `STATIONS_LIST_SPEC.md`
