package application;

import java.util.List;
import java.util.Map;

public class MoveValidator
{
	private Board board;
	private GameState gameState;
	private String errorMessage;
	
	public enum Direction { HORIZONTAL, VERTICAL, INVALID }
	
	public MoveValidator(Board board, GameState gameState)
	{
	    this.board = board;
	    this.gameState = gameState;
	}
	
	public boolean validate(Map<Position, Tile> placements)
	{
		errorMessage = "";
		
		if(placements.isEmpty())
		{
			errorMessage = "Place at least one tile before submitting.";
			return false;
		}
		
		Direction direction = getDirection(placements);
		
		if(direction == Direction.INVALID)
		{
			errorMessage = "Tiles must be placed in one row or one column.";
			return false;
		}
		
		if (isFirstMove())
		{
			if (!coversCenter(placements))
			{
				errorMessage = "The first word must cross the center tile.";
				return false;
			}
		}
		else
		{
			if (!isConnected(placements))
			{
				errorMessage = "New tiles must connect to an existing word.";
				return false;
			}
		}
		
		if(WordFinder.getFormedWords(placements, board, direction).isEmpty())
		{
			errorMessage = "Submitted tiles must form a word.";
			return false;
		}
		
		if(!allWordsValid(placements))
		{
			if(errorMessage.isEmpty())
			{
				errorMessage = "Invalid word or entry.";
			}
			return false;
		}
		
		return true;
	}
	
	public String getErrorMessage()
	{
		return errorMessage;
	}
	
	public static Direction getDirection(Map<Position, Tile> placements)
	{
		if(placements.size() == 1) return Direction.HORIZONTAL;
		
		int[] rows = new int[placements.size()];
		int[] cols = new int[placements.size()];
		
		int i = 0;
		for(Position pos : placements.keySet())
		{
		    rows[i] = pos.row();
		    cols[i] = pos.col();
		    i++;
		}
		
		boolean allRowsSame = true;
		int current = rows[0];
		for(int r = 1; r < rows.length; r++)
		{
			if(rows[r] != current)
			{
				allRowsSame = false;
				break;
			}
		}
		
		boolean allColsSame = true;
		current = cols[0];
		for(int c = 1; c < cols.length; c++)
		{
			if(cols[c] != current)
			{
				allColsSame = false;
				break;
			}
		}
		
		if(allRowsSame) return Direction.HORIZONTAL;
		if(allColsSame) return Direction.VERTICAL;
		else return Direction.INVALID;
	}
	
	private boolean isConnected(Map<Position, Tile> placements)
	{
		if (isFirstMove()) return true;

		for (Position pos : placements.keySet())
		{
			List<Tile> adjacent = board.getAdjacentTiles(pos.row(), pos.col());
			for (Tile t : adjacent)
			{
				if (t != null && !placements.containsValue(t)) return true;
			}
		}

		return false;
	}
	
	private boolean coversCenter(Map<Position, Tile> placements)
	{
		for(Position pos : placements.keySet())
		{
		    if(pos.row() == 7 && pos.col() == 7) return true;
		}
		
		return false;
	}
	
	private boolean allWordsValid(Map<Position, Tile> placements)
	{
		for (String word : WordFinder.getFormedWords(placements, board, getDirection(placements)))
		{
			if (!gameState.isValidWord(word))
			{
				errorMessage = "\"" + word + "\" is not in the dictionary.";
				return false;
			}
		}
		return true;
	}
	
	private boolean isFirstMove()
	{
		for (int row = 0; row < 15; row++)
		{
			for (int col = 0; col < 15; col++)
			{
				if (board.isOccupied(row, col))
				{
					return false;
				}
			}
		}
		
		return true;
	}
}
