package ismartdev.mn.bundan.util;

/**
 * Created by Ulzii on 1/7/2017.
 */


import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class HMAC {
    private final static String app_access = "726964594134724|HCrmRQQfWdShJ2XIFL9x4OXlvJA";
    private final static String app_secret = "39fd42d838c381c6649a4808a063f04e";

    public static String hmacDigestSha256() {
        String result="";
        try {
            // Get an hmac_sha1 key from the raw key bytes
            byte[] keyBytes = app_access.getBytes();
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA256");

            // Get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);

            // Compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(app_secret.getBytes());

            // Convert raw bytes to Hex
            byte[] hexBytes = new Hex().encode(rawHmac);

            //  Covert array of Hex bytes to a String
            result = new String(hexBytes, "ISO-8859-1");
            return  result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}