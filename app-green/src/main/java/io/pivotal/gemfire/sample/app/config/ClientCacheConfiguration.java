package io.pivotal.gemfire.sample.app.config;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;

import io.pivotal.gemfire.sample.app.entity.Person;



@Configuration
@EnableGemfireRepositories("io.pivotal.gemfire.sample.app.repository")
public class ClientCacheConfiguration {
    
    /*
     * Connection parameter members (TODO - @Profile should adjust these *only*)
     */
    @Value("${gemfire.locator.host}")
    private String locatorHost;

    @Value("${gemfire.locator.port}")
    private Integer locatorPort;

    /*
     * Create a connection - client/server topology (TODO - maybe change this to use a connection Pool)
     */
    @Bean
    public ClientCache cache() {
        ClientCacheFactory ccf = new ClientCacheFactory();

        ccf.addPoolLocator(locatorHost, locatorPort);

        ccf.setPdxPersistent(true);
        ccf.setPdxReadSerialized(false);
        ccf.setPdxIgnoreUnreadFields(true);
        ccf.setPdxSerializer(new ReflectionBasedAutoSerializer("io.pivotal.gemfire.sample.app.entity.*"));

        return ccf.create();
    }

    @Bean
    public Region<Integer, Person> personRegion(ClientCache cache) {
        ClientRegionFactory<Integer, Person> crf = cache.createClientRegionFactory(ClientRegionShortcut.PROXY);
        return crf.create("Person");
    }

}
