package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

		if(!hasContiguousMainWord(placements, direction))
		{
			errorMessage = "Tiles must form one continuous word without empty gaps.";
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

	private boolean hasContiguousMainWord(Map<Position, Tile> placements, Direction direction)
	{
		if(placements.size() == 1)
		{
			return true;
		}

		int fixed = -1;
		int min = 15;
		int max = -1;

		for(Position pos : placements.keySet())
		{
			int moving;
			if(direction == Direction.HORIZONTAL)
			{
				fixed = pos.row();
				moving = pos.col();
			}
			else if(direction == Direction.VERTICAL)
			{
				fixed = pos.col();
				moving = pos.row();
			}
			else
			{
				return false;
			}

			if(moving < min) min = moving;
			if(moving > max) max = moving;
		}

		for(int i = min; i <= max; i++)
		{
			Position pos = direction == Direction.HORIZONTAL ? new Position(fixed, i) : new Position(i, fixed);
			if(!placements.containsKey(pos) && !board.isOccupied(pos.row(), pos.col()))
			{
				return false;
			}
		}

		return true;
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
		Direction direction = getDirection(placements);
		if(placements.size() == 1)
		{
			return validateWords(WordFinder.getFormedWords(placements, board, direction));
		}

		List<String> invalidCrossWords = getInvalidWords(WordFinder.getCrossWords(placements, board, direction));
		if(!invalidCrossWords.isEmpty())
		{
			setInvalidWordsError(invalidCrossWords);
			return false;
		}

		return validateWords(WordFinder.getMainWords(placements, board, direction));
	}

	private boolean validateWords(List<String> words)
	{
		List<String> invalidWords = getInvalidWords(words);
		if(invalidWords.isEmpty())
		{
			return true;
		}

		setInvalidWordsError(invalidWords);
		return false;
	}

	private List<String> getInvalidWords(List<String> words)
	{
		List<String> invalidWords = new ArrayList<>();
		
		for (String word : words)
		{
			if (word.length() <= 1)
			{
				continue;
			}
			
			if (!gameState.isValidWord(word))
			{
				invalidWords.add(word.toUpperCase(Locale.ROOT));
			}
		}

		return invalidWords;
	}

	private void setInvalidWordsError(List<String> invalidWords)
	{
		if(invalidWords.size() == 1)
		{
			errorMessage = "\"" + invalidWords.get(0) + "\" is not in the dictionary.";
		}
		else
		{
			errorMessage = "\"" + String.join(", ", invalidWords) + "\" are not in the dictionary.";
		}
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
