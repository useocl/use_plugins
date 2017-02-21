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
	
	private static final SystemInformation SYSTEM_INFORMATION = determineSystemInformation();
	
	public static SystemInformation getSystemInformation() {
		return SYSTEM_INFORMATION;
	}
	
	private static SystemInformation determineSystemInformation(){
		String os = System.getProperty("os.name");
		if(os == null){
			return UNKNOWN;
		}
		os = os.toLowerCase();
		
		String arch = System.getProperty("os.arch");
		if(arch == null){
			return UNKNOWN;
		}
		boolean archX64 = arch.contains("64");
		
		if(os.contains("mac") || os.contains("nix") || os.contains("nux") || os.contains("aix")){
			return archX64 ? UNIX_64BIT : UNIX_32BIT;
		} else if(os.contains("win")){
			return archX64 ? WINDOWS_64BIT : WINDOWS_32BIT;
		}
		
		return UNKNOWN;
	}
}
