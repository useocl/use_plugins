package org.tzi.kodkod.helper;

import java.io.File;

/**
 * Helper class to get path information for the plugin jar.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class PathHelper {

	public static File getJarFile() {
		return new File(PathHelper.class.getProtectionDomain().getCodeSource().getLocation().getPath());
	}

	public static String getPluginPath() {
		File jarFile = getJarFile();
		String jarDir = jarFile.getParentFile().getPath();
		return jarDir;
	}
}
