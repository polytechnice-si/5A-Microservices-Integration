# Citizen Registry Document API

## Network

  - Assumes a MongoDB database available on `tcs-database:21017`;
  - Receives `POST` request on the `tcs-service-document/registry` endpoint;
  - produces and consumes `application/json` data only;
  - answers `200` if everything went well, `400` elsewhere.

## Operations 

The services follows a document approach, and handle the following events:

  - `REGISTER`: registers a citizen;
  - `RETRIEVE`: get a citizen based on his/her social security number.
  - `DELETE`: deletes a citizen;
  - `LIST`: lists citizens with matching a given regular expression;
  - `DUMP`: lists all citizens;
  - `PURGE`: delete the contents of the registry (use with caution);


### Registering a citizen

```json
{
  "event": "REGISTER",
  "citizen": {
    "last_name": "Doe",
    "first_name": "John",
    "ssn": "1234567890",
    "zip_code": "06543",
    "address": "nowhere, middle of",
    "birth_year": "1970"
  }
}
```

### Retrieve a given citizen

```json
{
  "event": "RETRIEVE",
  "ssn": "1234567890"
}
```


### Remove a citizen

```json
{
  "event": "DELETE",
  "ssn": "1234567890"
}
```

### Find citizens with a given name

```json
{
  "event": "LIST",
  "filter": "D.*"
}
```

### Get all citizens registered

```json
{
  "event": "DUMP"
}
```

### Purge the database (remove all contents!)

```json
{
  "event": "PURGE",
  "use_with": "caution"
}
```