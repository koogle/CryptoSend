package com.kontraproduktion.cryptosend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import interfaces.EncryptionTypeFragment;

/**
 * Created by Jakob on 10/11/15.
 */
public class PasswordEncryptionTab extends EncryptionTypeFragment {

    static final String TAG = PasswordEncryptionTab.class.getSimpleName();

    private EditText passwordInputField = null;

    public PasswordEncryptionTab() {
        super();
        mEncryptionAlgorithm = new PasswordFileEncryptor();
        mDecryptionAlgorithm = new PasswordFileDecryptor();
    }
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PasswordEncryptionTab newInstance() {
        PasswordEncryptionTab fragment = new PasswordEncryptionTab();
        Bundle args = new Bundle();

        // Add values here
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.password_encryption_tab, container, false);

        passwordInputField = (EditText) rootView.findViewById(R.id.passwordField);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void setupEncryptionAlgorithm() {
        ((PasswordFileEncryptor) mEncryptionAlgorithm).setPassword(
                passwordInputField.getText().toString());
    }

    @Override
    public void setupDecryptionAlgorithm() {
        ((PasswordFileDecryptor) mDecryptionAlgorithm).setPassword(
                passwordInputField.getText().toString());
    }
}
