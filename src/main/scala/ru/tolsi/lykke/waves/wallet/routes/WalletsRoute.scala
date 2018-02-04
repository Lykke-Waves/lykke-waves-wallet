package ru.tolsi.lykke.waves.wallet.routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.wavesplatform.wavesj.{Base58, PrivateKeyAccount}
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import play.api.libs.json.{Json, Writes}
import ru.tolsi.lykke.common.NetworkType
import ru.tolsi.lykke.common.http.NetworkScheme

object WalletsRoute {

  case class ResponseObject(privateKey: String, publicAddress: String)

  implicit val ResponseWrites: Writes[ResponseObject] = Json.writes[ResponseObject]
}

case class WalletsRoute(networkType: NetworkType) extends PlayJsonSupport with NetworkScheme {

  import WalletsRoute._

  val route: Route = path("wallets") {
    post {
      val randomSeed = PrivateKeyAccount.generateSeed()

      val account = PrivateKeyAccount.fromSeed(randomSeed, 0, scheme)

      complete(ResponseObject(Base58.encode(account.getPrivateKey), account.getAddress))
    }
  }
}