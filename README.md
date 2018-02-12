# lykke-waves-wallet
Waves Wallet Module for Lykke Exchange.

This module signs transactions and creates private keys/addresses for exchange needs.

# Build

This project uses [sbt](https://www.scala-sbt.org/) for building:

```
sbt clean debian:packageBin
```

After that, the .deb package will be available in the `target` project folder.

# Installation

Just install the .deb package and start the service.

# Configuration

The app reads settings from the URL or local file specified in the `SettingsUrl` environment variable.

There are allowed settings:

```
NetworkType: String - "main" or "test"
ServiceHost: String - ex. "localhost"
ServicePort: Int - ex. 8081
```

By default it will be a HTTP service at `localhost:8081` for Waves Mainnet.

# Todos

Due to the rather tight deadlines of the contest and lack of free time, the project will be completed a little later.

- [x] Make the required logging format
- [ ] Make the routes tests
- [ ] Clean up the code
- [x] NetworkType (testnet) support
