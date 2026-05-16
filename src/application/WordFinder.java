package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import application.MoveValidator.Direction;

public class WordFinder
{
	public static List<String> getFormedWords(Map<Position, Tile> placements, Board board, Direction direction)
	{
		List<String> words = new ArrayList<>();
		
		if(placements.size() == 1)
		{
			Position pos = placements.keySet().iterator().next();
			addWordIfLongEnough(words, collectWord(pos.row(), pos.col(), true, placements, board));
			addWordIfLongEnough(words, collectWord(pos.row(), pos.col(), false, placements, board));
			words.removeIf(word -> word.length() <= 1);
			return words;
		}
		
		if(direction == Direction.HORIZONTAL)
		{
			words.addAll(getMainWords(placements, board, direction));
			words.addAll(getCrossWords(placements, board, direction));
		}
		else if(direction == Direction.VERTICAL)
		{
			words.addAll(getMainWords(placements, board, direction));
			words.addAll(getCrossWords(placements, board, direction));
		}
		
		words.removeIf(word -> word.length() <= 1);
		return words;
	}

	public static List<String> getMainWords(Map<Position, Tile> placements, Board board, Direction direction)
	{
		List<String> words = new ArrayList<>();

		if(placements.isEmpty())
		{
			return words;
		}

		Position start = placements.keySet().iterator().next();
		if(direction == Direction.HORIZONTAL)
		{
			addWordIfLongEnough(words, collectWord(start.row(), start.col(), true, placements, board));
		}
		else if(direction == Direction.VERTICAL)
		{
			addWordIfLongEnough(words, collectWord(start.row(), start.col(), false, placements, board));
		}

		return words;
	}

	public static List<String> getCrossWords(Map<Position, Tile> placements, Board board, Direction direction)
	{
		List<String> words = new ArrayList<>();

		if(direction == Direction.HORIZONTAL)
		{
			for (Position pos : placements.keySet()) {
			    addWordIfLongEnough(words, collectWord(pos.row(), pos.col(), false, placements, board));
			}
		}
		else if(direction == Direction.VERTICAL)
		{
			for (Position pos : placements.keySet()) {
			    addWordIfLongEnough(words, collectWord(pos.row(), pos.col(), true, placements, board));
			}
		}

		return words;
	}
	
	private static void addWordIfLongEnough(List<String> words, String word)
	{
		if(word.length() > 1 && !words.contains(word))
		{
			words.add(word);
		}
	}

	private static String collectWord(int row, int col, boolean horizontal, Map<Position, Tile> placements, Board board)
	{
		// step backwards until empty
		if (horizontal)
		{
			while (col > 0 && (board.isOccupied(row, col - 1) || placements.containsKey(new Position(row, col - 1))))
			{
				col--;
			}
		}
		else
		{
			while (row > 0 && (board.isOccupied(row - 1, col) || placements.containsKey(new Position(row - 1, col))))
			{
				row--;
			}
		}

		// collect forwards
		StringBuilder word = new StringBuilder();
		while (horizontal ? (col < 15 && (board.isOccupied(row, col) || placements.containsKey(new Position(row, col))))
				: (row < 15 && (board.isOccupied(row, col) || placements.containsKey(new Position(row, col)))))
		{
			Tile tile = board.isOccupied(row, col) ? board.getTile(row, col) : placements.get(new Position(row, col));
			word.append(tile.getLetter());
			if (horizontal) col++;
			else row++;
		}

		return word.toString();
	}
}
