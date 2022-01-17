package com.pluralsight.michaelhoffman.camel.fraud.config;

import org.apache.camel.model.dataformat.CsvDataFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IntegrationConfig {

    @Bean("csvDataFormatTransaction")
    public CsvDataFormat csvDataFormatTransaction() {
        CsvDataFormat csvDataFormatTransaction = new CsvDataFormat();
        csvDataFormatTransaction.setDelimiter(",");
        csvDataFormatTransaction.setSkipHeaderRecord("true");
        return csvDataFormatTransaction;
    }

}
