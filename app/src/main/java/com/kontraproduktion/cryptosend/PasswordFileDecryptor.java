package com.kontraproduktion.cryptosend;

import java.io.File;

import interfaces.DecryptInterface;
import interfaces.FileProcessingAlgorithm;
import interfaces.PasswordInterface;

/**
 * Created by Jakob on 21/11/15.
 */
public class PasswordFileDecryptor extends FileProcessingAlgorithm implements PasswordInterface, DecryptInterface {

    @Override
    public File decrypt() {
        return null;
    }

    @Override
    public byte[] process() {
        return new byte[0];
    }

    @Override
    public void setPassword(String password) {

    }
}
