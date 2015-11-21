package helper;

import android.util.Log;

import org.mindrot.jbcrypt.BCrypt;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Jakob Frick on 20/11/15.
 */
public class EncryptionHelper {
    private static final String TAG = EncryptionHelper.class.getSimpleName();
    // Singleton pattern
    private static final EncryptionHelper instance = new EncryptionHelper();

    private EncryptionHelper() {}

    public static EncryptionHelper getInstance() {
        return instance;
    }

    public byte[] encryptWithAES(byte[] inputData, String password) {
        SecretKeySpec newSecretKeySpec = this.generateAESKeySpecification(password);

        byte[] encryptedData = null;
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, newSecretKeySpec);
            encryptedData = cipher.doFinal(inputData);
        } catch (Exception e) {
            Log.e(TAG, "AES encryption error");
        }
        return encryptedData;
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
