package interfaces;

import android.support.v4.app.Fragment;

/**
 * Created by koogle on 26/12/15.
 */
public abstract class EncryptionTypeFragment extends Fragment {
    protected FileProcessingAlgorithm mEncryptionAlgorithm = null;
    protected FileProcessingAlgorithm mDecryptionAlgorithm = null;

    public FileProcessingAlgorithm getEncryptionAlgorithm() {
        return mEncryptionAlgorithm;
    }

    public FileProcessingAlgorithm getDecryptionAlgorithm() {
        return mDecryptionAlgorithm;
    }

    public abstract void setupEncryptionAlgorithm();
    public abstract void setupDecryptionAlgorithm();
}
