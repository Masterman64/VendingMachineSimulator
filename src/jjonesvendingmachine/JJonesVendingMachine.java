/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jjonesvendingmachine;

import java.util.Random;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Jacob Jones
 * Date Completed - 9/30/2019
 */
public class JJonesVendingMachine extends Application {
    //Initializes the variables needed throughout the program
    private final String[] sodas = {"Coca Cola", "Pepsi", "Sprite", "A&W Root Beer", "Arizona Tea", "Bottled Water"};
    private final double price = 0.75;
    private final int[] dispenserQuantity = {20,20,20,20,20,20};
    private double totalMoney = 0;
    private int shakeCount = 0;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    public void start(Stage primaryStage) {
        
        //Initializes and customizes the layout
        GridPane layout = new GridPane();
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);
        
        //Initializes and customizes the stage
        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setTitle("Vending Machine");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        //Asks how much the user wants to pay
        Label howMuch = new Label("How much do you want to pay?");
        howMuch.setAlignment(Pos.CENTER);
        
        //The current amount of money left
        Label infoText = new Label("");
        infoText.setAlignment(Pos.CENTER);
        
        //Displays text relating to drink obtaining throughout the program
        TextField totalField = new TextField("$ ");
        totalField.setAlignment(Pos.CENTER);
        totalField.setMaxSize(75, 10);
        totalField.positionCaret(2);
        
        //Initializes the soda button's container
        //Above the other button initializations because the reset button needs to have access to it
        VBox drinkCol = new VBox();
        drinkCol.setAlignment(Pos.CENTER);
        
        //Sets up the button to confirm the user's totalMoney
        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String totalText = totalField.getText(); //Obtains the field's text
                
                //Checks if the text has a $ or space
                if(totalText.contains("$") || totalText.contains(" ")){ 
                    if(totalText.contains(" ")){ //Checks if there are any spaces in the text
                        totalText = totalText.substring(totalText.lastIndexOf(" ") + 1);
                    } else { //Executes if there is just a $ and no spaces
                        totalText = totalText.substring(totalText.lastIndexOf("$") + 1);
                    }
                }
                
                //Checks if the text is empty after the manipulation and contains only numbers
                if(!totalText.isEmpty() && totalText.matches("^[0-9\\.]*$")) {
                    totalMoney = Double.parseDouble(totalText); //Sets the totalMoney to whatever number the user put in
                    
                    //Disables the confirm button and text field, as they are no longer needed
                    confirmButton.setDisable(true);
                    totalField.setDisable(true);
                    
                    //Enables the drink buttons, as they are now needed
                    for(Node button : drinkCol.getChildren()){
                        button.setDisable(false);
                    }
                    
                    //Tells the user how much money they have left
                    infoText.setText("You now have " + String.format("$ %.2f", totalMoney));
                } else { //Executes if the text field has a letter or punctuation that's not a $ or period
                    infoText.setText("Please enter a valid entry");
                }
            }
        });
        
        //Initializes the reset button
        Button resetButton = new Button("Reset");
        resetButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                //Resets all of the variables to their default values
                shakeCount = 0;
                totalMoney = 0;
                //Enables and resets the text field and confirm button
                totalField.setDisable(false);
                totalField.setText("$ ");
                confirmButton.setDisable(false);
                
                //Resets the infoText text
                infoText.setText("");
                
                //Resets the drink buttons
                resetButtons(drinkCol.getChildren());
                
                //Resets the buttons and quantities to their default values
                for(int i = 0; i < drinkCol.getChildren().size(); i++){
                    dispenserQuantity[i] = 20;
                    Button button = (Button)drinkCol.getChildren().get(i);
                    button.setText(sodas[i] + ": " + dispenserQuantity[i]);
                }
            }
        });
        
        //Initializes the column of soda buttons
        for(int i = 0; i < sodas.length; i++){
            final int index = i; //Done to get the index into the anonymous class
            
            //Initializes and customizes the button for a drink
            Button drinkButton = new Button(sodas[index] + ": " + dispenserQuantity[index]);
            drinkButton.setMaxWidth(125);
            drinkCol.getChildren().add(drinkButton);
            drinkButton.setDisable(true); //Disables the button when the totalMoney hasn't been set
            drinkButton.setOnAction(new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent event) {
                    //Checks if the user can pay for the drink and if there's any left
                    if (totalMoney >= price && dispenserQuantity[index] > 0){
                        //Sets the normal variables
                        dispenserQuantity[index] -= 1; //Removes a soda from the total count
                        totalMoney -= price; //Deducts the price from the user's totalMoney
                        
                        //Changes the button and text to match the new quantity and totalMoney
                        infoText.setText("You now have " + String.format("$ %.2f", totalMoney) + " left");
                        drinkButton.setText(sodas[index] + ": " + dispenserQuantity[index]);
                        
                        //Checks if the drink quantity is now 0
                        if(dispenserQuantity[index] == 0){
                            drinkCol.getChildren().get(index).setDisable(true);
                            infoText.setText("Sorry, we have no more of that item.\nYou now have " + String.format("$ %.2f", totalMoney) + " left");
                            
                            //For the off chance that the user buys all of the soda, I let them know how weird they are.
                            int noSodaCount = 0;
                            for(int soda : dispenserQuantity){
                                if(soda == 0){
                                    noSodaCount++;
                                }
                            }
                            if(noSodaCount == dispenserQuantity.length){
                                infoText.setText("...You bought all of our sodas.\nYou must be really thirsty, aren't you?");
                            }
                        }
                    }  
                    if(totalMoney < price){ //Disables the buttons if the user does not have sufficient funds
                        infoText.setText("You only have " + String.format("$ %.2f", totalMoney) + " left.\nPlease reset to order more.");
                        resetButtons(drinkCol.getChildren());
                    }
                }
            });
        }
        
        //Initializes the image and it's view
        //Initialized under everything else because it needs to customize everything else
        Image machineImage = new Image("resources/vendingMachine.png");
        ImageView machineView = new ImageView(machineImage);
        
        //As an easter egg, the user can click on the machine to potentially get a free drink
        machineView.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                if(shakeCount <= 10){ //The user can only get 10 free drinks before the machine breaks
                    Random random = new Random();
                    int randNum = random.nextInt(sodas.length + 24); //Makes it so that the user has a 25% chance of getting a free soda
                    if(randNum >= sodas.length){
                        infoText.setText("You shook the machine, and nothing happened");
                    } else {
                        if(dispenserQuantity[randNum] <= 0){
                            infoText.setText("You shook the machine, and nothing happened");
                        } else { //Gives the user a free drink if they get lucky
                            if(shakeCount == 11 && randNum < sodas.length){ //On the eleventhth shake, the machine will break
                                infoText.setText("The " + sodas[randNum] + " clogged the dispenser,\npreventing you rom getting any more drinks.");
                                confirmButton.setDisable(true);
                                totalField.setDisable(true);
                                resetButtons(drinkCol.getChildren());
                            } else if(shakeCount >= 5){ //The program begins to warn the user when they're at half the drinks they can get until the machine breaks
                                infoText.setText("You shook the machine, and got a free " + sodas[randNum] + "!\nYou should probably stop shaking it now.");
                                Button drinkButton = (Button)(drinkCol.getChildren().get(randNum));
                                dispenserQuantity[randNum] -= 1;
                                
                                //Checks if the drink's quantity was depleted because of the user's shenanigans
                                if(dispenserQuantity[randNum] != 0){
                                    drinkButton.setText(sodas[randNum] + ": " + dispenserQuantity[randNum]);
                                } else {//Disables the drink button if it has no more drinks to give
                                    drinkButton.setText(sodas[randNum] + ": " + dispenserQuantity[randNum]);
                                    drinkButton.setDisable(true);
                                }
                                shakeCount++; //Counts how many times the user has received a free drink
                            } else { //The program will go as normal if the user has received less than five free drinks
                                infoText.setText("You shook the machine, and got a free " + sodas[randNum] + "!");
                                Button drinkButton = (Button)(drinkCol.getChildren().get(randNum));
                                dispenserQuantity[randNum] -= 1;
                                
                                //Checks if the drink's quantity was depleted because of the user's shenanigans
                                if(dispenserQuantity[randNum] != 0){
                                    drinkButton.setText(sodas[randNum] + ": " + dispenserQuantity[randNum]);
                                } else { //Disables the drink button if it has no more drinks to give
                                    drinkButton.setText(sodas[randNum] + ": " + dispenserQuantity[randNum]);
                                    drinkButton.setDisable(true);
                                }
                                shakeCount++; //Counts how many times the user has received a free drink
                            }
                            
                        }
                    }
                    
                } else { //The program reminds them of their mistake if they try to shake the machine again
                    infoText.setText("The machine will not unclog,\nno matter how much you shake it.");
                    //For the off chance that the user buys all of the soda, I let them know how weird they are.
                    int noSodaCount = 0;
                    for(int soda : dispenserQuantity){
                        if(soda == 0){
                            noSodaCount++;
                        }
                    }
                    if(noSodaCount == dispenserQuantity.length){//If the user broke the machine AND got all of the soda, the program becomes disturbed.
                        infoText.setText("Not only did you clog the machine, but you\nalso emptied the entire machine. You monster.");
                    }
                }
                
                
            }
            
        });
        
        //Fits the machine into the scene
        machineView.setFitHeight(200);
        machineView.setFitWidth(175);
        
        //Initializes and customizes the row that obtains the monetary information
        HBox moneyRow = new HBox(totalField, confirmButton, resetButton);
        moneyRow.setAlignment(Pos.CENTER);
        
        //Initializes and customizes the column that receives and outputs information
        VBox infoCol = new VBox(howMuch, moneyRow, infoText);
        infoCol.setAlignment(Pos.CENTER);
        layout.add(infoCol, 0, 0);
        
        //Initializes and customizes the row that has the machine picture and soda buttons
        HBox sodaRow = new HBox(machineView, drinkCol);
        sodaRow.setAlignment(Pos.CENTER);
        layout.add(sodaRow, 0, 1);
    } 
    
    /**
     * This method will reset the buttons within a given list
     * @param buttonList - The list of buttons to be reset
     */
    private void resetButtons(ObservableList<Node> buttonList){
        for(Node button : buttonList){
            button.setDisable(true);
        }
    }
}
