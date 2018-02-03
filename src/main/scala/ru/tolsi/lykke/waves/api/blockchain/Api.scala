package ru.tolsi.lykke.waves.api.blockchain

import ru.tolsi.lykke.waves.api.{TransactionId, TransactionState}

trait Api {
  def getTransactionsState(transactions: Seq[TransactionId]): Map[TransactionId, TransactionState]
}

