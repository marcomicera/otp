import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HOTPGenerator {

    HOTPGenerator() {
    }
    /**
     * This method uses the JCE to provide the HMAC-SHA-1 algorithm. HMAC
     * computes a Hashed Message Authentication Code and in this case SHA1 is
     * the hash algorithm used.
     *
     * @param keyBytes the bytes to use for the HMAC-SHA-1 key
     * @param text the message or text to be authenticated.
     *
     */
    public static byte[] hmac_sha1(byte[] keyBytes, byte[] text)
            throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmacSha1;
        try {
            hmacSha1 = Mac.getInstance("HmacSHA1");
        } catch (NoSuchAlgorithmException nsae) {
            hmacSha1 = Mac.getInstance("HMAC-SHA-1");
        }
        SecretKeySpec macKey
                = new SecretKeySpec(keyBytes, "RAW");
        hmacSha1.init(macKey);
        return hmacSha1.doFinal(text);
    }

    private static final int[] DIGITS_POWER
            // 0 1  2   3    4     5      6       7        8
            = {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000};

    /**
     * This method generates an OTP value for the given set of parameters.
     *
     * @param secret the shared secret
     * @param movingFactor the counter, time, or other value that changes on a
     * per use basis.
     * @param codeDigits the number of digits in the OTP, not including the
     * checksum, if any.
     * @param truncationOffset the offset into the MAC result to begin
     * truncation. If this value is out of the range of 0 ... 15, then dynamic
     * truncation will be used. Dynamic truncation is when the last 4 bits of
     * the last byte of the MAC are used to determine the start offset.
     * @throws NoSuchAlgorithmException if no provider makes either HmacSHA1 or
     * HMAC-SHA-1 digest algorithms available.
     * @throws InvalidKeyException The secret provided was not a valid
     * HMAC-SHA-1 key.
     *
     * @return A numeric String in base 10 that includes {@link codeDigits}
     * digits plus the optional checksum digit if requested.
     */
    static public String generateOTP(byte[] secret, long movingFactor, int codeDigits, int truncationOffset)
            throws NoSuchAlgorithmException, InvalidKeyException {
        // put movingFactor value into text byte array
        String result = null;
        int digits = codeDigits;
        byte[] text = new byte[8];
        for (int i = text.length - 1; i >= 0; i--) {
            text[i] = (byte) (movingFactor & 0xff); // movingFactor è il counter
            movingFactor >>= 8;
        }

        // compute hmac hash
        byte[] hash = hmac_sha1(secret, text);// secret è la dongle key

        // put selected bytes into result int
        int offset = hash[hash.length - 1] & 0xf;
        if ((0 <= truncationOffset)
                && (truncationOffset < (hash.length - 4))) {
            offset = truncationOffset;
        }
        int binary =
            ((hash[offset] & 0x7f) << 24) | 
            ((hash[offset + 1] & 0xff) << 16) | 
            ((hash[offset + 2] & 0xff) << 8) | 
            (hash[offset + 3] & 0xff)
        ;

        int otp = binary % DIGITS_POWER[codeDigits];

        result = Integer.toString(otp);
        while (result.length() < digits) {
            result = "0" + result;
        }
        return result;
    }

  //Questa funziona fornisce il contatore del dongle
    public static long getCounter() throws IOException {
        FileReader fr = new FileReader("../../Counter.txt");
        BufferedReader br = new BufferedReader(fr);
        String s = "";
        s = br.readLine();
        long result = Long.parseLong(s);
        return result;
    }
  //Questa funziona aggiorna il contatore del dongle
  public static void updateCounter(long counter) throws FileNotFoundException, IOException {
        try (PrintWriter writeText = new PrintWriter("../../Counter.txt", "UTF-8")) {
            writeText.println(counter);
        }
  }
}
