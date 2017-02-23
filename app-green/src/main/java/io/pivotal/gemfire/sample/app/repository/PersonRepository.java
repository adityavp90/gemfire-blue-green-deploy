package io.pivotal.gemfire.sample.app.repository;


import org.springframework.data.gemfire.repository.GemfireRepository;
import org.springframework.stereotype.Repository;

import io.pivotal.gemfire.sample.app.entity.Person;

@Repository
public interface PersonRepository extends GemfireRepository<Person, Integer> {

	Person findPersonById(Integer id);

}