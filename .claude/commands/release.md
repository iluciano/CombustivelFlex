Gere um release AAB para o app CombustivelFlex seguindo estes passos obrigatoriamente nesta ordem:

1. Leia o versionCode e versionName atuais em `app/build.gradle`
2. Pergunte ao usuário qual será o novo versionCode e versionName
3. Atualize `app/build.gradle` com os novos valores
4. Execute `./gradlew bundleRelease`
5. Confirme o caminho do AAB gerado: `app/build/outputs/bundle/release/app-release.aab`
6. Crie um commit git com a mensagem no formato: `Release X.Y.Z (versionCode N) — <resumo das mudanças desta versão>`

Não gere o AAB sem antes atualizar a versão. Se o usuário não informar os valores, pergunte antes de prosseguir.
