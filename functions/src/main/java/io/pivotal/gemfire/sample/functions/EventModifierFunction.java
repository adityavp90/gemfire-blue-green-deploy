package io.pivotal.gemfire.sample.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.partition.PartitionRegionHelper;
import org.apache.geode.internal.cache.EntryEventImpl;
import org.apache.geode.internal.logging.LogService;
import org.apache.geode.pdx.PdxInstance;
import org.apache.geode.pdx.PdxInstanceFactory;
import org.apache.geode.pdx.internal.PdxField;
import org.apache.geode.pdx.internal.PdxInstanceImpl;
import org.apache.logging.log4j.Logger;


@SuppressWarnings("rawtypes")
public class EventModifierFunction implements Function {
	
	private static final Logger logger = LogService.getLogger();
	private Cache cache;
	
	@Override
	public void execute(FunctionContext fc) {
		RegionFunctionContext rfc = (RegionFunctionContext) fc;
		EntryEvent event = (EntryEvent) rfc.getArguments();
		Region r = PartitionRegionHelper.getLocalDataForContext(rfc);

		this.cache = (Cache) r.getRegionService();
		
		event = eventModifier(event);
		rfc.getResultSender().lastResult(event);
		
	}
	
	private EntryEvent eventModifier(EntryEvent event) {

		EntryEventImpl eei = (EntryEventImpl) event;
		PdxInstanceImpl impl = (PdxInstanceImpl) event.getNewValue();
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

			if (impl.getField("name") != null) {
				String name = impl.getField("name").toString();
				factory.writeString("firstName", name.split(" ")[0]);
				factory.writeString("lastName", name.split(" ")[1]);
			} else {
				factory.writeString("name", impl.getField("firstName") + " " + impl.getField("lastName"));
			}

			PdxInstance updatedInstance = factory.create();

			eei.setNewValue(updatedInstance);
			eei.makeSerializedNewValue();
		return eei;
	}

	@Override
	public String getId() {
		return getClass().getSimpleName();
	}

	public boolean hasResult() {
		return true;
	}

	public boolean isHA() {
		return false;
	}

	public boolean optimizeForWrite() {
		return false;
	}

}
