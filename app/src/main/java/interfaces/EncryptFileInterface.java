package interfaces;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by koogle on 20/11/15.
 */
public interface EncryptFileInterface {
    public void setFile(File file) throws FileNotFoundException;
    public void setInputStream(String filename, InputStream inputStream);

    public File encrypt(File file);
    public File encrypt(String filename, InputStream inputStream);
}
