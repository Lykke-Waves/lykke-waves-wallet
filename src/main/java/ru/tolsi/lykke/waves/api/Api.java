package ru.tolsi.lykke.waves.api;

import java.util.List;
import java.util.Map;

public interface Api {
    Map<String, TransactionState> getTransactionsState(List<String> transactions);
}
