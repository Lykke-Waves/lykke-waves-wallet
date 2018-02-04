package ru.tolsi.lykke.waves.wallet.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.wavesplatform.wavesj.PrivateKeyAccount
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import play.api.libs.json.{Json, Reads, Writes}
import ru.tolsi.lykke.common.http.{ErrorMessage, NetworkScheme}
import ru.tolsi.lykke.common.{NetworkType, UnsignedTransferTransaction}

import scala.util.control.NonFatal
import scala.util.{Failure, Success}

object SignRoute {

  case class RequestObject(privateKeys: Seq[String], transactionContext: String)

  case class ResponseObject(signedTransaction: String)

  implicit val RequestReads: Reads[RequestObject] = Json.reads[RequestObject]
  implicit val ResponseWrites: Writes[ResponseObject] = Json.writes[ResponseObject]
}

case class SignRoute(networkType: NetworkType) extends PlayJsonSupport with NetworkScheme {

  import SignRoute._

  val route: Route = path("wallets") {
    post {
      entity(as[RequestObject]) { req =>
        complete {
          if (req.privateKeys.lengthCompare(1) > 0 || req.privateKeys.isEmpty) {
            StatusCodes.BadRequest -> "Only one private key is supported"
          } else {
            UnsignedTransferTransaction.fromJsonString(req.transactionContext) match {
              case Success(tx) =>
                val pk = req.privateKeys.head
                try {
                  val acc = PrivateKeyAccount.fromPrivateKey(pk, scheme)
                  tx.signTransaction(acc).getJson
                } catch {
                  case NonFatal(e) =>
                    StatusCodes.BadRequest -> Json.toJson(ErrorMessage("Invalid private key", Some(Map("privateKeys" -> Seq(e.getMessage)))))
                }
              case Failure(f) =>
                StatusCodes.BadRequest -> Json.toJson(ErrorMessage("Can't parse transaction context", Some(Map("transactionContext" -> Seq(f.getMessage)))))
            }
          }
        }
      }
    }
  }
}
