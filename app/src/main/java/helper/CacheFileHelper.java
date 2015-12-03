package helper;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.kontraproduktion.cryptosend.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by Jakob Frick on 21/11/15.
 */
public class CacheFileHelper {
    private static final String TAG = CacheFileHelper.class.getSimpleName();

    private static final CacheFileHelper instance = new CacheFileHelper();
    private File latestFile = null;

    public static byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private CacheFileHelper() {}

    private long getMaxCacheSize(Context context) {
        return context.getResources().getInteger(R.integer.max_cache_size) * 1000000;
    }

    public static CacheFileHelper getInstance() {
        return instance;
    }

    public File createNewCacheFile(String filename, String extension, Context context) throws IOException {
        balanceCacheSize(context);

        File cacheDir = context.getFilesDir(); // context.getCacheDir();
        latestFile = new File(cacheDir, filename + extension); // File.createTempFile(filename, extension, cacheDir);
        return latestFile;
    }

    public File getLatestFile() {
        return latestFile;
    }

    public void balanceCacheSize(Context context) {
        File cacheDir = context.getFilesDir(); // context.getCacheDir();
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
