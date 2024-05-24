# Products Information System

A JavaFX application to manage products in a MySQL database.

## Features
- Add a product
- Search for products by model or type
- Delete a product by ID
- Exit the application

## Setup
1. **Clone the Repository:**
    ```bash
    git clone <repository-url>
    ```
2. **Open in IDE:**
    - Click on `File -> Open Project` and select the cloned project directory.
3. **Configure Database Connection:**
    - Update `Db_system.java`:
    ```java
    String url = "jdbc:mysql://localhost:3306/ProductsDB_Abdulrahman";
    String username = "your-username";
    String password = "your-password";
    ```
4. **Build and Run:**
    - Right-click the project and select `Build`.
    - Right-click the project and select `Run`.

## Database Setup
1. **Create Database:**
    ```sql
    CREATE DATABASE ProductsDB_Abdulrahman;
    ```
2. **Create Table:**
    ```sql
    CREATE TABLE ProductsTBL_Abdulrahman (
      ID INT NOT NULL AUTO_INCREMENT,
      Type VARCHAR(20) NOT NULL,
      Model VARCHAR(40) NOT NULL,
      Price FLOAT NOT NULL,
      Count INT NOT NULL,
      DeliveryDate DATE NOT NULL,
      PRIMARY KEY (ID)
    );
    ```




