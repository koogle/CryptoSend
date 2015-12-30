package interfaces;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import helper.CacheFileHelper;

/**
 * Created by Jakob Frick on 21/11/15.
 */
public class FileProcessor {
    private static final String TAG = FileProcessor.class.getSimpleName();

    private FileProcessingAlgorithm mAlgorithm = null;

    public void setAlgorithm(FileProcessingAlgorithm mAlgorithm) {
        this.mAlgorithm = mAlgorithm;
    }

    public void loadFile(Uri fileUri, Activity activity) {
        String filename = CacheFileHelper.resolveFileName(fileUri, activity);
        Log.d(TAG, "loadFile mFilename " + filename);
        InputStream inputStream = null;
        try {
            inputStream = activity.getContentResolver().openInputStream(fileUri);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Could not find file to encrypt");
            return;
        }

        if(mAlgorithm != null) {
            mAlgorithm.setContext(activity.getBaseContext());
            mAlgorithm.setInputStream(filename, inputStream);
        }
    }

    public File processFile() {
        if(mAlgorithm != null) {
            return mAlgorithm.apply();
        } else {
            return null;
        }
    }
}
