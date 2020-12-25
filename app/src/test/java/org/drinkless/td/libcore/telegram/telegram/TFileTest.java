package org.drinkless.td.libcore.telegram.telegram;

import org.drinkless.td.libcore.telegram.DriveFile;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;

public class TFileTest {

	@Test
	public void fileToTFile() {
	}

	@Test
	public void captionToTFile() {
		String caption = "C:\\Users\\Sunzc\\Documents\\Projects#TelegramDrive#f#TelegramDrive.iml";
		DriveFile file = new DriveFile(caption);
		assertFalse(file.isFile());
		assertEquals(caption, file.getFilePath());
		assertEquals("TelegramDrive.iml", file.getName());
		assertEquals("TelegramDrive", file.currentDirectory());
		assertEquals("C:\\Users\\Sunzc\\Documents\\Projects", file.parentDirectory());
	}

	@Test
	public void renameTFile() {
		String caption = "C:\\Users\\Sunzc\\Documents\\Projects#TelegramDrive#f#TelegramDrive.iml";
		DriveFile file = new DriveFile(caption);
		file.rename("测试文件.iml");
		assertEquals("测试文件.iml", file.getName());
	}


	@Test
	public void moveTFile() {
		String caption = "C:\\Users\\Sunzc\\Documents\\Projects#TelegramDrive#f#TelegramDrive.iml";
		String destCaption = "C:\\Users\\Sunzc\\Documents#JavaProject#d#demo";
		DriveFile file = new DriveFile(caption);
		DriveFile destDir = new DriveFile(destCaption);
		file.move(destDir);
		assertEquals("C:\\Users\\Sunzc\\Documents\\JavaProject#demo#f#TelegramDrive.iml", file.getFilePath());
	}

	@Test
	public void listFiles() {

	}

	public void deleteTFile() {
	}
}