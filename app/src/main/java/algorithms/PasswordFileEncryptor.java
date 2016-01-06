package algorithms;

import android.app.Activity;

import com.kontraproduktion.cryptosend.PasswordEncryptionTab;
import com.kontraproduktion.cryptosend.R;

import java.io.File;

import helper.CryptoHelper;
import interfaces.EncryptInterface;
import templates.FileProcessingAlgorithm;
import interfaces.PasswordInterface;

/**
 * Created by Jakob Frick on 20/11/15.
 */
public class PasswordFileEncryptor extends FileProcessingAlgorithm implements PasswordInterface, EncryptInterface {
    private static final String TAG = PasswordEncryptionTab.class.getSimpleName();

    private CryptoHelper mCryptoHelper = CryptoHelper.getInstance();
    private String mPassword = null;

    public PasswordFileEncryptor() {
        this.mExtension = ".crypt";
    }

    public PasswordFileEncryptor(Activity activity) {
        this();
        this.setActivity(activity);
    }

    @Override
    public void setActivity(Activity activity) {
        super.setActivity(activity);
        this.mFailMessege = activity.getString(R.string.wrong_password);
    }


    @Override
    public void setPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    // Encrypt is implemented as applying of the process function
    @Override
    public File encrypt() {
        return this.apply();
    }

    @Override
    protected boolean setupProcessing() {
        return mPassword != null;
    }

    @Override
    protected byte[] processBlock(byte[] inputData) {
        String salt = CryptoHelper.getInstance().generateSalt();
        byte[] encryptedData = mCryptoHelper.encryptWithAES(inputData, mPassword, salt);
        byte [] saltBytes = salt.getBytes();

        byte [] resultData = new byte[saltBytes.length + encryptedData.length];
        System.arraycopy(saltBytes, 0, resultData, 0, saltBytes.length);
        System.arraycopy(encryptedData, 0, resultData, saltBytes.length, encryptedData.length);
        return resultData;
    }

    @Override
    protected boolean tearDownProcessing() {
        return true;
    }
}
