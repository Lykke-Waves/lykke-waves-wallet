package ru.tolsi.lykke.waves.api

sealed trait TransactionState
object InProgress extends TransactionState
object Completed extends TransactionState
object Failed extends TransactionState