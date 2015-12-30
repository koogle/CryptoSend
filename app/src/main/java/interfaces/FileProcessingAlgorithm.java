package interfaces;

import android.content.Context;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import helper.CacheFileHelper;

/**
 * Created by Jakob on 21/11/15.
 */
public abstract class FileProcessingAlgorithm {
    private static final String TAG = FileProcessingAlgorithm.class.getSimpleName();
    // Block size to process at once
    protected static final int BLOCK_SIZE = 1024*1024;

    protected OutputStream mOutputStream = null;
    protected boolean mAppendExtension = true;
    protected InputStream mInputStream = null;
    protected String mFilename = null;
    protected File mOutputFile = null;
    protected Context mContext = null;
    protected String mExtension = "";

    // padding to read more than standard block size
    protected int mPadding = 0;

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    private void setFilename(String mFilename) {
        if(mFilename.length() < 3)
            mFilename = "###" + mFilename;
        this.mFilename = mFilename;
    }

    private void createOutputStream() throws IOException {
        mOutputFile = CacheFileHelper.getsInstance().createNewCacheFile(mFilename, mExtension, mContext, mAppendExtension);
        mOutputStream = new BufferedOutputStream(new FileOutputStream(mOutputFile));
    }

    private void closeOutputStream()  throws IOException {
        mOutputStream.close();
    }

    public void setInputStream(String filename, InputStream inputStream) {
        setFilename(filename);
        this.mInputStream = inputStream;
    }

    public File apply() {

        try {
            createOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Could not create output write stream");
            e.printStackTrace();
        }

        process();

        try {
            closeOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Could not close file: " + mOutputFile.getName());
            e.printStackTrace();
        }

        return getResultFile();
    }

    abstract protected boolean setupProcessing();
    abstract protected byte[] processBlock(byte[] inputData);
    abstract protected boolean tearDownProcessing();

    public void process() {
        if(!setupProcessing() || mInputStream == null || mOutputStream == null) {
            Log.e(TAG, "Failed to setup processing");
            return;
        }

        byte[] buffer = new byte[BLOCK_SIZE + mPadding];
        try {
            while (mInputStream.read(buffer) != -1) {
                mOutputStream.write(processBlock(buffer));
            }
        } catch (IOException e) {
            Log.e(TAG, "Error while processing file");
            e.printStackTrace();
        }

        if(!tearDownProcessing()) {
            Log.e(TAG, "Failed to tear dwon processing");
            return;
        }
    }

    public File getResultFile() {
        return mOutputFile;
    }

}
