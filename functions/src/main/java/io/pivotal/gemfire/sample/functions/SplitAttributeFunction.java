package io.pivotal.gemfire.sample.functions;

import java.util.Set;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.partition.PartitionRegionHelper;
import org.apache.geode.internal.logging.LogService;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("rawtypes")
public class SplitAttributeFunction implements Function {

	private static final Logger logger = LogService.getLogger();
	private Cache cache;

	@Override
	public void execute(FunctionContext fc) {
		RegionFunctionContext rfc = (RegionFunctionContext) fc;
		Region r = PartitionRegionHelper.getLocalDataForContext(rfc);

		this.cache = (Cache) r.getRegionService();
		// r.putAll(r.getAll(r.keySet()));
		Set<Integer> regionKeys = r.keySet();
		for (Object o : regionKeys) {
			r.put(o,r.get(o));
		}
	}

	@Override
	public String getId() {
		return getClass().getSimpleName();
	}

	public boolean hasResult() {
		return false;
	}

	public boolean isHA() {
		return false;
	}

	public boolean optimizeForWrite() {
		return false;
	}

}
