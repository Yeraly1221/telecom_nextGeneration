# Telecom NextGeneration

Spring Boot backend for a simple telecom account system. It supports customer registration and login with JWT, SIM card creation, tariff/subscription management, balance top-ups, and usage tracking.

## What it does

- Register customers and confirm accounts by email.
- Issue JWT tokens for login.
- Create SIM cards and subscriptions.
- Manage tariffs, account balance, payments, and usage records.

## Project Structure

- `security/` handles registration, login, JWT, and email confirmation.
- `customer/` stores customer data and profile lookup logic.
- `account/` manages balance operations.
- `simcard/` manages phone numbers and SIM cards.
- `tariff/` stores tariff plans.
- `subscription/` links a SIM card to a tariff and tracks remaining traffic.
- `payment/` stores deposit and withdrawal history.
- `usagerecords/` stores traffic usage history.

## Screenshots

### Registration
User enters name, email, and password. After submit, the app sends a confirmation email.

![Registration Form](docs/screenshots/registrform.jpg)

### Email confirmation
The confirmation link is opened from the email and the account becomes active.

![Email Confirmation](docs/screenshots/emailconf.jpg)

### SIM card creation
The app creates a SIM card and assigns it to a customer.

![SIM Card Creation](docs/screenshots/simcreate.jpg)

### Balance deposit
The customer tops up the account balance.

![Deposit Balance](docs/screenshots/depositform.jpg)

### Login
The customer logs in after email confirmation and receives a JWT token.

![Sign In](docs/screenshots/signin.jpg)

### Database record examples
These screenshots show how the data is stored in PostgreSQL after the main operations.

![User in database](docs/screenshots/userindock.jpg)
![SIM card in database](docs/screenshots/siminsql.jpg)
![Account balance in database](docs/screenshots/accountinsql.jpg)

## Technologies

- Java 17
- Spring Boot
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- Docker Compose

## Run locally

### 1. Start PostgreSQL

`compose.yaml` only starts the database:

```bash
docker compose up -d
```

### 2. Configure environment

Create a `.env` file in the project root:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5332/jwt_security
SPRING_DATASOURCE_USERNAME=admin
SPRING_DATASOURCE_PASSWORD=admin

APPLICATION_SECURITY_JWT_SECRET_KEY=base64-encoded-secret-key
APPLICATION_SECURITY_JWT_EXPIRATION=900000
APPLICATION_BASE_URL=http://localhost:8080

SPRING_MAIL_HOST=localhost
SPRING_MAIL_PORT=1025
SPRING_MAIL_USERNAME=test@test.com
SPRING_MAIL_PASSWORD=
```

| Variable | Purpose | Required |
|---|---|---|
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL | Yes |
| `SPRING_DATASOURCE_USERNAME` | Database user | Yes |
| `SPRING_DATASOURCE_PASSWORD` | Database password | Yes |
| `APPLICATION_SECURITY_JWT_SECRET_KEY` | Base64 JWT secret | Yes |
| `APPLICATION_SECURITY_JWT_EXPIRATION` | JWT lifetime in milliseconds | Yes |
| `APPLICATION_BASE_URL` | Base URL used in confirmation email | Yes |
| `SPRING_MAIL_HOST` | SMTP host | Yes |
| `SPRING_MAIL_PORT` | SMTP port | Yes |
| `SPRING_MAIL_USERNAME` | SMTP username | Yes |
| `SPRING_MAIL_PASSWORD` | SMTP password | Yes |

### 3. Run the app

```bash
./mvnw spring-boot:run
```

The app uses the `dev` profile by default.

## Main Flow

1. Register a customer with `/api/v1/auth/register`.
2. Open the confirmation link from the email.
3. Log in with `/api/v1/auth/login` to receive a JWT.
4. Use the JWT to call protected endpoints.
5. Create a SIM card and subscription.
6. Deposit money or use traffic.

## API examples

### Auth

```http
POST /api/v1/auth/register
POST /api/v1/auth/login
GET  /api/v1/auth/confirm?token=...
```

Request example:

```json
{
  "firstName": "Aruzhan",
  "lastName": "S",
  "email": "aruzhan@example.com",
  "password": "secret123"
}
```

Login example:

```json
{
  "email": "aruzhan@example.com",
  "password": "secret123"
}
```

Response example:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "message": "Logged in successfully"
}
```

### SIM card

```http
POST /api/v1/simcard/makeSimCard/{customerId}
GET  /api/v1/simcard/getPhoneNumber/{id}
```

Request example:

```json
{
  "phoneNumber": "+77001234567"
}
```

### Account

```http
POST /api/account/deposite/{customerId}
GET  /api/account/balance/{id}
```

Deposit request example:

```json
{
  "amount": 5000,
  "paymentType": "CARD",
  "transactionType": "DEPOSIT"
}
```

### Subscription

```http
POST /api/subscription/makeSubscip/{simCardId}
PUT  /api/subscription/changeTariff/{subscriptionId}
GET  /api/subscription/remaining/{subscriptionId}
PUT  /api/subscription/updateTariff/{subscriptionId}
```

Subscription request example:

```json
{
  "tariff_id": 1
}
```

## Configuration

- `application.security.jwt.secret-key` must be base64 encoded.
- `application.security.jwt.expiration` is in milliseconds.
- `application.base-url` is used to build the email confirmation link.
- `spring.mail.*` must point to a working SMTP server.

## Known Issues

- `compose.yaml` starts only PostgreSQL, not the application or MailDev.
- Some endpoint names still contain typos in code, such as `deposite` and `makeSubscip`.
- Access control still needs improvement because resources are identified by path IDs.

## Troubleshooting

- If login returns `401`, confirm the account first with the email link.
- If the app cannot start, check PostgreSQL connection settings and profile variables.
- If email sending fails, make sure a local SMTP server is running on the configured port.
- `docker compose up` does not start the application or MailDev; it only starts PostgreSQL.
