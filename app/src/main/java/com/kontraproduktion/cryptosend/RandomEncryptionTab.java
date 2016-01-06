package com.kontraproduktion.cryptosend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import algorithms.PasswordFileEncryptor;
import helper.CryptoHelper;
import templates.EncryptionTypeFragment;

/**
 * Created by koogle on 05/01/16.
 */
public class RandomEncryptionTab extends EncryptionTypeFragment {
    static final String TAG = EncryptionTypeFragment.class.getSimpleName();
    private TextView passwordLabel = null;

    public RandomEncryptionTab() {
        super();
        mEncryptionAlgorithm = new PasswordFileEncryptor();
        mSupportsDecryption = false;
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static RandomEncryptionTab newInstance() {
        RandomEncryptionTab fragment = new RandomEncryptionTab();
        Bundle args = new Bundle();

        // Add values here
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mSupportsDecryption = false;

        final View rootView = inflater.inflate(R.layout.random_encryption_tab, container, false);

        passwordLabel = (TextView) rootView.findViewById(R.id.passwordLabel);

        Button createRandomPassword = (Button) rootView.findViewById(R.id.gen_password_btn);
        createRandomPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordLabel.setText(generateRandomString());
            }
        });

        return rootView;
    }

    private String generateRandomString() {
        return CryptoHelper.getInstance().generateRandomString(12);
    }


    @Override
    public void setupEncryptionAlgorithm() {
        ((PasswordFileEncryptor) mEncryptionAlgorithm).setPassword(
                passwordLabel.getText().toString());
    }

    @Override
    public void setupDecryptionAlgorithm() {

    }
}
