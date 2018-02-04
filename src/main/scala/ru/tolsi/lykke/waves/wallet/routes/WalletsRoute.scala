package ru.tolsi.lykke.waves.wallet.routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.wavesplatform.wavesj.{Base58, PrivateKeyAccount}
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import play.api.libs.json.{JsObject, JsString}
import ru.tolsi.lykke.common.NetworkType
import ru.tolsi.lykke.common.http.NetworkScheme

case class WalletsRoute(networkType: NetworkType) extends PlayJsonSupport with NetworkScheme {

  private def responseObject(privateKey: String, publicAddress: String) =
    JsObject(Map(
      "privateKey" -> JsString(privateKey),
      "publicAddress" -> JsString(publicAddress)))

  val route: Route = path("wallets") {
    post {
      val randomSeed = PrivateKeyAccount.generateSeed()

      val account = PrivateKeyAccount.fromSeed(randomSeed, 0, scheme)

      complete(responseObject(Base58.encode(account.getPrivateKey), account.getAddress))
    }
  }
}