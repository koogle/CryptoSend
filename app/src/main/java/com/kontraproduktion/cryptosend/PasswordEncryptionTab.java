package com.kontraproduktion.cryptosend;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

import interfaces.FileProcessor;

/**
 * Created by Jakob on 10/11/15.
 */
public class PasswordEncryptionTab extends Fragment {

    static final String TAG = PasswordEncryptionTab.class.getSimpleName();

    static final int CHOOSE_FILE_TO_ENCRYPT_REQUEST = 1;
    static final int CHOOSE_FILE_TO_DECRYPT_REQUEST = 2;

    private FileProcessor processor = new FileProcessor();
    private PasswordFileEncryptor encryptor = new PasswordFileEncryptor();

    public PasswordEncryptionTab() {
        processor.setAlgorithm(encryptor);
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



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CHOOSE_FILE_TO_ENCRYPT_REQUEST && data != null) {

            processor.loadFile(data.getData(), getActivity());
            // Create dialog here and soo on ...
            encryptor.setPassword("1234567890");
            File encryptedFile = processor.processFile();

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);

            Uri fileUri = FileProvider.getUriForFile(getContext(), "com.kontraproduktion.cryptosend.fileprovider", encryptedFile);
            //fileUri = Uri.parse(fileUri.toString() + ".txt");
            Log.d(TAG, "file URI is " + fileUri.toString() + "  " + getActivity().getContentResolver().getType(fileUri));

       //     File auxFile = new File(fileUri.getPath());
       //     Log.d(TAG, "" + auxFile.getAbsolutePath() + "     " + encryptedFile.getAbsolutePath());

            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.putExtra(Intent.EXTRA_TITLE, "tst.txt");
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            shareIntent.setType("*/*");

//            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("content://" + cacheFileProvider.AUTHORITY + "/" + encryptedFile.getName()));
//            shareIntent.setType("text/plain");
       //     MainActivity.this.setResult(Activity.RESULT_OK,
         //           mResultIntent);
         //   shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(encryptedFile));
          //  shareIntent.setType("*/*");
            startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_intent_title)));
        }
    }

}
