package com.kontraproduktion.cryptosend;

import android.util.Log;

import java.io.File;
import java.io.IOException;

import helper.CryptoHelper;
import helper.IOHelper;
import interfaces.DecryptInterface;
import interfaces.FileProcessingAlgorithm;
import interfaces.PasswordInterface;

/**
 * Created by Jakob on 21/11/15.
 */
public class PasswordFileDecryptor extends FileProcessingAlgorithm implements PasswordInterface, DecryptInterface {
    private static final String TAG = PasswordFileDecryptor.class.getSimpleName();

    private String password = null;

    @Override
    public File decrypt() {
        return this.apply();
    }

    @Override
    public byte[] process() {
        if(password == null || inputStream == null || context == null || filename == null || filename.isEmpty())
            return null;

        byte[] decryptedData;
        try {
            byte[] inputData = IOHelper.readBytes(inputStream);
            CryptoHelper cryptoHelper = CryptoHelper.getInstance();
            decryptedData = cryptoHelper.decryptWithAES(inputData, password);
        } catch (IOException e) {
            Log.e(TAG, "IOException while reading file");
            e.printStackTrace();
            return null;
        }
        return decryptedData;
    }

    @Override
    public void setPassword(String password) {

    }
}
