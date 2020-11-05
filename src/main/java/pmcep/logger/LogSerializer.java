package pmcep.logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.IOUtils;

import pmcep.utils.RuntimeUtils;

/**
 * This class serializes message sent to the {@link #serialize(String)} method
 * by archiving each session.
 * 
 * @author Andrea Burattin
 */
public class LogSerializer {

	private static Path CURRENT_SESSION_FILE;
	private static Path ARCHIVE_FILE;
	
	static {
		CURRENT_SESSION_FILE = Paths.get(RuntimeUtils.getSupportFolder() + System.currentTimeMillis() + ".txt");
		ARCHIVE_FILE = Paths.get(RuntimeUtils.getSupportFolder() + "last-sessions.tar.gz");
		
		try {
			Files.createFile(CURRENT_SESSION_FILE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method is used to store messages into a file which is then archived
	 * in a compressed file
	 * 
	 * @param message the message to archive
	 */
	public static void serialize(String message) {
		try {
			Files.write(CURRENT_SESSION_FILE, message.getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method performs the actual archiving of the current session file,
	 * which is also removed
	 */
	public static void archive() {
		try {
			// append the log to the archive
			appendFileInTarArchive(ARCHIVE_FILE, CURRENT_SESSION_FILE);
			
			// delete the file
			Files.delete(CURRENT_SESSION_FILE);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to append a file into a compressed archive (using .tar.gz_
	 * 
	 * @param archivePath the path of the archive
	 * @param fileToStore the path of the file to archive
	 * @throws IOException
	 */
	private static void appendFileInTarArchive(Path archivePath, Path fileToStore) throws IOException {
		if (!Files.exists(archivePath)) {
			FileOutputStream fos = new FileOutputStream(archivePath.toString());
			OutputStream gzo = new GzipCompressorOutputStream(fos);
			ArchiveOutputStream aos = new TarArchiveOutputStream(gzo);
			aos.finish();
			aos.close();
		}
		
		File tarFile = archivePath.toFile();
		File fileToAdd = new File(fileToStore.toString());
		File tempFile = File.createTempFile("serializer", "tar.gz");

		FileInputStream fis = new FileInputStream(tarFile);
		InputStream bi = new BufferedInputStream(fis);
		InputStream gzi = new GzipCompressorInputStream(bi);
		ArchiveInputStream ais = new TarArchiveInputStream(gzi);

		FileOutputStream fos = new FileOutputStream(tempFile);
		OutputStream gzo = new GzipCompressorOutputStream(fos);
		ArchiveOutputStream aos = new TarArchiveOutputStream(gzo);

		// copy the existing entries
		ArchiveEntry nextEntry;
		while ((nextEntry = ais.getNextEntry()) != null) {
			aos.putArchiveEntry(nextEntry);
			IOUtils.copy(ais, aos);
			aos.closeArchiveEntry();
		}

		// create the new entry
		TarArchiveEntry entry = new TarArchiveEntry(fileToStore.toFile().getName());
		entry.setSize(fileToAdd.length());
		aos.putArchiveEntry(entry);
		FileInputStream fis2 = new FileInputStream(fileToAdd);
		IOUtils.copy(fis2, aos);
		aos.closeArchiveEntry();
		
		fis2.close();

		aos.finish();
		ais.close();
		aos.close();

		// copies the new file over the old
		tarFile.delete();
		tempFile.renameTo(tarFile);
	}

}
