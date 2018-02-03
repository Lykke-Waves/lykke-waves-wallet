package ru.tolsi.lykke.waves.api.wallet

import com.wavesplatform.wavesj.PrivateKeyAccount
import com.wavesplatform.wavesj.Transaction
import ru.tolsi.lykke.waves.wallet.UnsignedTransferTransaction
import scala.concurrent.Future

trait SignService {
  def createAccount(): Future[PrivateKeyAccount]
  def sign(utx: UnsignedTransferTransaction): Future[Transaction]
}
