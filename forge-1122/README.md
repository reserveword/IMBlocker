# IMBlocker Forge 1.12.2

This directory is a standalone Forge 1.12.2 client module for IMBlocker.

## Build

Use Java 8 and run:

```text
gradlew setupDecompWorkspace
gradlew build
```

The module deliberately has its own ForgeGradle 3 build because Minecraft 1.12.2 uses the legacy LaunchWrapper/MCP toolchain. It is not included in the modern root Gradle build.

## Scope

The module tracks text-field focus in the 1.12.2 client, toggles the Windows input method through IMM32, switches command input to English, and positions the native composition window near the focused field. The Windows backend uses the same conversion-status strategy as the official 1.16.5 Forge implementation, with a short post-enable cooldown and preservation of vendor-specific conversion flags.

The default `CONVERSION_STATUS` mode is intended for Microsoft Pinyin, Sogou, iFlytek, QQ Pinyin, Baidu, Rime/Weasel and other Windows IMM32 IMEs. If a third-party IME ignores conversion-status changes, set `englishStateMode=DISABLE_IM` in `config/imblocker.cfg`; English fields then temporarily disable the IME instead of relying on the vendor's conversion API.

The implementation is client-only and is not required on a dedicated server.
