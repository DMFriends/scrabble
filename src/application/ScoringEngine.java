package application;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ScoringEngine
{
	private Board board;

	public ScoringEngine(Board board)
	{
		this.board = board;
	}

	public int calculateScore(Map<Position, Tile> placements)
	{
		if (placements.isEmpty()) return 0;
		return scoreWord(placements) + scoreCrossWords(placements);
	}

	private int scoreWord(Map<Position, Tile> placements)
	{
		if(placements.size() == 1)
		{
			Position pos = placements.keySet().iterator().next();
			int horizontalLength = wordLengthAlongLine(pos.row(), pos.col(), true, placements);
			int verticalLength = wordLengthAlongLine(pos.row(), pos.col(), false, placements);
			
			if(horizontalLength > 1 && verticalLength > 1)
			{
				return scoreWordAlongLine(pos.row(), pos.col(), true, placements)
						+ scoreWordAlongLine(pos.row(), pos.col(), false, placements);
			}
			if(horizontalLength > 1)
			{
				return scoreWordAlongLine(pos.row(), pos.col(), true, placements);
			}
			if(verticalLength > 1)
			{
				return scoreWordAlongLine(pos.row(), pos.col(), false, placements);
			}
			return 0;
		}
		
		MoveValidator.Direction d = MoveValidator.getDirection(placements);
		if (d == MoveValidator.Direction.INVALID) return 0;
		Position start = placements.keySet().iterator().next();
		boolean horizontal = (d == MoveValidator.Direction.HORIZONTAL);
		return scoreWordAlongLine(start.row(), start.col(), horizontal, placements);
	}

	// Letter multipliers only for newly placed tiles on premium squares.
	private int applyLetterMultiplier(int letterValue, PremiumType premium, boolean newlyPlaced)
	{
		if (!newlyPlaced) return letterValue;
		if (premium == PremiumType.DOUBLE_LETTER) return letterValue * 2;
		if (premium == PremiumType.TRIPLE_LETTER) return letterValue * 3;
		return letterValue;
	}

	private int wordPremiumFactor(PremiumType premium)
	{
		if (premium == PremiumType.DOUBLE_WORD) return 2;
		if (premium == PremiumType.TRIPLE_WORD) return 3;
		return 1;
	}

	private int scoreWordAlongLine(int row, int col, boolean horizontal, Map<Position, Tile> placements)
	{
		Position start = wordStart(row, col, horizontal, placements);
		int r = start.row();
		int c = start.col();
		List<Position> positions = new ArrayList<>();
		while (horizontal
			? (c < 15 && cellHasTile(r, c, placements))
			: (r < 15 && cellHasTile(r, c, placements)))
		{
			positions.add(new Position(r, c));
			if (horizontal) c++;
			else r++;
		}

		int letterSum = 0;
		for (Position p : positions)
		{
			Tile tile = tileAt(p, placements);
			if (tile == null) continue;
			PremiumType prem = board.getPremiumType(p.row(), p.col());
			boolean isNew = placements.containsKey(p);
			letterSum += applyLetterMultiplier(tile.getPointValue(), prem, isNew);
		}

		int wordMultiplier = 1;
		for (Position p : positions)
		{
			if (!placements.containsKey(p)) continue;
			wordMultiplier *= wordPremiumFactor(board.getPremiumType(p.row(), p.col()));
		}

		return letterSum * wordMultiplier;
	}

	private int scoreCrossWords(Map<Position, Tile> placements)
	{
		if(placements.size() == 1)
		{
			return 0;
		}
		
		MoveValidator.Direction d = MoveValidator.getDirection(placements);
		if (d == MoveValidator.Direction.INVALID) return 0;

		Set<String> scoredCrossWords = new HashSet<>();
		int total = 0;
		if (d == MoveValidator.Direction.HORIZONTAL)
		{
			for (Position pos : placements.keySet())
			{
				if (wordLengthAlongLine(pos.row(), pos.col(), false, placements) > 1)
				{
					Position start = wordStart(pos.row(), pos.col(), false, placements);
					if (scoredCrossWords.add(start.row() + "," + start.col() + ",V"))
					{
						total += scoreWordAlongLine(pos.row(), pos.col(), false, placements);
					}
				}
			}
		}
		else
		{
			for (Position pos : placements.keySet())
			{
				if (wordLengthAlongLine(pos.row(), pos.col(), true, placements) > 1)
				{
					Position start = wordStart(pos.row(), pos.col(), true, placements);
					if (scoredCrossWords.add(start.row() + "," + start.col() + ",H"))
					{
						total += scoreWordAlongLine(pos.row(), pos.col(), true, placements);
					}
				}
			}
		}
		return total;
	}

	private Position wordStart(int row, int col, boolean horizontal, Map<Position, Tile> placements)
	{
		if (horizontal)
		{
			while (col > 0 && cellHasTile(row, col - 1, placements))
			{
				col--;
			}
		}
		else
		{
			while (row > 0 && cellHasTile(row - 1, col, placements))
			{
				row--;
			}
		}
		return new Position(row, col);
	}

	private int wordLengthAlongLine(int row, int col, boolean horizontal, Map<Position, Tile> placements)
	{
		Position start = wordStart(row, col, horizontal, placements);
		int r = start.row();
		int c = start.col();
		int len = 0;
		while (horizontal
			? (c < 15 && cellHasTile(r, c, placements))
			: (r < 15 && cellHasTile(r, c, placements)))
		{
			len++;
			if (horizontal) c++;
			else r++;
		}
		return len;
	}

	private boolean cellHasTile(int row, int col, Map<Position, Tile> placements)
	{
		if (row < 0 || row >= 15 || col < 0 || col >= 15) return false;
		if (placements.containsKey(new Position(row, col))) return true;
		return board.getTile(row, col) != null;
	}

	private Tile tileAt(Position p, Map<Position, Tile> placements)
	{
		Tile t = placements.get(p);
		if (t != null) return t;
		return board.getTile(p.row(), p.col());
	}
}
