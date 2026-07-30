**Digital Banking System**

A backend banking application developed using Java, Spring Boot, Spring Security, JPA (Hibernate), and MySQL. The system allows users to manage bank accounts, perform transactions, and securely access banking services using JWT-based authentication and role-based authorization.

Features
User registration and login with JWT authentication

Role-based access control for Customer, Employee, and Admin

Create and manage Savings, Checking, and Business accounts

Deposit, Withdraw, and Transfer money between accounts

Account status management (Active, Suspended, Closed)

Transaction history with filtering and search functionality

Soft delete implementation for users and accounts

Audit logging to track important system activities

RESTful APIs with validation and exception handling



Tech Stack

Java 17

Spring Boot

Spring Security

Spring Data JPA (Hibernate)

MySQL

Maven

JWT Authentication

Docker



Main APIs

Authentication

POST /api/auth/register

POST /api/auth/login



Accounts

POST /api/accounts

GET /api/accounts/{accountNumber}

GET /api/accounts/customer/{customerId}

PATCH /api/accounts/{accountNumber}/status

DELETE /api/accounts/{accountNumber}



Transactions

POST /api/transactions/deposit

POST /api/transactions/withdraw

POST /api/transactions/transfer

GET /api/transactions/search



Running the Project


Prerequisites
Java 17+

MySQL

Maven



Steps

mvn clean install

mvn spring-boot:run



The application will start on:

http://localhost:8080



What I Learned :

Spring Boot application development

JWT authentication and authorization

Role-based access control

JPA and Hibernate ORM

Database design and transaction management

REST API development and testing using Postman
