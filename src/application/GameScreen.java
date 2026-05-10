package application;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
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
	private static final int TILE_SIZE = 45;
	
	public GameScreen(Stage primaryStage, GameState gameState)
	{
		this.primaryStage = primaryStage;
		this.gameState = gameState;
		this.dragController = new DragController(gameState, this);
		this.scene = new Scene(buildUI(), 1000, 800);
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
	    Button exportBoard = buildExportBoardButton();

	    rightPanel.getChildren().addAll(scoreboard, controls, exportBoard);
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
				cell.setPrefSize(TILE_SIZE, TILE_SIZE);
				cell.setMinSize(TILE_SIZE, TILE_SIZE);
				cell.setMaxSize(TILE_SIZE, TILE_SIZE);
				
				dragController.enableBoardCell(cell, row, col);
				
				PremiumType premium = gameState.getBoard().getPremiumType(row, col);
	            switch (premium)
	            {
		            case TRIPLE_WORD   -> cell.setStyle("-fx-background-color: red; -fx-border-color: #999; -fx-"
		            		+ "border-width: 0.5;");
		            case DOUBLE_WORD   -> cell.setStyle("-fx-background-color: pink; -fx-border-color: #999; -fx-"
		            		+ "border-width: 0.5;");
		            case TRIPLE_LETTER -> cell.setStyle("-fx-background-color: #4444cc; -fx-border-color: #999; -fx-"
		            		+ "border-width: 0.5;");
		            case DOUBLE_LETTER -> cell.setStyle("-fx-background-color: lightblue; -fx-border-color: #999;"
		            		+ "-fx-border-width: 0.5;");
		            default -> cell.setStyle("-fx-background-color: beige; -fx-border-color: #999; -fx-border-width: "
		            		+ "0.5;");
	            }
	            
	            if (row == 7 && col == 7)
	            {
	                Label star = new Label("\u2605");
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
			StackPane tileView = buildTileView(t, "wheat");
			
			dragController.enableRackTile(tileView, t);
			
			rack.getChildren().add(tileView);
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
			
			playerLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: black;");
			
			if(p == gameState.getCurrentPlayer())
			{
			    playerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: black;");
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
		Button endGame = new Button("End Game");
		
		submit.setOnAction(_ -> onSubmit());
		recall.setOnAction(_ -> onRecall());
		pass.setOnAction(_ -> onPass());
		swap.setOnAction(_ -> onSwap());
		endGame.setOnAction(_ -> onEndGame());
		
		controls.getChildren().addAll(submit, recall, pass, swap, endGame);
		return controls;
	}
	
	private Button buildExportBoardButton()
	{
		Button exportBoard = new Button("Export Board as PNG");
		exportBoard.setOnAction(_ -> onExportBoardPng());
		return exportBoard;
	}
	
	public void refreshRack()
	{
		rackBox.getChildren().clear();
		for(Tile t : gameState.getCurrentPlayer().getRack().getTiles())
		{
			StackPane tileView = buildTileView(t, "wheat");
			
			dragController.enableRackTile(tileView, t);
			
			rackBox.getChildren().add(tileView);
		}
	}
	
	public void refreshScoreBoard()
	{
		scoreboard.getChildren().clear();
		for(Player p : gameState.getPlayers())
		{
			Label playerLabel = new Label(p.getName() + ": " + p.getScore());
			
			playerLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: black;");
			
			if(p == gameState.getCurrentPlayer())
			{
			    playerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: black;");
			}
						
			scoreboard.getChildren().add(playerLabel);
		}
	}
	
	public void refreshStatus()
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
				Position position = new Position(row, col);
				Tile tile = gameState.getBoard().getTile(row, col);
				boolean isTentative = false;
				
				if(tile == null)
				{
				    tile = gameState.getBoard().getTentativePlacements().get(position);
				    isTentative = tile != null;
				}

				if(tile != null)
				{
					String tileColor = isTentative ? "#b7f3ff" : "wheat";
					StackPane tileView = buildTileView(tile, tileColor);
					if(isTentative)
					{
						tileView.setOnMouseClicked(_ -> recallTentativeTile(position));
					}
					cell.getChildren().add(tileView);
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

	private StackPane buildTileView(Tile tile, String backgroundColor)
	{
		StackPane tileView = new StackPane();
		tileView.setPrefSize(TILE_SIZE, TILE_SIZE);
		tileView.setMinSize(TILE_SIZE, TILE_SIZE);
		tileView.setMaxSize(TILE_SIZE, TILE_SIZE);
		tileView.setStyle("-fx-background-color: " + backgroundColor + "; -fx-border-color: black;");

		Label letter = new Label(tile.toString().toUpperCase());
		letter.setAlignment(Pos.CENTER);
		letter.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: black;");

		Label points = new Label(String.valueOf(tile.getPointValue()));
		points.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: black;");
		StackPane.setAlignment(points, Pos.BOTTOM_RIGHT);
		StackPane.setMargin(points, new Insets(0, 4, 3, 0));

		tileView.getChildren().addAll(letter, points);
		return tileView;
	}
	
	private void recallTentativeTile(Position position)
	{
		if(gameState.recallTile(position))
		{
		    refreshRack();
		    refreshBoard();
		    refreshStatus();
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
		if (success)
		{
			if (gameState.isGameOver())
			{
				switchToEndScreen();
				return;
			}
			refreshStatus();
			refreshRack();
			refreshScoreBoard();
			refreshBoard();
			switchToBuffer();
		}
		else
		{
			statusLabel.setText(gameState.getLastMoveError());
			refreshRack();
			refreshBoard();
		}
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
	    if(gameState.isGameOver())
	    {
	    	switchToEndScreen();
	    	return;
	    }
	    
	    switchToBuffer();
	}
	
	private void onEndGame()
	{
		gameState.recallTiles();
		gameState.endGame();
		switchToEndScreen();
	}
	
	private void switchToBuffer()
	{
	    primaryStage.setMaximized(false);
	    BufferScreen buffer = new BufferScreen(primaryStage, gameState, this);
	    primaryStage.setScene(buffer.getScene());
	}
	
	private void onExportBoardPng()
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Export Board as PNG");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));
		fileChooser.setInitialFileName("scrabble_board.png");
		
		File file = fileChooser.showSaveDialog(primaryStage);
		if(file == null)
		{
			return;
		}
		
		file = ensurePngExtension(file);
		WritableImage image = boardGrid.snapshot(null, null);
		
		try
		{
			ImageIO.write(toBufferedImage(image), "png", file);
		}
		catch(IOException e)
		{
			Alert alert = new Alert(Alert.AlertType.ERROR, "Could not export the board image.");
			alert.showAndWait();
		}
	}
	
	private BufferedImage toBufferedImage(WritableImage image)
	{
		int width = (int) image.getWidth();
		int height = (int) image.getHeight();
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		PixelReader pixelReader = image.getPixelReader();
		
		for(int y = 0; y < height; y++)
		{
			for(int x = 0; x < width; x++)
			{
				bufferedImage.setRGB(x, y, pixelReader.getArgb(x, y));
			}
		}
		
		return bufferedImage;
	}
	
	private File ensurePngExtension(File file)
	{
		if(file.getName().toLowerCase().endsWith(".png"))
		{
			return file;
		}
		
		return new File(file.getParentFile(), file.getName() + ".png");
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
					if(gameState.isGameOver())
					{
						switchToEndScreen();
						return;
					}
					
					refreshStatus();
					refreshRack();
					refreshScoreBoard();
					refreshBoard();
				}
			}
		});
	}
}
