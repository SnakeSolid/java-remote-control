# Remote Control Bot

Simple telegram bot allowing to control PC from chat.

## Build

Clone repository and start following command:

```sh
mvn package assembly:single
```

## Usage

Start application using following command:

```sh
java -jar target/remote-control-0.0.1-SNAPSHOT-jar-with-dependencies.jar \
  --bot-token "TOKEN" \
  --allow-user ID \
  --scripts-path "SCRIPTS"
```

Where `TOKEN` is telegram bot token. `ID` owners user id, can be set to -1 bot
will send user id in reply message. `SCRIPTS` path to script file.

## License

Source code is primarily distributed under the terms of the MIT license. See LICENSE for details.
