package io.pivotal.gemfire.sample.client;

import java.util.List;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.cache.execute.ResultCollector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.pivotal.gemfire.sample.app.entity.Person;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {io.pivotal.gemfire.sample.app.config.ClientCacheConfiguration.class})
public class ClientApplicationTests {

	@Autowired 
	private Region<Integer, Person> personRegion;
	
	
	@Test
	public void testFunctionCall(){
		System.out.println("Hello World!");
		executeEntryListingFucntion(personRegion);
	}
	
    public void executeEntryListingFucntion(Region personRegion){
    	Execution execution = FunctionService.onRegion(personRegion);
    	ResultCollector collector = execution.execute("EntryListingFunction");
    	List<String> regionResults = (List<String>) collector.getResult();
    	System.out.println("Entries for region: " + personRegion.getName());
    	for(String entry : regionResults){
    		System.out.println(" - " + entry);
    	}
    }

}
