package Socket.Encryption;

import lombok.Getter;

import java.security.*;

@Getter
public class RSA extends Crypto {

    PublicKey publicKey;
    PrivateKey privateKey;

    public RSA() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            KeyPair kp;
            kpg.initialize(1024);
            // Khởi tạo cặp khóa
            kp = kpg.genKeyPair();
            // PublicKey
            this.publicKey = kp.getPublic();
            // PrivateKey
            this.privateKey = kp.getPrivate();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

}
