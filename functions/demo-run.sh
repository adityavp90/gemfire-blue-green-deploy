#! /bin/bash

mvn package -DskipTests | grep BUILD
ls -l | grep functions

printf "\n"
printf "### Adding data from blue-app (localhost:8080) - Will store objects which have just the name field\n"
curl -X PUT localhost:8080/person/1
curl -X PUT localhost:8080/person/2

printf "\n\n"
printf "### Deploying Jar to Gemfire cluster and assigning cache-writer to region /Person\n"
export PATH=/Users/apadhye/Pivotal/GemFire/pivotal-gemfire-9.0.1/bin:/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin
gfsh <<EOF
connect --locator=localhost[41111]
deploy --jar=/Users/apadhye/Pivotal/GemFire/gemfire-blue-green-deploy/functions/target/functions-1.0-SNAPSHOT.jar
alter region --name=/Person --cache-writer=io.pivotal.gemfire.sample.cachewriter.AttributeSyncCacheWriter
EOF

printf "### Data in Gemfire:\n"
mvn -Dtest=EntryListingTest test | grep PDX

printf "### Since the cacheWriter is active, any new data added using the blue-app will have all 3 fields - name,firstName,lastName\n"
curl -X PUT localhost:8080/person/3
curl -X PUT localhost:8080/person/4

echo ""
mvn -Dtest=EntryListingTest test | grep PDX

printf "### Executing Function to migrate data already present in Gemfire to have all 3 fields\n"
mvn -Dtest=SplitAttributeTest test | grep Function

echo ""
mvn -Dtest=EntryListingTest test | grep PDX

printf "### At this point we can switch to the green-app. Any new data added from the green-app will have all 3 fields as well as the CacheWriter is still active.\n"
curl -X PUT localhost:8081/person/5
curl -X PUT localhost:8081/person/6

echo "" 
mvn -Dtest=EntryListingTest test | grep PDX
echo ""

printf "### Now that the migration is complete, we can get rid of the cache-writer. This can be achieved by unregistering the function it's invoking\n"
gfsh <<EOF
connect --locator=localhost[41111]
destroy function --id=EventModifierFunction --member=server1
EOF

printf "### Invoke function to remove the name field from all objects in the database.\n"
mvn -Dtest=RemoveNameAttributeTest test | grep Function
mvn -Dtest=EntryListingTest test | grep PDX
echo ""

printf "### Any new data added or modified from the green-app will now only have the 2 fields - firstName and lastName\n"
curl -X PUT localhost:8081/person/1
curl -X PUT localhost:8081/person/7

mvn -Dtest=EntryListingTest test | grep PDX
