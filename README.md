# simplejpa

Helidon MP application that uses the dbclient API with an oracle database.

## Setting up db

- Start your local oracle db with `docker-compose up -d`
- connect using Sql Developer: connect as username: sys, password: password, role: sysdba, sid: ORCLCDB
- in Sql Developer, run the following to create `pokemon` user:
```sql
alter session set "_ORACLE_SCRIPT"=true;
CREATE USER fhir IDENTIFIED BY password;
GRANT CONNECT, RESOURCE, DBA TO fhir;
-- for now all privilages
GRANT ALL PRIVILEGES TO fhir;
```
- to connect as `pokemon` with sql developer, use `default` as role, username: pokemon, password: password


## Issues

#### TODO: docker-compose should create the user on docker startup.

#### `mvn package` failure

unfortunately `mvn compile` overwrites the files copied in the `mvn process-resources` phase. So to build do: `mvn clean compile package` so that the second `compile` in package will not do anything, and items in META-INF will stay intact.

## Code Style

We are using google style. In VSCode add this to your `settings.json` file:
```json
    "java.format.settings.url": "https://raw.githubusercontent.com/google/styleguide/gh-pages/eclipse-java-google-style.xml",
    "java.format.settings.profile": "GoogleStyle",
```

## Development and Debugging

Once you install `helidon` cli, you can use it to run a dev loop:
`helidon dev`, or to run with a debug socket `helidon dev --app-debug`
## Build and run


This example requires a database.
Instructions for H2 can be found here: https://www.h2database.com/html/cheatSheet.html


With JDK17+
```bash
mvn package
java -jar target/simplejpa.jar
```

## Exercise the application
```
curl -X GET http://localhost:8081/simple-greet
{"message":"Hello World!"}
```

```
curl -X GET http://localhost:8081/pokemon
[{"id":1,"type":12,"name":"Bulbasaur"}, ...]

curl -X GET http://localhost:8081/type
[{"id":1,"name":"Normal"}, ...]

curl -H "Content-Type: application/json" --request POST --data '{"id":100, "type":1, "name":"Test"}' http://localhost:8081/pokemon
```


## Try metrics

```
# Prometheus Format
curl -s -X GET http://localhost:8081/metrics
# TYPE base:gc_g1_young_generation_count gauge
. . .

# JSON Format
curl -H 'Accept: application/json' -X GET http://localhost:8081/metrics
{"base":...
. . .
```



## Try health

```
curl -s -X GET http://localhost:8081/health
{"outcome":"UP",...

```



## Building a Native Image

The generation of native binaries requires an installation of GraalVM 22.1.0+. 

In order to produce a native binary, you must run the H2 Database as a separate process
and use a network connection for access. The simplest way to do this is by starting a Docker
container as follows:

```
docker run -d -p 1521:1521 -p 81:81 -e H2_OPTIONS='-ifNotExists' --name=h2 oscarfonts/h2
```

The resulting container will listen to port 1521 for network connections.
Switch the `url` in `application.yaml` :

```
javax.sql.DataSource.test.dataSource.url=jdbc:h2:tcp://localhost:1521/test
```

Next, uncomment the following dependency in your project's pom file:

```
<dependency>
    <groupId>io.helidon.integrations.db</groupId>
    <artifactId>h2</artifactId>
</dependency>
```

With all these changes, re-build your project and verify that all tests are passing.
Finally, you can build a native binary using Maven as follows:

```
mvn -Pnative-image install -DskipTests
```

The generation of the executable binary may take a few minutes to complete depending on
your hardware and operating system. When completed, the executable file will be available
under the `target` directory and be named after the artifact ID you have chosen during the
project generation phase.



## Building the Docker Image

```
docker build -t simplejpa .
```

## Running the Docker Image

```
docker run --rm -p 8081:8081 simplejpa:latest
```

Exercise the application as described above.
                                

## Building a Custom Runtime Image

Build the custom runtime image using the jlink image profile:

```
mvn package -Pjlink-image
```

This uses the helidon-maven-plugin to perform the custom image generation.
After the build completes it will report some statistics about the build including the reduction in image size.

The target/simplejpa-jri directory is a self contained custom image of your application. It contains your application,
its runtime dependencies and the JDK modules it depends on. You can start your application using the provide start script:

```
./target/simplejpa-jri/bin/start
```

Class Data Sharing (CDS) Archive
Also included in the custom image is a Class Data Sharing (CDS) archive that improves your application’s startup
performance and in-memory footprint. You can learn more about Class Data Sharing in the JDK documentation.

The CDS archive increases your image size to get these performance optimizations. It can be of significant size (tens of MB).
The size of the CDS archive is reported at the end of the build output.

If you’d rather have a smaller image size (with a slightly increased startup time) you can skip the creation of the CDS
archive by executing your build like this:

```
mvn package -Pjlink-image -Djlink.image.addClassDataSharingArchive=false
```

For more information on available configuration options see the helidon-maven-plugin documentation.
