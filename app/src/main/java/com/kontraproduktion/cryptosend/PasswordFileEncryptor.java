package com.kontraproduktion.cryptosend;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import helper.CacheFileHelper;
import helper.CryptoHelper;
import interfaces.EncryptInterface;
import interfaces.FileProcessingAlgorithm;
import interfaces.PasswordInterface;

/**
 * Created by Jakob Frick on 20/11/15.
 */
public class PasswordFileEncryptor extends FileProcessingAlgorithm implements PasswordInterface, EncryptInterface {
    private static final String TAG = PasswordEncryptionTab.class.getSimpleName();

    private String password = null;

    public PasswordFileEncryptor(Context context) {
        this();
        this.setContext(context);
    }

    public PasswordFileEncryptor() {
        this.extension = ".crypt";
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public byte[] process() {
        if(password == null || inputStream == null || context == null || filename == null || filename.isEmpty())
            return null;

        byte[] encryptedData;
        try {
            byte[] inputData = CacheFileHelper.readBytes(inputStream);
            CryptoHelper cryptoHelper = CryptoHelper.getInstance();
            encryptedData = cryptoHelper.encryptWithAES(inputData, password);
        } catch (IOException e) {
            Log.e(TAG, "IOException while reading file");
            e.printStackTrace();
            return null;
        }
        return encryptedData;
    }

    // Encrypt is implemented as applying of the process function
    @Override
    public File encrypt() {
        return this.apply();
    }
}
