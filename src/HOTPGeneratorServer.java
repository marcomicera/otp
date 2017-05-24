
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HOTPGeneratorServer {

    private final static int WINDOW = 4; //Finestra di valori calcolati dal local server per OTP deve essere pari.

    HOTPGeneratorServer() {
    }

    // These are used to calculate the check-sum digits.
    //                                0  1  2  3  4  5  6  7  8  9
    private static final int[] doubleDigits
            = {0, 2, 4, 6, 8, 1, 3, 5, 7, 9};

    /**
     * Calculates the checksum using the credit card algorithm. This algorithm
     * has the advantage that it detects any single mistyped digit and any
     * single transposition of adjacent digits.
     *
     * @param num the number to calculate the checksum for
     * @param digits number of significant places in the number
     *
     * @return the checksum of num
     */
    public static int calcChecksum(long num, int digits) {
        boolean doubleDigit = true;
        int total = 0;
        while (0 < digits--) {
            int digit = (int) (num % 10);
            num /= 10;
            if (doubleDigit) {
                digit = doubleDigits[digit];
            }
            total += digit;
            doubleDigit = !doubleDigit;
        }
        int result = total % 10;
        if (result > 0) {
            result = 10 - result;
        }
        return result;
    }

    /**
     * This method uses the JCE to provide the HMAC-SHA-1 algorithm. HMAC
     * computes a Hashed Message Authentication Code and in this case SHA1 is
     * the hash algorithm used.
     *
     * @param keyBytes the bytes to use for the HMAC-SHA-1 key
     * @param text the message or text to be authenticated.
     *
     * @throws NoSuchAlgorithmException if no provider makes either HmacSHA1 or
     * HMAC-SHA-1 digest algorithms available.
     * @throws InvalidKeyException The secret provided was not a valid
     * HMAC-SHA-1 key.
     *
     */
    public static byte[] hmac_sha1(byte[] keyBytes, byte[] text)
            throws NoSuchAlgorithmException, InvalidKeyException {
        //        try {
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
        //        } catch (GeneralSecurityException gse) {
        //            throw new UndeclaredThrowableException(gse);
        //        }
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
     * @param addChecksum a flag that indicates if a checksum digit should be
     * appended to the OTP.
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
    static public String generateOTP(byte[] secret, long movingFactor, int codeDigits, boolean addChecksum, int truncationOffset)
            throws NoSuchAlgorithmException, InvalidKeyException {
        // put movingFactor value into text byte array
        String result = null;
        int digits = addChecksum ? (codeDigits + 1) : codeDigits;
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
        int binary
                = ((hash[offset] & 0x7f) << 24)
                | ((hash[offset + 1] & 0xff) << 16)
                | ((hash[offset + 2] & 0xff) << 8)
                | (hash[offset + 3] & 0xff);

        int otp = binary % DIGITS_POWER[codeDigits];
        if (addChecksum) {
            otp = (otp * 10) + calcChecksum(otp, codeDigits);
        }
        result = Integer.toString(otp);
        while (result.length() < digits) {
            result = "0" + result;
        }
        return result;
    }

    //****************************************************************************************************************************


    public static boolean HOTPCheck(int user_otp, long dongle_counter, String dongle_key) {
        System.out.println("Valori della finestra: ");
        String[] HOTPWindow = new String[WINDOW + 1];
        for (int i = 0; i < WINDOW + 1; i++) {
            long set = (dongle_counter - (WINDOW / 2) + i);
            HOTPWindow[i] = HOTPGen(set, dongle_key);
            if(Integer.parseInt(HOTPWindow[i]) == user_otp )
            {
                if(i != (WINDOW / 2) ){ //Questo significa che client e server sono perfettamente allineati, altrimenti i == WINDOW / 2
                    System.out.println("Local serve e Client non perfettamente allineati \n mando a server remoto il nuovo counter corretto");
                    //... devono mandare i al server remoto
                }
                return true;     //trovato valore nella finestra che coincide
            }
        }
        /*AGGIUNGERE CHECK SU QUALE VALORE COINCIDE DELLA FINESTRA
        • Se rientra nella finestra, login successfull con ri-allineamento del counter del database
        • Se non rientra nella finestra, login unsuccessfull con ri-allineamento del counter del database
        */
        /*
        for (int i = 0; i < WINDOW + 1; i++) {
            System.out.println("HOTPWindow [" + i + "] = " + HOTPWindow[i] + "");
        }*/
        return false;
    }

    public static String HOTPGen(long dongle_counter, String dongle_key) {
        String HOTPString = "";
        try {
            //HOTPGeneratorServer htopgen = new HOTPGeneratorServer(); // A che serve? Non viene mai usato e la classe ha tutti metodi statici
            System.out.println("La dongle_key è: " + dongle_key);
            byte[] secret = dongle_key.getBytes();//("14FEA54A019BC73A14FEA54A019BC73A"); //Giustificare perché la dongle key è 16 byte
            long movingFactor;
            movingFactor = dongle_counter;//HOTPGeneratorServer.getCounter();
            System.out.println("Valore counter: " + movingFactor);
            int codeDigits = 6;
            boolean addChecksum = false;
            int truncationOffset = 0;
            HOTPString = generateOTP(secret, movingFactor, codeDigits, addChecksum, truncationOffset);
            System.out.println("Codice otp generato: " + HOTPString);
            System.out.println("Movingfactor counter: " + movingFactor);
            updateCounter(movingFactor + 1);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException ex) {
            Logger.getLogger(HOTPGeneratorServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return HOTPString;
    }

    //Source: http://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static long getCounter() throws IOException {
        FileReader fr = new FileReader("../../Counter.txt");
        BufferedReader br = new BufferedReader(fr);
        String s = "";
        s = br.readLine();
        long result = Long.parseLong(s);
        return result;
    }

    public static void updateCounter(long counter) throws FileNotFoundException, IOException {
        try (PrintWriter writeText = new PrintWriter("../../Counter.txt", "UTF-8")) {
            writeText.println(counter);
        }
    }    //****************************************************************************************************************************
}
