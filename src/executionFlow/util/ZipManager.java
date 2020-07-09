package executionFlow.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


/**
 * Responsible for managing zip files.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		2.0
 * @since		2.0
 */
public class ZipManager 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private File zipFile;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * Creates zip file manager.
	 * 
	 * @param		zipFile Zip file path
	 */
	public ZipManager(File zipFile)
	{
		this.zipFile = zipFile;
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Puts files into the ZIP file. If zip file already exists, it will be put
	 * only files that are not within it.
	 * 
	 * @param		pathList Path of the files to be put into the zip
	 * 
	 * @throws		IOException If zip cannot be saved
	 */
	public void put(List<Path> pathList) throws IOException
	{
		// If the zip file already exists, gets the files that are not in it
		if (zipFile.exists()) {
			pathList = getNewFiles(pathList);
		}
		
		// Puts paths inside the zip
		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile, true))) {
			for (Path p : pathList) {
				ZipEntry ze = new ZipEntry(p.getFileName().toString());
				BufferedInputStream bis = new BufferedInputStream(new FileInputStream(p.toFile()));
				int bytesRead;
				
				
				zos.putNextEntry(ze);
				
				while ( (bytesRead = bis.read()) != -1 ) {
					zos.write(bytesRead);
				}
				
				zos.flush();
				zos.closeEntry();
				bis.close();
			}
		}
	}
	
	/**
	 * Reads zip content.
	 * 
	 * @return		List of files that are inside the zip
	 * 
	 * @throws		IOException If zip file does not exist
	 */
	private List<String> read() throws IOException 
	{
		final List<String> zipContent = new ArrayList<>();
		
		
		try (FileInputStream fis = new FileInputStream(zipFile);
                BufferedInputStream bis = new BufferedInputStream(fis);
                ZipInputStream zis = new ZipInputStream(bis)) {
			ZipEntry ze;
			
            while ((ze = zis.getNextEntry()) != null) {
            	zipContent.add(ze.getName());
            }
        }
		
		return zipContent;
	}
	
	/**
	 * Returns files that are not within Zip file.
	 * 
	 * @param 		pathList List of files that will be checked if each of your
	 * files is inside the zip
	 */
	private List<Path> getNewFiles(List<Path> pathList)
	{
		List<Path> newFiles = new ArrayList<>();
		List<String> zipContent;
		
		
		try {
			if (zipFile.exists()) {
				zipContent = read();
				
				for (Path p : pathList) {
					if (!zipContent.contains(p.getName(p.getNameCount()-1).toString())) {
						newFiles.add(p);
					}
				}
			}
		} catch(IOException e) {
			zipFile.delete();
			newFiles = pathList;
		}
		
		return newFiles;
	}
}
