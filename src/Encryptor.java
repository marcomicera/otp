import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

// http://stackoverflow.com/questions/15554296/simple-java-aes-encrypt-decrypt-example

public class Encryptor {
    private static final String ALGORITHM = "AES";
    private SecretKey key;

    public Encryptor(/*byte[] key*/) {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128); // for example
            key = keyGen.generateKey();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Encryptor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public byte[] encrypt(byte[] plainText) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        return cipher.doFinal(plainText);
    }

    public byte[] decrypt(byte[] cipherText) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);

        return cipher.doFinal(cipherText);
    }
}