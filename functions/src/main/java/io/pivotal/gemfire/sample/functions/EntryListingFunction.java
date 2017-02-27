package io.pivotal.gemfire.sample.functions;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.Region;

import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;

import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.partition.PartitionRegionHelper;


public class EntryListingFunction implements Function{

	public void execute(FunctionContext fc) {
		if(fc instanceof RegionFunctionContext){
			RegionFunctionContext rfc = (RegionFunctionContext) fc;
			Region r = PartitionRegionHelper.getLocalDataForContext(rfc);
			Cache c = (Cache) r.getRegionService();
			String memberName = c.getDistributedSystem().getName();
			int i=0;
			if(r.size() > 0) {
				for(Object o : r.values()){
					if(++i < r.size()){
						rfc.getResultSender().sendResult(memberName + " : " + o.toString());					
					} else {
						rfc.getResultSender().lastResult(memberName + " : " + o.toString());
					}	
				}
			} else {
				rfc.getResultSender().lastResult(memberName + " : " + "No Entries");
			}	
		}
	}
	
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
