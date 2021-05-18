# IMBlocker

[中文版](https://github.com/reserveword/IMBlocker/blob/master/README.md)

## About

IMBlocker is a [Minecraft](https://minecraft.net/) mod that automatically switch your input method on/off.

When playing Minecraft, you close input method for playing and open input method for chatting. Switching input method frequently makes you annoyed.

That's why we need IMBlocker, who can detect the game's current state and switch input method for you.

## How it works

- How Minecraft Processes inputs
    - Minecraft calls **both** *key input* processing and *char input* processing every time we input
    - *key input* is not what we care
    - *char input* is dispatched to Minecraft's current GUI `Screen`, which then usually delegates down to a `TextFieldWidget` somewhere
    - Normally a `TextFieldWidget` reacts for this input, but when GUI `Screen` does not exist, they decided not to call delegate, or the `TextFieldWidget` is not active, nothing would react
    - Whether reacted or not, the *char input* processing is called
    - We can't find out if the game would like to receive out *char input* unless we **do input something**
- How IMBlocker detect *char input* state
    - We input a character
    - If any `TextFieldWidget` receives it, the game accepts *char input*
    - Normal *char input* throughout the game works too
    - We use ASM to modify `TextFieldWidget.oncharTyped` so it tells us when called
    - We only input `'\0'` as it won't be accepted by normal game, and normal *char input* remains uninterrupted
- Special cases
    - Some `Screen`s like *Book and Quill* and *Sign* directly receive input, rather than redirecting it to `TextFieldWidget`s
    - We can't capture their inputs, so we make a whitelist for them
