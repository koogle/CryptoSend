package helper;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.kontraproduktion.cryptosend.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by Jakob Frick on 21/11/15.
 */
public class CacheFileHelper {
    private static final String TAG = CacheFileHelper.class.getSimpleName();

    private static CacheFileHelper sInstance = new CacheFileHelper();
    private File mLatestFile = null;

    public static String resolveFileName(Uri uri, Activity activity) {
        String filename = null;
        String uriString = uri.toString();

        if (uriString.startsWith("content://")) {
            Cursor cursor = null;
            try {
                cursor = activity.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    String mimeType = MimeTypeMap.getSingleton().getExtensionFromMimeType(
                                                activity.getContentResolver().getType(uri));
                    filename = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                    if(mimeType != null && !filename.endsWith("." + mimeType)) {
                        filename += "." + mimeType;
                    }
                }
            } finally {
                if(cursor != null) {
                    cursor.close();
                }
            }
        } else if (uriString.startsWith("file://")) {
            filename = (new File(uriString)).getName();
        }
        return filename;
    }

    private CacheFileHelper() {}

    private long getMaxCacheSize(Context context) {
        return context.getResources().getInteger(R.integer.max_cache_size) * 1024 * 1024;
    }

    public static CacheFileHelper getsInstance() {
        return sInstance;
    }

    public File createNewCacheFile(String filename, String extension, Context context, boolean mAppendExtension) throws IOException {
        balanceCacheSize(context);

        File cacheDir = context.getFilesDir(); // mContext.getCacheDir();
        String newFilename = filename;

        if(mAppendExtension) {
            newFilename += extension;
        } else {
            if(filename.endsWith(extension)) {
                newFilename = filename.substring(0, filename.length() - extension.length());
            } else {
                Log.d(TAG, "Filename <" + filename + "> did not end with extension " + extension + "; continue processing...");
            }
        }

        mLatestFile = new File(cacheDir, newFilename); // File.createTempFile(mFilename, mExtension, cacheDir);
        return mLatestFile;
    }

    public File getLatestFile() {
        return mLatestFile;
    }

    public void balanceCacheSize(Context context) {
        File cacheDir = context.getFilesDir(); // mContext.getCacheDir();
        if(currentCacheSize(cacheDir) > getMaxCacheSize(context)) {
            trimCache(context.getResources().getInteger(R.integer.max_cache_size) - currentCacheSize(cacheDir), cacheDir);

            if(currentCacheSize(cacheDir) > getMaxCacheSize(context)) {
                Log.e(TAG, "Could not trim cache to satisfy max. cache size");
            }
        }
    }

    private long currentCacheSize(File cacheDir) {
        long currentSize = 0;
        if(cacheDir != null && cacheDir.isDirectory()) {
            for(File file : cacheDir.listFiles()) {
                currentSize += file.length();
            }
        }
        return currentSize;
    }

    private void trimCache(long bytesToTrim, File cacheDir) {
        if(cacheDir == null || !cacheDir.isDirectory() || bytesToTrim <= 0) {
            return;
        }

        File[] files = cacheDir.listFiles();
        // Sort by date
        Arrays.sort(files, new Comparator() {
            public int compare(Object obj, Object other) {
                long diff = ((File) obj).lastModified() - ((File) other).lastModified();

                if (diff > 0) {
                    return -1;
                } else if (diff < 0) {
                    return +1;
                } else {
                    return 0;
                }
            }
        });

        for(File file : files) {
            bytesToTrim -= file.length();
            file.delete();

            if(bytesToTrim <= 0) return;
        }
    }
}
