/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafx_project2;

import java.sql.*;
import java.util.Optional;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 *
 * @author Abdulrahman
 */
public class Db_system {

    // SQL queries
    static final String INSERT_QUERY = "INSERT INTO ProductsTBL_Abdulrahman (type, model, price, count, deliverydate) VALUES (?, ?, ?, ?, ?)";
    static final String SEARCH_QUERY_TYPEMODEL = "SELECT * FROM ProductsTBL_Abdulrahman WHERE model LIKE ? OR type LIKE ?";
    static final String SEARCH_QUERY_ALL = "SELECT * FROM ProductsTBL_Abdulrahman";
    static final String DELETE_QUERY = "DELETE FROM ProductsTBL_Abdulrahman WHERE ID = ?";
    static final String FIND_ID_QUERY = "SELECT * FROM ProductsTBL_abdulrahman where ID = ?";

    //database information.
    static final String DB_URL = "jdbc:mysql://localhost:3306/ProductsDB";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "Password";

    // Assign initial Values to variables
    static PreparedStatement preparedStatement = null;
    static ResultSet resultSet = null;
    static Connection connection = null;

    // Get database connection using the above url, user and password
    // Catch exceptions if failed to obtain a Connections.
    static Connection getConnection() {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Confirmation Dialog");
        errorAlert.setHeaderText(null);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException se) {
            // An SQL Exception Occurred while trying to connect to the Database.
            // Make sure the database name, url, and password are all correct
            errorAlert.setContentText("An SQL Exception Occurred while trying to connect to the Database.\n Make sure the database name, url and password are all correct");
            // Show the error alert and wait for the user to close it
            Optional<ButtonType> result = errorAlert.showAndWait();;
            if (!result.isPresent() || result.get() == ButtonType.OK) {
                System.exit(1);
            }
        } catch (ClassNotFoundException cnfe) {
            errorAlert.setContentText("Please make sure you have added jdbc drivers to your project.");
            errorAlert.show();

        }
        return connection;
    }
    
     // method to close the database Connection & PreparedStatement & ResultSet
    static void closeConnection(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) {
        // Try and catch blocks to catch exceptions if any occurred while trying to close the connections.
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException se) {
            System.out.println("An SQL Exception occurred while trying to close the connection.");
        }

    }

    //using the input and then calling it in the performSearchQuery(); method
    static ResultSet performSearch(String searchCriteria) {
        return performSearchQuery(SEARCH_QUERY_TYPEMODEL, searchCriteria);
    }
    
    //using the input and then calling it in the performSearchQuery(); method
    static ResultSet performSearchAll(String searchCriteria) {
        return performSearchQuery(SEARCH_QUERY_ALL, searchCriteria);
    }

    //method that returns resultset about the search query.
    static ResultSet performSearchQuery(String query, String searchCriteria) {
        try {
            // Get the database connection
            connection = getConnection();
            // Prepare the search query
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            if (!searchCriteria.isEmpty()) {
                preparedStatement.setString(1, "%" + searchCriteria + "%"); // Use the search criteria for both model and type fields
                preparedStatement.setString(2, "%" + searchCriteria + "%");
            }
            // Execute the query and retrieve the results
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            Label statusLabel = new Label();
            statusLabel.setTextFill(Color.RED);
            statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            statusLabel.setText("An SQL Exception Occurred. Please try Again.");
        }
        return null;
    }

    //method that returns user info as a label.
    static Label myInfo() {
        // Create and Customize the label's appearance
        Label infoLabel = new Label("z1ka");
        infoLabel.setFont(new Font("Arial", 10));
        infoLabel.setTextFill(Color.MIDNIGHTBLUE);
        infoLabel.setAlignment(Pos.CENTER);
        return infoLabel;
    }

}
