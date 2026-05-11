package application;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class EndScreen
{
	private Stage primaryStage;
	private GameState gameState;
	private Scene scene;
	
	public EndScreen(Stage primaryStage, GameState gameState)
	{
		this.primaryStage = primaryStage;
		this.gameState = gameState;
		this.scene = new Scene(buildUI(), 600, 500);
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

	    Label title = new Label("Game Over!");
	    title.setStyle("-fx-font-size: 48px; -fx-font-weight: bold;");

	    Label winner = new Label(getWinnerText());
	    winner.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + getWinnerTextColor() + ";");

	    VBox scoreList = buildScoreList();

	    Button playAgain = new Button("Play Again");
	    playAgain.setOnAction(_ -> onPlayAgain());

	    Button exportCSV = new Button("Export Scores");
	    exportCSV.setOnAction(_ -> onExportCSV());

	    Button exit = new Button("Exit");
	    exit.setOnAction(_ -> onExit());

	    HBox buttons = new HBox(10, playAgain, exportCSV, exit);
	    buttons.setAlignment(Pos.CENTER);

	    root.getChildren().addAll(title, winner, scoreList, buttons);
	    return root;
	}
	
	private String getWinnerText()
	{
		List<Player> winners = gameState.getWinners();
		
		if(winners.size() == 1)
		{
			return winners.get(0).getName() + " wins!";
		}
		
		return "The game ended in a tie.";
	}
	
	private String getWinnerTextColor()
	{
		return gameState.getWinners().size() == 1 ? "green" : "black";
	}
	
	private void onExportCSV()
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save Scores");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
		fileChooser.setInitialFileName("scrabble_scores.csv");

		File file = fileChooser.showSaveDialog(primaryStage);
		if (file != null)
		{
			try (PrintWriter writer = new PrintWriter(file))
			{
				writer.println("Player,Score");
				for (Player p : gameState.getPlayers())
				{
					writer.println(p.getName() + "," + p.getScore());
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private VBox buildScoreList()
	{
		VBox scoreList = new VBox();
		List<Player> winners = gameState.getWinners();
		
		for(Player p : gameState.getPlayers())
		{
			Label playerScore = new Label(p.getName() + ": " + p.getScore());
			if(winners.size() == 1 && p == winners.get(0))
			{
				playerScore.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");
			}
			else
			{
				playerScore.setStyle("-fx-font-size: 28px;");
			}
			scoreList.getChildren().add(playerScore);
		}
		
		scoreList.setAlignment(Pos.CENTER);
		
		return scoreList;
	}
	
	private void onPlayAgain()
	{
		StartScreen startScreen = new StartScreen(primaryStage);
	    primaryStage.setScene(startScreen.getScene());
	}

	private void onExit()
	{
		primaryStage.close();
	}
}
