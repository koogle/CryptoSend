package com.kontraproduktion.cryptosend;

import android.content.Context;

import java.io.File;

import helper.CryptoHelper;
import interfaces.EncryptInterface;
import interfaces.FileProcessingAlgorithm;
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

    public PasswordFileEncryptor(Context context) {
        this();
        this.setContext(context);
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
        byte[] outputData = mCryptoHelper.encryptWithAES(inputData, mPassword);
        if(outputData != null) {

        }
        return outputData;
    }

    @Override
    protected boolean tearDownProcessing() {
        return true;
    }
}
