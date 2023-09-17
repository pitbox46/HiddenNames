# HiddenNames

Allows you to hide player nameplates

## Developers API

API for allow to manage the mod from other, this API is only available in the 1.16 version.

### Getting Started

Gradle:

The frist step is add Curse Maven to your repositories.

```gradle

repositories {

    maven {
        url "https://cursemaven.com"
    }

}

```

The next step is add the mod as dependency.

```gradle

dependencies {

    implementation fg.deobf('curse.maven:hiddennames-479134:<latest-1.16.5-file-id>')

}

```

And now you can use `HiddenNamesAPI` class in your project.

### HiddenNamesAPI class

This class contains all methods to use the API.

#### Methods

- **setPlayerVisibility**: Use this method to set the visibility of player, needs two parameters `player` and `visibility`.

- **setConfigBlockHide**: Use this method to configure if a block hides the player name, needs one parameter `enabled`.

- **setConfigDefaultVisibility**: Use this method to configure the default name tag visibility, needs one parameter `defaultVisible`.

Okay, this is all. Happy Hacking 😀.
