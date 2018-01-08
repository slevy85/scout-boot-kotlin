# Develop and run Standard Application

## Develop with Eclipse IDE

### Import into Eclipse IDE

After creation of the project using the Apache archetype:
* Change into the project's main directory
* In the shell prepare the project for usage with the Eclipse IDE

  mvn eclipse:eclipse

* Start Eclipse IDE with empty workspace
* Import this project as an existing Maven project

### Run the application from the Eclipse IDE

Once the project is successfully imported in the Eclipse IDE:
* In the package explorer: Select scout-boot-kotlin-standard.$artifactId.Application.java
* Use context menu "Run As Java Application" 
* Open a browser of your choice
* Enter the following URL in the address bar http://localhost:8080
* Login with user 'root' and password 'eclipse' (or alice with password test)
* Create some tasks in the application
* Check that you can access these tasks using the application's REST API

  http://localhost:8080/api/tasks/inbox
  http://localhost:8080/api/tasks/own
  http://localhost:8080/api/tasks/today
  http://localhost:8080/api/tasks/all


# Build with Maven and execute on Command Line

After creation of the project using the Apache archetype:
* Change into the project's main directory
* In the shell build and run the application using the following commands

  mvn clean package
  java -jar target/standard-*.jar

## Create Docker Image and execute as Container

After creation of the project using the Apache archetype:
* Change into the project's main directory
* In the shell build and run the application using the following commands

  mvn clean install docker:build

* The docker image is built according to file 'Dockerfile' in the projects main directory
* Start the container and check it's log  

  docker run -p 8080:8080 -d scout-boot-kotlin-standard.standard:1.0-SNAPSHOT
  docker ps
  docker logs -f <container-id>

* Find out the container's IP address (assuming that your machine is named 'default')

  docker-machine ip default

* Open your browser at URL http://<ip>:8080

## Run as Multi-Container Application with PostreSQL DB

After creation of the project using the Apache archetype:
* Create the app docker image as described in the previous section
* Use docker-compose with the commands provided below

  docker-compose up -d
  docker-compose ps
  docker volume ls

* The multi-conainer app is configured according to the description in file 'docker-compose.yaml' 
* To stop/(re)start or to remove the multi-container app use the following commands.

  docker-compose stop
  docker-compose start
  docker-compose rm
  docker volume rm standard_data

To access the running database attach to the database container. To attach you need to get the database container id first.

  docker exec -it standard_db bash

Inside the container start the psql client

  psql -h localhost -p 5432 -U postgres
  \l
  \c postgres
  \d
  select id, first_name, last_name from user_entity;
  
The last command should display the following info

    id   | first_name | last_name
  -------+------------+-----------
   root  | Root       |
   alice | Alice      |
   bob   | Bob        |
  (3 rows)

To exit psql and the docker container type

  \q
  exit