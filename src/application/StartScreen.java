package application;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StartScreen
{
	private Stage primaryStage;
	private Scene scene;
	private List<TextField> nameFields;
	
	public StartScreen(Stage primaryStage)
	{
		this.primaryStage = primaryStage;
		this.nameFields = new ArrayList<>();
		this.scene = new Scene(buildUI(), 600, 500);
	}
	
	public Scene getScene()
	{
		return scene;
	}
	
	private Parent buildUI()
	{
		VBox root = new VBox(20);
	    root.setPadding(new Insets(40));
	    root.setAlignment(Pos.CENTER);

	    Label title = new Label("Scrabble");
	    title.setStyle("-fx-font-size: 48px; -fx-font-weight: bold;");

	    Label playerCountLabel = new Label("Number of Players:");
	    ComboBox<Integer> playerCountBox = new ComboBox<>();
	    playerCountBox.getItems().addAll(2, 3, 4);
	    playerCountBox.setValue(2);

	    VBox nameFieldsBox = new VBox(10);
	    buildNameFields(2, nameFieldsBox);

	    playerCountBox.setOnAction(_ -> buildNameFields(playerCountBox.getValue(), nameFieldsBox));

	    Button startButton = new Button("Start Game");
	    startButton.setOnAction(_ -> onStartClicked());

	    root.getChildren().addAll(title, playerCountLabel, playerCountBox, nameFieldsBox, startButton);
	    return root;
	}
	
	private void buildNameFields(int count, VBox container)
	{
		container.getChildren().clear();
		nameFields.clear();

		for (int i = 0; i < count; i++)
		{
			TextField field = new TextField();
			field.setPromptText("Player " + (i + 1) + " name: ");
			nameFields.add(field);
			container.getChildren().add(field);
		}
	}
	
	private void onStartClicked()
	{
		List<Player> players = new ArrayList<>();
	    
		for (int i = 0; i < nameFields.size(); i++)
		{
			String name = nameFields.get(i).getText().trim();
			if (name.isEmpty()) name = "Player " + (i + 1);
			players.add(new Player(name));
		}
	    
	    switchToGame(players);
	}
	
	private void switchToGame(List<Player> players)
	{
		GameState gameState = new GameState(players, Main.dictionary);
	    GameScreen gameScreen = new GameScreen(primaryStage, gameState);
	    BufferScreen buffer = new BufferScreen(primaryStage, gameState, gameScreen);
	    primaryStage.setScene(buffer.getScene());
	    buffer.centerOnScreen();
		primaryStage.setResizable(false);
	}
}
