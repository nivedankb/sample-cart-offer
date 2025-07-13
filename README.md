# Prerequisities
JDK 11
Docker

# How bring the mockservice up
```bash
cd mockserver  
docker compose up  
```
the mocker server will start at port 1080

# How bring the service up
./mvnw clean install -DskipTests  

```bash
java -jar target/simple-springboot-app-0.0.1-SNAPSHOT.jar  
```

```bash
java --add-opens java.base/java.lang=ALL-UNNAMED -jar target/simple-springboot-app-0.0.1-SNAPSHOT.jar
```

The server will start at port 9001

# How to run the tests
```bash
./mvnw test  
```