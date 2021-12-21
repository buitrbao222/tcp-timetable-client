package Socket;

import Socket.Encryption.AES;
import Socket.Encryption.Crypto;
import Socket.Encryption.HybridSystem;
import Socket.Exception.DecryptionException;
import Socket.Exception.EncryptionException;
import Utils.IOStream;
import Utils.JSON;
import org.json.JSONObject;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.net.Socket;
import java.security.PublicKey;

public class Connection {

    private final Socket socket;
    private final IOStream io;

    public Connection(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.io = new IOStream(this.socket);
    }

    public String getTimetables(JSONObject options) {
        try {
            // Receive public key
            String publicKeyJson = receive();
            String publicKeyString = JSON.getString(publicKeyJson, "Public key");
            PublicKey publicKey = Crypto.toPublicKey(publicKeyString);

            // Send secret key
            AES aes = new AES();
            SecretKey secretKey = aes.getSecretKey();
            String encryptedSecretKey = HybridSystem.encryptSecretKey(secretKey, publicKey);
            String secretKeyJson = JSON.toJSON("Secret key", encryptedSecretKey);
            send(secretKeyJson);

            String message = receive();

            System.out.println(message);

            if (message.equalsIgnoreCase("Exchanging session key successful")) {
                // Send request with options
                send(options.toString(), secretKey);

                // Receive JSON data
                String data = receive(secretKey);

                System.out.println("Client receive: " + data);

                return data;
            }

        } catch (IOException | DecryptionException | EncryptionException e) {
            e.printStackTrace();
        }

        // Returns "error" if failed
        return "error";
    }

    public void send(String message) throws IOException {
        this.io.send(message);
    }

    public String receive() throws IOException {
        return this.io.receive();
    }

    public void send(String message, SecretKey secretKey) throws IOException, EncryptionException {
        io.send(Crypto.encrypt(Crypto.Cipher_AES, secretKey, message));
    }

    public String receive(SecretKey secretKey) throws IOException, DecryptionException {
        return Crypto.decrypt(Crypto.Cipher_AES, secretKey, io.receive());
    }

    public void close() throws IOException {
        this.io.close();
        this.socket.close();
    }

}
