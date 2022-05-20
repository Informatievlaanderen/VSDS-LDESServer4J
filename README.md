# LDES Server

## How to use the LDES Server

The LDES Server is a configurable component that can be used to ingest, store, transform and (re-)publish an LDES.
We'll show you how to set up your own LDES Server both locally and via Docker using a mongo db for storage and a rest endpoint for publication.
Afterwards, you can change storage and publication options by plugging in other components.

### Locally (in IDE)

To start an LDES Server locally in your IDE, you'll need to add components for storage and publication to your classpath.
In IntelliJ you can do this by updating your project structure.
Go to the `ldes-server-application` module and add jars for storage and publication.
Your project structure will look as follows: ![ClasspathDependencies](image/ClasspathDependencies.png)

Now you only need to provide the correct environment variables by overriding the placeholders in [application.yml](ldes-server-application/src/main/resources/application.yml).

And you can spin up your own LDES Server.

### Via Docker

Use the [Dockerfile](Dockerfile) to locally build an image of the LDES Server.
In the current image mongo db is used for storage and rest is used for publication.
Update the environment variables in the config file [config.env](config.env) and use the [docker-compose.yml](docker-compose.yml) to spin up your own LDES Server.