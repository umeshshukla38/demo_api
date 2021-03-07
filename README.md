# README #

### What is this repository for? ###

* This repository for rest-assured demo which is related api to api automation tests.

### How do I get set up? ###

* First install and configure java, configure home path for java.
* Clone repository in local system and do maven clean install
* Java 8 Or 11, Rest-Assured Api's
* Database configuration -> No

### Contribution guidelines ###

* Writing tests
* Code review
* Other guidelines

### Execution Queries
* Single Test : mvn clean install -Denv=local -Ddevice_type=0 -Dtest=Users.java -test
* Test Suites : mvn clean install -Denv=prod -Ddevice_type=0 -DsuiteXmlFile=demo.xml