# Eureka! Clinical Analytics Service
[Georgia Clinical and Translational Science Alliance (Georgia CTSA)](http://www.georgiactsa.org), [Emory University](http://www.emory.edu), Atlanta, GA

## What does it do?
It provides backend services for managing phenotypes, cohorts and running phenotyping jobs.

Latest release: [![Latest release](https://maven-badges.herokuapp.com/maven-central/org.eurekaclinical/eurekaclinical-analytics-service/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.eurekaclinical/eurekaclinical-analytics-service)

## Version 2.0
Support the new job modes, and update dependencies.

## Version 1.0
This service is a refactoring of the eureka-services module of the eureka project. It replaces the eureka-services module. The current functionality is the same as in the last release of the eureka project.

## Build requirements
* [Oracle Java JDK 8](http://www.oracle.com/technetwork/java/javase/overview/index.html)
* [Maven 3.2.5 or greater](https://maven.apache.org)

## Runtime requirements
* [Oracle Java JRE 8](http://www.oracle.com/technetwork/java/javase/overview/index.html)
* [Tomcat 7](https://tomcat.apache.org)
* Also running
  * The [eurekaclinical-analytics-webapp](https://github.com/eurekaclinical/eurekaclinical-analytics-webapp) war
  * The [eurekaclinical-protempa-service](https://github.com/eurekaclinical/eurekaclinical-protempa-service) war
  * The [eurekaclinical-user-webapp](https://github.com/eurekaclinical/eurekaclinical-user-webapp) war
  * The [eurekaclinical-user-service](https://github.com/eurekaclinical/eurekaclinical-user-service) war
  * The [cas-server](https://github.com/eurekaclinical/cas) war

## REST APIs

### `/api/protected/destinations`
Manages job actions.

#### Role-based authorization
Must have `research` role.

#### Requires successful authentication
Yes

#### Destination object
Destinations are job actions that create a resource. They all have the following properties:

Properties:
* `id`: unique number identifying the cohort (set by the server on object creation, and required thereafter).
* `type`: always must have value `COHORT`.
* `name`: required unique name of the cohort.
* `description`: an optional description of the cohort.
* `links`: an array of Link objects that point to resources related to the cohort see Link object above.
* `ownerUserId`: required username string of the owning user.
* `read`: required boolean indicating whether the user may read this object.
* `write`: required boolean indicating whether the user may update this object.
* `execute`: required boolean indicating whether the user may use this cohort specification as an action.
* `createdAt`: timestamp, in milliseconds since the epoch, indicating when this cohort specification was created; populated server-side.
* `updatedAt`: timestamp, in milliseconds since the epoch, indicating when this cohort specification was updated; populated server-side.
* `getStatisticsSupported`: required boolean indicating whether the resource created by a job executing this action supports getting statistics.
* `jobConceptListSupported`: required boolean indicating whether a job executing this action has a concept/phenotype list.
* `requiredConcepts`: any concepts or phenotypes that must be in the concept list.

#### CohortDestination object
Creates a patient set containing only patients who match the specified criteria.

Properties:
* `cohort`: a required Cohort object (see below).

#### Cohort object
A specification of a patient cohort in terms of concepts and phenotypes.

Properties:
* `id`: unique numerical id of the cohort (set by the server on object creation, and required thereafter).
* `node`: required Literal or BinaryOperator object (see below). Use a Literal object if the cohort is defined by a single concept or phenotype. If the cohort is defined by multiple concepts or phenotypes, use a chain of BinaryOperator objects ending with a Literal object.

#### BinaryOperator object
`ANDs` two nodes together.

Properties:
`leftNode`: required Literal object.
`op`: the operator, always `AND`.
`rightNode`: required BinaryOperator or Literal object.

#### Literal object
Represents a concept or phenotype included in a cohort definition.

Properties:
* `name`: required unique key of the concept or phenotype.
* `start`: always `null`.
* `finish`: always `null`.

#### I2b2Destination object
Populates an i2b2 data warehouse.

Properties:
No additional properties

#### Neo4jDestination object
Populates a Neo4j database.

Properties:
No additional properties.

#### Calls
Uses status codes as specified in the [Eureka! Clinical microservice specification](https://github.com/eurekaclinical/dev-wiki/wiki/Eureka%21-Clinical-microservice-specification).

##### GET `/api/protected/destinations[?type=[I2B2,COHORT,PATIENT_SET_SENDER]`
Gets all data destinations visible to the current user.  Optionally, filter the returned destinations by type:
* `I2B2`: i2b2 database destination.
* `COHORT`: Cohort specified in the cohorts screens.
* `PATIENT_SET_SENDER`: patient set sender.

###### Example:
URL: https://localhost:8443/eurekaclinical-analytics-service/api/protected/destinations?type=I2B2

Returns:
```
[
  { "type":"I2B2",
    "id":1,
    "name":"Load into i2b2 on localhost with Eureka ontology",
    "description":null,
    "phenotypeFields":null,
    "links[],
    "ownerUserId":1,
    "read":true,
    "write":true,
    "execute":true,
    "getStatisticsSupported":true,
    "jobConceptListSupported":true,
    "requiredConcepts":["Encounter"],
    "created_at":1430774820000,
    "updated_at":1430774820000 }
]
```

##### GET `/api/protected/destinations/{id}`
Gets the data destination with the specified unique name (id), if it is visible to the current user.

##### POST `/api/protected/destinations`
Create a new data destination, returning a URI representing the created destination object.

##### PUT `/api/protected/destinations`
Updates an existing data destination
Returns nothing.

##### DELETE `/api/protected/destinations/{id}`
Deletes the destination with the specified unique numerical id. Returns nothing.

### `/api/protected/sourceconfig`
Manages data source configurations.

#### Role-based authorization
Must have `researcher` role.

#### Requires successful authentication
Yes

#### SourceConfig object
Fully specifies a source configuration.

Properties:
* `id`: unique id number of the source config (set by the server on object creation, and required thereafter).
* `displayName`: optional display name of the source config for display in user interfaces.
* `ownerUsername`: required username of the owning user.
* `dataSourceBackends`: an array representing sources of data:
  * `id`: the id string of the data source backend.
  * `options`: an array of the parameters to set:
    * `name`: the unique name of the parameter.
    * `displayName`: optional display name of the parameter for display in user interfaces.
    * `required`: required boolean indicating whether the parameter must have a non-null value.
    * `propertyType`: required data type of the parameter, one of:
      * `STRING`
      * `BOOLEAN`
      * `INTEGER`
      * `LONG`
      * `FLOAT`
      * `DOUBLE`
      * `CHARACTER`
      * `STRING_ARRAY`
      * `DOUBLE_ARRAY`
      * `FLOAT_ARRAY`
      * `INTEGER_ARRAY`
      * `LONG_ARRAY`
      * `BOOLEAN_ARRAY`
      * `CHARACTER_ARRAY`
    * `prompt`: required boolean indicating whether the user should be prompted for a value for this parameter.
    * `value`: the value of the parameter.
* `knowledgeSourceBackends`: an array representing sources of concepts:
  * `id`: the id string of the knowledge source backend.
  * `displayName`: optional display name of the knowledge source backend for display in user interfaces.
  * `options`: an array of parameters to set:
    * `name`: required unique name of the parameter.
    * `displayName`: optional display name of the parameter for display in user interfaces.
    * `required`: required boolean indicating whether the parameter must have a non-null value.
    * `propertyType`: required data type of the parameter, one of:
      * `STRING`
      * `BOOLEAN`
      * `INTEGER`
      * `LONG`
      * `FLOAT`
      * `DOUBLE`
      * `CHARACTER`
      * `STRING_ARRAY`
      * `DOUBLE_ARRAY`
      * `FLOAT_ARRAY`
      * `INTEGER_ARRAY`
      * `LONG_ARRAY`
      * `BOOLEAN_ARRAY`
      * `CHARACTER_ARRAY`
    * `prompt`: required boolean indicating whether the user should be prompted for a value for this parameter.
    * `value`: the value of the parameter.
* `algorithmSourceBackends`: array representing sources of value processing algorithms; currently read-only.
  * `id`: the id string of the algorithm source backend.
  * `displayName`: optional display name of the algorithm source backend for display in user interfaces.
  * `options`: an array of parameters to set:
    * `name`: required unique name of the parameter.
    * `displayName`: optional display name of the parameter for display in user interfaces.
    * `required`: required boolean indicating whether the parameter must have a non-null value.
    * `propertyType`: required data type of the parameter, one of:
      * `STRING`
      * `BOOLEAN`
      * `INTEGER`
      * `LONG`
      * `FLOAT`
      * `DOUBLE`
      * `CHARACTER`
      * `STRING_ARRAY`
      * `DOUBLE_ARRAY`
      * `FLOAT_ARRAY`
      * `INTEGER_ARRAY`
      * `LONG_ARRAY`
      * `BOOLEAN_ARRAY`
      * `CHARACTER_ARRAY`
    * `prompt`: required boolean indicating whether the user should be prompted for a value for this parameter.
    * `value`: the value of the parameter.
* `read`: required boolean indicating whether the current user can read this source config.
* `write`: required boolean indicating whether the current user can update this source config.
* `execute`: required boolean indicating whether the current user can read data and concepts from this source config.

#### Calls
Uses status codes as specified in the [Eureka! Clinical microservice specification](https://github.com/eurekaclinical/dev-wiki/wiki/Eureka%21-Clinical-microservice-specification).

##### GET /api/protected/sourceconfig
Gets a list of the available source configurations.

##### GET /api/protected/sourceconfig/{id}
Returns the source configuration with the specified numerical unique id.

##### GET /api/protected/sourceconfig/parameters/list
Gets a list of the available source configurations' parameter names and values.

###### Example:
URL: https://localhost:8443/eurekaclinical-analytics-service/api/protected/sourceconfig/parameters/list

Returns:
```
[
  { "id":"Spreadsheet",
    "name":"Spreadsheet",
    "uploads":[
      { "name":"Eureka Spreadsheet Data Source Backend",
        "sourceId":null,
        "acceptedMimetypes":["application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"],
        "sampleUrl":"../docs/sample.xlsx",
        "required":true}
    ]
  }
]
```

##### GET /api/protected/sourceconfig/parameters/{id}
Returns the parameter names and values for the source configuration with the specified numerical unique id.

### `/api/protected/concepts`
System concepts provided by the system.

#### Role-based authorization
Must have `researcher` role.

#### Requires successful authentication
Yes

#### Concept object
Representation of concepts provided by the system. They are read-only.

Properties:
* `id`: always `null`.
* `key`: required unique name of the concept.
* `userId`: always `null`.
* `description`: optional description of the concept.
* `displayName`: optional user-visible name for the concept.
* `inSystem`: required boolean indicating whether it is a concept provided by the system. Always `true`.
* `created`: required timestamp, in milliseconds since the epoch, indicating when the concept was created (set by the server).
* `lastModified`: timestamp, in milliseconds since the epoch, indicating when the concept was last modified (set by the server).
* `summarized`: read-only boolean indicating whether the concept was retrieved with the `summarize` query parameter set to `true`.
* `type`: the type of concept, always `SYSTEM`.
* `internalNode`: read-only boolean indicating whether the concept has any children.
* `systemType`: required, one of `CONSTANT`, `EVENT`, `PRIMITIVE_PARAMETER`, `LOW_LEVEL_ABSTRACTION`, `COMPOUND_LOW_LEVEL_ABSTRACTION`, `HIGH_LEVEL_ABSTRACTION`, `SLICE_ABSTRACTION`, `SEQUENTIAL_TEMPORAL_PATTERN_ABSTRACTION`, `CONTEXT`.
* `children`: an array of Concept objects.
* `isParent`: whether this concept has any children.
* `properties`: array of property names.

#### Calls
Uses status codes as specified in the [Eureka! Clinical microservice specification](https://github.com/eurekaclinical/dev-wiki/wiki/Eureka%21-Clinical-microservice-specification).

##### GET `/api/protected/concepts[?summarize=true]`
Returns the top-level system concepts accessible by the current user. Optionally, return each concept in a summarized form suitable for listing.

###### Example:
URL: https://localhost:8443/eurekaclinical-analytics-service/api/protected/concepts

Returns: 
```
[
  { "type":"SYSTEM",
    "id":null,
    "key":"Patient",
    "userId":null,
    "description":"",
    "displayName":"Patient",
    "inSystem":true,
    "created":null,
    "lastModified":null,
    "summarized":true,
    "internalNode":false,
    "systemType":"CONSTANT",
    "children":null,
    "properties":["patientId"],
    "parent":false},
  { "type":"SYSTEM",
    "id":null,
    "key":"PatientDetails",
    "userId":null,
    "description":"",
    "displayName":"Patient Details",
    "inSystem":true,
    "created":null,
    "lastModified":null,
    "summarized":true,
    "internalNode":false,
    "systemType":"CONSTANT",
    "children":null,
    "properties": [
      "ageInYears",
      "dateOfBirth",
      "dateOfDeath",
      "gender",
      "language",
      "maritalStatus",
      "race",
      "vitalStatus",
      "patientId"
    ],
    "parent":false},
  { "type":"SYSTEM",
    "id":null,
    "key":"Encounter",
    "userId":null,
    "description":"",
    "displayName":"Encounter",
    "inSystem":true,
    "created":null,
    "lastModified":null,
    "summarized":true,
    "internalNode":false,
    "systemType":"EVENT",
    "children":null,
    "properties": ["age","type","encounterId"],
    "parent":false},
  { "type":"SYSTEM",
    "id":null,
    "key":"VitalSign",
    "userId":null,
    "description":"",
    "displayName":"Vital Sign",
    "inSystem":true,
    "created":null,
    "lastModified":null,
    "summarized":true,
    "internalNode":true,
    "systemType":"PRIMITIVE_PARAMETER",
    "children":null,
    "properties":[],
    "parent":true}
]
```

##### POST `/api/protected/concepts[?summarize=true]`
Retrieves the concepts enumerated in the form body. Optionally, returns each concept in a summarized form suitable for listing.

Form parameters:
* key: The keys of the system concepts of interest (optional). If omitted, the empty list is returned.
* summarize: yes or no if you want returned concepts in a summarized form suitable for listing (optional).

##### GET `/api/protected/concepts/{key}`
Gets the requested system concept with the specified key or the 404 (NOT FOUND) status code if no such system concept exists and is accessible to the current user. 

###### Example:
URL: https://localhost:8443/eurekaclinical-analytics-service/api/protected/concepts/Patient

Returns:
```
{ "type":"SYSTEM",
  "id":null,
  "key":"Patient",
  "userId":null,
  "description":"",
  "displayName":"Patient",
  "inSystem":true,
  "created":null,
  "lastModified":null,
  "summarized":false,
  "internalNode":false,
  "systemType":"CONSTANT",
  "children":[],
  "properties":["patientId"],
  "parent":false }
```

##### GET `/api/protected/concepts/propsearch/{searchKey}`
Gets the concepts with the specified text in their display name, case insensitive.

###### Example:
URL: https://localhost:8443/eurekaclinical-analytics-service/api/protected/concepts/propsearch/ICD9%20Procedure%20Codes

Returns: 
```
[
  { "type":"SYSTEM",
    "id":null,
    "key":"ICD9:Procedures",
    "userId":null,
    "description":"",
    "displayName":"ICD9 Procedure Codes",
    "inSystem":true,
    "created":null,
    "lastModified":null,
    "summarized":true,
    "internalNode":true,
    "systemType":"EVENT",
    "children":null,
    "properties":[],
    "parent":true}
]
```

##### GET `/api/protected/concepts/search/{searchKey}`
Gets an array of the keys of the system concepts with the specified text in their display name, case insensitive.

###### Example:
URL: https://localhost:8443/eurekaclinical-analytics-service/api/protected/concepts/search/ICD10:Diagnoses

Returns:
```
["ICD10:Diagnoses", "ICD10:S00-T88", "ICD10:S00-S09", ...]
```

## Building it
See the parent project's [README.md](https://github.com/eurekaclinical/eureka/blob/master/README.md).

## Performing system tests
See the parent project's [README.md](https://github.com/eurekaclinical/eureka/blob/master/README.md).

## Installation
### Configuration
This webapp is configured using a properties file located at `/etc/eureka/application.properties`. It supports the following properties:
* `cas.url`: https://hostname.of.casserver:port/cas-server
* `eureka.common.callbackserver`: https://hostname:port
* `eureka.common.demomode`: true or false depending on whether to act like a demonstration; default is false.
* `eureka.common.ephiprohibited`: true or false depending on whether to display that managing ePHI is prohibited; default is true.
* `eureka.support.uri`: URI link for contacting support. Could be http, https, or mailto.
* `eureka.support.uri.name`: Display name of the URI link for contacting support.
* `eureka.services.url`: URL of the server running the services layer; default is https://localhost:8443/eurekaclinical-analytics-service.
* `eureka.services.callbackserver`: URL of the server running the services layer; default is https://localhost:8443.
* `eureka.services.defaultprops`: concept subtrees to show in the concept tree: default is Patient PatientDetails Encounter  ICD9:Diagnoses ICD9:Procedures ICD10:Diagnoses ICD10:Procedures LAB:LabTest MED:medications VitalSign

A Tomcat restart is required to detect any changes to the configuration file.

### WAR installation
1) Stop Tomcat.
2) Remove any old copies of the unpacked war from Tomcat's webapps directory.
3) Copy the warfile into the Tomcat webapps directory, renaming it to remove the version if necessary. For example, rename `eurekaclinical-analytics-service-1.0.war` to `eurekaclinical-analytics-service.war`.
4) Start Tomcat.

## Maven dependency
```
<dependency>
    <groupId>org.eurekaclinical</groupId>
    <artifactId>eurekaclinical-analytics-service</artifactId>
    <version>version</version>
</dependency>
```

## Developer documentation
* [Javadoc for latest development release](http://javadoc.io/doc/org.eurekaclinical/eurekaclinical-analytics-service) [![Javadocs](http://javadoc.io/badge/org.eurekaclinical/eurekaclinical-analytics-service.svg)](http://javadoc.io/doc/org.eurekaclinical/eurekaclinical-analytics-service)

## Getting help
Feel free to contact us at help@eurekaclinical.org.

