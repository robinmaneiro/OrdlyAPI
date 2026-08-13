# Ordly API

REST API for the Ordly app. Serves the menu, manages baskets, and handles auth for both guests and registered users. Built with Spring Boot and Kotlin.

## Architecture

Package-by-feature. Each feature is a self-contained slice with its own controller, service, and models:

```
auth        registration, login, JWT issuing + refresh
guest       anonymous guest sessions and their tokens
account     user profile (/users/me)
menu        categories and products
bag         the basket: items, quantities, guest→user merge
security    JWT service, auth filter, Spring Security config, BCrypt
database    shared documents + repositories (users, tokens, sessions)
util        request validation and error handling
```

Controllers stay thin. They validate input and delegate, while the services hold the logic and are the layer that's unit tested.

Auth has two token families, guest and authenticated, each with a short-lived access token and a long-lived refresh token. The token type and role travel as JWT claims and are checked by a request filter. A basket is created the moment a guest session starts, so someone can add items before ever signing up. When that guest later logs in, their basket is merged into the account's so nothing in the cart is lost.

## Tech stack

- Kotlin + Spring Boot 3.5
- Spring Web for the REST layer
- Spring Data MongoDB (MongoDB Atlas)
- Spring Security
- JJWT (HS256) for tokens, BCrypt for password hashing
- Jakarta Bean Validation
- JUnit 5 + MockK for tests

## API

Everything sits under `/api/v1`:

```
auth     POST /auth/register   POST /auth/login   POST /auth/refresh   GET /auth/logout
guests   GET  /guests/create   POST /guests/refresh   GET /guests/me
users    GET  /users/me   PATCH /users/me/phone   PATCH /users/me/dateOfBirth   PATCH /users/me/email
menu     GET  /menu/categories   GET /menu/categories/{id}   GET /menu/items/all   GET /menu/items/{id}
basket   GET  /basket/{bagId}   POST /basket/{bagId}
         PATCH /basket/{bagId}/{itemId}   DELETE /basket/{bagId}/{itemId}   DELETE /basket/{bagId}/all
         GET  /basket/{sourceBagId}/merge/{targetBagId}
```

## Configuration

Secrets come from the environment, so nothing sensitive lives in the repo. `application.properties` only references them as `${...}` placeholders.

```bash
export MONGO_DB_CONNECTION_STRING="mongodb+srv://user:pass@cluster.mongodb.net/menu?retryWrites=true&w=majority"
export JWT_SECRET_BASE64="<base64 string that decodes to at least 32 bytes>"
```

The JWT secret is base64-decoded into an HS256 signing key, so it needs to be ≥ 32 bytes once decoded.

## Testing

JUnit 5 with MockK. Service tests mock the repositories, so the suite runs without a database. `HashEncoder` and `JwtService` are exercised against the real BCrypt/JWT implementations instead of being mocked. Dummy config in `src/test/resources` keeps the suite green out of the box.

```bash
./gradlew test
```

## Building

Requires JDK 17. If your system default is newer, point `JAVA_HOME` at a 17 install first, since Gradle 8.14 doesn't run on JDK 25.

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)   # macOS

./gradlew build          # compile + test
./gradlew bootRun        # run locally on :8080
```
