package com.kontraproduktion.cryptosend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import interfaces.EncryptionTypeFragment;

/**
 * Created by koogle on 05/01/16.
 */
public class RandomEncryptionTab extends EncryptionTypeFragment {
    static final String TAG = EncryptionTypeFragment.class.getSimpleName();
    private EditText passwordInputField = null;

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
        mSupportsDecryption = false;

        final View rootView = inflater.inflate(R.layout.password_encryption_tab, container, false);

        passwordInputField = (EditText) rootView.findViewById(R.id.passwordField);
        return rootView;
    }


    @Override
    public void setupEncryptionAlgorithm() {

    }

    @Override
    public void setupDecryptionAlgorithm() {

    }
}
