package Socket.Encryption;

import Socket.Exception.DecryptionException;
import Socket.Exception.EncryptionException;

import javax.crypto.SecretKey;
import java.security.PrivateKey;
import java.security.PublicKey;

public class HybridSystem {

    public static String encrpySecretKey(SecretKey secretKey, PublicKey publicKey) throws EncryptionException {
        return Crypto.encrpy(Crypto.Cipher_RSA, publicKey, Crypto.keyToString(secretKey));
    }

    public static SecretKey decrpySecretKey(String secretKeyString, PrivateKey privateKey) throws DecryptionException {
        return Crypto.toSecretKey(Crypto.decrpy(Crypto.Cipher_RSA, privateKey, secretKeyString));
    }

}
