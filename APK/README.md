# Comikey Downloader Android

Projeto Android nativo em Java para abrir o login oficial do Comikey em um WebView, mantendo a sessão no aparelho.

## Segurança

- Não existe campo para colar cookies.
- Não há ponte JavaScript que exporte cookies.
- A sessão fica sob controle do `CookieManager` do WebView.
- O botão `Sair` remove a sessão local e o cache.
- URLs do Comikey e os domínios necessários do Google OAuth permanecem dentro do WebView; outros links externos abrem no navegador do aparelho.
- Cookies de terceiros são habilitados somente para permitir o fluxo de login social dentro do WebView. O app nunca lê, exporta ou envia cookies.

## Compilação

Abra esta pasta no Android Studio e gere o APK de debug ou release. O projeto usa Android Gradle Plugin 8.2.2, compileSdk 34 e minSdk 23.

O ambiente desta tarefa não possui um SDK Android configurado de forma completa. A tentativa de compilação chegou ao Gradle, mas parou antes de compilar porque `ANDROID_HOME`/`sdk.dir` não apontam para um SDK instalado. Por isso esta entrega contém o projeto Android-fonte em `APK/`, pronto para abrir no Android Studio ou para outro agente compilar — não um arquivo `.apk` falso ou vazio.

Com um SDK Android 34 configurado, o comando esperado é:

```bash
gradle assembleDebug
```

O resultado será gerado em `app/build/outputs/apk/debug/app-debug.apk`.

## Login com Google

Esta versão mantém `accounts.google.com`, domínios Google relacionados e o retorno do Comikey na mesma sessão do WebView. Depois de recompilar e instalar a versão 1.0.1, limpe a sessão antiga usando o botão `Sair` e tente o login novamente.

Se o Google mostrar uma mensagem explícita de que o navegador incorporado não é seguro, isso é uma política do provedor OAuth/Comikey e não pode ser contornado com cookies. Nesse caso, o login precisa ser implementado pelo fluxo oficial de navegador externo/Custom Tab com retorno autorizado pelo Comikey.