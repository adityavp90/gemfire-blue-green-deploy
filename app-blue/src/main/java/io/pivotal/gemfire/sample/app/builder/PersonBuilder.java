package io.pivotal.gemfire.sample.app.builder;

import org.springframework.stereotype.Component;
import io.pivotal.gemfire.sample.app.entity.Person;

@Component
public class PersonBuilder {
	
	public Person buildPerson(int id){
		Person p = new Person();
		
		p.setId(id);
	    p.setName("John" + id + " Doe" + id);
	    p.setAge(2);	           
		return p;
	}
	
}
