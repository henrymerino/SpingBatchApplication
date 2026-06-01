package com.batch.steps;


import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Slf4j
public class ItemDescompressStep implements Tasklet {

    private final ResourceLoader resourceLoader;

    public ItemDescompressStep(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        //log.info("Inicio del paso de descompresion");

        Resource resource = resourceLoader.getResource("classpath:files/persons.zip");
        String filePath = resource.getFile().getAbsolutePath();
        try (ZipFile zipFile = new ZipFile(filePath);) {
            File desDir = new File(resource.getFile().getParent(), "destination");

            if (!desDir.exists()) {
                desDir.mkdir();
            }

            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                File file = new File(desDir, zipEntry.getName());
                if (file.isDirectory()) {
                    file.mkdirs();
                } else {
                    try (InputStream inputStream = zipFile.getInputStream(zipEntry);
                         FileOutputStream fileOutputStream = new FileOutputStream(file);) {
                        byte[] buffer = new byte[1024];
                        int length;

                        while ((length = inputStream.read(buffer)) > 0) {
                            fileOutputStream.write(buffer, 0, length);
                        }
                    }

                }
            }

            zipFile.close();
        //    log.info("Fin del paso de descompresion");
            return RepeatStatus.FINISHED;
        }

    }
}
