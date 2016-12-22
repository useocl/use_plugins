package org.tzi.kodkod.helper;

/**
 * Provides information about the system USE is running on, i.e. operating
 * system and architecture.
 */
public enum SystemInformation {
	WINDOWS_32BIT,
	WINDOWS_64BIT,
	UNIX_32BIT,
	UNIX_64BIT,
	UNKNOWN;
	
	private static final SystemInformation SYSTEM_INFORMATION = assignSystemInformation();
	
	public static SystemInformation getSystemInformation() {
		return SYSTEM_INFORMATION;
	}
	
	private static SystemInformation assignSystemInformation(){
		String os = System.getProperty("os.name");
		
		if(os == null){
			return UNKNOWN;
		}
		os = os.toLowerCase();
		
		String arch = System.getProperty("os.arch");
		boolean archX64;
		if(arch != null && arch.contains("64")){
			archX64 = true;
		} else {
			archX64 = false;
		}
		
		if(os.indexOf("mac") >= 0 || os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") >= 0){
			return archX64 ? UNIX_64BIT : UNIX_32BIT;
		} else if(os.indexOf("win") >= 0){
			return archX64 ? WINDOWS_64BIT : WINDOWS_32BIT;
		}
		
		return UNKNOWN;
	}
}
