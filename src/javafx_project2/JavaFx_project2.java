/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafx_project2;

import java.sql.*;
import java.time.LocalDate;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import java.util.Comparator;
import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

/**
 *
 * @author Abdulrahman
 */
public class JavaFx_project2 extends Application {

    private Stage primaryStage;
    private BorderPane root;
    private MenuBar menuBar; // Declare menuBar as an instance variable

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        createMenuBar();
        Label infoLabel = Db_system.myInfo(); // calling my info method from Db_system class
        root.setCenter(infoLabel); // Set the infoLabel in the center of the root layout container
        primaryStage.setTitle("Product Information System");
        primaryStage.setResizable(true); // Make the stage resizable
        primaryStage.setWidth(620); // Set the preferred width of the stage
        primaryStage.setHeight(650); // Set the preferred height of the stage
        root.setPadding(new Insets(10)); // set padding value
        primaryStage.show();
        Db_system.getConnection();
    }

    public void createMenuBar() {
        this.menuBar = new MenuBar();
        // Products Menu
        Menu productsMenu = new Menu("Products");
        // Product Submenu
        Menu productSubMenu = new Menu("Product");

        MenuItem addMenuItem = new MenuItem("Add");
        //show the add Scene when user presses on Add in the sub menu
        addMenuItem.setOnAction(event -> showAddScene());

        MenuItem searchMenuItem = new MenuItem("Search");
        //show the search Scene when user presses on Search in the sub menu
        searchMenuItem.setOnAction(event -> showSearchScene());

        MenuItem deleteMenuItem = new MenuItem("Delete");
        //show the delete Scene when user presses on Delete in the sub menu
        deleteMenuItem.setOnAction(event -> showDeleteScene());

        // add the add-search-delete menu items to the product menu
        productSubMenu.getItems().addAll(addMenuItem, searchMenuItem, deleteMenuItem);
        MenuItem exitMenuItem = new MenuItem("Exit");

        exitMenuItem.setOnAction(event -> {
            //close database connection, and then close the primaryStage when user presses Exit.
            Db_system.closeConnection(Db_system.connection, Db_system.preparedStatement, Db_system.resultSet);
            primaryStage.close();
        });
        // add the product submenu inside the products menu + add the exit menu item
        productsMenu.getItems().addAll(productSubMenu, exitMenuItem);
        menuBar.getMenus().add(productsMenu);
        root = new BorderPane();
        root.setTop(menuBar);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
    }

    private void showAddScene() {
        // Create the necessary JavaFX components for the Add Product scene
        Label infoLabel = Db_system.myInfo(); // calling my info method from Db_system class
        Label titleLabel = new Label("Add Product:");
        Label typeLabel = new Label("Type:");
        ChoiceBox<String> typeChoiceBox = new ChoiceBox<>();
        Label modelLabel = new Label("Model:");
        TextField modelTextField = new TextField();
        Label priceLabel = new Label("Price:");
        TextField priceTextField = new TextField();
        Label countLabel = new Label("Count:");
        Slider countSlider = new Slider(0, 10, 0); // from 0 to 10, value default at 0.
        countSlider.setBlockIncrement(1);
        countSlider.setMinorTickCount(0);
        countSlider.setSnapToTicks(true);
        countSlider.setShowTickMarks(true);
        countSlider.setShowTickLabels(true);
        countSlider.setMajorTickUnit(1);
        Label deliveryDateLabel = new Label("Delivery Date:");
        DatePicker deliveryDatePicker = new DatePicker();
        Button saveButton = new Button("Save");
        Label statusLabel = new Label();
        statusLabel.setTextFill(Color.RED);
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        Label statusLabelg = new Label();
        statusLabelg.setTextFill(Color.GREEN);
        statusLabelg.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        // Set initial values for the input UI fields
        typeChoiceBox.getItems().addAll("", "PC", "TV", "Phone", "Car", "Projector");
        typeChoiceBox.getSelectionModel().selectFirst();
        modelTextField.setText("");
        priceTextField.setText("");
        countSlider.setValue(0.0);
        deliveryDatePicker.setValue(null);

        // Set the functionality for the Add button
        saveButton.setOnAction(event -> {
            try {
                statusLabelg.setText(""); // Clear the error message before performing the search
                statusLabel.setText(""); // Clear the status message before performing the search
                Db_system.getConnection();
                String type = typeChoiceBox.getValue();
                String model = modelTextField.getText();
                String priceText = priceTextField.getText();
                int count = (int) countSlider.getValue();
                LocalDate deliveryDate = deliveryDatePicker.getValue();

                // Perform validation for the input fields
                boolean isValid = true;
                StringBuilder errorMessage = new StringBuilder();
                if (type == null || type.isEmpty()) {
                    isValid = false;
                    errorMessage.append("- Please select a type.\n");
                }
                if (model.isEmpty()) {
                    isValid = false;
                    errorMessage.append("- Please enter a model.\n");
                }
                if (priceText.isEmpty()) {
                    isValid = false;
                    errorMessage.append("- Please enter a price.\n");
                } else {
                    try {
                        float price = Float.parseFloat(priceText);
                        if (Float.compare(price, 0.0f) == 0) {
                            isValid = false;
                            errorMessage.append("- Price must be greater than 0.\n");
                        }
                    } catch (NumberFormatException e) {
                        isValid = false;
                        errorMessage.append("- Invalid price format. Please enter a valid number.\n");
                    }
                }
                if (count <= 0) {
                    isValid = false;
                    errorMessage.append("- Count must be greater than 0.\n");
                }
                if (deliveryDate == null) {
                    isValid = false;
                    errorMessage.append("- Please enter a delivery date.\n");
                }
                // take the user inputs only if the validation is true
                // meaning none of the "if statements" above is true, then ifValid will still be true.
                if (isValid) {
                    PreparedStatement preparedStatement = Db_system.connection.prepareStatement(Db_system.INSERT_QUERY);
                    preparedStatement.setString(1, type);
                    preparedStatement.setString(2, model);
                    preparedStatement.setFloat(3, Float.parseFloat(priceText));
                    preparedStatement.setInt(4, count);
                    preparedStatement.setString(5, deliveryDate.toString());
                    // Execute the statement and check the affected rows
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        statusLabelg.setText("Product added successfully.");
                        // Reset the input data in the form to their initial values after successful operation
                        typeChoiceBox.getSelectionModel().selectFirst();
                        modelTextField.setText("");
                        priceTextField.setText("");
                        countSlider.setValue(0.0);
                        deliveryDatePicker.setValue(null);
                    } else {
                        statusLabel.setText("Failed to add the product.");
                    }
                } else {
                    // Display error message (red-bold)
                    statusLabel.setText("Error adding the product:\n" + errorMessage.toString());
                }
            } catch (SQLException se) {
                statusLabel.setText("An SQL Exception occurred. Please try again.");
            }
        });
        
        
        // Create a VBox to hold the components
        VBox addRoot = new VBox(10);
        addRoot.setPrefWidth(100);
        saveButton.setMinWidth(addRoot.getPrefWidth()); //bigger button width
        addRoot.setPadding(new Insets(10));
        addRoot.getChildren().addAll(
                menuBar,
                infoLabel,
                titleLabel,
                typeLabel, typeChoiceBox,
                modelLabel, modelTextField,
                priceLabel, priceTextField,
                countLabel, countSlider,
                deliveryDateLabel, deliveryDatePicker,
                saveButton,
                statusLabel, statusLabelg
        );

        // Create a new scene add scene
        Scene addScene = new Scene(addRoot);
        // Set the new scene as the active scene
        primaryStage.setScene(addScene);

    }

    private void showSearchScene() {
        // Create the necessary JavaFX components for the Search Product scene
        Label infoLabel = Db_system.myInfo(); // calling my info method from Db_system class
        Label titleLabel = new Label("Search Product");
        Label searchLabel = new Label("Search using type or model:");
        TextField searchTextField = new TextField();
        Button searchButton = new Button("Search");
        Button refreshButton = new Button("Refresh");
        TableView<ObservableList<String>> tableView = new TableView<>();
        Label statusLabel = new Label();
        statusLabel.setTextFill(Color.RED);
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        // Set up the columns in the TableView
        TableColumn<ObservableList<String>, String> idColumn = new TableColumn<>("ID");
        TableColumn<ObservableList<String>, String> typeColumn = new TableColumn<>("Type");
        TableColumn<ObservableList<String>, String> modelColumn = new TableColumn<>("Model");
        TableColumn<ObservableList<String>, String> priceColumn = new TableColumn<>("Price");
        TableColumn<ObservableList<String>, String> countColumn = new TableColumn<>("Count");
        TableColumn<ObservableList<String>, String> deliveryColumn = new TableColumn<>("DeliveryDate");
        // Add the columns to the TableView
        tableView.getColumns().addAll(idColumn, typeColumn, modelColumn, priceColumn, countColumn, deliveryColumn);
        // Add more columns to the TableView as needed
        // Set up the search functionality
        searchButton.setOnAction(event -> {
            String searchCriteria = searchTextField.getText().trim();
            ResultSet searchResults;
            // if user inputs nothing, show all records
            if (searchCriteria.isEmpty()) {
                searchResults = Db_system.performSearchAll(searchCriteria);
            } else {
                searchResults = Db_system.performSearch(searchCriteria);
            }
            try {
                populateTable(tableView, searchResults);
            } catch (SQLException se) {
                statusLabel.setText("An SQL Exception Occurred. Please try Again.");
            }
            // Display status message if no records are available for the search criteria
            if (tableView.getItems().isEmpty()) {
                statusLabel.setText("No records available for this search criteria!");
            } else {
                statusLabel.setText(""); // Clear the status message if there are search results
            }
        });

        //show up-to-date records when the user opens the search scene
        searchButton.fire();

        refreshButton.setOnAction(event -> {
            // Restart the search scene
            showSearchScene();
        });

        // Create an HBox to hold the buttons
        HBox buttonsBox = new HBox(5);
        buttonsBox.getChildren().addAll(searchButton, refreshButton);

        // Create a VBox to hold the components
        VBox searchRoot = new VBox(15);
        searchRoot.setPrefWidth(100);
        searchButton.setMinWidth(searchRoot.getPrefWidth());    //getting both buttons to be the same size
        refreshButton.setMinWidth(searchRoot.getPrefWidth());
        searchRoot.setPadding(new Insets(10));
        searchRoot.getChildren().addAll(
                menuBar,
                infoLabel,
                titleLabel,
                searchLabel,
                searchTextField,
                buttonsBox,
                tableView,
                statusLabel
        );

        // Create a new scene search scene
        Scene searchScene = new Scene(searchRoot);
        // Set the new scene as the active scene
        primaryStage.setScene(searchScene);

    }

    private void populateTable(TableView<ObservableList<String>> tableView, ResultSet resultSet) throws SQLException {
        tableView.getItems().clear();
        tableView.getColumns().clear();

        // Get the ResultSetMetaData to retrieve column information
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        // Create TableColumn objects based on the column information
        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
            final int index = columnIndex; // for use in lambda expression
            TableColumn<ObservableList<String>, String> column = new TableColumn<>(metaData.getColumnName(columnIndex));
            // Set the cell value factory to retrieve data from the ObservableList
            column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(index - 1)));
            // Set custom comparator for numeric columns
            if (isNumericColumn(metaData.getColumnType(columnIndex))) {
                column.setComparator(Comparator.comparingDouble(Double::parseDouble));
            }

            tableView.getColumns().add(column);
        }

        // Iterate over the result set and populate the table
        while (resultSet.next()) {
            ObservableList<String> rowData = FXCollections.observableArrayList();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                rowData.add(resultSet.getString(columnIndex));
            }
            tableView.getItems().add(rowData);
        }
    }
    //check if column is numerical

    private boolean isNumericColumn(int columnType) {
        return columnType == Types.NUMERIC
                || columnType == Types.DECIMAL
                || columnType == Types.INTEGER
                || columnType == Types.FLOAT
                || columnType == Types.REAL
                || columnType == Types.DOUBLE;
    }

    private void showDeleteScene() {
        // Create the necessary JavaFX components for the Delete Product scene
        Label infoLabel = Db_system.myInfo(); // calling my info method from Db_system class
        Label titleLabel = new Label("Delete Product by ID:");
        Label idLabel = new Label("ID:");
        TextField idTextField = new TextField();
        Button deleteButton = new Button("Delete");
        Label statusLabel = new Label();
        statusLabel.setTextFill(Color.RED);
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        Label statusLabelg = new Label();
        statusLabelg.setTextFill(Color.GREEN);
        statusLabelg.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        // Set the functionality for the Delete button
        deleteButton.setOnAction(event -> {
            try {
                statusLabel.setText(""); // Clear the error message
                statusLabelg.setText(""); // Clear the error message
                Db_system.getConnection();
                int id = Integer.parseInt(idTextField.getText());

                // A query to find the ID
                PreparedStatement preparedStatement = Db_system.connection.prepareStatement(Db_system.FIND_ID_QUERY);
                preparedStatement.setInt(1, id);

                // Execute the query and check the resultset
                ResultSet resultSet = preparedStatement.executeQuery();

                // If ID exists
                if (resultSet.next()) {
                    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmAlert.setTitle("Confirmation Dialog");
                    confirmAlert.setHeaderText(null);
                    confirmAlert.setContentText("The deleted product records will be permanently lost. Do you want to commit the changes?");
                    //show confirmation dialoge and wait for user input
                    Optional<ButtonType> result = confirmAlert.showAndWait();

                    // If the user confirms the deletion
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        preparedStatement = Db_system.connection.prepareStatement(Db_system.DELETE_QUERY);
                        preparedStatement.setInt(1, id);

                        // Execute the query and check the affected rows
                        int rowsAffected = preparedStatement.executeUpdate();

                        if (rowsAffected > 0) {
                            statusLabelg.setText("Product deleted successfully.");
                            idTextField.clear();
                        } else {
                            statusLabel.setText("Deletion failed.");
                            idTextField.clear();
                        }
                        // If the user denies the deletion    
                    } else {
                        statusLabel.setText("Deletion canceled.");
                        idTextField.clear();
                    }
                } else {
                    statusLabel.setText("No products found with the given ID.");
                }
            } catch (NumberFormatException e) {
                statusLabel.setText("Invalid ID. Please enter a valid number.");
            } catch (SQLException se) {
                statusLabel.setText("An SQL Exception occurred. Please try again.");
            }
        });

        // Create a VBox to hold the components
        VBox deleteRoot = new VBox(15);
        deleteRoot.setPrefWidth(100);
        deleteButton.setMinWidth(deleteRoot.getPrefWidth()); //bigger button width
        deleteRoot.setPadding(new Insets(10));
        deleteRoot.getChildren().addAll(
                menuBar,
                infoLabel,
                titleLabel,
                idLabel, idTextField,
                deleteButton,
                statusLabel, statusLabelg
        );

        // Create a new scene delete scene
        Scene deleteScene = new Scene(deleteRoot);
        // Set the new scene as the active scene
        primaryStage.setScene(deleteScene);

    }

}
