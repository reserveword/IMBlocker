# IMBlocker

[中文版](https://github.com/reserveword/IMBlocker/blob/master/README.md)

## Summary

IMBlocker is a [Minecraft](https://minecraft.net/) mod that automatically toggles your IME.

While playing Minecraft, you need to disable the input method for playing and enable it for chatting. Switching the input method frequently makes you annoyed.

That's why we need IMBlocker, which can tweak your IME depends on current input environment.

## Features

- Automatically set IME state based on current input context
- Monitor the chat field and automatically set the conversion state when detected command syntax
- Make candidate window be able to locate text location (Windows Only)
- Make candidate window be displayable at full screen mode on Windows
- Fix non-character keys leaking to the game while candidates showing on Linux

## How it works

- The key factor of determining input method state.
    - There **IS** and **ONLY** one widget can receive and process non-global key events in a standard GUI system, which is so-called **focused widget**
    - On focused, some types of widget expect to receive language characters (such as text fields), while others prefer to receive original key values (like ingame, buttons, lists etc.). Their preferred input type implies the primary state of input method.
    - Though Minecraft doesn't implement a unified focus management system, there is always only one widget to be able to process key inputs, we call it **effective focused widget**.
    - Not all of focus changes are valid in Minecraft, this mod will automatically filter invalid ones.
- How IMBlocker detect input environment
    - Add `setFocused` callbacks through `Mixin`s.
    - Implement the focus management system, which converts all focus requests into actual focus changes and applys them to focus transfer path, then tweaks the input method depends on updated focus state.
- Exceptions
    - Some `Screen`s like `BookEditScreen` and `SignEditScreen` directly process key inputs, without generating focus requests
    - We regard these `Screen`s as effective focused widget and make a whitelist for them, manually create focus requests upon open/close. If an input entry looks like a `Screen` instead of some type of text field, please try to identify it using screen recovering feature and add it to the whitelist.
- Limitations
    - Widgets without being injected cannot be managed by the focus system of this mod, thus mods with independent GUI implementation may not be able to benefit from this mod. In this case, please report them to [#13](https://github.com/reserveword/IMBlocker/issues/13) for developers.
    - There's a known bug in GLFW's window management: if the game window is created without focus, focus callbacks will not be triggered on the first time it gains the focus, which may cause input locking. Solution: make the game window lost and gain the focus again.
    - Suggestions and feedbacks are welcomed  

## Supported Mods with custom GUI framework  
 
[Roughly Enough Items](https://github.com/shedaniel/RoughlyEnoughItems) (waiting for update)  
[EMI](https://github.com/emilyploszaj/emi) (waiting for update)  
[Axiom](https://axiom.moulberry.com/) (waiting for update)  
[Replay Mod](https://www.replaymod.com/) (waiting for update)  
[FTB Library](https://github.com/FTBTeam/FTB-Library) (waiting for update)  
[Meteor Client](https://www.meteorclient.com/) (update soon)  
[LibGui](https://github.com/CottonMC/LibGui)  
[Reese's Sodium Options](https://github.com/FlashyReese/reeses-sodium-options)  
[BlockUI](https://github.com/ldtteam/BlockUI) (waiting for update)  
[SuperMartijn642's Core Lib](https://github.com/SuperMartijn642/SuperMartijn642sCoreLib) (waiting for update)  
[Notes](https://github.com/MattCzyr/Notes) (waiting for update)  
[Essential Mod](https://essential.gg/)*

*On Fabric, you need to extract the file named "essential-<32-bit code>.jar" from the root directory of its mod JAR and **only** put this file into your mod folder.