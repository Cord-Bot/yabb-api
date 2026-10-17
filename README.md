# Yabb-API

API Service required by YABB to store user birthdays.

# Self-Host Guide

> [!WARNING]
> This API service is meant to be run internally and accessible only by the bot, and as such, does not contain any form
of authentication. Do not make this service publicly available once deployed.

## Step 1: Set up the required services

| Service Type | Supported Variants |
|--------------|--------------------|
| `Database`   | PostgreSQL         |

## Step 2: Set up the required environment variables

| Variable      | Description                                            |
|---------------|--------------------------------------------------------|
| `DB_URL`      | Example: `jdbc:postgresql://<host>:<port>/<database>`  |
| `DB_USERNAME` | Database username                                      |
| `DB_PASSWORD` | Database password                                      |

## Step 3: Deploy the API service

> [!NOTE]
> Docker is the preferred way of running the service. However, if you wish to run the service directly,
check out the [help](/HELP.md) section. You will need JDK 25 to build from source and run the service.

It is recommended to use the compose stack provided in the bot repository as it bundles the bot
and the API service in the same network. However, if you wish to run the API separately, you
can do so via either using the following run command or a custom compose stack

### Docker Run

```shell
docker run -d \
  --name yabb-api \
  -e DB_URL="jdbc:postgresql://<host>:<port>/<database>" \
  -e DB_USERNAME="<username>" \
  -e DB_PASSWORD="<password>" \
  -p 8080:8080 \
  --health-cmd='curl -f http://localhost:9000/q/health || exit 1' \
  --health-interval=30s \
  --health-timeout=5s \
  --health-retries=3 \
  ghcr.io/yabb/yabb-api:latest
```

### Docker Compose

```yaml
services:
  api:
    container_name: yabb-api
    image: ghcr.io/yabb/yabb-api:latest
    environment:
      DB_URL: jdbc:postgresql://<host>:<port>/<database>
      DB_USERNAME: <username>
      DB_PASSWORD: <password>
      PORT: 8080
      MANAGEMENT_PORT: 9000
    healthcheck:
      test: [ "CMD", "curl", "-f", "http://localhost:9000/q/health" ]
      interval: 10s
      timeout: 5s
      retries: 3
```

# Health Check Endpoints

| Endpoint            | Description                            |
|---------------------|----------------------------------------|
| `/q/health/live`    | Application is running                 |
| `/q/health/ready`   | Application is ready to serve requests |
| `/q/health/started` | Application startup has completed      |
| `/q/health`         | Aggregated health status               |

By default, the health check interface will listen to port 9000. You can change this by supplying a custom value
in env variable `MANAGEMENT_PORT`.

# License

This API is licensed under the [AGPLv3](/LICENSE) license.