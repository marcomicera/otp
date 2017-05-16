import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

// http://stackoverflow.com/questions/15554296/simple-java-aes-encrypt-decrypt-example

public class Encryptor {
    private static final String ALGORITHM = "AES";
    private static final int    KEY_LENGHT = 128; // in bits
    private SecretKey key;

    public Encryptor() {
        try {
            if(keyExists()) {
                System.out.println("Key exists: fetching it...");
                key = getAESKey();
            } else {
                System.out.println("Key does not exist: creating it...");
                KeyGenerator keyGen = KeyGenerator.getInstance("AES");
                keyGen.init(KEY_LENGHT);
                key = keyGen.generateKey();
                storeAESKeyDB(key);
            }
        } catch(NoSuchAlgorithmException ex) {
            Logger.getLogger(Encryptor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean keyExists() {
        File f = new File("../../Key");
        return f.exists();
    }

    public static SecretKeySpec getAESKey() {
        try(FileInputStream fin = new FileInputStream("../../Key");
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

    public static void storeAESKeyDB(SecretKey key) {
        try(FileOutputStream fout = new FileOutputStream("../../Key");
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
        return encrypt(plainText.getBytes());
    }
    
    public byte[] encrypt(int plainText) throws GeneralSecurityException  {
        return encrypt(ByteBuffer.allocate(4).putInt(plainText).array());
    }

    public byte[] encrypt(long plainText) throws GeneralSecurityException  {
        return encrypt(longToBytes(plainText));
    }

    public byte[] decrypt(byte[] cipherText) throws GeneralSecurityException  {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);

        return cipher.doFinal(cipherText);
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
    
    /*public int byteArrayToInt(byte[] array) {
        return ByteBuffer.wrap(array).getInt();
    }
    
    public long byteArrayToLong(byte[] array) {
        return ByteBuffer.wrap(array).getLong();
    }*/
}