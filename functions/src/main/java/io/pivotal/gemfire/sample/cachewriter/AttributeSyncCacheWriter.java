package io.pivotal.gemfire.sample.cachewriter;

import java.util.List;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.cache.execute.ResultCollector;
import org.apache.geode.cache.util.CacheWriterAdapter;
import org.apache.geode.internal.logging.LogService;
import org.apache.logging.log4j.Logger;

import io.pivotal.gemfire.sample.temp.entity.Person;

@SuppressWarnings("rawtypes")
public class AttributeSyncCacheWriter extends CacheWriterAdapter<Integer, Person> {

	private static final Logger logger = LogService.getLogger();
	private Cache cache;
	private static String RECURSING = "recursing";

	public void beforeCreate(EntryEvent event) {
		executeEventModifier(event);
	}

	public void beforeUpdate(EntryEvent event) {
		executeEventModifier(event);
	}
	
	private void executeEventModifier(EntryEvent event){
		this.cache = (Cache) event.getRegion().getRegionService();
		if(FunctionService.isRegistered("EventModifierFunction")){
			Execution execution = FunctionService.onRegion(event.getRegion()).withArgs(event);		
			ResultCollector collector = execution.execute("EventModifierFunction");
			List<EntryEvent> regionResults = (List<EntryEvent>) collector.getResult();
			event = regionResults.get(0);
		}
	}
	
}


