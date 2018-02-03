package ru.tolsi.lykke.waves.wallet;

import com.wavesplatform.wavesj.*;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.Blake2bDigest;

import java.nio.ByteBuffer;
import java.util.Optional;

public class UnsignedTransferTransaction extends Asset {
    private static final byte TRANSFER = 4;
    private static final int MIN_BUFFER_SIZE = 120;

    private String fromAddress;
    private String toAddress;
    private long amount;
    private String assetId;
    private long fee;
    private String feeAssetId;
    private String attachment;

    public UnsignedTransferTransaction(String fromAddress, String toAddress, long amount, Optional<String> assetId, long fee, Optional<String> feeAssetId, Optional<String> attachment) {
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.amount = amount;
        this.assetId = assetId.orElse(null);
        this.fee = fee;
        this.feeAssetId = feeAssetId.orElse(null);
        this.attachment = attachment.orElse(null);
    }

    //region Getters
    public String getFromAddress() {
        return fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public long getAmount() {
        return amount;
    }

    public Optional<String> getAssetId() {
        return Optional.ofNullable(assetId);
    }

    public long getFee() {
        return fee;
    }

    public Optional<String> getFeeAssetId() {
        return Optional.ofNullable(feeAssetId);
    }

    public Optional<String> getAttachment() {
        return Optional.ofNullable(attachment);
    }

    public Transaction sign(PrivateKeyAccount account) {
        return Transaction.makeTransferTx(account, toAddress, amount, assetId, fee, feeAssetId, attachment);
    }
    //endregion

    //region ID calculation
    private static final Digest BLAKE2B256 = new Blake2bDigest(256);

    private static String normalize(String assetId) {
        return assetId == null || assetId.isEmpty() ? Asset.WAVES : assetId;
    }

    private static boolean isWaves(String assetId) {
        return WAVES.equals(normalize(assetId));
    }

    private static byte[] hash(byte[] message, int ofs, int len, Digest alg) {
        byte[] res = new byte[alg.getDigestSize()];
        alg.update(message, ofs, len);
        alg.doFinal(res, 0);
        return res;
    }

    private static byte[] idHash(byte[] message, int ofs, int len) {
        return hash(message, ofs, len, BLAKE2B256);
    }

    private static void putAsset(ByteBuffer buffer, String assetId) {
        if (isWaves(assetId)) {
            buffer.put((byte) 0);
        } else {
            buffer.put((byte) 1).put(Base58.decode(assetId));
        }
    }

    public String id(PublicKeyAccount account) {
        byte[] attachmentBytes = (attachment == null ? "" : attachment).getBytes();
        int datalen = (isWaves(assetId) ? 0 : 32) +
                (isWaves(feeAssetId) ? 0 : 32) +
                attachmentBytes.length + MIN_BUFFER_SIZE;
        long timestamp = System.currentTimeMillis();

        ByteBuffer buf = ByteBuffer.allocate(datalen);
        buf.put(TRANSFER).put(account.getPublicKey());
        putAsset(buf, assetId);
        putAsset(buf, feeAssetId);
        buf.putLong(timestamp).putLong(amount).putLong(fee).put(Base58.decode(toAddress))
                .putShort((short) attachmentBytes.length).put(attachmentBytes);

        byte[] toSign = buf.array();
        byte[] id = idHash(toSign, 0, toSign.length);
        return Base58.encode(id);
    }
    //endregion
}
