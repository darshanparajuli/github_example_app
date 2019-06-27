# Github Example App

A simple example app demonstrating Github api access.

## Try it

1. Clone this repo
2. Import this project into Android Studio
3. Generate a [personal access token](https://help.github.com/en/articles/creating-a-personal-access-token-for-the-command-line) and add it to `local.properties` file like this:
```
github.accessToken=<TOKEN>
```
4. Run the app

Note:
Since Github rate limits api access, #3 is highly recommended. It's basically necessary for "Top Contributors" activity to work after a refresh or two.
