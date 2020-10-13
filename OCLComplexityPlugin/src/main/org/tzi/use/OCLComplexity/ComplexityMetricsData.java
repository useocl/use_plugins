package org.tzi.use.OCLComplexity;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Container for saving the data.
 */
public class ComplexityMetricsData extends AbstractMetricsData {

    public ComplexityMetricsData() {
    }

    /**
     * Read a csv file with metric infos (name, description, tag).
     * A csv file is used for easy editing and sharing.
     */
    private Map<String, String[]> readMetaFile() {
        // file from the same directory
        String filename = "resources/metric_meta.csv";
        String delimiter = ";";
        Map<String, String[]> result = new HashMap<>();

        try {
            Path resourceDirectory = Paths.get(filename);
            String absolutePath = resourceDirectory.toFile().getAbsolutePath();
            // first try to get resource over classpath otherwise try absolute path
            InputStream inputStream = getClass().getResourceAsStream("/" + filename);
            if(inputStream == null) {
                inputStream = new FileInputStream(absolutePath);
            }

            BufferedReader bufferedReader = bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String row;
            while ((row = bufferedReader.readLine()) != null) {
                // Name, Description, Tag
                String[] data = row.split(delimiter);
                if(data.length == 3) {
                    result.put(data[0], new String[]{data[1], data[2]});
                } else {
                    System.err.println("Error while reading row from metric_meta.csv");
                }
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Set<Metric> build() {
        Set<Metric> result = new HashSet<>();
        Map<String, String[]> metricsMeta = readMetaFile();
        for(Map.Entry<String, String[]> meta: metricsMeta.entrySet()) {
                String metaName = meta.getKey();
                String metaDescr = meta.getValue()[0];
                String metaTag = meta.getValue()[1];
                result.add(new Metric(metaName, metaDescr, getMetric(metaName), metaTag));
        }
        return result;
    }
}
