/**
 * 
 */
package org.tzi.use.plugins.monitor;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.tzi.use.config.Options;
import org.tzi.use.plugins.monitor.vm.adapter.VMAdapter;
import org.tzi.use.plugins.monitor.vm.adapter.jvm.JVMAdapter;

/**
 * @author Lars Hamann
 *
 */
public class AdapterRegistry {
	
	public static VMAdapter[] loadAviableAdapter() {
		
		List<VMAdapter> result = new ArrayList<VMAdapter>();
		
		// Built-in is JVMAdapter
		result.add(new JVMAdapter());		
		
		// Read all jars which are named "MonitorAdapter_*.jar"
		File pluginDir = null;
		try {
			pluginDir = new File(new URL(Options.pluginDir).toURI());
		} catch (MalformedURLException e) {
			// Cannot happen, runtime is already up
		} catch (URISyntaxException e) {
			// Cannot happen, runtime is already up
		}
		
		String[] adapterJars = pluginDir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith("MonitorAdapter_") && name.endsWith(".jar");
			}
		});
		
		for (String adapterJar : adapterJars) {
			
		}
		
		return result.toArray(new VMAdapter[result.size()]);
	}
	
}
