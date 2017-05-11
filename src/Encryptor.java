import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

// http://stackoverflow.com/questions/15554296/simple-java-aes-encrypt-decrypt-example

public class Encryptor {
    private static final String ALGORITHM = "AES";
    private static final int    KEY_LENGHT = 128; // in bits
    private SecretKey key;

    public Encryptor() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(KEY_LENGHT);
            key = keyGen.generateKey();
        } catch(NoSuchAlgorithmException ex) {
            Logger.getLogger(Encryptor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public byte[] encrypt(byte[] plainText) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        return cipher.doFinal(plainText);
    }
    
    public byte[] encrypt(String plainText) throws GeneralSecurityException  {
        return encrypt(plainText.getBytes());
    }
    
    public byte[] encrypt(int plainText) throws GeneralSecurityException  {
        return encrypt(ByteBuffer.allocate(4).putInt(plainText).array());
    }
    
    // does not work
    public byte[] encrypt(long plainText) throws GeneralSecurityException  {
        return encrypt(ByteBuffer.allocate(4).putLong(plainText).array());
    }

    public byte[] decrypt(byte[] cipherText) throws GeneralSecurityException  {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);

        return cipher.doFinal(cipherText);
    }
    
    public int byteArrayToInt(byte[] array) {
        return ByteBuffer.wrap(array).getInt();
    }
    
    public long byteArrayToLong(byte[] array) {
        return ByteBuffer.wrap(array).getLong();
    }
}