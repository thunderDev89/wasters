package com.acemen.android.wasters.restful;

import android.net.Uri;
import android.util.Base64;

import com.acemen.android.wasters.restful.helper.ApiClient;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Audrik ! on 22/08/2016.
 */
public class Encrypt {

    private static final String ALGORITHM_RSA = "RSA";

    private static final String ALGORITHM_RSA_PADDING = "RSA/ECB/PKCS1Padding";

    private static final String ALGORITHM_HMAC_SHA1 = "HmacSHA1";

    private static final String BASIC_AUTHORIZATION = "Basic";

    private static final String BEARER_AUTHORIZATION = "Bearer";

    private static PublicKey publicKey = null;

    private static PublicKey getPublicKey() {
        if (publicKey == null) {
            try {
                /*
                String key = "";

                final BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    key += line + "\n";
                }
                bufferedReader.close();

                key = key.replace("-----BEGIN PUBLIC KEY-----\n", "");
                key = key.replace("-----END PUBLIC KEY-----", "");
                */

                //TODO Read public key from file
                final String key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAum4DcYFWCYFyqNzfOwr2" +
                        "ERJ0P+OOjiU94btu82+0/46Mx9kBaQ45BerdHGzYaTU8ebwmOy4wjZcINANra+H1" +
                        "jGVagXw/5tv3n1QIPX/PReFdHcrreZVGaHEF3opDbg92NKIAH9txp/SX1RuZWQCv" +
                        "BmMAhc+mthJ6YxqOQGQTdGojCNg/REwgzHcKh6TfsA6PiSHoECkOrUDcA/ufa3Fk" +
                        "qfYDhI7sOLPF1nIhNQKt7WDkpEAn4dQoFoxrHBh0OSrVJVJxD9fRpMEUUfxcA1Rl" +
                        "SPeRX179187idIlPOq2UOaddBNH9tdynkgObB9fkUjKdH5ySY94NCNHgxnHzoNlq" +
                        "OwIDAQAB";

                final KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
                publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(Base64.decode(key.getBytes(), Base64.DEFAULT)));
            } catch (Exception e) {
                throw new UnsupportedOperationException(e);
            }
        }
        return publicKey;
    }

    public static String encryptValue(String data) {
        try {
            final PublicKey publicKey = getPublicKey();

            final Cipher cipher = Cipher.getInstance(ALGORITHM_RSA_PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            return Base64.encodeToString(cipher.doFinal(data.getBytes()), Base64.DEFAULT);
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }

    private static String encryptSignature(String signature, String salt) {
        try {
            final SecretKeySpec secretKeySpec = new SecretKeySpec(salt.getBytes(), ALGORITHM_HMAC_SHA1);

            final Mac mac = Mac.getInstance(ALGORITHM_HMAC_SHA1);
            mac.init(secretKeySpec);

            return encryptValue(Base64.encodeToString(mac.doFinal(signature.getBytes()), Base64.DEFAULT));
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public static String encryptUrl(String method, String route) {
        final String timestamp = String.valueOf(System.currentTimeMillis() / 1000L);

        final String salt = ApiClient.CLIENT_SECRET + timestamp.substring(timestamp.length() - 5);
        final String url = ApiClient.BASE_URL + route + "?clientId=" + ApiClient.CLIENT_ID + "&timestamp=" + timestamp;

        final String signature = encryptSignature(method + ":" + url, salt);
        return url + "&signature=" + Uri.encode(signature);
    }

    private static String encryptBase64Encode(String value) {
        return Base64.encodeToString(value.getBytes(), Base64.DEFAULT);
    }

    public static String encryptAuthorizationBasic() {
        return BASIC_AUTHORIZATION + " " + encryptBase64Encode(ApiClient.CLIENT_ID + ":" + ApiClient.CLIENT_SECRET);
    }

    public static String encryptAuthorizationBearer(String value) {
        return BEARER_AUTHORIZATION + " " + value;
    }
}
