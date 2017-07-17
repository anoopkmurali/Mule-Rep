package com.concretepage.springbatch;
import java.io.IOException;
import java.io.Writer;

import org.springframework.batch.item.file.FlatFileHeaderCallback;
 
class StringHeaderWriter implements FlatFileHeaderCallback {
 
    private final String header;
 
    StringHeaderWriter(String exportFileHeader1) {
        this.header = exportFileHeader1;
    }
 
    public void writeHeader(Writer writer) throws IOException {
        writer.write(header);
    }
}