package com.concretepage.springbatch;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class CustomBatchConfigurer extends DefaultBatchConfigurer {

    @Override
    @Autowired
    public void setDataSource( @Qualifier("batchDataSource") DataSource dataSource) {
        super.setDataSource(dataSource);
    }

}