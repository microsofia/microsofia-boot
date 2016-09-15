package microsofia.boot.loader.resource;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The resource is a jar.
 * */
public class JarResource extends FileResource {
    private static Log log = LogFactory.getLog(JarResource.class.getName());
    private ZipFile zipFile;

	public JarResource(File file) {
		super(file);
	}
	
	protected void finalize() throws Throwable {
		close();
	}

	public synchronized ZipFile getZipFile() {
	    if (zipFile==null){
            try {
                zipFile = new ZipFile(file);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Exception caught while creating a ZipFile for " + file.getAbsolutePath(), ex);
            }
        }
	    return zipFile;
	}

	public synchronized void close() {
	    if (zipFile != null){
            try {
                zipFile.close();
            } catch (Exception ex) {
                if (log.isDebugEnabled()) {
                    log.debug("Could not close ZipFile instance for file: " + file.getAbsolutePath() + ".", ex);
                }
            }
            zipFile = null;
	    }
	}
	
	/**
	 * Returns all the path entries of the jar.
	 * 
	 * */
	@Override
	public Iterator<String> getAllEntries() throws Exception {
		return entriesIterator(false);
	}
	
	/**
	 * Returns all the path entries of the jar including the folders.
	 * 
	 * */
	@Override
	public Iterator<String> getAllEntriesIncludingFolders() throws Exception {
		return entriesIterator(true);
	}
	
	/**
	 * Return the URL of the resource path.
	 * 
	 * */
	@Override
	public List<URL> getEntry(String path) throws Exception {
		List<URL> entries = super.getEntry(path);
		if (entries == null) {
			ZipEntry zipEntry = getZipFile().getEntry(path);
			if (zipEntry != null) {
				entries = new ArrayList<URL>(1);
				entries.add(createEntryURL(zipEntry));
			}
		}
		return entries;
	}

	/**
	 * Return the content of the resource path.
	 * 
	 * */
	@Override
	public byte[] getEntryBytes(String path) throws Exception {
		byte[] superBytes = super.getEntryBytes(path);
		if (superBytes == null) {
			ZipEntry zipEntry = getZipFile().getEntry(path);
			if (zipEntry != null) {
				InputStream in = getZipFile().getInputStream(zipEntry);
				return readBytes(in);
			}
		}
		return superBytes;
	}

	/**
	 * Return the URL of the resource path.
	 * 
	 * */
	@Override
	public URL getEntryRoot(String path) throws Exception {
		URL superUrl = super.getEntryRoot(path);
		if (superUrl == null) {
			ZipEntry zipEntry = getZipFile().getEntry(path);
			if (zipEntry != null) {
				return this.getFileURL();
			}
		}
		return superUrl;
	}

	protected URL createEntryURL(ZipEntry zipEntry) throws MalformedURLException {
		return URI.create("jar:" + this.getFileURL() + "!/" + zipEntry.getName()).toURL();
	}

	private Iterator<String> entriesIterator(final boolean includeFolders) throws Exception {
		final Iterator<String> superEntries = super.getAllEntries();

		final Enumeration<? extends ZipEntry> zipEntries = getZipFile().entries();

		return new Iterator<String>() {
			private ZipEntry cachedZipEntry;

			@Override
			public boolean hasNext() {
				if (superEntries.hasNext()) {
					return true;
				}
				if (cachedZipEntry != null) {
					return true;
				}
				while (zipEntries.hasMoreElements()) {
					ZipEntry zipEntry = zipEntries.nextElement();
					if (includeFolders ||!zipEntry.isDirectory()) {
						cachedZipEntry = zipEntry;
						return true;
					}
				}
				return false;
			}

			@Override
			public String next() {
				if (superEntries.hasNext()) {
					return superEntries.next();
				}
				if (cachedZipEntry != null) {
					String next = cachedZipEntry.getName();
					cachedZipEntry = null;
					return next;
				}
				while (zipEntries.hasMoreElements()) {
					ZipEntry zipEntry = zipEntries.nextElement();
					if (includeFolders || !zipEntry.isDirectory()) {
						return zipEntry.getName();
					}
				}
				throw new NoSuchElementException("No more entries in resource.");
			}

			@Override
			public void remove() {
			}
		};
	}
}