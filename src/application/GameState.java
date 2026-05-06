package application;

import java.util.List;
import java.util.Set;

public class GameState
{
	private Set<String> dictionary; //possibleWords = LoadTextFile.loadWords("sowpods.txt");
	private Board board;
	private TileBag tileBag;
	private List<Player> players;
	private int currentPlayerIndex;
	private int consecutivePasses;
	private GamePhase phase;
	
	public GameState(List<Player> players, Set<String> dictionary)
	{
		this.players = players;
		this.board = new Board();
		this.tileBag = new TileBag();
		this.currentPlayerIndex = 0;
	    this.consecutivePasses = 0;
	    this.phase = GamePhase.PLAYING;

	    for (Player player : players)
	    {
	        player.drawTiles(tileBag);
	    }
	}
	
	public Player getCurrentPlayer()
	{
		return players.get(currentPlayerIndex);
	}
	
	public void nextTurn(boolean wordWasPlayed)
	{
		if (wordWasPlayed)
		{
			consecutivePasses = 0;
		}
		else
		{
			consecutivePasses++;
		}

		currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

		if (isGameOver())
		{
			phase = GamePhase.FINISHED;
		}
	}

	public boolean isGameOver()
	{
		boolean playerHasEmptyRack = false;
		
		for(Player p : players)
		{
			if(p.hasEmptyRack())
			{
				playerHasEmptyRack = true;
				break;
			}
		}
		
		return ((tileBag.isEmpty() && playerHasEmptyRack) || consecutivePasses >= players.size() * 2);
	}
	
	public Player getWinner()
	{
		Player winner = null;
		int currentHigh = 0;
		
		for(Player p : players)
		{
			if(p.getScore() > currentHigh)
			{
				currentHigh = p.getScore();
				winner = p;
			}
		}
		
		return winner;
	}
}
