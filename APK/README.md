# Comikey Downloader Android

Projeto Android nativo em Java para abrir o login oficial do Comikey em um WebView, mantendo a sessão no aparelho.

## Segurança

- Não existe campo para colar cookies.
- Não há ponte JavaScript que exporte cookies.
- A sessão fica sob controle do `CookieManager` do WebView.
- O botão `Sair` remove a sessão local e o cache.
- Apenas URLs do domínio Comikey permanecem dentro do WebView; links externos abrem no navegador do aparelho.

## Compilação

Abra esta pasta no Android Studio e gere o APK de debug ou release. O projeto usa Android Gradle Plugin 8.2.2, compileSdk 34 e minSdk 23.

O ambiente desta tarefa não possui um SDK Android configurado de forma completa. A tentativa de compilação chegou ao Gradle, mas parou antes de compilar porque `ANDROID_HOME`/`sdk.dir` não apontam para um SDK instalado. Por isso esta entrega contém o projeto Android-fonte em `APK/`, pronto para abrir no Android Studio ou para outro agente compilar — não um arquivo `.apk` falso ou vazio.

Com um SDK Android 34 configurado, o comando esperado é:

```bash
gradle assembleDebug
```

O resultado será gerado em `app/build/outputs/apk/debug/app-debug.apk`.