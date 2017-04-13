import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;

public class HOTPGenerator {
    private final String algorithm;
    private final int passwordLength;
    private final int modDivisor;

    public static final int DEFAULT_PASSWORD_LENGTH = 6; // decimal digits
    public static final String HOTP_HMAC_ALGORITHM = "HmacSHA1"; // specified by the HOTP standard RFC 4226

    public HOTPGenerator() throws NoSuchAlgorithmException {
        this(DEFAULT_PASSWORD_LENGTH);
    }

    public HOTPGenerator(final int passwordLength) throws NoSuchAlgorithmException {
        this(passwordLength, HOTP_HMAC_ALGORITHM);
    }

    /**
     * <p>Creates a new HMAC-based one-time password generator using the given password length and algorithm. Note that
     * <a href="https://tools.ietf.org/html/rfc4226">RFC&nbsp;4226</a> specifies that HOTP must always use HMAC-SHA1 as
     * an algorithm, but derived one-time password systems like TOTP may allow for other algorithms.</p>
     *
     * @param passwordLength the length, in decimal digits, of the one-time passwords to be generated; must be between
     * 6 and 8, inclusive
     * @param algorithm the name of the {@link javax.crypto.Mac} algorithm to use when generating passwords; note that
     * HOTP only allows for {@value HOTPGenerator#HOTP_HMAC_ALGORITHM}, but derived
     * standards like TOTP may allow for other algorithms
     *
     * @throws NoSuchAlgorithmException if the given algorithm is not supported by the underlying JRE
     */
    protected HOTPGenerator(final int passwordLength, final String algorithm) throws NoSuchAlgorithmException {
        switch(passwordLength) {
            case 6: {
                this.modDivisor = 1_000_000;
                break;
            }

            case 7: {
                this.modDivisor = 10_000_000;
                break;
            }

            case 8: {
                this.modDivisor = 100_000_000;
                break;
            }

            default: {
                throw new IllegalArgumentException("Password length must be between 6 and 8 digits.");
            }
        }

        this.passwordLength = passwordLength;

        // Our purpose here is just to throw an exception immediately if the algorithm is bogus.
        Mac.getInstance(algorithm);
        this.algorithm = algorithm;
    }

    /**
     * Generates a one-time password using the given key and counter value.
     *
     * @param key a secret key to be used to generate the password
     * @param counter the counter value to be used to generate the password
     *
     * @return an integer representation of a one-time password; callers will need to format the password for display
     * on their own
     *
     * @throws InvalidKeyException if the given key is inappropriate for initializing the {@link Mac} for this generator
     */
    public int generateOneTimePassword(final Key key, final long counter) throws InvalidKeyException {
        final Mac mac;

        try {
            mac = Mac.getInstance(this.algorithm);
            mac.init(key);
        } catch (final NoSuchAlgorithmException e) {
            // This should never happen since we verify that the algorithm is legit in the constructor.
            throw new RuntimeException(e);
        }

        final ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(0, counter);

        final byte[] hmac = mac.doFinal(buffer.array());
        final int offset = hmac[hmac.length - 1] & 0x0f;

        for(int i = 0; i < 4; i++) {
            // Note that we're re-using the first four bytes of the buffer here; we just ignore the latter four from
            // here on out.
            buffer.put(i, hmac[i + offset]);
        }

        final int hotp = buffer.getInt(0) & 0x7fffffff;

        return hotp % this.modDivisor;
    }

    public int getPasswordLength() {
        return this.passwordLength;
    }

    public String getAlgorithm() {
        return this.algorithm;
    }
}