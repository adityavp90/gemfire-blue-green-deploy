package io.pivotal.gemfire.sample.cachewriter;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.util.CacheWriterAdapter;
import org.apache.geode.internal.cache.EntryEventImpl;
import org.apache.geode.internal.logging.LogService;
import org.apache.geode.pdx.PdxInstance;
import org.apache.geode.pdx.PdxInstanceFactory;
import org.apache.geode.pdx.internal.PdxField;
import org.apache.geode.pdx.internal.PdxInstanceImpl;
import org.apache.logging.log4j.Logger;

import io.pivotal.gemfire.sample.temp.entity.Person;



@SuppressWarnings("rawtypes")
public class AttributeSyncCacheWriter extends CacheWriterAdapter<Integer,Person> {

	private static final Logger logger = LogService.getLogger();
	private Cache cache;
	private static String RECURSING = "recursing";

	
	public void beforeCreate(EntryEvent event) {
		this.cache = (Cache) event.getRegion().getRegionService();
		eventModifier(event);
	}
	
	public void beforeUpdate(EntryEvent event) {
		this.cache = (Cache) event.getRegion().getRegionService();
		eventModifier(event);
	}
	
	private void eventModifier(EntryEvent event){
		EntryEventImpl eei = (EntryEventImpl) event;
		PdxInstanceImpl impl = (PdxInstanceImpl) event.getNewValue();
		if(impl.getField("firstName") != null || (impl.getField("lastName") != null)){
			return;
		}
		PdxInstanceFactory factory = this.cache.createPdxInstanceFactory(impl.getClassName());			

		for (PdxField field : impl.getPdxType().getFields()) {
			String fieldName = field.getFieldName();
			Object fieldValue = impl.getField(fieldName);
			switch (field.getFieldType()) {
			case STRING:
				factory.writeString(fieldName, (String) fieldValue);
				break;
			case INT:
				factory.writeInt(fieldName, (int) fieldValue);
				break;
			case DOUBLE:
				factory.writeDouble(fieldName, (double) fieldValue);
				break;
			default:
				factory.writeObject(fieldName, fieldValue);
			}
		}

		if(impl.getField("name") != null){
			String name = impl.getField("name").toString();
			factory.writeString("firstName", name.split(" ")[0]);
			factory.writeString("lastName", name.split(" ")[1]);
		}
		else {
			factory.writeString("name", impl.getField("firstName") + " " + impl.getField("lastName"));
		}
		
		PdxInstance updatedInstance = factory.create();
	
		eei.setNewValue(updatedInstance);
		eei.makeSerializedNewValue();
	}

}
