package com.kontraproduktion.cryptosend;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import helper.CacheFileHelper;
import interfaces.EncryptionTypeFragment;
import interfaces.FileProcessor;


public class MainActivity extends AppCompatActivity {
    static final String TAG = MainActivity.class.getSimpleName();

    static final int CHOOSE_FILE_REQUEST = 1;

    private EncryptionTypeFragment mEncryptionTypeFragment = null;
    private FileProcessor mProcessor = new FileProcessor();
    private Button mEncryptionButton = null;
    private Button mDecryptionButton = null;
    private TextView mStatusLabel = null;
    private Uri mFileToWorkOn = null;
    private ProgressBar mProgressBar;

    private enum ProcessingType {
        ENCRYPT, DECRYPT
    };

    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private TabsAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFileToWorkOn = null;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new TabsAdapter(getSupportFragmentManager());

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mStatusLabel = (TextView) findViewById(R.id.statusTextview);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        Button selectFileBtn = (Button) findViewById(R.id.chooseFileBtn);
        selectFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                fileIntent.setType("*/*"); // intent type to filter application based on your requirement
                startActivityForResult(fileIntent, CHOOSE_FILE_REQUEST);
            }
        });

        mEncryptionButton = (Button) findViewById(R.id.do_encrypt_btn);
        mEncryptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerFileProcessing(ProcessingType.ENCRYPT);
            }
        });

        mDecryptionButton = (Button) findViewById(R.id.do_decrypt_btn);
        mDecryptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerFileProcessing(ProcessingType.DECRYPT);
            }
        });

        // Test if invoked with intent
        if(getIntent().getAction() == Intent.ACTION_SEND && getIntent().getParcelableExtra(Intent.EXTRA_STREAM) != null) {
            Log.d(TAG, "Started from intent" + getIntent().getAction());
            setFileToWorkOn((Uri) getIntent().getParcelableExtra(Intent.EXTRA_STREAM));
        }
    }

    private void startFileShareIntent(Uri fileToShareUri) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileToShareUri);
        shareIntent.setType("*/*");

        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_intent_title)));
    }

    private void setFileToWorkOn(Uri file) {
        mFileToWorkOn = file;
        if(mStatusLabel != null) {
            mStatusLabel.setText(CacheFileHelper.resolveFileName(mFileToWorkOn, this));
        }
    }

    private class FileProcessingTask extends AsyncTask<EncryptionTypeFragment, Void, Uri> {
        private Activity mActivity;
        private ProcessingType mType;
        private AlphaAnimation mInAnimation;
        private AlphaAnimation mOutAnimation;

        public FileProcessingTask(Activity activity, ProcessingType type) {
            this.mActivity = activity;
            this.mType = type;
        }

        @Override
        protected void onPreExecute() {
            mInAnimation = new AlphaAnimation(0f, 1f);
            mInAnimation.setDuration(200);
            mProgressBar.setAnimation(mInAnimation);
            mProgressBar.setVisibility(View.VISIBLE);

            if(mType == ProcessingType.DECRYPT) {
                mProcessor.setAlgorithm(mEncryptionTypeFragment.getDecryptionAlgorithm());
                mEncryptionTypeFragment.setupDecryptionAlgorithm();
            }

            if (mType == ProcessingType.ENCRYPT) {
                mProcessor.setAlgorithm(mEncryptionTypeFragment.getEncryptionAlgorithm());
                mEncryptionTypeFragment.setupEncryptionAlgorithm();
            }
        }

        @Override
        protected Uri doInBackground(EncryptionTypeFragment... params) {
            mProcessor.loadFile(mFileToWorkOn, mActivity);
            File processedFile = mProcessor.processFile();

            if(processedFile == null) {
                return null;
            }

            Uri fileToShareUri = FileProvider.getUriForFile(getApplicationContext(),
                    "com.kontraproduktion.cryptosend.fileprovider", processedFile);
            return fileToShareUri;
        }

        @Override
        protected void onPostExecute(Uri fileToShareUri) {
            super.onPostExecute(fileToShareUri);

            mOutAnimation = new AlphaAnimation(1f, 0f);
            mOutAnimation.setDuration(200);
            mProgressBar.setAnimation(mOutAnimation);
            mProgressBar.setVisibility(View.GONE);

            if(fileToShareUri != null) {
                startFileShareIntent(fileToShareUri);
            }
        }
    }

    private void triggerFileProcessing(ProcessingType type) {
        if(mFileToWorkOn == null || mEncryptionTypeFragment == null) {
            Toast.makeText(this, getString(R.string.no_file_selected), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "attempted processing without file or type");
            return;
        }

        FileProcessingTask processingTask = new FileProcessingTask(this, type);
        processingTask.execute(mEncryptionTypeFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class TabsAdapter extends FragmentPagerAdapter {

        public TabsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            // return PlaceholderFragment.newInstance(position + 1);
            Fragment tabFragment = null;
            switch (position) {
                case 0:
                    mEncryptionTypeFragment = PasswordEncryptionTab.newInstance();
                    break;
                case 1:
                    mEncryptionTypeFragment = RandomEncryptionTab.newInstance();
                    break;
                default:
                    tabFragment = PlaceholderFragment.newInstance(position + 1);
            }

            tabFragment = mEncryptionTypeFragment;
            mEncryptionButton.setVisibility(mEncryptionTypeFragment.supportsEncryption() ?
                    View.VISIBLE : View.GONE);
            mDecryptionButton.setVisibility(mEncryptionTypeFragment.supportsDecryption() ?
                    View.VISIBLE : View.GONE);

            return tabFragment;
        }

        @Override
        public int getCount() {
            // Concentrate on pwd encryption for now
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Password encryption";
                case 1:
                    return "Private/Public encryption";
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

            return rootView;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSE_FILE_REQUEST && data != null) {
            setFileToWorkOn(data.getData());
        }
    }
}
