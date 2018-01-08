# scout-boot-kotlin
Demo using Spring Boot with Eclipse Scout with Kotlin language.

This repository is a kotlin version of the archetypes proposed by Eclipse scout.
- [ScoutBoot minimal archetype](https://github.com/BSI-Business-Systems-Integration-AG/ScoutBoot)
- [ScoutBoot standard archetype](https://github.com/BSI-Business-Systems-Integration-AG/ScoutBoot)

### Information
[Kotlin](https://kotlinlang.org/)  
[Eclipse Scout](https://www.eclipse.org/scout/)  
[Spring Boot](https://projects.spring.io/spring-boot/)  
[Spring Boot with Kotlin](https://spring.io/blog/2016/02/15/developing-spring-boot-applications-with-kotlin)  
[Eclipse Scout with Spring Boot](https://github.com/BSI-Business-Systems-Integration-AG/ScoutBoot)  

### Prerequistes
- Java 8
- Maven 3

### Usage
You must first install in your maven repository the Scout Boot project 
```
git clone https://github.com/BSI-Business-Systems-Integration-AG/ScoutBoot.git
cd ScoutBoot
mvn clean install -Dmaven.test.skip
```

Then you can launch the hello world application
```
git clone https://github.com/slevy85/scout-boot-kotlin.git
cd scout-boot-kotlin
mvn clean install
java -jar target/scout-boot-kotlin*.jar 
```
Or the standard application 
```
git clone https://github.com/slevy85/scout-boot-kotlin.git
cd scout-boot-kotlin/standard
mvn clean install
java -jar target/standard*.jar 
```

Then visit http://localhost:8080.
