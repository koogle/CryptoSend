package com.kontraproduktion.cryptosend;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.Arrays;

import helper.CryptoHelper;
import interfaces.DecryptInterface;
import interfaces.FileProcessingAlgorithm;
import interfaces.PasswordInterface;

/**
 * Created by Jakob on 21/11/15.
 */
public class PasswordFileDecryptor extends FileProcessingAlgorithm implements PasswordInterface, DecryptInterface {
    private static final String TAG = PasswordFileDecryptor.class.getSimpleName();
    private static final int SALT_SIZE = 29;
    private static final int BCRYPT_HEADER = 16;

    private CryptoHelper mCryptoHelper = CryptoHelper.getInstance();
    private String mPassword = null;

    public PasswordFileDecryptor() {
        this.mExtension = ".crypt";
        this.mAppendExtension = false;

        // Read addition header information
        this.mPadding = BCRYPT_HEADER + SALT_SIZE;
    }

    public PasswordFileDecryptor(Activity activity) {
        this();
        this.setActivity(activity);
    }

    @Override
    public void setActivity(Activity activity) {
        super.setActivity(activity);
        this.mFailMessege = activity.getString(R.string.wrong_password);
    }

    @Override
    public File decrypt() {
        return this.apply();
    }

    @Override
    public void setPassword(String password) {
        this.mPassword = password;
    }

    @Override
    protected boolean setupProcessing() {
        return mPassword != null;
    }

    @Override
    protected byte[] processBlock(byte[] inputData) {
        // Read salt:
        if(inputData.length < SALT_SIZE)
            return null;

        String salt = new String(Arrays.copyOfRange(inputData, 0, SALT_SIZE));
        return mCryptoHelper.decryptWithAES(Arrays.copyOfRange(inputData, SALT_SIZE, inputData.length), mPassword, salt);
    }

    @Override
    protected boolean tearDownProcessing() {
        return true;
    }
}
