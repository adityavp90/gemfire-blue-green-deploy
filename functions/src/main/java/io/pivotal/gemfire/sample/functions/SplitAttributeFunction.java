package io.pivotal.gemfire.sample.functions;

import java.util.Set;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.partition.PartitionRegionHelper;
import org.apache.geode.pdx.PdxInstance;

public class SplitAttributeFunction implements Function{
	
	@Override
	public void execute(FunctionContext fc) {
		//placeholder for function to split name into fname and lname for all object in region 
	}
	
	@Override
	public String getId() {
		return getClass().getSimpleName();
	}

	public boolean hasResult(){
		return true;
	}
	
	public boolean isHA(){
		return false;		
	}
	
	public boolean optimizeForWrite(){
		return false;
	}

}
