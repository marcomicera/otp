import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

// http://stackoverflow.com/questions/15554296/simple-java-aes-encrypt-decrypt-example

public class Encryptor {
    private static final String ALGORITHM = "AES";
    private byte[] key;

    public Encryptor(byte[] key) {
        this.key = key;
    }

    public byte[] encrypt(byte[] plainText) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        return cipher.doFinal(plainText);
    }

    public byte[] decrypt(byte[] cipherText) throws Exception
    {
        SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        return cipher.doFinal(cipherText);
    }
}