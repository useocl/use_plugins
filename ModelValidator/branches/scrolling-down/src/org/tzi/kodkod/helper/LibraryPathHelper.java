package org.tzi.kodkod.helper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * @author Hendrik Reitmann
 */
public class LibraryPathHelper {

	/**
	 * Adds the directory to the 'java.library.path'.
	 * @param directory
	 * @throws IOException
	 */
	public static void addDirectory(String directory) throws IOException {
		try {
			Field field = ClassLoader.class.getDeclaredField("usr_paths");
			field.setAccessible(true);
			String[] paths = (String[]) field.get(null);
			for (int i = 0; i < paths.length; i++) {
				if (directory.equals(paths[i])) {
					return;
				}
			}
			String[] tmp = new String[paths.length + 1];
			System.arraycopy(paths, 0, tmp, 1, paths.length);
			tmp[0] = directory;
			field.set(null, tmp);
			System.setProperty("java.library.path", directory + File.pathSeparator + System.getProperty("java.library.path"));
		} catch (IllegalAccessException e) {
			throw new IOException("Failed to get permissions to set library path");
		} catch (NoSuchFieldException e) {
			throw new IOException("Failed to get field handle to set library path");
		}
	}
}
