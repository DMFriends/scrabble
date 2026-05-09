package application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TileBag
{
	private List<Tile> allTiles = new ArrayList<>();
	
	public TileBag()
	{
		List<String[]> letters = LoadTextFile.loadCSV("/resources/letters.csv");
		
		if (letters == null)
		{
			System.err.println("Failed to load letters.csv");
			return;
		}
		
		for(String[] line : letters)
		{
			for(int i = 0; i < Integer.parseInt(line[1]); i++)
			{
				if (line[0].charAt(0) == '0') allTiles.add(new BlankTile());
				else allTiles.add(new Tile(line[0].charAt(0), Integer.parseInt(line[2]), false));
			}
		}
		
		shuffle();
	}
	
	public void shuffle()
	{
		Collections.shuffle(allTiles);
	}
	
	public Tile drawTile()
	{
		if (allTiles.isEmpty()) return null;
		return allTiles.remove(allTiles.size() - 1);
	}
	
	public List<Tile> drawTiles(int n)
	{
		List<Tile> tiles = new ArrayList<>();
		
		for(int i = 0; i < n; i++)
		{
			Tile t = drawTile();
	        if (t == null) break;
	        tiles.add(t);
		}
		
		return tiles;
	}
	
	public void returnTile(Tile t)
	{
		allTiles.add(t);
		shuffle();
	}
	
	public void returnTiles(ArrayList<Tile> tiles)
	{
		for(Tile t : tiles)
		{
			allTiles.add(t);
		}
		
		shuffle();
	}
	
	public boolean isEmpty()
	{
		return allTiles.isEmpty();
	}
	
	public int tilesRemaining()
	{
		return allTiles.size();
	}
}
