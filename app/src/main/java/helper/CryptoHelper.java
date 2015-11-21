package helper;

import android.util.Log;

import org.mindrot.jbcrypt.BCrypt;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Jakob Frick on 20/11/15.
 */
public class CryptoHelper {
    private static final String TAG = CryptoHelper.class.getSimpleName();
    // Singleton pattern
    private static final CryptoHelper instance = new CryptoHelper();

    private CryptoHelper() {}

    public static CryptoHelper getInstance() {
        return instance;
    }

    private byte[] processWithAES(byte[] inputData, String password, int mode) {
        SecretKeySpec newSecretKeySpec = this.generateAESKeySpecification(password);

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


    public byte[] encryptWithAES(byte[] inputData, String password) {
        try {
            return processWithAES(inputData, password, Cipher.getInstance("AES").ENCRYPT_MODE);
        } catch (Exception e) {
            Log.e(TAG, "AES not available");
            return null;
        }
    }

    public byte[] decryptWithAES(byte[] inputData, String password) {
        try {
            return processWithAES(inputData, password, Cipher.getInstance("AES").DECRYPT_MODE);
        } catch (Exception e) {
            Log.e(TAG, "AES not available");
            return null;
        }
    }

    public SecretKeySpec generateAESKeySpecification(String password) {
        SecretKeySpec secretKeySpec = null;

        try {
            String bcryptedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            MessageDigest digest = java.security.MessageDigest
                    .getInstance("SHA-256");
            digest.update(bcryptedPassword.getBytes());
            byte key[] = digest.digest();

            secretKeySpec = new SecretKeySpec(key, "AES");
        } catch (Exception e) {
            Log.e(TAG, "AES secret key spec error");
        }

        return secretKeySpec;
    }

}
