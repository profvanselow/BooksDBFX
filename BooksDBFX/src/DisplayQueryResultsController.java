// Exercise 24.2: DisplayQueryResultsController.java
// Controller for the DisplayQueryResults app
import java.sql.SQLException;
import java.util.Arrays;

import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;//
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;

import javax.swing.*;


public class DisplayQueryResultsController {
   @FXML private BorderPane borderPane;
   @FXML private ComboBox<String> queryComboBox;

   // database URL, username and password
   final String DATABASE_URL = "jdbc:derby:C:\\Users\\svanselow\\OneDrive - Florida Gulf Coast University\\IDEA Projects\\BooksDBFX\\lib\\books";
   private static final String USERNAME = "deitel";
   private static final String PASSWORD = "deitel";
   
   // default query retrieves all data from Authors table
   private static final String DEFAULT_QUERY = "SELECT * FROM authors";
   
   // used for configuring JTable to display and sort data
   private ResultSetTableModel tableModel;

   public void initialize() {
      String[] queryNames = {"All authors", "All titles", 
         "A specific author", "A specific title"};

      queryComboBox.getItems().addAll(Arrays.asList(queryNames));

      // create ResultSetTableModel and display database table
      try {
         // create TableModel for results of DEFAULT_QUERY
         tableModel = new ResultSetTableModel(DATABASE_URL,            
            USERNAME, PASSWORD, DEFAULT_QUERY);                        
         
         // create JTable based on the tableModel    
         JTable resultTable = new JTable(tableModel);

         // configure SwingNode to display JTable, then add to borderPane
         SwingNode swingNode = new SwingNode();
         swingNode.setContent(new JScrollPane(resultTable));
         borderPane.setCenter(swingNode);
      }
      catch (SQLException sqlException) {
         displayAlert(AlertType.ERROR, "Database Error", 
            sqlException.getMessage());
         tableModel.disconnectFromDatabase(); // close connection  
         System.exit(1); // terminate application
      } 
   }

   // query the database and display results in JTable
   @FXML
   void querySelected(ActionEvent event) {
      int selection = queryComboBox.getSelectionModel().getSelectedIndex();
      
      String query = null;

      // execute predefined query
      switch (selection) {
         case 0:
            query = DEFAULT_QUERY;
            break;
         case 1:
            query = "SELECT * FROM titles";
            break;
         case 2:
            TextInputDialog lastNameInputDialog = new TextInputDialog();
            lastNameInputDialog.showAndWait();
            String lastName = lastNameInputDialog.getEditor().getText();
            
            query = "SELECT authors.lastName, authors.firstName, "+
               "titles.title, titles.isbn FROM " +
               "titles INNER JOIN (authorISBN INNER JOIN authors ON" +
               " authorISBN.authorID = authors.authorID) ON " +
               "titles.isbn = authorISBN.isbn WHERE authors.lastName" +
               " = '" + lastName + "' ORDER BY " +
               "authors.lastName, authors.firstName ASC";
               break;
         case 3:
            TextInputDialog titleInputDialog = new TextInputDialog();
            titleInputDialog.showAndWait();
            String title = titleInputDialog.getEditor().getText();
            
            query = "SELECT titles.isbn, titles.title, " +
               "authors.lastName, authors.firstName FROM titles " +
               "INNER JOIN (authorISBN INNER JOIN authors ON " +
               "authorISBN.authorID = authors.authorID) ON " +
               "titles.ISBN = authorISBN.ISBN WHERE titles.title = '" +
               title + "' ORDER BY authors.lastName, " +
               "authors.firstName ASC";
               break;
      }

      // perform a new query
      try {
         tableModel.setQuery(query);
      } 
      catch (SQLException sqlException) {
         displayAlert(AlertType.ERROR, "Database Error", 
            sqlException.getMessage());
         
         // try to recover from invalid user query 
         // by executing default query
         try {
            tableModel.setQuery(DEFAULT_QUERY);
         } 
         catch (SQLException sqlException2) {
            displayAlert(AlertType.ERROR, "Database Error", 
               sqlException2.getMessage());
            tableModel.disconnectFromDatabase(); // close connection  
            System.exit(1); // terminate application
         } 
      } 
   }

   // display an Alert dialog
   private void displayAlert(AlertType type, String title, String message) {
      Alert alert = new Alert(type);
      alert.setTitle(title);
      alert.setContentText(message);
      alert.showAndWait();
   }
}



/**************************************************************************
 * (C) Copyright 1992-2018 by Deitel & Associates, Inc. and               *
 * Pearson Education, Inc. All Rights Reserved.                           *
 *                                                                        *
 * DISCLAIMER: The authors and publisher of this book have used their     *
 * best efforts in preparing the book. These efforts include the          *
 * development, research, and testing of the theories and programs        *
 * to determine their effectiveness. The authors and publisher make       *
 * no warranty of any kind, expressed or implied, with regard to these    *
 * programs or to the documentation contained in these books. The authors *
 * and publisher shall not be liable in any event for incidental or       *
 * consequential damages in connection with, or arising out of, the       *
 * furnishing, performance, or use of these programs.                     *
 *************************************************************************/
