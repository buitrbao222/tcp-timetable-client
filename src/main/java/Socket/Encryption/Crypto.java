package Socket.Encryption;

import Socket.Exception.DecryptionException;
import Socket.Exception.EncryptionException;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public abstract class Crypto {

    public static Cipher Cipher_RSA = null, Cipher_AES = null;

    static {
        try {
            Cipher_RSA = Cipher.getInstance("RSA");
            Cipher_AES = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(Cipher cipher, Key key, String plainText) throws EncryptionException {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte encryptOut[] = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encryptOut);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new EncryptionException("Can't encrypt message");
        }
    }

    public static String decrypt(Cipher cipher, Key key, String cipherText) throws DecryptionException {
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte decryptOut[] = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            return new String(decryptOut);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new DecryptionException("Can't decrypt message");
        }
    }

    public static String keyToString(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static SecretKey toSecretKey(String keyString) {
        byte[] decodedKey = Base64.getDecoder().decode(keyString);
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        return originalKey;
    }

    public static PublicKey toPublicKey(String keyString) {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(keyString);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decodedKey);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            return factory.generatePublic(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

}
