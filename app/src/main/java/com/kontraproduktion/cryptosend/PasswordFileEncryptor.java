package com.kontraproduktion.cryptosend;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import helper.EncryptionHelper;
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
        this.setContext(context);
    }

    public PasswordFileEncryptor() {}

    private byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
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
            byte[] inputData = this.readBytes(inputStream);
            EncryptionHelper encryptionHelper = EncryptionHelper.getInstance();
            encryptedData = encryptionHelper.encryptWithAES(inputData, password);
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
