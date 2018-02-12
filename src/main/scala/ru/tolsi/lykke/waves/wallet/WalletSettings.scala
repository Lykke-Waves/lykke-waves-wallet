package ru.tolsi.lykke.waves.wallet

import java.net.URL

import com.typesafe.scalalogging.StrictLogging
import play.api.libs.json._
import ru.tolsi.lykke.common.NetworkType

import scala.util.Try

object WalletSettings extends StrictLogging {
  val Default = WalletSettings(NetworkType.Main)

  implicit val WalletSettingsReader: Reads[WalletSettings] = Json.reads[WalletSettings]

  def loadSettings(pathOpt: Option[String]): WalletSettings = {
    val contentStreamOpt = pathOpt.map(u => new URL(u).openStream)
    contentStreamOpt.flatMap(c => Try {
      Json.parse(c).as[WalletSettings]
    }.toOption).getOrElse {
      logger.warn("Can't read config from 'SettingsUrl', load by default")
      WalletSettings.Default
    }
  }
}

case class WalletSettings(NetworkType: NetworkType)