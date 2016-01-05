package interfaces;

import android.support.v4.app.Fragment;

/**
 * Created by koogle on 26/12/15.
 */
public abstract class EncryptionTypeFragment extends Fragment {
    protected FileProcessingAlgorithm mEncryptionAlgorithm = null;
    protected FileProcessingAlgorithm mDecryptionAlgorithm = null;
    protected boolean mSupportsEncryption = true;
    protected boolean mSupportsDecryption = true;

    public FileProcessingAlgorithm getEncryptionAlgorithm() {
        return mEncryptionAlgorithm;
    }

    public FileProcessingAlgorithm getDecryptionAlgorithm() {
        return mDecryptionAlgorithm;
    }

    public boolean supportsEncryption() {
        return mSupportsEncryption;
    }

    public boolean supportsDecryption() {
        return mSupportsDecryption;
    }

    public abstract void setupEncryptionAlgorithm();
    public abstract void setupDecryptionAlgorithm();
}
