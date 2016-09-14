package microsofia.boot.loader.resource;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Encapsulates the artifact read access. The artifact can be a zip/jar or just a file.
 * */
public abstract class Resource {
	private static Log log = LogFactory.getLog(Resource.class.getName());

	/**
	 * Create the correct Resource type from the file.
	 * 
	 * @param file the path of the artifact
	 * @return the correct resource type
	 * */
	public static Resource createResource(File file) {
		if (file.isFile()) {
			ZipFile zipFile = null;
			try {
				zipFile = new ZipFile(file);
			} catch (Exception ex) {
				if (log.isDebugEnabled()) {
					log.debug("Could not create a ZipFile instance for file: " + file.getAbsolutePath() + ". The file may not be a zip/jar archive.", ex);
				}
			}
			if (zipFile != null) {
				return new JarResource(file);
			
			} else {
				return new FileResource(file);
			}

		}
		throw new RuntimeException(new FileNotFoundException("Cannot find file " + file.getAbsolutePath()));
	}

	/**
	 * Returns all the URLs found for an entry path (for the moment there is only one URL by type)
	 * 
	 * @param path the entry path
	 * @return all the found URL
	 * */
	public abstract List<URL> getEntry(String path) throws Exception;

	/**
	 * Returns the URL found for an entry path
	 * 
	 * @param path the entry path
	 * @return the found URL
	 * */
	public abstract URL getEntryRoot(String path) throws Exception;

	/**
	 * Returns the content for an entry path
	 * 
	 * @param path the entry path
	 * @return the content
	 * */
	public abstract byte[] getEntryBytes(String path) throws Exception;

	/**
	 * Returns all the path entries
	 * 
	 * @return all the path entries
	 * */
	public abstract Iterator<String> getAllEntries() throws Exception;
	
	/**
	 * Returns all the path entries including folders
	 * 
	 * @return all the path entries including folders
	 * */
	public abstract Iterator<String> getAllEntriesIncludingFolders() throws Exception;

	protected byte[] readBytes(InputStream in) throws IOException {
		BufferedInputStream bin = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		try {
			bin = new BufferedInputStream(in);
			int read = 0;
			while ((read = bin.read(buffer)) > -1) {
				baos.write(buffer, 0, read);
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}
		return baos.toByteArray();
	}
}
