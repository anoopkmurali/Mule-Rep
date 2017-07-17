package com.concretepage.springbatch;

import java.io.IOException;
import java.io.Writer;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.MapJobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;

import org.springframework.batch.item.file.FlatFileItemWriter;

import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;

import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration{

	
	@Bean
	public PlatformTransactionManager transactionManager() {
	    return new ResourcelessTransactionManager();
	}
    @Bean
    public JobRepository jobRepository() throws Exception {
        return new MapJobRepositoryFactoryBean(transactionManager()).getJobRepository();
    }

    @Bean
    public JobLauncher jobLauncher() throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository());
        return jobLauncher;
    }


	/*@Bean
	public JobExplorer jobExplorer() throws Exception {
	    MapJobExplorerFactoryBean jobExplorerFactory = new MapJobExplorerFactoryBean(mapJobRepositoryFactoryBean());
	    jobExplorerFactory.afterPropertiesSet();
	    return jobExplorerFactory.getObject();
	}

	@Bean
	public MapJobRepositoryFactoryBean mapJobRepositoryFactoryBean() {
	    MapJobRepositoryFactoryBean mapJobRepositoryFactoryBean = new MapJobRepositoryFactoryBean();
	    mapJobRepositoryFactoryBean.setTransactionManager(transactionManager());
	    return mapJobRepositoryFactoryBean;
	}

	@Bean
	public JobRepository jobRepository() throws Exception {
	    return mapJobRepositoryFactoryBean().getObject();
	}

	@Bean
	public JobLauncher jobLauncher() throws Exception {
	    SimpleJobLauncher simpleJobLauncher = new SimpleJobLauncher();
	    simpleJobLauncher.setJobRepository(jobRepository());
	    return simpleJobLauncher;
	}
	
	 
*/	 
	 
	
	private static final String QUERY_FIND_STUDENTS =
            "SELECT " +               
                "stdId, " +
                "subMarkOne, " +
                "subMarkTwo " +
            "FROM student " ;
	
	
	@Bean
    public ItemReader<Student> reader() {
		
        JdbcCursorItemReader<Student> databaseReader = new JdbcCursorItemReader<>();
 
        databaseReader.setDataSource(getDataSourceMVAS());
        databaseReader.setSql(QUERY_FIND_STUDENTS);
        databaseReader.setRowMapper(new BeanPropertyRowMapper<>(Student.class));
 
        return databaseReader;
    }

    /*@Bean
    public ItemReader<Student> reader() {
        FlatFileItemReader<Student> reader = new FlatFileItemReader<Student>();
        reader.setResource(new ClassPathResource("student-data.csv"));
        reader.setLineMapper(new DefaultLineMapper<Student>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[] {"stdId", "subMarkOne", "subMarkTwo" });
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<Student>() {{
                setTargetType(Student.class);
            }});
        }});
        return reader;
    }*/
   

    @Bean
    public ItemWriter<Marksheet> writer() throws IOException {
System.out.println("Writer ..........");
    	
    	String exportFileHeader = "NAME,TOTAL MARK";
        StringHeaderWriter headerWriter = new StringHeaderWriter(exportFileHeader);    	
    	FlatFileItemWriter<Marksheet> writer = new FlatFileItemWriter<Marksheet>();
    	writer.setResource(new ClassPathResource("student-marksheet.csv"));
    	writer.setHeaderCallback(headerWriter);
    	
    	
    	
    	
    	
    	
    	DelimitedLineAggregator<Marksheet> delLineAgg = new DelimitedLineAggregator<Marksheet>();
    	delLineAgg.setDelimiter(",");
    	BeanWrapperFieldExtractor<Marksheet> fieldExtractor = new BeanWrapperFieldExtractor<Marksheet>();
    	fieldExtractor.setNames(new String[] {"stdId", "totalSubMark"});
    	
    	
    	delLineAgg.setFieldExtractor(fieldExtractor);
   
    	writer.setLineAggregator(delLineAgg);
    	System.out.println("End of writer");
        return writer;
    }
    
    @Bean
    public ItemProcessor<Student, Marksheet> processor() {
        return new StudentItemProcessor();
    }

    @Bean
    public Job createMarkSheet(JobBuilderFactory jobs, Step step) {
        return jobs.get("createMarkSheet")
        		.incrementer(new RunIdIncrementer())
                .flow(step)
                .end()
                .build();
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory, ItemReader<Student> reader,
            ItemWriter<Marksheet> writer, ItemProcessor<Student, Marksheet> processor) {
        return stepBuilderFactory.get("step")
                .<Student, Marksheet> chunk(5)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
    
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSourcemvas) {
        return new JdbcTemplate(dataSourcemvas);
    }
    
    @Bean
	public DataSource getDataSourceMVAS() {
	    BasicDataSource dataSource = new BasicDataSource();
	    dataSource.setDriverClassName("com.mysql.jdbc.Driver");
	    dataSource.setUrl("jdbc:mysql://localhost:3306/mysql");
	    dataSource.setUsername("root");
	    dataSource.setPassword("root");
	    return dataSource;
	}
    
   
 
}
