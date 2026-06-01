package com.batch.service;

import com.batch.entities.Person;
import org.springframework.stereotype.Service;

import java.util.List;


public interface IPersonService {

    void saveAll(List<Person> personList);
}
