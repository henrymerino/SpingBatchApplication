package com.batch.config;

import com.batch.service.IPersonService;
import com.batch.steps.ItemDescompressStep;
import com.batch.steps.ItemProccessorStep;
import com.batch.steps.ItemReaderStep;
import com.batch.steps.ItemWriterStep;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
public class BatchConfiguration {

    // ---------------------------
    //  Tasklets
    // ---------------------------

    @Bean
    public ItemDescompressStep itemDescompressStep(ResourceLoader resourceLoader) {
        return new ItemDescompressStep(resourceLoader);
    }

    @Bean
    public ItemReaderStep itemReaderStep(ResourceLoader resourceLoader) {
        return new ItemReaderStep(resourceLoader);
    }

    @Bean
    public ItemProccessorStep itemProccessorStep() {
        return new ItemProccessorStep();
    }

    @Bean
    public ItemWriterStep itemWriterStep(IPersonService iPersonService) {
        return new ItemWriterStep(iPersonService);
    }

    // ---------------------------
    //  Steps
    // ---------------------------

    @Bean
    public Step decompressStep(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager,
                               ItemDescompressStep tasklet) {
        return new StepBuilder("decompressStep", jobRepository)
                .tasklet(tasklet, transactionManager)
                .build();
    }

    @Bean
    public Step readerStep(JobRepository jobRepository,
                           PlatformTransactionManager transactionManager,
                           ItemReaderStep tasklet) {
        return new StepBuilder("readerStep", jobRepository)
                .tasklet(tasklet, transactionManager)
                .build();
    }

    @Bean
    public Step processorStep(JobRepository jobRepository,
                              PlatformTransactionManager transactionManager,
                              ItemProccessorStep tasklet) {
        return new StepBuilder("processorStep", jobRepository)
                .tasklet(tasklet, transactionManager)
                .build();
    }

    @Bean
    public Step writerStep(JobRepository jobRepository,
                           PlatformTransactionManager transactionManager,
                           ItemWriterStep tasklet) {
        return new StepBuilder("writerStep", jobRepository)
                .tasklet(tasklet, transactionManager)
                .build();
    }

    // ---------------------------
    //  Job
    // ---------------------------

    @Bean
    public Job job(JobRepository jobRepository,
                   Step decompressStep,
                   Step readerStep,
                   Step processorStep,
                   Step writerStep) {
        return new JobBuilder("myBatchJob", jobRepository)
                .start(decompressStep)
                .next(readerStep)
                .next(processorStep)
                .next(writerStep)
                .build();
    }
}
