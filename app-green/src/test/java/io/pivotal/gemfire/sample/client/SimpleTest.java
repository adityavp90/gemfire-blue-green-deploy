package io.pivotal.gemfire.sample.client;

import io.pivotal.gemfire.sample.app.builder.PersonBuilder;
import io.pivotal.gemfire.sample.app.entity.Person;
import org.apache.geode.cache.Region;
import org.jgroups.util.UUID;
import org.junit.After;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * Created by Charlie Black on 6/30/17.
 */
@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SpringBootTest(classes = {io.pivotal.gemfire.sample.app.config.ClientCacheConfiguration.class})
public class SimpleTest {
    @Resource
    private Region<Integer, Person> personRegion;

    private PersonBuilder personBuilder = new PersonBuilder();

    @Test
    public void AInsertABunch() {
        for (int i = 1; i < 100; i++) {
            personRegion.put(i, personBuilder.buildPerson(i));
        }
    }
    @Test
    @After
    public void BupdateABunch(){
        for (int i = 1; i < 100; i++) {
            Person person = personRegion.get(i);
            person.setFirstName(UUID.randomUUID().toString());
            person.setLastName(UUID.randomUUID().toString());
            personRegion.put(i, person);
        }
    }
}


