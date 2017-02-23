package io.pivotal.gemfire.sample.app.controller;

import org.apache.geode.cache.Region;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import io.pivotal.gemfire.sample.app.entity.Person;
import io.pivotal.gemfire.sample.app.builder.PersonBuilder;
import io.pivotal.gemfire.sample.app.repository.PersonRepository;

@RestController()
class PersonController {
	@Autowired
	private PersonRepository personRepo; 
	
	@Autowired 
	private Region<Integer, Person> personRegion;
	
	@Autowired
	private PersonBuilder personBuilder;
	
    @RequestMapping(value = "/person/{id}", method = GET)
    public Person getPerson(@PathVariable("id") Integer id) {
    	return personRepo.findPersonById(id);
    }
    
    @RequestMapping(value = "/person/{id}", method = PUT)
    public Person newPerson(@PathVariable("id") Integer id) {
    	Person p = personBuilder.buildPerson(id);
    	personRegion.put(id, p);
    	return p;    
    }
}