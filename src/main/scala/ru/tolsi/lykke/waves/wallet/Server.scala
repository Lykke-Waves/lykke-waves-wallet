package ru.tolsi.lykke.waves.wallet

import akka.http.scaladsl.server.{HttpApp, Route}
import ru.tolsi.lykke.common.Util
import ru.tolsi.lykke.common.http.LykkeApiServer
import ru.tolsi.lykke.common.http.routes.IsAliveRoute
import ru.tolsi.lykke.waves.wallet.routes.{SignRoute, WalletsRoute}

object Server extends HttpApp with LykkeApiServer with App {
  private val settingsUrl = Option(System.getProperty("SettingsUrl"))
  private val settings = WalletSettings.loadSettings(settingsUrl)

  private val isAliveRoute = IsAliveRoute(ProjectInfo.NameString, ProjectInfo.VersionString,
    sys.env.getOrElse("ENV_INFO", ""), Util.isDebug).route

  private val walletsRoute = WalletsRoute(settings.NetworkType).route

  private val signRoute = SignRoute(settings.NetworkType).route

  override def routes: Route = handleRejections {
    pathPrefix("api") {
      isAliveRoute ~ walletsRoute ~ signRoute
    }
  }

  Server.startServer(settings.ServiceHost, settings.ServicePort)
}
