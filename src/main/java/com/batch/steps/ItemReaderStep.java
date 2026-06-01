package com.batch.steps;

import com.batch.entities.Person;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.ResourceLoader;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ItemReaderStep implements Tasklet {

    private final ResourceLoader resourceLoader;

    public ItemReaderStep(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
       log.info("Inicio del paso de lectura");

        List<Person> personList = new ArrayList<>();

        try (Reader reader = new FileReader(resourceLoader.getResource("classpath:files/destination/persons.csv").getFile());
             CSVReader csvReader = new CSVReaderBuilder(reader)
                     .withCSVParser(new CSVParserBuilder().withSeparator(',').build())
                     .withSkipLines(1) // Saltamos encabezado
                     .build()) {

            String[] actualLine;

            while ((actualLine = csvReader.readNext()) != null) {
                if (actualLine.length < 3) {
                    log.warn("Fila incompleta ignorada: {}", (Object) actualLine);
                    continue;
                }

                Person person = new Person();
                person.setName(actualLine[0]);
                person.setLastName(actualLine[1]);

                try {
                    int age = (actualLine[2] != null && !actualLine[2].trim().isEmpty())
                            ? Integer.parseInt(actualLine[2].trim())
                            : 0;
                    person.setAge(age);
                } catch (NumberFormatException e) {
                    log.warn("Edad inválida para {} {}, se usará 0", person.getName(), person.getLastName());
                    person.setAge(0);
                }

                personList.add(person);
            }

        } catch (Exception e) {
            log.error("Error leyendo el CSV", e);
            throw e; // Lanza la excepción para que Spring Batch registre el error
        }

        // Guardamos en ExecutionContext
        chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .put("personList", personList);

        log.info("Fin del paso de lectura, {} registros leídos", personList.size());
        return RepeatStatus.FINISHED;
    }
}
