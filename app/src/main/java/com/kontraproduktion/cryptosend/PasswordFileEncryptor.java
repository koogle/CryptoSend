package com.kontraproduktion.cryptosend;

import android.content.Context;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import helper.CacheFileHelper;
import helper.EncryptionHelper;
import interfaces.EncryptFileInterface;
import interfaces.PasswordEncryptionInterface;

/**
 * Created by Jakob Frick on 20/11/15.
 */
public class PasswordFileEncryptor implements EncryptFileInterface, PasswordEncryptionInterface {
    private static final String TAG = PasswordEncryptionTab.class.getSimpleName();

    private String password = null;
    private String filename = null;
    private InputStream inputStream = null;

    private Context context = null;

    public PasswordFileEncryptor(Context context) {
        this.context = context;
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

    public void setContext(Context context) {
        this.context = context;
    }

    private void setFilename(String filename) {
        if(filename.length() < 3)
            filename = "###" + filename;
        this.filename = filename;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void setFile(File file) throws FileNotFoundException {
        setFilename(file.getName());
        this.inputStream = new FileInputStream(file);
    }

    @Override
    public void setInputStream(String filename, InputStream inputStream) {
        setFilename(filename);
        this.inputStream = inputStream;
    }

    @Override
    public File encrypt(String password, File file) {
        this.setPassword(password);
        return this.encrypt(file);
    }

    @Override
    public File encrypt(String password, String filename, InputStream inputStream) {
        this.setPassword(password);
        return this.encrypt(filename, inputStream);
    }

    @Override
    public File encrypt(File file) {
        try {
            this.setFile(file);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File was not found");
            e.printStackTrace();
        }
        return this.encrypt();
    }

    @Override
    public File encrypt(String filename, InputStream inputStream) {
        this.setInputStream(filename, inputStream);
        return this.encrypt();
    }

    @Override
    public File encrypt() {
        if(password == null || inputStream == null || context == null || filename == null || filename.isEmpty())
            return null;

        byte[] encryptedData = null;
        try {
            byte[] inputData = this.readBytes(inputStream);
            EncryptionHelper encryptionHelper = EncryptionHelper.getInstance();
            encryptedData = encryptionHelper.encryptWithAES(inputData, password);
        } catch (IOException e) {
            Log.e(TAG, "IOException while reading file");
            e.printStackTrace();
            return null;
        }

        File encryptedFile = null;
        try {
            encryptedFile = CacheFileHelper.getInstance().createNewCacheFile(filename, ".crypt", context);

            BufferedOutputStream bufferedOutStream = new BufferedOutputStream(new FileOutputStream(encryptedFile));
            bufferedOutStream.write(encryptedData);
            bufferedOutStream.flush();
            bufferedOutStream.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not create temp file or write to it");
            e.printStackTrace();
        }

        return encryptedFile;
    }
}
