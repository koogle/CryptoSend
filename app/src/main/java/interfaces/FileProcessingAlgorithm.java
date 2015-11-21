package interfaces;

import android.content.Context;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import helper.CacheFileHelper;

/**
 * Created by Jakob on 21/11/15.
 */
public abstract class FileProcessingAlgorithm {
    private static final String TAG = FileProcessingAlgorithm.class.getSimpleName();

    protected InputStream inputStream = null;
    protected byte[] processedData = null;
    protected String filename = null;
    protected String extension = "";

    protected Context context = null;

    public void setContext(Context context) {
        this.context = context;
    }

    private void setFilename(String filename) {
        if(filename.length() < 3)
            filename = "###" + filename;
        this.filename = filename;
    }

    public void setInputStream(String filename, InputStream inputStream) {
        setFilename(filename);
        this.inputStream = inputStream;
    }

    public File apply() {
        processedData = process();
        return getResultFile();
    }

    abstract public byte[] process();

    public File getResultFile() {
        // Nothing was done
        if(processedData == null)
            return null;

        File resultFile = null;
        try {
            resultFile = CacheFileHelper.getInstance().createNewCacheFile(filename, extension, context);

            BufferedOutputStream bufferedOutStream = new BufferedOutputStream(new FileOutputStream(resultFile));
            bufferedOutStream.write(processedData);
            bufferedOutStream.flush();
            bufferedOutStream.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not create temp file or write to it");
            e.printStackTrace();
        }

        return resultFile;
    }

}
