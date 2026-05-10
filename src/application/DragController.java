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
	private Label dragSource;
	
	public DragController(GameState gameState, GameScreen gameScreen)
	{
		this.gameState = gameState;
		this.gameScreen = gameScreen;
		this.draggedTile = null;
		this.dragSource = null;
	}
	
	public void enableRackTile(Label label, Tile tile)
	{
		label.setOnDragDetected(event -> onDragDetected(event, label, tile));
		label.setOnDragDone(event -> onDragDone(event));
		label.setOnMouseEntered(_ -> onMouseEntered(label));
		label.setOnMouseExited(_ -> onMouseExited(label));
	}
	
	public void enableBoardCell(StackPane c, int row, int col)
	{
		c.setOnDragOver(event -> onDragOver(event));
		c.setOnDragDropped(event -> onDragDropped(event, row, col));
	}
	
	private void onDragDetected(MouseEvent event, Label label, Tile tile)
	{
		draggedTile = tile;
		dragSource = label;

		Dragboard db = label.startDragAndDrop(TransferMode.MOVE);
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
		if (event.getTransferMode() == TransferMode.MOVE)
		{
			dragSource.setVisible(false);
		}
		draggedTile = null;
		dragSource = null;
		event.consume();
	}
	
	private void onMouseEntered(Label label)
	{
		label.setStyle("-fx-background-color: goldenrod; -fx-border-color: black; "
				+ "-fx-font-size: 18px; -fx-font-weight: bold;");
	}

	private void onMouseExited(Label label)
	{
		label.setStyle("-fx-background-color: wheat; -fx-border-color: black; "
				+ "-fx-font-size: 18px; -fx-font-weight: bold;");
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
		if (tile instanceof BlankTile blank && !blank.isAssigned())
		{
			char letter = promptBlankLetter();
			blank.setAssignedLetter(letter);
		}

		boolean success = gameState.placeTile(row, col, tile);
		if (success)
		{
			gameScreen.refreshRack();
			gameScreen.refreshBoard();
		}
		return success;
	}
	
	
}
