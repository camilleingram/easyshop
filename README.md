# EasyShop E-Commerce API

EasyShop is a Spring Boot backend API for an e-commerce website. This project simulates a real-world online shopping experience with user authentication, product browsing, filtering, and profile management. This application connects to a MySQL database and supports full CRUD operations for products, categories, and user profiles.

---

## Project Overview

This project was developed as part of a final Java Capstone. It includes:

- RESTful API built with Spring Boot
- Secure user registration and login (JWT)
- Product search and filtering
- Admin functionality for managing categories and products
- User profile management and updates
- SQL-backed with MySQL and tested via Postman + Unit tests

---

## Screenshots

### Home Screen
![Product Filtering Screenshot](Screenshot_2025-06-26_at_11.06.36_PM.png)

### Product Browsing + Filtering
![Product Filtering Screenshot](Screenshot_2025-06-26_at_11.07.22_PM.png)

### Profile Page
![Profile Page Screenshot](Screenshot_2025-06-26_at_11.07.53_PM.png)

### Login Modal
![Login Screenshot](Screenshot_2025-06-26_at_11.08.11_PM.png)

### Shopping Cart
![Cart Screenshot](Screenshot_2025-06-26_at_11.07.41_PM.png)

---

## 💡 Interesting Feature: Dynamic Profile Field Updates

A standout feature in this project is the dynamic profile update functionality. Instead of writing separate queries for each profile field, I used a flexible DAO method that can update **any allowed field** by passing in the column name and value safely.

### 🧩 DAO Snippet:
```java
@Override
public void updateProfile(int userId, String param, String value) {
    List<String> updatedParams = List.of("first_name", "last_name", "phone", "email", "address", "city", "state", "zip");

    if (!updatedParams.contains(param)) {
        throw new IllegalArgumentException("Invalid column name: " + param);
    }

    String query = "UPDATE profiles SET " + param + " = ? WHERE user_id = ?";

    try (Connection connection = getConnection()) {
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, value);
        preparedStatement.setInt(2, userId);
        preparedStatement.executeUpdate();
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
}
```

### Why It’s Cool:
- Saves lines of repetitive code
- Makes the controller cleaner and easier to maintain
- Validates column names to prevent SQL injection

---

## How to Run

1. Clone the repo into your `C:\pluralsight\LearnToCode_Capstones` folder
2. Import project into IntelliJ or your IDE
3. Create your MySQL database using the included `create_database.sql`
4. Run the project using `SpringBootApplication`
5. Use Postman or the included web frontend to test

---

## Testing

- You can login using the test user:
  ```json
  {
    "username": "admin",
    "password": "password"
  }
  ```

- JWT token will be returned and used for any admin routes
- Use Postman or the website to:
  - Update your profile
  - Search products
  - Add items to the cart
  - Test admin-only product/category routes

---

## Unit Testing

Unit tests were written using **JUnit 5** and **Mockito** to verify:
- Product searching
- Product updating
- Profile updates

---

## Developed by

Camille Ingram – 2025 Java Developer Cohort
