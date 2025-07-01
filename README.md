#  Rewards API – Spring Boot

A RESTful Web API built using Spring Boot that calculates customer reward points based on their transactions. The application simulates a real-time rewards engine using an in-memory repository.

---

## Features

- Add new customers
- Post transactions for customers
- Calculate monthly and total rewards (based on 3-month history)
- View total accumulated points per customer
- In-memory storage using `ConcurrentHashMap`
- Multithreaded transaction processing
- JavaDoc and test coverage included

---

##  Reward Rules

| Purchase Amount      | Points Earned                          |
|----------------------|----------------------------------------|
| \$0–\$50             | 0                                      |
| \$51–\$100           | 1 point per dollar over \$50           |
| Over \$100           | 2 points per dollar over \$100 + 50 pts |

>  Example: \$120 → 50 pts (for \$51–\$100) + 2×20 = 90 pts

---

##  Tech Stack

- Java 17
- Spring Boot 3.x
- Maven
- JUnit 5
- Lombok
- In-memory data store (`ConcurrentHashMap`)

---

##  How to Run

```bash
# build and run
mvn spring-boot:run
```

> Port: `http://localhost:8080`

---

##  REST API Endpoints

###  Add Customer

```
POST /api/rewards/customers/{customerId}
```

Creates a new customer profile.

---
![image](https://github.com/user-attachments/assets/edaefba2-f11e-423d-8355-ddf71645c4d6)


### 🧾 Add Transactions

```
POST /api/rewards/transactions
```

**Body:**
```json
[
  { "customerId": "cust1", "date": "2025-05-01", "amount": 100 },
  { "customerId": "cust2", "date": "2025-06-15", "amount": 200 },
  { "customerId": "custX", "date": "2025-06-01", "amount": 300 }
]
```

Only existing customers are accepted. Others are ignored silently.

---

![image](https://github.com/user-attachments/assets/0aef8729-261d-4d80-bf8f-31bfffc01295)


### Calculate Rewards

```
POST /api/rewards
```

**Body:**
```json
["cust1", "cust2"]
```

Returns monthly breakdown and total reward points for the given customers.

---

![image](https://github.com/user-attachments/assets/79505491-7f89-4e16-818c-371a94bd3878)


### 🔍 Get Rewards for One Customer

```
GET /api/rewards/customers/{customerId}
```

**Response:**
```json
{
  "customerId": "cust1",
  "monthlyRewards": [
    { "month": "June", "points": 90 },
    { "month": "May", "points": 80 }
  ],
  "totalPoints": 170
}
```

---
![image](https://github.com/user-attachments/assets/591fc643-ec22-4a9b-8eff-9138466e61cd)

### 📊 Get Total Points for All Customers

```
GET /api/rewards/totals
```

Returns a map like:

```json
{
  "cust1": 450,
  "cust2": 750
}
```

---
![image](https://github.com/user-attachments/assets/86daaa5c-4f5f-46b8-bbe1-fcdeadf98663)


## 🧪 Unit Tests

Run with:

```bash
mvn test
```

Tests cover:
- Reward calculation logic
- 3-month filter behavior
- Transaction processing
- Exception handling for unknown customers

---

##  Sample Flow to Test (Postman)

1.  `POST /customers/cust1`
2.  `POST /transactions` with some `cust1` transactions
3.  `POST /rewards` → `["cust1"]`
4.  `GET /customers/cust1`
5.  `GET /totals`

---

## Folder Structure

```
src/
├── controller/           → RewardController.java
├── model/                → DTOs + Transaction
├── repository/           → In-memory store
├── service/              → RewardService interface & impl
├── util/                 → RewardCalculator.java
└── test/                 → Unit tests
```

---

##  Notes

- All data is stored in memory — no DB required
- Ready for extension to use real databases or caching
- Thread-safe and production-ready logic patterns

---


