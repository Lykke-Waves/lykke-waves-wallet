package ru.tolsi.lykke.waves.wallet.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.typesafe.scalalogging.StrictLogging
import com.wavesplatform.wavesj.PrivateKeyAccount
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import play.api.libs.json._
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

case class SignRoute(networkType: NetworkType) extends PlayJsonSupport with NetworkScheme with StrictLogging {

  import SignRoute._

  val route: Route = path("sign") {
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
                  Json.parse(tx.signTransaction(acc).getJson).as[JsObject] ++
                    // adding transaction id field for opeprationId search
                    JsObject(Map("id" -> JsString(tx.id(acc).id)))
                } catch {
                  case NonFatal(e) =>
                    logger.error("Error on signing", e)
                    StatusCodes.BadRequest -> Json.toJson(ErrorMessage("Invalid private key", Some(Map("privateKeys" -> Seq(e.getMessage)))))
                }
              case Failure(f) =>
                logger.error("Error on parsing tx context", f)
                StatusCodes.BadRequest -> Json.toJson(ErrorMessage("Can't parse transaction context", Some(Map("transactionContext" -> Seq(f.getMessage)))))
            }
          }
        }
      }
    }
  }
}
