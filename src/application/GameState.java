package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameState
{
	private Set<String> dictionary;
	private Board board;
	private TileBag tileBag;
	private ScoringEngine scoringEngine;
	private List<Player> players;
	private int currentPlayerIndex;
	private int consecutivePasses;
	private GamePhase phase;
	private String lastMoveError;
	
	public GameState(List<Player> players, Set<String> dictionary)
	{
		this.players = players;
		this.board = new Board();
		this.tileBag = new TileBag();
		this.scoringEngine = new ScoringEngine(board);
		this.dictionary = dictionary;
	    this.currentPlayerIndex = 0;
	    this.consecutivePasses = 0;
	    this.phase = GamePhase.PLAYING;
	    this.lastMoveError = "";

	    for (Player player : players)
	    {
	        player.drawTiles(tileBag);
	    }
	}
	
	public Player getCurrentPlayer()
	{
		return players.get(currentPlayerIndex);
	}
	
	public Board getBoard()
	{
		return board;
	}
	
	public List<Player> getPlayers()
	{
		return players;
	}
	
	public boolean placeTile(int row, int col, Tile tile)
	{
		if (phase == GamePhase.PLAYING && !board.isOccupied(row, col))
		{
			if (board.placeTile(row, col, tile))
			{
				getCurrentPlayer().getRack().removeTile(tile);
				return true;
			}
		}
		
		return false;
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
	
	public void endGame()
	{
		phase = GamePhase.FINISHED;
	}
	
	public Player getWinner()
	{
		Player winner = players.get(0);
		int currentHigh = winner.getScore();
		
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
	
	public List<Player> getWinners()
	{
		List<Player> winners = new ArrayList<>();
		int currentHigh = players.get(0).getScore();
		
		for(Player p : players)
		{
			if(p.getScore() > currentHigh)
			{
				currentHigh = p.getScore();
			}
		}
		
		for(Player p : players)
		{
			if(p.getScore() == currentHigh)
			{
				winners.add(p);
			}
		}
		
		return winners;
	}
	
	public void swapTiles(List<Tile> tiles)
	{
		for (Tile t : tiles)
		{
			getCurrentPlayer().getRack().removeTile(t);
		}
		
		tileBag.returnTiles(new ArrayList<>(tiles));
		getCurrentPlayer().drawTiles(tileBag);
		nextTurn(false);
	}
	
	public boolean isValidWord(String word)
	{
		return dictionary.contains(word.toLowerCase());
	}
	
	public boolean commitMove()
	{
		Map<Position, Tile> placements = board.getTentativePlacements();
		lastMoveError = "";

		MoveValidator validator = new MoveValidator(board, this);
		if (!validator.validate(placements))
		{
			lastMoveError = validator.getErrorMessage();
			if(lastMoveError == null || lastMoveError.isEmpty())
			{
				lastMoveError = "Invalid word or entry.";
			}
			return false;
		}

		int score = scoringEngine.calculateScore(placements);
		getCurrentPlayer().addScore(score);
		board.commitMove();
		getCurrentPlayer().drawTiles(tileBag);
		nextTurn(true);
		return true;
	}
	
	public String getLastMoveError()
	{
		return lastMoveError;
	}
	
	public void recallTiles()
	{
		Map<Position, Tile> placements = board.getTentativePlacements();
		for (Tile t : placements.values())
		{
			getCurrentPlayer().getRack().returnTile(t);
		}
		board.recallTiles();
	}
	
	public boolean recallTile(Position position)
	{
		Tile tile = board.recallTile(position);
		if(tile == null)
		{
			return false;
		}
		
		getCurrentPlayer().getRack().returnTile(tile);
		return true;
	}

	public boolean moveTentativeTile(Position from, Position to)
	{
		return phase == GamePhase.PLAYING && board.moveTentativeTile(from, to);
	}

	public TileBag getTileBag()
	{
		return tileBag;
	}
}
