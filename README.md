# Restaurant-Management-System
A full-stack web application for restaurant management built with Spring Boot, Spring Security, Thymeleaf, and MySQL.


## ✨ Features
- **Menu Management:** Full CRUD (Create, Read, Update, Delete) functionality for menu items.
- **Order Management:**
    - Place new orders for tables.
    - View a list of active and paid orders.
    - Edit active orders (change quantities, add/remove items).
    - Generate a detailed, printable bill for any order.
- **Role-Based Security:**
    - **Admin Role:** Full access to all features, including menu management and sales reports.
    - **Waiter Role:** Limited access to operational tasks like placing and managing orders.
- **Sales Reporting:** A dashboard to view total sales for the current week and month.
- **Web Interface:** A server-side rendered UI built with Thymeleaf and styled with Bootstrap.

---
## 🛠️ Tech Stack
- **Backend:**
    - Java 17
    - Spring Boot
    - Spring Security (for authentication & authorization)
    - Spring Data JDBC (`JdbcTemplate`)
    - Maven
- **Frontend:**
    - Thymeleaf (Server-Side Templating)
    - Bootstrap 5
- **Database:**
    - MySQL
---

## 🚀 How to Run Locally

#### Prerequisites
- JDK 17 or newer
- Apache Maven
- MySQL Server
#### Steps
1.  **Clone the repository:**
    ```bash
    git clone https://github.com/Prajwalmuthaye/Restaurant-Management-System.git
    ```
2.  **Create the database:**
    Open MySQL and run the following command to create the database:
    ```sql
    CREATE DATABASE restaurant_db;
    USE restaurant_db;
    CREATE TABLE menu_items (
      id          INT AUTO_INCREMENT PRIMARY KEY,
      name        VARCHAR(255) NOT NULL,
      description VARCHAR(500),
      price       DECIMAL(10, 2) NOT NULL,
      category    VARCHAR(100)
    );
    CREATE TABLE orders (
      id              INT AUTO_INCREMENT PRIMARY KEY,
      table_number    VARCHAR(50) NOT NULL,
      order_time      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      status          VARCHAR(50) NOT NULL,
      total_price     DECIMAL(10, 2) NOT NULL
    );
    CREATE TABLE order_items (
      id             INT AUTO_INCREMENT PRIMARY KEY,
      order_id       INT NOT NULL,
      menu_item_id   INT NOT NULL,
      quantity       INT NOT NULL,
      FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
      FOREIGN KEY (menu_item_id) REFERENCES menu_items(id)
    );

    CREATE TABLE users (
      username VARCHAR(50) NOT NULL PRIMARY KEY,
      password VARCHAR(100) NOT NULL,
      enabled  BOOLEAN NOT NULL DEFAULT TRUE
    );

    CREATE TABLE authorities (
      username  VARCHAR(50) NOT NULL,
      authority VARCHAR(50) NOT NULL,
      FOREIGN KEY (username) REFERENCES users (username),
      UNIQUE (username, authority)
    );
    
    ```
3.  **Configure the database connection:**
    Open the `src/main/resources/application.properties` file and update the `spring.datasource.username` and `spring.datasource.password` properties with your MySQL credentials.

4.  **Run the application:**
    Navigate to the project's root directory and run the following Maven command:
    ```bash
    mvn spring-boot:run
    ```
    Alternatively, you can run the `ManagementSystemApplication.java` file from your IDE.

5.  **Access the application:**
    Open your web browser and navigate to `http://localhost:8080`.

---

## 🔐 Login Credentials

The application is seeded with two default users. The password for both is `password123`.

- **Username:** `admin` (Role: ADMIN)
- **Username:** `waiter` (Role: WAITER)

## OUTPUT

![homepage](https://github.com/user-attachments/assets/c5f0470e-df42-42d1-ab08-722218981b10)
