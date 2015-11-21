package helper;

import android.content.Context;
import android.util.Log;

import com.kontraproduktion.cryptosend.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by Jakob Frick on 21/11/15.
 */
public class CacheFileHelper {
    private static final String TAG = CacheFileHelper.class.getSimpleName();

    private static final CacheFileHelper instance = new CacheFileHelper();

    private CacheFileHelper() {}

    public static CacheFileHelper getInstance() {
        return instance;
    }

    public File createNewCacheFile(String filename, String extension, Context context) throws IOException {
        balanceCacheSize(context);

        File cacheDir = context.getCacheDir();
        return File.createTempFile(filename, extension, cacheDir);
    }

    public void balanceCacheSize(Context context) {
        File cacheDir = context.getCacheDir();
        if(currentCacheSize(cacheDir) > context.getResources().getInteger(R.integer.max_cache_size)) {
            trimCache(context.getResources().getInteger(R.integer.max_cache_size) - currentCacheSize(cacheDir), cacheDir);

            if(currentCacheSize(cacheDir) > context.getResources().getInteger(R.integer.max_cache_size)) {
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
