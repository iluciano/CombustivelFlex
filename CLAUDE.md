# CLAUDE.md — Contexto do Projeto: Postos Próximos

> Este arquivo fornece contexto completo para o Claude Code implementar a tela
> de "Postos Próximos" em um app Android nativo (Java).
> Uma imagem da tela de referência será compartilhada separadamente.

---

## 🎯 Objetivo

Implementar a tela **"Postos Próximos"** conforme o design de referência (imagem anexa).
A tela exibe postos de combustível próximos ao usuário, ordenados por distância,
com o preço da gasolina comum e um botão para ver no mapa.

---

## 🛠️ Stack Tecnológica

| Camada | Tecnologia |
|---|---|
| Linguagem | **Java** (Android nativo) |
| IDE | Android Studio |
| Banco de dados | **Cloud Firestore** (Firebase) |
| Localização | **FusedLocationProviderClient** (Google Play Services) |
| UI da lista | **RecyclerView + CardView** |
| Ícones/Assets | Logos das bandeiras como drawables |

---

## 📁 Estrutura de Arquivos Esperada

```
app/
├── src/main/
│   ├── java/com/seu/pacote/
│   │   ├── MainActivity.java
│   │   ├── PostosActivity.java           ← TELA PRINCIPAL A IMPLEMENTAR
│   │   ├── model/
│   │   │   └── Posto.java               ← Model do posto
│   │   └── adapter/
│   │       └── PostoAdapter.java        ← Adapter do RecyclerView
│   ├── res/
│   │   ├── layout/
│   │   │   ├── activity_postos.xml      ← Layout da tela
│   │   │   └── item_posto.xml           ← Layout de cada card
│   │   └── drawable/
│   │       ├── ic_ipiranga.xml (ou .png)
│   │       ├── ic_shell.xml
│   │       ├── ic_br.xml
│   │       ├── ic_ale.xml
│   │       └── ic_totalenergies.xml
└── build.gradle
```

---

## 🗂️ Estrutura do Firestore

**Coleção:** `postos`

Cada documento representa um posto com os seguintes campos:

```json
{
  "nome": "Posto Ipiranga",
  "bandeira": "ipiranga",
  "latitude": -23.550520,
  "longitude": -46.633308,
  "preco_gasolina_comum": 5.79,
  "preco_gasolina_aditivada": 6.10,
  "preco_etanol": 3.89,
  "atualizado_em": "2026-05-11"
}
```

**Valores válidos para `bandeira`:** `ipiranga`, `shell`, `br`, `ale`, `totalenergies`

---

## 📱 Comportamento da Tela

### Fluxo completo:
1. App solicita permissão de localização (`ACCESS_FINE_LOCATION`)
2. Obtém localização atual via `FusedLocationProviderClient`
3. Busca todos os documentos da coleção `postos` no Firestore
4. Calcula a distância entre o usuário e cada posto via `Location.distanceBetween()`
5. Ordena os postos do mais próximo ao mais distante
6. Exibe a lista em um `RecyclerView`
7. Botão **"VER NO MAPA"** abre o Google Maps com os postos via Intent

### Exibição de cada item (card):
- Logo da bandeira (ícone à esquerda)
- Nome do posto
- Distância em km (ex: `1,2 km`)
- Preço da gasolina comum (ex: `R$ 5,79`)
- Label: `Gasolina comum`
- Chevron `>` à direita (indicando que é clicável)

### Formatação de distância:
- Menos de 1 km → exibir em metros: `850 m`
- 1 km ou mais → exibir com 1 casa decimal: `1,2 km`

### Formatação de preço:
- Padrão brasileiro: `R$ 5,79` (vírgula como separador decimal)

---

## 🔧 Dependências necessárias no `build.gradle` (app)

```groovy
dependencies {
    // Firebase
    implementation platform('com.google.firebase:firebase-bom:33.0.0')
    implementation 'com.google.firebase:firebase-firestore'

    // Google Play Services - Localização
    implementation 'com.google.android.gms:play-services-location:21.2.0'

    // RecyclerView e CardView
    implementation 'androidx.recyclerview:recyclerview:1.3.2'
    implementation 'androidx.cardview:cardview:1.0.0'
}
```

No `build.gradle` do projeto (nível raiz), garantir o plugin do Google Services:
```groovy
plugins {
    id 'com.google.gms.google-services' version '4.4.1' apply false
}
```

No `build.gradle` do app:
```groovy
plugins {
    id 'com.google.gms.google-services'
}
```

---

## 🔐 AndroidManifest.xml — Permissões necessárias

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />
```

---

## 🎨 Design Reference

A tela segue o design da imagem compartilhada. Diretrizes visuais:

- **Background geral:** Branco (`#FFFFFF`)
- **Título:** "Postos próximos" — bold, grande, preto
- **Subtítulo:** "Localização atual" — azul, com ícone de pin
- **Cards:** Fundo branco, separados por divisórias sutis (sem sombra exagerada)
- **Distância:** Cinza escuro, abaixo do nome
- **Preço:** Azul escuro, bold, alinhado à direita
- **Label combustível:** Cinza claro, pequeno, abaixo do preço (`Gasolina comum`)
- **Botão VER NO MAPA:** Azul sólido (`#1A73E8`), texto branco, bordas arredondadas, largura total
- **Bottom Navigation:** 4 tabs — Início, Histórico, Postos (ativo/azul), Mais

---

## 🧭 Lógica de Negócio — Cálculo de Distância

```java
// Dentro do listener do Firestore, para cada posto:
float[] distanceResult = new float[1];
Location.distanceBetween(
    userLatitude, userLongitude,
    posto.getLatitude(), posto.getLongitude(),
    distanceResult
);
posto.setDistanciaMetros(distanceResult[0]);

// Ordenação após busca completa:
Collections.sort(postos,
    Comparator.comparingDouble(Posto::getDistanciaMetros));
```

---

## ⚠️ Observações Importantes

1. **`google-services.json`** deve estar em `app/` — o Claude Code NÃO deve criá-lo,
   pois é gerado pelo Firebase Console e contém chaves privadas do projeto.

2. **Permissão em runtime** — A partir do Android 6.0 (API 23), localização precisa
   ser solicitada em tempo de execução, não apenas no Manifest.

3. **Ícones das bandeiras** — Se não houver os drawables, o Claude Code deve criar
   placeholders coloridos (`ShapeDrawable`) com a inicial da bandeira até os assets
   reais serem fornecidos.

4. **Tratamento de erro** — Implementar fallback para quando:
   - Localização não está disponível
   - Sem conexão com Firestore
   - Lista vazia

5. **Dados do CSV** — Os dados do CSV serão importados para o Firestore via script
   Node.js separado. O app apenas lê do Firestore, nunca do CSV diretamente.

---

## 📋 Tarefas para o Claude Code Executar

Siga esta ordem:

- [ ] 1. Criar `Posto.java` (model com todos os campos + `distanciaMetros`)
- [ ] 2. Criar `item_posto.xml` (layout de cada card da lista)
- [ ] 3. Criar `activity_postos.xml` (layout completo da tela)
- [ ] 4. Criar `PostoAdapter.java` (RecyclerView Adapter)
- [ ] 5. Criar `PostosActivity.java` (Activity principal com lógica de localização + Firestore)
- [ ] 6. Atualizar `build.gradle` com as dependências
- [ ] 7. Atualizar `AndroidManifest.xml` com permissões e declaração da Activity
- [ ] 8. Criar drawables placeholder para cada bandeira (se não existirem)

---

## 💬 Como usar este arquivo com o Claude Code

1. Abra o terminal integrado do VS Code
2. Inicie o Claude Code: `claude`
3. Compartilhe este arquivo e a imagem da tela de referência
4. Peça: _"Implemente a tela seguindo o CLAUDE.md e a imagem anexa"_

O Claude Code lerá este arquivo automaticamente se ele estiver na raiz do projeto.
