package microsofia.boot.loader.resource;

import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The resource is a file.
 * */
public class FileResource extends Resource {
    protected File file;
    private String filename;
    private List<String> entries;
    private List<URL> urlEntries;

    public FileResource(File file) {
        if (!file.isFile()) {
            throw new IllegalArgumentException("The specified File instance does not represent a file. " + file.getAbsolutePath());
        }
        this.file = file;
        filename = file.getName();
        entries = new ArrayList<String>(1);
        entries.add(filename);
        urlEntries = new ArrayList<URL>(1);
        try {
            urlEntries.add(file.toURI().toURL());
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException("Cannot convert File to URL: " + file.getAbsolutePath(), ex);
        }
    }

    public File getFile() {
        return file;
    }

    public String getFilename() {
        return filename;
    }

    public URL getFileURL() {
        return urlEntries.get(0);
    }

    /**
	 * There is only one entry.
	 * 
	 * */
    @Override
    public Iterator<String> getAllEntries() throws Exception {
        return entries.iterator();
    }

    /**
	 * There is only one entry.
	 * 
	 * */
    @Override
    public Iterator<String> getAllEntriesIncludingFolders() throws Exception {
        return entries.iterator();
    }

    /**
	 * Returns the file URL if the path is the file's one.
	 * 
	 * */
    @Override
    public List<URL> getEntry(String path) throws Exception {
        if (filename.equals(path)) {
            return urlEntries;
        }
        return null;
    }

    /**
	 * Returns the content of the file if the path is the file's one.
	 * 
	 * */
    @Override
    public byte[] getEntryBytes(String path) throws Exception {
        if (filename.equals(path)) {
            FileInputStream fin = new FileInputStream(file);
            return readBytes(fin);
        }
        return null;
    }

    /**
	 * Returns the file URL if the path is the file's one.
	 * 
	 * */
    @Override
    public URL getEntryRoot(String path) throws Exception {
        if (filename.equals(path)) {
            return urlEntries.get(0);
        }
        return null;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + getFile().getAbsolutePath();
    }
}
