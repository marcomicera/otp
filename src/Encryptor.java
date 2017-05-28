import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

// http://stackoverflow.com/questions/15554296/simple-java-aes-encrypt-decrypt-example

public class Encryptor {
    private final static String ENCODING = "ISO-8859-1";
    private static final String ALGORITHM = "AES";
    private static final int    KEY_LENGHT = 128; // in bits
    private static final String KEY_FILE = "../../Key";
    private SecretKey key;

    public Encryptor() {
        try {
            if(keyExists()) {
                System.out.println("Key exists: fetching it...");
                key = getKeyFromFile();
            } else {
                System.out.println("Key does not exist: creating it...");
                KeyGenerator keyGen = KeyGenerator.getInstance("AES");
                keyGen.init(KEY_LENGHT);
                key = keyGen.generateKey();
                storeKeyInFile(key);
            }
        } catch(NoSuchAlgorithmException ex) {
            Logger.getLogger(Encryptor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean keyExists() {
        File f = new File(KEY_FILE);
        return f.exists();
    }

    public static SecretKeySpec getKeyFromFile() {
        try(FileInputStream fin = new FileInputStream(KEY_FILE);
            ObjectInputStream oin = new ObjectInputStream(fin);
        ) {
            return (SecretKeySpec)oin.readObject();  
        } catch(FileNotFoundException | ClassNotFoundException ex) {
            Logger.getLogger(Encryptor.class.getName()).log(Level.SEVERE, null, ex);
        } catch(IOException ex) {
            Logger.getLogger(Encryptor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    public static void storeKeyInFile(SecretKey key) {
        try(FileOutputStream fout = new FileOutputStream(KEY_FILE);
            ObjectOutputStream oout = new ObjectOutputStream(fout);
        ) {
            oout.writeObject(key);               
        } catch(IOException ioe) {
            System.out.println("Error while saving generated key.");
        }
    }

    public byte[] encrypt(byte[] plainText) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        return cipher.doFinal(plainText);
    }
    
    public byte[] encrypt(String plainText) throws GeneralSecurityException  {
        try {
            return encrypt(plainText.getBytes(ENCODING));
        } catch(UnsupportedEncodingException ex) {
            Logger.getLogger(Encryptor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public byte[] encrypt(int plainText) throws GeneralSecurityException  {
        return encrypt(ByteBuffer.allocate(4).putInt(plainText).array());
    }
    
    public byte[] encrypt(boolean plainText) throws GeneralSecurityException  {
        return encrypt((plainText) ? 1 : 0);
    }

    public byte[] encrypt(long plainText) throws GeneralSecurityException  {
        return encrypt(longToBytes(plainText));
    }

    public byte[] decrypt(byte[] cipherText) throws GeneralSecurityException  {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);

        return cipher.doFinal(cipherText);
    }
    
    public byte[] decrypt(String cipherText) throws GeneralSecurityException  {
        try {
            return decrypt(cipherText.getBytes(ENCODING));
        } catch(UnsupportedEncodingException ex) {
            Logger.getLogger(Encryptor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    // http://stackoverflow.com/questions/4485128/how-do-i-convert-long-to-byte-and-back-in-java
    public static byte[] longToBytes(long l) {
        byte[] result = new byte[8];
        for(int i = 7; i >= 0; i--) {
            result[i] = (byte)(l & 0xFF);
            l >>= 8;
        }
        return result;
    }
    
    // http://stackoverflow.com/questions/4485128/how-do-i-convert-long-to-byte-and-back-in-java
    public static long bytesToLong(byte[] b) {
        long result = 0;
        for(int i = 0; i < 8; i++) {
            result <<= 8;
            result |= (b[i] & 0xFF);
        }
        return result;
    }
    
    public static int bytesToInt(byte[] b) {
        int result = 0;
        for(int i = 0; i < 4; i++) {
            result <<= 8;
            result |= (b[i] & 0xFF);
        }
        return result;
    }
}