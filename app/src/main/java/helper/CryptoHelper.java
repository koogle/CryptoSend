package helper;

import android.util.Log;

import org.mindrot.jbcrypt.BCrypt;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Jakob Frick on 20/11/15.
 */
public class CryptoHelper {
    private static final String TAG = CryptoHelper.class.getSimpleName();
    // Change this in your distributions
    private static final String PEPPER = "Dr@7PykW!5zG>?";

    // Singleton pattern
    private static CryptoHelper sInstance = new CryptoHelper();
    private SecureRandom mSecureRandom = new SecureRandom();

    private CryptoHelper() {}

    public static CryptoHelper getInstance() {
        return sInstance;
    }

    private byte[] processWithAES(byte[] inputData, String password, String salt, int mode) {
        SecretKeySpec newSecretKeySpec = this.generateAESKeySpecification(password, salt);

        byte[] processedData = null;
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(mode, newSecretKeySpec);
            processedData = cipher.doFinal(inputData);
        } catch (Exception e) {
            Log.e(TAG, "AES encryption error");
            return null;
        }
        return processedData;
    }


    public byte[] encryptWithAES(byte[] inputData, String password, String salt) {
        try {
            return processWithAES(inputData, password, salt, Cipher.getInstance("AES").ENCRYPT_MODE);
        } catch (Exception e) {
            Log.e(TAG, "AES not available");
            return null;
        }
    }

    public byte[] decryptWithAES(byte[] inputData, String password, String salt) {
        try {
            return processWithAES(inputData, password, salt, Cipher.getInstance("AES").DECRYPT_MODE);
        } catch (Exception e) {
            Log.e(TAG, "AES not available");
            return null;
        }
    }

    public SecretKeySpec generateAESKeySpecification(String password, String salt) {
        SecretKeySpec secretKeySpec = null;

        try {
            String bcryptedPassword = BCrypt.hashpw(PEPPER + password, salt);

            MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            digest.update(bcryptedPassword.getBytes());
            byte key[] = digest.digest();

            secretKeySpec = new SecretKeySpec(key, "AES");
        } catch (Exception e) {
            Log.e(TAG, "AES secret key spec error");
            e.printStackTrace();
        }

        return secretKeySpec;
    }

    public String generateSalt() {
        return BCrypt.gensalt();
    }

    public String generateRandomString(int length) {
        return new BigInteger(130, mSecureRandom).toString(length);
    }
}
