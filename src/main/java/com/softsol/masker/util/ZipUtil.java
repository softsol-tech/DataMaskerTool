package com.softsol.masker.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.util.Collection;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.softsol.masker.exception.MaskerException;

public class ZipUtil {

	private ZipUtil() {

	}

	/**
	 * This method will extract the given zip file to given output location.
	 * password field is an optional field.
	 * 
	 * @param zipfile
	 * @param password
	 * @param outputDir
	 * @throws IOException
	 * @throws Exception
	 */
	public static void deCompress(String inputFile, String outputDir) {

		try (FileInputStream is = new FileInputStream(inputFile);
				ArchiveInputStream i = new ZipArchiveInputStream(is)) {
			ArchiveEntry entry = null;
			while ((entry = i.getNextEntry()) != null) {
				if (!i.canReadEntryData(entry)) {
					// log something?
					continue;
				}
				String name = outputDir + File.separator + entry.getName();
				File f = new File(name);
				if (entry.isDirectory()) {
					if (!f.isDirectory() && !f.mkdirs()) {
						throw new MaskerException("Failed to create directory ");
					}
				} else {
					File parent = f.getParentFile();
					if (!parent.isDirectory() && !parent.mkdirs()) {
						throw new MaskerException("Failed to create directory " + parent);
					}
					try (OutputStream o = Files.newOutputStream(f.toPath())) {
						IOUtils.copy(i, o);
					} catch (IOException e) {
						throw new MaskerException("Failed to Extract file: " + f, e);
					}
				}
			}
		} catch (IOException e) {
			throw new MaskerException("Failed to Decompress", e);
		}
	}

	/**
	 * Add all files from the source directory to the destination zip file  *
	 * 
	 * @param source      the directory with files to add
	 * @param destination the zip file that should contain the files
	 * @throws IOException      if the io fails
	 * @throws ArchiveException if creating or adding to the archive fails  
	 */
	public static void compress(File source, File dest) {

		try (FileOutputStream toOutputStream = new FileOutputStream(dest);
				CheckedOutputStream cos = new CheckedOutputStream(toOutputStream, new CRC32());
				ZipArchiveOutputStream zos = new ZipArchiveOutputStream(cos)) {
			zos.setUseZip64(Zip64Mode.Always);
			Collection<File> fileList = FileUtils.listFiles(source, null, true);
			for (File file : fileList) {
				writeIntoZip(source, zos, file);
			}
			zos.finish();
		} catch (IOException e) {
			throw new MaskerException("IO Issue while compressing folder : " + source.getAbsolutePath(), e);
		}
	}

	/**
	 * Write file into zip
	 * 
	 * @param source
	 * @param zos
	 * @param eachFile
	 * @throws IOException  
	 */
	private static void writeIntoZip(File source, ZipArchiveOutputStream zos, File eachFile) throws IOException {
		String entryName = getEntryName(source, eachFile);
		ZipArchiveEntry entry = new ZipArchiveEntry(entryName);
		zos.putArchiveEntry(entry);
		try (InputStream inputStream = new FileInputStream(eachFile);
				BufferedInputStream input = new BufferedInputStream(inputStream)) {
			IOUtils.copy(input, zos);
			zos.closeArchiveEntry();
		}
	}

	/**
	 * Remove the leading part of each entry that contains the source directory name
	 * 
	 * @param source the directory where the file entry is found
	 * @param file   the file that is about to be added
	 * @return the name of an archive entry
	 * @throws IOException if the io fails  
	 */
	private static String getEntryName(File source, File file) throws IOException {
		int index = source.getAbsolutePath().length() + 1;
		String path = file.getCanonicalPath();

		return path.substring(index);
	}

	public static boolean isArchive(File f) {
		int fileSignature = 0;
		try (RandomAccessFile raf = new RandomAccessFile(f, "r")) {
			fileSignature = raf.readInt();
		} catch (IOException e) {
			// handle if you like
		}
		return fileSignature == 0x504B0304 || fileSignature == 0x504B0506 || fileSignature == 0x504B0708;
	}

}
