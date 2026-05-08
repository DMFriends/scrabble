package application;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class GameScreen
{
	private Stage primaryStage;
	private GameState gameState;
	private DragController dragController;
	private Scene scene;
	private GridPane boardGrid;
	private HBox rackBox;
	private VBox scoreboard;
	private Label statusLabel;
	
	public GameScreen(Stage primaryStage, GameState gameState)
	{
		this.primaryStage = primaryStage;
		this.gameState = gameState;
		this.dragController = new DragController(gameState, this);
		this.scene = new Scene(buildUI(), 900, 700);
	}
	
	public Scene getScene()
	{
		return scene;
	}
	
	private Parent buildUI()
	{
		BorderPane root = new BorderPane();
		root.setStyle("-fx-background-color: #f0f0f0;");

	    statusLabel = new Label(gameState.getCurrentPlayer().getName() + "'s turn");
	    statusLabel.setStyle("-fx-font-size: 16px;");
	    BorderPane.setAlignment(statusLabel, Pos.CENTER);
	    root.setTop(statusLabel);

	    boardGrid = buildBoard();

	    StackPane boardWrapper = new StackPane(boardGrid);
	    boardWrapper.setStyle("-fx-background-color: black;");
	    boardWrapper.setPadding(new Insets(2));
	    boardWrapper.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

	    rackBox = buildRack();

	    VBox centerBox = new VBox(10);
	    centerBox.setAlignment(Pos.CENTER);
	    centerBox.setPadding(new Insets(10));
	    centerBox.getChildren().addAll(boardWrapper, rackBox);

	    root.setCenter(centerBox);
	    
	    VBox rightPanel = new VBox(20);
	    rightPanel.setPadding(new Insets(10));
	    rightPanel.setAlignment(Pos.TOP_CENTER);

	    scoreboard = buildScoreBoard();
	    HBox controls = buildControls();
	    controls.setAlignment(Pos.CENTER);

	    rightPanel.getChildren().addAll(scoreboard, controls);
	    root.setRight(rightPanel);

	    return root;
	}
	
	private GridPane buildBoard()
	{
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		
		for(int row = 0; row < 15; row++)
		{
			for(int col = 0; col < 15; col++)
			{
				StackPane cell = new StackPane();
				cell.setPrefSize(45, 45);
				
				dragController.enableBoardCell(cell, row, col);
				
				PremiumType premium = gameState.getBoard().getPremiumType(row, col);
	            switch (premium)
	            {
		            case TRIPLE_WORD   -> cell.setStyle("-fx-background-color: red; -fx-border-color: #999; -fx-"
		            		+ "border-width: 0.5;");
		            case DOUBLE_WORD   -> cell.setStyle("-fx-background-color: pink; -fx-border-color: #999; -fx-"
		            		+ "border-width: 0.5;");
		            case TRIPLE_LETTER -> cell.setStyle("-fx-background-color: blue; -fx-border-color: #999; -fx-"
		            		+ "border-width: 0.5;");
		            case DOUBLE_LETTER -> cell.setStyle("-fx-background-color: lightblue; -fx-border-color: #999;"
		            		+ "-fx-border-width: 0.5;");
		            default -> cell.setStyle("-fx-background-color: beige; -fx-border-color: #999; -fx-border-width: 0.5;");
	            }
	            
	            if (row == 7 && col == 7)
	            {
	                Label star = new Label("★");
	                star.setStyle("-fx-font-size: 20px; -fx-text-fill: darkred;");
	                cell.getChildren().add(star);
	            }

	            grid.add(cell, col, row);
			}
		}
		
		return grid;
	}
	
	private HBox buildRack()
	{
		HBox rack = new HBox(5);
		rack.setAlignment(Pos.CENTER);
		rack.setPadding(new Insets(10));

		for(Tile t : gameState.getCurrentPlayer().getRack().getTiles())
		{
			Label tileLabel = new Label(t.toString());
			tileLabel.setPrefSize(45, 45);
			tileLabel.setAlignment(Pos.CENTER);
			tileLabel.setStyle("-fx-background-color: wheat; -fx-border-color: black; "
					+ "-fx-font-size: 18px; -fx-font-weight: bold;");
			
			dragController.enableRackTile(tileLabel, t);
			
			rack.getChildren().add(tileLabel);
		}

		rackBox = rack;
		return rack;
	}
	
	private VBox buildScoreBoard()
	{
		VBox scoreboard = new VBox();
		scoreboard.setAlignment(Pos.TOP_RIGHT);
		scoreboard.setPadding(new Insets(10));
		
		for(Player p : gameState.getPlayers())
		{
			Label playerLabel = new Label(p.getName() + ": " + p.getScore());
			
			playerLabel.setStyle("-fx-font-size: 18px;");
			
			if(p == gameState.getCurrentPlayer())
			{
			    playerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
			}
						
			scoreboard.getChildren().add(playerLabel);
		}
		
		this.scoreboard = scoreboard;
		return scoreboard;
	}
	
	private HBox buildControls()
	{
		HBox controls = new HBox(5);
		
		Button submit = new Button("Submit");
		Button recall = new Button("Recall");
		Button pass = new Button("Pass");
		Button swap = new Button("Swap");
		
		submit.setOnAction(e -> {onSubmit();});
		recall.setOnAction(e -> {onRecall();});
		pass.setOnAction(e -> {onPass();});
		swap.setOnAction(e -> onSwap());
		
		controls.getChildren().addAll(submit, recall, pass, swap);
		return controls;
	}
	
	public void refreshRack()
	{
		rackBox.getChildren().clear();
		for(Tile t : gameState.getCurrentPlayer().getRack().getTiles())
		{
			Label tileLabel = new Label(t.toString());
			tileLabel.setPrefSize(45, 45);
			tileLabel.setAlignment(Pos.CENTER);
			tileLabel.setStyle("-fx-background-color: wheat; -fx-border-color: black; "
					+ "-fx-font-size: 18px; -fx-font-weight: bold;");
			
			dragController.enableRackTile(tileLabel, t);
			
			rackBox.getChildren().add(tileLabel);
		}
	}
	
	public void refreshScoreBoard()
	{
		scoreboard.getChildren().clear();
		for(Player p : gameState.getPlayers())
		{
			Label playerLabel = new Label(p.getName() + ": " + p.getScore());
			
			playerLabel.setStyle("-fx-font-size: 18px;");
			
			if(p == gameState.getCurrentPlayer())
			{
			    playerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
			}
						
			scoreboard.getChildren().add(playerLabel);
		}
	}
	
	private void refreshStatus()
	{
	    statusLabel.setText(gameState.getCurrentPlayer().getName() + "'s turn");
	}
	
	public void refreshBoard()
	{
		for(int row = 0; row < 15; row++)
		{
			for(int col = 0; col < 15; col++)
			{
				StackPane cell = (StackPane) boardGrid.getChildren().get(row * 15 + col);
				cell.getChildren().clear();
				Tile tile = gameState.getBoard().getTile(row, col);
				
				if(tile == null)
				{
				    tile = gameState.getBoard().getTentativePlacements().get(new Position(row, col));
				}

				if(tile != null)
				{
					Label tileLabel = new Label(tile.toString());
					tileLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
					cell.getChildren().add(tileLabel);
				}
				else if(row == 7 && col == 7)
				{
					Label star = new Label("\u2605");
					star.setStyle("-fx-font-size: 20px; -fx-text-fill: darkred;");
					cell.getChildren().add(star);
				}
			}
		}
	}
	
	private void switchToEndScreen()
	{
		primaryStage.setMaximized(false);
	    EndScreen endScreen = new EndScreen(primaryStage, gameState);
	    primaryStage.setScene(endScreen.getScene());
	}
	
	private void onSubmit()
	{
		boolean success = gameState.commitMove();
		if(success)
		{
			if (gameState.isGameOver())
			{
				switchToEndScreen();
			    return;
			}
			
			refreshStatus();
		}
		else
		{
			statusLabel.setText(gameState.getLastMoveError());
		}

	    refreshRack();
	    refreshScoreBoard();
	    refreshBoard();
	}
	
	private void onRecall()
	{
		gameState.recallTiles();
	    refreshRack();
	    refreshBoard();
	    refreshStatus();
	}
	
	private void onPass()
	{
		gameState.recallTiles();
		gameState.getCurrentPlayer().drawTiles(gameState.getTileBag());
	    gameState.nextTurn(false);
	    refreshStatus();
	    refreshRack();
	    refreshScoreBoard();
	    refreshBoard();
	}
	
	private void onSwap()
	{
		List<Tile> currentTiles = gameState.getCurrentPlayer().getRack().getTiles();

		VBox checkboxes = new VBox(5);
		List<CheckBox> boxes = new ArrayList<>();
		for (Tile t : currentTiles)
		{
			CheckBox cb = new CheckBox(String.valueOf(t.getLetter()));
			boxes.add(cb);
			checkboxes.getChildren().add(cb);
		}

		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Swap Tiles");
		dialog.setHeaderText("Select tiles to swap:");
		dialog.getDialogPane().setContent(checkboxes);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
		dialogStage.getIcons().add(new Image("assets/scrabble.png"));

		dialog.showAndWait().ifPresent(result ->
		{
			if (result == ButtonType.OK)
			{
				List<Tile> tilesToSwap = new ArrayList<>();
				for (int i = 0; i < boxes.size(); i++)
				{
					if (boxes.get(i).isSelected())
					{
						tilesToSwap.add(currentTiles.get(i));
					}
				}
				if (!tilesToSwap.isEmpty())
				{
					gameState.swapTiles(tilesToSwap);
					refreshStatus();
					refreshRack();
					refreshScoreBoard();
					refreshBoard();
				}
			}
		});
	}
}
