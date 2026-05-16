package application;

import java.util.Optional;

import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;

public class DragController
{
	private GameState gameState;
	private GameScreen gameScreen;
	private Tile draggedTile;
	private StackPane dragSource;
	private Position draggedFromBoardPosition;
	
	public DragController(GameState gameState, GameScreen gameScreen)
	{
		this.gameState = gameState;
		this.gameScreen = gameScreen;
		this.draggedTile = null;
		this.dragSource = null;
		this.draggedFromBoardPosition = null;
	}
	
	public void enableRackTile(StackPane tileView, Tile tile)
	{
		tileView.setOnDragDetected(event -> onDragDetected(event, tileView, tile, null));
		tileView.setOnDragDone(event -> onDragDone(event));
		tileView.setOnMouseEntered(_ -> onMouseEntered(tileView));
		tileView.setOnMouseExited(_ -> onMouseExited(tileView));
	}

	public void enableTentativeBoardTile(StackPane tileView, Tile tile, Position position)
	{
		tileView.setOnDragDetected(event -> onDragDetected(event, tileView, tile, position));
		tileView.setOnDragDone(event -> onDragDone(event));
	}
	
	public void enableBoardCell(StackPane c, int row, int col)
	{
		c.setOnDragOver(event -> onDragOver(event));
		c.setOnDragDropped(event -> onDragDropped(event, row, col));
	}
	
	private void onDragDetected(MouseEvent event, StackPane tileView, Tile tile, Position boardPosition)
	{
		draggedTile = tile;
		dragSource = tileView;
		draggedFromBoardPosition = boardPosition;

		Dragboard db = tileView.startDragAndDrop(TransferMode.MOVE);
		ClipboardContent content = new ClipboardContent();
		content.putString(tile.toString());
		db.setContent(content);

		event.consume();
	}

	private void onDragOver(DragEvent event)
	{
		if (event.getGestureSource() != event.getSource() && event.getDragboard().hasString())
		{
			event.acceptTransferModes(TransferMode.MOVE);
		}
		event.consume();
	}

	private void onDragDropped(DragEvent event, int row, int col)
	{
		boolean success = handleDrop(row, col, draggedTile);
		event.setDropCompleted(success);
		event.consume();
	}

	private void onDragDone(DragEvent event)
	{
		if (event.getTransferMode() == TransferMode.MOVE && draggedFromBoardPosition == null)
		{
			dragSource.setVisible(false);
		}
		draggedTile = null;
		dragSource = null;
		draggedFromBoardPosition = null;
		event.consume();
	}
	
	private void onMouseEntered(StackPane tileView)
	{
		tileView.setStyle(getTileStyle("goldenrod"));
	}

	private void onMouseExited(StackPane tileView)
	{
		tileView.setStyle(getTileStyle("wheat"));
	}

	private String getTileStyle(String backgroundColor)
	{
		return "-fx-background-color: " + backgroundColor + "; -fx-border-color: black;";
	}

	private char promptBlankLetter()
	{
		TextInputDialog dialog = new TextInputDialog("A");
		dialog.setTitle("Blank Tile");
		dialog.setHeaderText("Choose a letter for the blank tile (A-Z):");

		Optional<String> result = dialog.showAndWait();
		if (result.isPresent())
		{
			String input = result.get().trim().toUpperCase();
			if (input.length() == 1 && Character.isLetter(input.charAt(0)))
			{
				return input.charAt(0);
			}
		}
		return 'A';
	}

	private boolean handleDrop(int row, int col, Tile tile)
	{
		if(tile == null)
		{
			return false;
		}

		boolean success;
		if(draggedFromBoardPosition != null)
		{
			success = gameState.moveTentativeTile(draggedFromBoardPosition, new Position(row, col));
			if(success)
			{
				gameScreen.refreshBoard();
			}
			return success;
		}

		if (tile instanceof BlankTile blank && !blank.isAssigned())
		{
			char letter = promptBlankLetter();
			blank.setAssignedLetter(letter);
		}

		success = gameState.placeTile(row, col, tile);
		if (success)
		{
			gameScreen.refreshRack();
			gameScreen.refreshBoard();
		}
		return success;
	}
	
	
}
