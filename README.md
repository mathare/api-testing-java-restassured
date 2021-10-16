[![Continuous Integration Status](https://github.com/mathare/api-testing-java-restassured/actions/workflows/ci.yml/badge.svg)](https://github.com/mathare/api-testing-java-restassured/actions)

# API Testing with Java & RestAssured

## Overview
This project provides an example for testing a RESTful API using Cucumber BDD feature files with step definitions written in Java and making use of the popular RestAssured library as an alternative to automated API testing with Postman. As such, it can be used to kickstart testing of other APIs with minimal changes to the project.

NB This project is not a complete implementation of a test suite for the target API but shows enough to act as a good template for other projects and shows examples of common query types. It is an example of how to structure an API test suite in Java but only a subset of the possible tests have been written. It follows the same structure as my [Java HTTP Client](https://github.com/mathare/api-testing-java-httpclient) project and tests the same API allowing for an easy comparison between two common API testing libraries in Java.

## Why Use RestAssured?
RestAssured is an open source Java-based library that provides a domain-specific language for writing powerful automated tests for RESTful APIs. Despite it being one of the most popular API testing libraries for Java this project is actually my first real foray into RestAssured outside of a couple of short introductory training courses.

Most RestAssured examples seem to heavily use the `given()`, `when()` and `then()` methods, which I find makes the steps more confusing when one is also using Cucumber BDD. Therefore I have opted to steer clear of using these methods as much as possible, which means my code may differ from the way more experienced RestAssured practitioners would do things. I have tried to make my RestAssured calls succint but clear and obvious as to what they do. 

### Comparison with HTTP Client
The project is a port of the [Java HTTP Client](https://github.com/mathare/api-testing-java-httpclient) but uses RestAssured rather than the native Java HTTP Client library. The API under test remains the same so the feature files are the same in both projects, which is exactly as it should be for BDD - the implementation shouldn't affect the features. Obviously the comparison data files (expected responses and schemas) are the same in both projects - they relate to the API rather than how it is tested. The bulk of the differences between this project and the HTTP Client implementation are in the RequestHelpers class as that is where the methods that interact with the API are defined. The changes to the step definitions themselves are relatively minor, and are mostly due to the different reponse types between the two libraries. As RestAssured has a dependency on Hamcrest I have switched the assertions in this project to Hamcrest rather than using JUnit so that things are done in the typical RestAssured style. I have also favoured RestAssured methods of creating JSON objects, removing the dependency on a couple of JSON libraries that were used in the HTTP Client project.

## API Under Test
The REST API being tested by this project is the [JSON Placeholder API](https://jsonplaceholder.typicode.com/), a simple third-party API that has endpoints suitable for demonstrating many key principles of automated API testing. It is often put forward as a suitable candiate for learning automated API testing, making it an excellent choice for this project, which itself is intended to help anyone looking to learn how to implement API testing using Cucumber and Java.

The JSON Placeholder API has six main endpoints:
* [/posts](https://jsonplaceholder.typicode.com/posts) - 100 posts
* [/comments](https://jsonplaceholder.typicode.com/comments) - 500 comments
* [/albums](https://jsonplaceholder.typicode.com/albums) - 100 albums
* [/photos](https://jsonplaceholder.typicode.com/photos) - 5000 photos
* [/todos](https://jsonplaceholder.typicode.com/todos) - 200 todos
* [/users](https://jsonplaceholder.typicode.com/users) - 10 users

Path parameters can be used to return a specific data object. For example,  a single post can be returned by specifying an ID path parameter e.g. [/posts/1](https://jsonplaceholder.typicode.com/posts/1). Alternatively, query parameters can be specified in the URI to filter the data e.g. [/posts?userId=1](https://jsonplaceholder.typicode.com/posts?userId=1) will return all posts created by the user with a userId of 1. Some of the API resources are related to one another e.g., posts have comments, albums have photos, users make posts etc. so URIs can have nested path parameters. For example, to get the comments associated with a single post one can make a request to [/posts/1/comments](https://jsonplaceholder.typicode.com/posts/1/comments).  

The main drawback of using this API is endpoints return static responses - there is no underlying database or backend storage. This means that when creating new data for a given endpoint (e.g. via a POST request to the [/posts](https://jsonplaceholder.typicode.com/posts) endpoint) a valid API response will be returned but no new data is actually created. When testing a real-world API one may send a POST request to create new data then send a GET request to ensure the new data has been created correctly but that won't work with this API. However, that doesn't significantly impact our testing and where there are knock-on effects I have tried to highlight these in the Cucumber feature files. 

On the plus side, the JSON Placeholder API is free to use and has no rate limits, unlike some other APIs that are put forward as suitable automated testing candidates.

Note, the API has no authorisation/authentication applied so that side of REST API testing is not covered in this project.

## Test Framework
As stated above, this project contains a Java test framework suitable for REST APIs and utilises Cucumber BDD. The use of Cucumber means the tests themselves are clean and clear, written in plain English, so they can be understood by anyone working with the project, including non-technical roles. Although this project is just an example of how to set up API testing in Java, in a real-life project the use of BDD is essential for collaboration between QAs, developers, and business roles (e.g. Product Owners, Business Analysts etc). Quality is everyone’s responsibility, which means the tests themselves need to be easily understood by all stakeholders.

### Tech Stack
As this is a Java project, build and dependency management is handled by Maven, so there is a `pom.xml` file defining the versions of the dependencies:
* Java v11
* Cucumber v6.11.0
* RestAssured v4.4.0

The code is written in Java and built using v11 of the JDK. There are more up-to-date JDK versions available  - Oracle is up to Java 17 at the time of writing. However, I used Amazon Coretto 11 (the latest LTS release of this popular OpenJDK build) as it is the distribution I am most used to.

The Cucumber version is the latest version at the time of writing (released May 2021).

I have used the latest RestAssured version (also released May 2021) available at the time of writing and as it has a dependency on Hamcrest I have used that as the assertion library. As RestAssured has built-in methods for schema validation, no further libraries are needed (unlike in the HTTP Client project).

### Project Structure
The project uses a standard structure and naming convention, incorporating the URL of the website under test, i.e. the test code is all stored under `src/test/java/com/typicode/jsonplaceholder`. Below that we have:
* `features`  - this folder contains the Cucumber `.feature` files, one per API endpoint. Separating out the tests for each endpoint into separate feature files makes it easier to extend the framework in the future. Each feature file is named after the endpoint it tests, e.g. `Albums.feature` contains the tests for the Albums endpoint. Most of the endpoints have only a single test but `Posts.feature` has been built up with an extensive set of tests to illustrate what can be tested.
* `helpers` - this package contains a single class: `RequestHelpers.java`. This class contains all the methods that send the HTTP requests, separating them out from the step definitions for clarity and easy re-use. You could argue this package would be better named `utils` or similar but that is all down to personal preference
* `steps` - a collection of classes containing the implementation of the steps from the Cucumber feature files. The main class is `CommonSteps.java` containing the implementation of the steps that would be used by more than one feature file, avoiding duplication of code across individual steps classes. There is also a `PostsSteps.java` class containing a small number of steps that are specific to the tests in the `Posts.feature` file. In reality, as the API test suite is built out these steps are likely to be genericised and moved to the `CommonSteps.java` class so they can easily be shared between features but I have chosen to show the use of common and specific test steps classes in this project.
* `TestRunner.java` - an empty test runner class, decorated with the annotations required to run Cucumber tests, including the `CucumberOptions` annotation which defines the location of the features and associated steps.

There is also a test resources folder `src/test/resources` containing a number of files against which responses can be verified. The folder is split as follows:
* `expectedResponses` - the expected reponse bodies of a number of requests are stored here as JSON files
* `schemas` -  JSON format schemas for each of the endpoints

### Running Tests
The tests are easy to run as they are bound to the Maven `test` goal so running the tests is as simple as executing `mvn test` within the directory in which the repo has been cloned. Alternatively, the empty `TestRunner` class can be executed using a JUnit runner within an IDE.

#### Test Reports
A report is generated for each test run, using the Cucumber `pretty` plugin to produce an HTML report called `cucumber-report.html` in the `target` folder. This is a simple report showing a summary of the test run (number of tests run, number passed/failed/skipped, duration, environment etc) above a breakdown of each individual feature file, showing the status of each scenario and the individual steps within that scenario, including a stack trace for failing steps. 

### CI Pipeline
This repo contains a CI pipeline implemented using [GitHub Actions](https://github.com/features/actions). Any push to the `main` branch or any pull request on the `main` branch will trigger the pipeline, which runs in a Linux VM on the cloud within GitHub. The pipeline consists of a single `run-tests` job which checks out the repo then runs the test suite via `mvn test`. Once the tests have finished, whether they pass or fail, a test report is uploaded as a GitHub artifact. At the end of the steps the environment tears itself down and produces a [status report](https://github.com/mathare/api-testing-java-restassured/actions). Each status report shows details of the test run, including logs for each step and a download link for the test report artifact.

In addition to the automated triggers above, the CI pipeline has a manual trigger actionable by clicking "Run workflow" on the [Continuous Integration](https://github.com/mathare/api-testing-java-restassured/actions/workflows/ci.yml) page. This allows the user to select the branch to run the pipeline on, so tests can be run on a branch without the need for a pull request. This option is only visible if you are the repo owner.