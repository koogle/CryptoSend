package com.kontraproduktion.cryptosend;

import android.content.Context;

import java.io.File;

import helper.CryptoHelper;
import interfaces.DecryptInterface;
import interfaces.FileProcessingAlgorithm;
import interfaces.PasswordInterface;

/**
 * Created by Jakob on 21/11/15.
 */
public class PasswordFileDecryptor extends FileProcessingAlgorithm implements PasswordInterface, DecryptInterface {
    private static final String TAG = PasswordFileDecryptor.class.getSimpleName();

    private CryptoHelper mCryptoHelper = CryptoHelper.getInstance();
    private String mPassword = null;

    public PasswordFileDecryptor() {
        this.mExtension = ".crypt";
        this.mAppendExtension = false;

        // Read addition header information
        this.mPadding = 16;
    }

    public PasswordFileDecryptor(Context context) {
        this();
        this.setContext(context);
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
        return mCryptoHelper.decryptWithAES(inputData, mPassword);
    }

    @Override
    protected boolean tearDownProcessing() {
        return true;
    }
}
