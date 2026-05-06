package application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board
{
	private Cell[][] board;
	private Map<Position, Tile> tentativePlacements = new HashMap<>();
	
	public Board() 
	{
	    board = new Cell[15][15];
	    initializeBoard();
	}
	
	private void initializeBoard()
	{
	    // Premium square coordinates
	    int[][] tripleWord = {{0,0},{0,7},{0,14},{7,0},{7,14},{14,0},{14,7},{14,14}};
	    int[][] doubleWord = {{1,1},{2,2},{3,3},{4,4},{7,7},{10,10},{11,11},{12,12},{13,13},
	                          {1,13},{2,12},{3,11},{4,10},{10,4},{11,3},{12,2},{13,1}};
	    int[][] tripleLetter = {{1,5},{1,9},{5,1},{5,5},{5,9},{5,13},{9,1},{9,5},{9,9},{9,13},{13,5},{13,9}};
	    int[][] doubleLetter = {{0,3},{0,11},{2,6},{2,8},{3,0},{3,7},{3,14},{6,2},{6,6},{6,8},
	    						{6,12},{7,3},{7,11},{8,2},{8,6},{8,8},{8,12},{11,0},{11,7},
	    						{11,14},{12,6},{12,8},{14,3},{14,11}};

		// First fill everything with NORMAL
		for(int row = 0; row < 15; row++)
		{
			for(int col = 0; col < 15; col++)
			{
				board[row][col] = new Cell(PremiumType.NORMAL);
			}
		}

	    // Then overwrite premium squares
		for(int[] pos : tripleWord)
			board[pos[0]][pos[1]] = new Cell(PremiumType.TRIPLE_WORD);
		for(int[] pos : doubleWord)
			board[pos[0]][pos[1]] = new Cell(PremiumType.DOUBLE_WORD);
		for(int[] pos : tripleLetter)
			board[pos[0]][pos[1]] = new Cell(PremiumType.TRIPLE_LETTER);
		for(int[] pos : doubleLetter)
			board[pos[0]][pos[1]] = new Cell(PremiumType.DOUBLE_LETTER);
	}
	
	public void placeTile(int row, int col, Tile t)
	{
		tentativePlacements.put(new Position(row, col), t);
	}
	
	public void commitMove()
	{
		for (Map.Entry<Position, Tile> entry : tentativePlacements.entrySet())
		{
	        Position pos = entry.getKey();
	        Tile tile = entry.getValue();
	        board[pos.row()][pos.col()].placeTile(tile);
	    }
		
	    tentativePlacements.clear();
	}
	
	public void recallTiles()
	{
		tentativePlacements.clear();
	}
	
	public Tile getTile(int row, int col)
	{
	    if (row < 0 || row >= 15 || col < 0 || col >= 15) {
	        return null;
	    }
	    
	    return board[row][col].getTile();
	}
	
	public boolean isOccupied(int row, int col)
	{
		return board[row][col] == null;
	}
	
	public boolean isCenter(int row, int col)
	{
		return (row == 7 && col == 7);
	}
	
	public List<Tile> getAdjacentTiles(int row, int col)
	{
	    List<Tile> adjacent = new ArrayList<>();
	    
	    int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
	    
		for (int[] dir : directions)
		{
			Tile tile = getTile(row + dir[0], col + dir[1]);
			if (tile != null)
			{
				adjacent.add(tile);
			}
		}
	    
	    return adjacent;
	}
}
