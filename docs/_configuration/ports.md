---
layout: default
title: Binding the server ports
nav_order: 6
---

# Configuring the available ports of de LDES server

The LDES server has multiple APIs that each serve a distinct function and will be used by different people.
To enable separate levels of protection for each of these APIs, the fetch, ingest and admin endpoints can each be configured to use a separate port.
The properties to bind to these ports are optional. This means that if no port is specified, the API will be available on the default server port.
Any and all of these port can share the same port, whether by sharing the default server port or specifying the same port number in the configuration.

## Example

```yaml
  ldes-server:
    ingest:
      port: 8089
    fetch:
      port: 8088
    admin:
      port: 8087
  ```

## The Admin API and swagger

All swagger endpoints are reachable under the admin port.

{: .note }
When the admin port is separate of the ingest and fetch ports, the try it out option will not work for the ingest and fetch endpoints.