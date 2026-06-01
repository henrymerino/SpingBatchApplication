package com.batch.steps;

import com.batch.entities.Person;
import com.batch.service.IPersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import java.util.List;

@Slf4j
public class ItemWriterStep implements Tasklet {

    private IPersonService iPersonService;



    public ItemWriterStep(IPersonService iPersonService) {
        this.iPersonService = iPersonService;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("Inicio del paso de escritura");

        List<Person> personList = (List<Person>) chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .get("personFinalList");

        if (personList == null || personList.isEmpty()) {
            log.warn("personFinalList está vacío o null. No se insertarán registros.");
            return RepeatStatus.FINISHED;
        }

        personList.forEach(person -> {
            if (person != null){
                log.info("Insertando persona: {}", person);
            }
        });

        iPersonService.saveAll(personList);

         log.info("Fin del paso de escritura. {} registros guardados", personList.size());
        return RepeatStatus.FINISHED;
    }
}
