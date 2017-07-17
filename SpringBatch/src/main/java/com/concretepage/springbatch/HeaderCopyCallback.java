package com.concretepage.springbatch;
import java.io.IOException;
import java.io.Writer;

import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.LineCallbackHandler;
import org.springframework.util.Assert;

/**
 * Designed to be registered with both {@link org.springframework.batch.item.file.FlatFileItemReader}
 * and {@link org.springframework.batch.item.file.FlatFileItemWriter} and copy header line from input
 * file to output file.
 */
public class HeaderCopyCallback implements LineCallbackHandler, FlatFileHeaderCallback {
	private String header = "";
	
	@Override
	public void handleLine(String line) {
		Assert.notNull(line, "line must not be null");
		this.header = line;
	}

	@Override
	public void writeHeader(Writer writer) throws IOException {
		writer.write("header from input: " + header);
	}
}