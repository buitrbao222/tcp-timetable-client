package Socket;

import DTO.Timetable;
import Socket.Encryption.AES;
import Socket.Encryption.Crypto;
import Socket.Encryption.HybridSystem;
import Socket.Exception.DecryptionException;
import Socket.Exception.EncryptionException;
import Utils.IOStream;
import Utils.JSON;
import Utils.JsonToTimetable;
import org.json.JSONObject;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.net.Socket;
import java.security.PublicKey;
import java.util.ArrayList;

public class Connection {

    private final Socket socket;
    private final IOStream io;

    public Connection(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.io = new IOStream(this.socket);
    }

    public ArrayList<Timetable> getTimetables(JSONObject options) {

        ArrayList<Timetable> timetables = new ArrayList<>();
        AES aes;
        SecretKey secretKey;

        try {
            //Receive public key
            String publicKeyJson = receive();
            String publicKeyString = JSON.getString(publicKeyJson, "Public key");
            PublicKey publicKey = Crypto.toPublicKey(publicKeyString);

            //Send secret key
            aes = new AES();
            secretKey = aes.getSecretKey();
            String secretKeyJson = JSON.toJSON("Secret key", HybridSystem.encryptSecretKey(aes.getSecretKey(), publicKey));
            send(secretKeyJson);

            String message = receive();
            System.out.println(message);
            if (message.equalsIgnoreCase("Exchanging session key successful")) {
                // Send request with options
                send(options.toString(), secretKey);

                // Receive json data
                String data = receive(secretKey);

                System.out.println("Client receive: " + data);

                timetables = JsonToTimetable.convert(data);
            }

        } catch (IOException | DecryptionException | EncryptionException e) {
            e.printStackTrace();
        }
        return timetables;
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
