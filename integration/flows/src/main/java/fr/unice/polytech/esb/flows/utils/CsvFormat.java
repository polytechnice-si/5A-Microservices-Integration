package fr.unice.polytech.esb.flows.utils;

import org.apache.camel.model.dataformat.CsvDataFormat;

public class CsvFormat {

    public static CsvDataFormat buildCsvFormat() {
        CsvDataFormat format = new CsvDataFormat();
        format.setDelimiter(",");
        format.setSkipHeaderRecord(true);
        format.setUseMaps(true);
        return format;
    }
    
}