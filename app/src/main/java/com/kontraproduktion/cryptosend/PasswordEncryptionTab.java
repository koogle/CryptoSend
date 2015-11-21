package com.kontraproduktion.cryptosend;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Jakob on 10/11/15.
 */
public class PasswordEncryptionTab extends Fragment {

    static final String TAG = PasswordEncryptionTab.class.getSimpleName();

    static final int CHOOSE_FILE_TO_ENCRYPT_REQUEST = 1;
    static final int CHOOSE_FILE_TO_DECRYPT_REQUEST = 2;

    private PasswordFileEncryptor encryptor = new PasswordFileEncryptor();

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
        View rootView = inflater.inflate(R.layout.password_encryption_tab, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.tab_label);
        textView.setText(getString(R.string.password_encryption_header));

        Button selectFileBtn = (Button) rootView.findViewById(R.id.choose_file_btn);
        selectFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                fileIntent.setType("*/*"); // intent type to filter application based on your requirement
                startActivityForResult(fileIntent, CHOOSE_FILE_TO_ENCRYPT_REQUEST);
            }
        });

        return rootView;
    }

    private String resolveFileName(Uri uri) {
        String filename = null;
        String uriString = uri.toString();

        if (uriString.startsWith("content://")) {
            Cursor cursor = null;
            try {
                cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    filename = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        } else if (uriString.startsWith("file://")) {
            filename = (new File(uriString)).getName();
        }
        return filename;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CHOOSE_FILE_TO_ENCRYPT_REQUEST && data != null) {

            String filename = resolveFileName(data.getData());
            InputStream inputStream = null;
            try {
                inputStream = getActivity().getContentResolver().openInputStream(data.getData());
            } catch (FileNotFoundException e) {
                Log.e(TAG, "Could not find file to encrypt");
                return;
            }
            encryptor.setContext(getContext());
            encryptor.setInputStream(filename, inputStream);

            // Create dialog here and soo on ...
            encryptor.setPassword("1234567890");
            File encryptedFile = encryptor.encrypt();

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(encryptedFile));
            shareIntent.setType("*/*");
            startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_intent_title)));
        }
    }

}
