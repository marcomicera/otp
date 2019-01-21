/*  The following code has been taken from RFC 4226 available at
    https://tools.ietf.org/html/rfc4226#page-27                 */

   /* Copyright (C) 2004, OATH.  All rights reserved.
    *
    * License to copy and use this software is granted provided that it
    * is identified as the "OATH HOTP Algorithm" in all material
    * mentioning or referencing this software or this function.
    *
    * License is also granted to make and use derivative works provided
    * that such works are identified as
    *  "derived from OATH HOTP algorithm"
    * in all material mentioning or referencing the derived work.
    *
    * OATH (Open AuTHentication) and its members make no
    * representations concerning either the merchantability of this
    * software or the suitability of this software for any particular
    * purpose.
    *
    * It is provided "as is" without express or implied warranty
    * of any kind and OATH AND ITS MEMBERS EXPRESSaLY DISCLAIMS
    * ANY WARRANTY OR LIABILITY OF ANY KIND relating to this software.
    *
    * These notices must be retained in any copies of any part of this
    * documentation and/or software.
    */

import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HOTPGeneratorServer {
    private final static int NARROW_WINDOW_SIZE = 4;
    private final static int LARGE_WINDOW_SIZE = 30;
    
    HOTPGeneratorServer() {
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
            text[i] = (byte) (movingFactor & 0xff); // movingFactor is the counter
            movingFactor >>= 8;
        }

        // compute hmac hash
        byte[] hash = hmac_sha1(secret, text); // secret is the dongle key

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
        
        result = Integer.toString(otp);
        while (result.length() < digits) {
            result = "0" + result;
        }
        return result;
    }

    public static long HOTPCheck(String user_otp, long dongle_counter, String dongle_key, boolean lw_on) {
        int WINDOW;
        if(lw_on)
            WINDOW = LARGE_WINDOW_SIZE;
        else
            WINDOW = NARROW_WINDOW_SIZE;
        
        // Debug printing start
        System.out.println(
            "Checking OTP " + user_otp + 
            " using counter " + dongle_counter + 
            " and key " + dongle_key + ".\n" +
            "Large window" + ((!lw_on) ? " not" : "") + " used."
        );
        System.out.println("Generated OTP values");
        // Debug printing end
        
        String[] HOTPWindow = new String[WINDOW + 1];
        for(int i = 0; i < WINDOW + 1; ++i) {
            long set = (dongle_counter - (WINDOW / 2) + i);
            HOTPWindow[i] = HOTPGen(set, dongle_key);
            System.out.println("Counter: " + set + "\tOTP: " + HOTPWindow[i]);
            // OTP value found in used window
            if(HOTPWindow[i].compareTo(user_otp) == 0) {
                System.out.println(
                    "OTP value found in " +
                    ((lw_on) ? "large" : "narrow") +
                    " window with index " +
                    set + "."
                );
                return set;     
            }
        }
        
        System.out.println(
            "OTP value not found in " +
            ((lw_on) ? "large" : "narrow") +
            " window.\n"
        );
        return -1;
    }

    public static String HOTPGen(long dongle_counter, String dongle_key) {
        String HOTPString = "";
        try {
            byte[] secret = dongle_key.getBytes();
            long movingFactor;
            movingFactor = dongle_counter;
            int codeDigits = 6;
            int truncationOffset = 0;
            HOTPString = generateOTP(secret, movingFactor, codeDigits, truncationOffset);
        } catch(NoSuchAlgorithmException | InvalidKeyException ex) {
            Logger.getLogger(HOTPGeneratorServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return HOTPString;
    }
}
