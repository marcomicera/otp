import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
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
            //Guardo se esiste il file key
            if (keyExists()) {
                System.out.println("la chiave esiste");
                String mykey = getAESKeyDB();//"1234567891234567";
                key = new SecretKeySpec(mykey.getBytes(), "AES");
            } else {
                System.out.println("la chiave non esiste");
                KeyGenerator keyGen = KeyGenerator.getInstance("AES");
                keyGen.init(KEY_LENGHT);
                key = keyGen.generateKey();
                storeAESKeyDB(key);//Salvo chiave nel file
            }
            // get base64 encoded version of the key
            String encodedKeyFile = Base64.getEncoder().encodeToString(key.getEncoded()); //http://stackoverflow.com/questions/5355466/converting-secret-key-into-a-string-and-vice-versa
            System.out.println("key2 : " + encodedKeyFile);
        } catch (NoSuchAlgorithmException | IOException ex) {
            Logger.getLogger(Encryptor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean keyExists() throws IOException {
        File f = new File("../../Key");
        return f.exists();
    }

    public static String getAESKeyDB() throws IOException {
        FileReader fr = new FileReader("../../Key");
        BufferedReader br = new BufferedReader(fr);
        String s = "";
        s = br.readLine();
        return s;
    }

    public static void storeAESKeyDB(SecretKey key) {
        try {
            String encodedKeyFile = Base64.getEncoder().encodeToString(key.getEncoded());//converto SecretKey in una chiave string per salvarla nel DB
            System.out.println("key2 from function: " + encodedKeyFile);
            File file = new File("../../Key");
            if (file.createNewFile()) {
                System.out.println("File Key is created!");
            } else {
                System.out.println("File Key already exists.");
            }

            FileWriter fw;
            fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);
                out.println(encodedKeyFile);

                //PrintWriter writeText = new PrintWriter("../../Key", "UTF-8");
                //writeText.println(encodedKeyFile);
            }catch (IOException ex) {
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