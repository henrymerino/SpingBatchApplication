package com.batch.steps;

import com.batch.entities.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ItemProccessorStep implements Tasklet {
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
       log.info("Inicio del paso de procesamiento");

        List<Person> personList = (List<Person>) chunkContext
                .getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .get("personList");

        if (personList == null || personList.isEmpty()) {
            log.warn("personList está vacío o null, se omite procesamiento");
            return RepeatStatus.FINISHED;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        List<Person> personFinalList = personList.stream()
                .peek(person -> person.setInsertionDate(formatter.format(LocalDateTime.now())))
                .collect(Collectors.toList());

        chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .put("personFinalList", personFinalList);

        log.info("Fin del paso de procesamiento, {} registros procesados", personFinalList.size());
        return RepeatStatus.FINISHED;
   }
}