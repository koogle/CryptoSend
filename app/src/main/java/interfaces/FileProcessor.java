package interfaces;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.IllegalFormatFlagsException;

/**
 * Created by Jakob Frick on 21/11/15.
 */
public class FileProcessor {
    private static final String TAG = FileProcessor.class.getSimpleName();

    private FileProcessingAlgorithm algorithm = null;

    private String resolveFileName(Uri uri, Activity activity) {
        String filename = null;
        String uriString = uri.toString();

        if (uriString.startsWith("content://")) {
            Cursor cursor = null;
            try {
                cursor = activity.getContentResolver().query(uri, null, null, null, null);
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

    public void setAlgorithm(FileProcessingAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public void loadFile(Uri fileUri, Activity activity) {
        String filename = resolveFileName(fileUri, activity);
        Log.d(TAG, "loadFile filename " + filename);
        InputStream inputStream = null;
        try {
            inputStream = activity.getContentResolver().openInputStream(fileUri);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Could not find file to encrypt");
            return;
        }

        if(algorithm != null) {
            algorithm.setContext(activity.getBaseContext());
            algorithm.setInputStream(filename, inputStream);
        }
    }

    public File processFile() {
        if(algorithm != null) {
            return algorithm.apply();
        } else {
            return null;
        }
    }
}
