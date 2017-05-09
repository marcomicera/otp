import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Encryptor {
    private static final String ALGORITHM = "AES";
    private byte[] key;

    public Encryptor() {
        try {
            key = KeyGenerator.getInstance(Encryptor.ALGORITHM).generateKey().getEncoded();
        } catch(NoSuchAlgorithmException ex) {
            Logger.getLogger(Encryptor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public byte[] encrypt(byte[] plainText) {
        SecretKeySpec secretKey = new SecretKeySpec(key, Encryptor.ALGORITHM);
        
        try {
            Cipher cipher = Cipher.getInstance(Encryptor.ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            
            return cipher.doFinal(plainText);
        } catch(GeneralSecurityException gse) {
            Logger.getLogger(Encryptor.class.getName()).log(Level.SEVERE, null, gse);
        }
        
        return null;
    }

    public byte[] decrypt(byte[] cipherText) {
        SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);

        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            
            return cipher.doFinal(cipherText);
        } catch(GeneralSecurityException gse) {
            Logger.getLogger(Encryptor.class.getName()).log(Level.SEVERE, null, gse);
        }
        
        return null;
    }
}