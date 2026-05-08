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
		//Direction direction = isStraightLine(placements);
		
		if(direction == Direction.HORIZONTAL)
		{
			Position start = placements.keySet().iterator().next();
			words.add(collectWord(start.row(), start.col(), true, placements, board));

			for (Position pos : placements.keySet()) {
			    String cross = collectWord(pos.row(), pos.col(), false, placements, board);
			    if (cross.length() > 1) words.add(cross);
			}
		}
		else if(direction == Direction.VERTICAL)
		{
			Position start = placements.keySet().iterator().next();
			words.add(collectWord(start.row(), start.col(), false, placements, board));

			for (Position pos : placements.keySet()) {
			    String cross = collectWord(pos.row(), pos.col(), true, placements, board);
			    if (cross.length() > 1) words.add(cross);
			}
		}
		
		return words;
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