package Socket.Encryption;

import Socket.Exception.DecryptionException;
import Socket.Exception.EncryptionException;

import javax.crypto.SecretKey;
import java.security.PrivateKey;
import java.security.PublicKey;

public class HybridSystem {

    public static String encryptSecretKey(SecretKey secretKey, PublicKey publicKey) throws EncryptionException {
        return Crypto.encrypt(Crypto.Cipher_RSA, publicKey, Crypto.keyToString(secretKey));
    }

    public static SecretKey decrpySecretKey(String secretKeyString, PrivateKey privateKey) throws DecryptionException {
        return Crypto.toSecretKey(Crypto.decrypt(Crypto.Cipher_RSA, privateKey, secretKeyString));
    }

}
