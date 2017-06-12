# Apache Geode / Pivotal GemFire - Blue-Green Deploy
Demo to show how zero-downtime/blue-green deployment can be achieved with Gemfire as the backend store/persistence layer.

This example shows migration from blue-app (old version) to green-app (new version)

When migrating from the blue-app to green-app, the Person class undergoes a change. The original version uses a single attribute (“name”) to capture a person’s full name. The new version of the application instead captures a person’s first and last name separately, using the attributes  firstName and lastName.
For example: 'name:John Doe' becomes 'firstName:John' and 'lastName:Doe' in the new version.

## Prerequisites
Gemfire with `gfsh` on env PATH  
Apache Maven to run the Client Spring Applications  

We will be using a CacheWriter and server-side function for the migration.
Read [here](https://docs.google.com/document/d/1zmsoOjleRIi1Ls14mfi_SFK1v5oDtFt0PeQWsRim0gY) for more information.

## Running the Demo:
Start the Gemfire cluster by running the `./startCluster.sh` script from the /server/scripts directory. Run the command from the folder itself.

Start both applications (app-blue/, app-green/) from their respective folders using:
<br/>
`mvn spring-boot:run`

The /functions/demo-run.sh shell-script does a step by step run-through of the demo (with comments explaining each step). Run it from the 'functions/' folder:
`./demo-run.sh`

Use the script /server/scripts/stopCluster.sh to stop the Gemfire Cluster and clean directory. This script also removes the /server and /locator folders from the directory:
Run it from the /server/scripts folder.
<br/>
`./stopCluster.sh`

<img src="screenshots/process.gif?raw=true">

<br/>
