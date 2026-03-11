# SFL Billing Report Microservice

## Development
To start your application in the dev profile, run:
```
./gradlew
```

## Building for production
### Packaging as jar

To build the final jar and optimize the spring application for production, run:
```
./gradlew -Pprod clean bootJar
```

To ensure everything worked, run:
```
java -jar build/libs/*.jar
```

### Packaging as war
To package your application as a war in order to deploy it to an application server, run:
```
./gradlew -Pprod -Pwar clean bootWar
```

To generate two package war
```
./gradlew clean build -Pprod -Pwar
```

## Testing
To launch your application's tests, run:
```
./gradlew test integrationTest
```

To run a test file:
```
./gradlew test integrationTest --tests "bo.com.luminia.sflbilling.msreport.web.rest.ExampleIT"
```

# Docker
Build docker image:
```
./gradlew bootJar jibDockerBuild
```
Upload image to **registry.gitlab.com** with default tags **version and latest**:
```
./gradlew jib -Djib.to.auth.username=$CI_REGISTRY_USER -Djib.to.auth.password=$CI_REGISTRY_TOKEN
```
- **CI_REGISTRY_USER**: Is your gitlab.com username
- **CI_REGISTRY_TOKEN**: They generate it in gitlab.com with registry permissions
