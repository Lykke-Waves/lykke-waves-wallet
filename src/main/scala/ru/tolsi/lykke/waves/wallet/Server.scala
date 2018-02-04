package ru.tolsi.lykke.waves.wallet

import akka.http.scaladsl.server.{HttpApp, Route}
import ru.tolsi.lykke.common.http.LykkeApiServer
import ru.tolsi.lykke.common.http.routes.IsAliveRoute
import ru.tolsi.lykke.common.{NetworkType, Util}
import ru.tolsi.lykke.waves.wallet.routes.{SignRoute, WalletsRoute}

object Server extends HttpApp with LykkeApiServer with App {
  private val networkType: NetworkType = NetworkType.Main

  override def routes: Route = handleRejections {
    pathPrefix("api") {
      IsAliveRoute(ProjectInfo.NameString,
        ProjectInfo.VersionString,
        sys.env.getOrElse("ENV_INFO", ""),
        Util.isDebug
      ).route ~ WalletsRoute(networkType).route ~ SignRoute(networkType).route
    }
  }

  Server.startServer("localhost", 8080)
}
