package ru.tolsi.lykke.waves.wallet;

import com.wavesplatform.wavesj.PrivateKeyAccount;
import com.wavesplatform.wavesj.Transaction;

public interface SignService {
    PrivateKeyAccount createAccount();
    Transaction sign(UnsignedTransferTransaction utx);
}
