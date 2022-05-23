# LDES Server

## How to use the LDES Server

The LDES Server is a configurable component that can be used to ingest, store, transform and (re-)publish an LDES.
We'll show you how to set up your own LDES Server both locally and via Docker using a mongo db for storage and a rest endpoint for ingestion and publication.
Afterwards, you can change storage, ingestion and publication options by plugging in other components.

### Locally (in IDE)

To start an LDES Server locally in your IDE, you'll need to add components for storage, ingestion and publication to your classpath.
In IntelliJ you can do this by updating your project structure.
Go to the `ldes-server-application` module and add jars for storage and publication.
Your project structure will look as follows: ![ClasspathDependencies](image/ClasspathDependencies.png)

Now you only need to provide the correct environment variables by overriding the placeholders in [application.yml](ldes-server-application/src/main/resources/application.yml).

And you can spin up your own LDES Server.

### Via Docker

Use the [Dockerfile](Dockerfile) to locally build an image of the LDES Server.
In the current image mongo db is used for storage and rest is used for ingestion and publication.
In the [docker-compose.yml](docker-compose.yml) we use a mongodb container so you do not need to update/configure anything.
Running the [docker-compose.yml](docker-compose.yml) allows to spin up your own LDES Server.