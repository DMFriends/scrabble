package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BufferScreen
{
	private Stage primaryStage;
	private GameState gameState;
	private GameScreen gameScreen;
	private Scene scene;

	public BufferScreen(Stage primaryStage, GameState gameState, GameScreen gameScreen)
	{
		this.primaryStage = primaryStage;
		this.gameState = gameState;
		this.gameScreen = gameScreen;
		this.scene = new Scene(buildUI(), 600, 400);
	}

	public Scene getScene()
	{
		return scene;
	}

	private Parent buildUI()
	{
		VBox root = new VBox(20);
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(40));
		root.setStyle("-fx-background-color: #f0f0f0;");

		Label handOffLabel = new Label("Pass the device to:");
		handOffLabel.setStyle("-fx-font-size: 18px;");

		Label playerName = new Label(gameState.getCurrentPlayer().getName());
		playerName.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;");

		Label instruction = new Label("Press 'Start Turn' when ready.");
		instruction.setStyle("-fx-font-size: 14px; -fx-text-fill: grey;");

		Button startTurn = new Button("Start Turn");
		startTurn.setStyle("-fx-font-size: 16px;");
		startTurn.setOnAction(_ -> {
		    gameScreen.refreshRack();
		    gameScreen.refreshScoreBoard();
		    gameScreen.refreshBoard();
		    gameScreen.refreshStatus();
		    primaryStage.setScene(gameScreen.getScene());
		    primaryStage.setMaximized(false);
		});

		root.getChildren().addAll(handOffLabel, playerName, instruction, startTurn);
		return root;
	}
}