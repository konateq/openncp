package eu.europa.ec.sante.ehdsi.openncp.util.security;

import com.google.common.io.BaseEncoding;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

public class HashUtil {

    private HashUtil() {
    }

    /**
     * Returns the MD5 hash of the given String
     *
     * @param stringToBeHashed
     * @return the MD5 hash of the given string
     * @throws NoSuchAlgorithmException
     */
    public static String getMD5Hash(String stringToBeHashed) throws NoSuchAlgorithmException {
        return getHash(stringToBeHashed, "MD5", false);
    }

    /**
     * Returns the SHA224 hash of the given String
     *
     * @param stringToBeHashed
     * @return the SHA224 hash of the given string
     * @throws NoSuchAlgorithmException
     */
    public static String getSHA224Hash(String stringToBeHashed) throws NoSuchAlgorithmException {
        return getHash(stringToBeHashed, "SHA224", false);
    }

    /**
     * Returns the SHA256 hash BASE 32 of the given String
     *
     * @param stringToBeHashed
     * @return the SHA256 hash BASE 32 of the given string
     * @throws NoSuchAlgorithmException
     */
    public static String getSHA256HashBase32(String stringToBeHashed) throws NoSuchAlgorithmException {
        return getHash(stringToBeHashed, "SHA256", true);
    }

    /**
     * Returns the hash of the given String
     *
     * @param stringToBeHashed
     * @return the hash of the given string
     * @throws NoSuchAlgorithmException
     */
    private static String getHash(String stringToBeHashed, String algorithm, boolean isBase32) throws NoSuchAlgorithmException {

        if (Security.getProvider("BC") == null) {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        }
        MessageDigest md = MessageDigest.getInstance(algorithm, new org.bouncycastle.jce.provider.BouncyCastleProvider());
        md.reset();
        md.update(stringToBeHashed.getBytes(StandardCharsets.UTF_8));
        byte[] hashBytes = md.digest();

        if (isBase32) {
            //convert the byte to BASE32 - noPadding '='
            BaseEncoding base32 = BaseEncoding.base32().omitPadding();
            return base32.encode(hashBytes);
        } else {
            //convert the byte to hex format method 2
            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        }
    }
}
