import junit.framework.TestCase;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.awt.Robot;
import java.awt.AWTException;
import java.awt.event.KeyEvent;

//import java.util.*;
//import java.io.*;
import javax.swing.*;
import java.sql.*;
import java.util.concurrent.ExecutionException;

public class TournamentTestSuite extends TestCase
{
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/TournamentJUnit";

	//  Database credentials
	static final String USER = "********";
	static final String PASS = "**********";

	private Connection conn = null;
	private Statement stmt = null;
	private Statement stmt2 = null;

	private static Robot r;
	private List<String> players;
	private static final String [] name = {"Colin Horner", "David Horner", "James Horner", "Dave Stewart", "Chris Newsom", "Al Thomka", "Steve Bowden", "Rob Wintler-Cox", "Rob Thomas", "Ed Zeltmann", "James Furdell", "James Daley", "John Locke", "Dave Hubbard", "Julie Schober", "Joe Schober", "Joe Kosack", "Kevin Stone", "Bowen Kerins", "Keith Elwin", "Zach Sharpe", "Daniele Acciari", "Cayle George", "Roy Wils", "Trent Augenstein", "Raymond Davidson", "Andrei Massenkoff", "Robert Gagno", "Lyman Sheats", "Donavan Stepp", "Sean J Grant", "Joshua Henderson", "Jason Werdrick", "Kevin Birrell", "Andy Rosa", "Karl DeAngelo", "Germillet David", "Tim Hansen", "Johnny Modica", "Adam Becker", "Grant Mortenson", "Dave Hegge", "Steve Zahler", "Adam McKinnie", "Joe Scalette", "Salem Ayoob", "Maka Honig", "Keith Johnson", "Josh Sharpe", "Greg DeFeo", "Henrik Bjork", "Brian Shepherd", "kevin Nickel", "Jon Replogle", "Sanjay Shah", "Derek Fugate", "John Kremmer", "Maurice Pelletier", "Bob Matthews", "Adam Lefkoff", "Jennifer Peavler", "Craig Senstock", "Mahesh Murthy", "Cryss Stephens", "Frank Romero", "Paul McGlone", "Francesco La Rocca", "Pete Hendricks", "Jerry Bernard", "Mike Pantino", "Koi Morris", "Tim Tournay", "Brent Sorensen", "Howard Levinee", "Don Johnson", "Sebastian Bobbio", "Blair Love", "Russell Crane", "Joe Schall", "Eden Stamm", "Jason Zahler", "Brian Broyles", "John Beerhalter", "Steve Daniels", "Chuck Jackson", "Robert Sovatsky", "Kent Anderson", "Brian Teyssier", "Dale Geiger", "Charlie Bucks", "Robert Sneed", "William McGinnis", "Debra Rymer", "Thomas Stepp"};

	public void setUp(){
		try
		{
			r = new Robot();
			r.setAutoDelay(20);
		}
		catch (AWTException e)
		{	System.exit(0);	}
		try{
			//STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			//STEP 3: Open a connection
			System.out.println("Connecting to a selected database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			System.out.println("Connected database successfully...");

			stmt = conn.createStatement();
			stmt2= conn.createStatement();
			stmt.executeUpdate("DELETE FROM tblmatch;");
			stmt.executeUpdate("DELETE FROM tblscore;");
			stmt.executeUpdate("DELETE FROM tblgame;");
			stmt.executeUpdate("DELETE FROM tblplayer;");
		}
		catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
			System.exit(0);
		}catch(Exception e){
			//Handle errors for Class.forName
			System.exit(0);
		}
	}

    public static void main(String args[]) {
      org.junit.runner.JUnitCore.main("TournamentTestSuite");
    }

    //@Test
    public void testTwentyNinePlayers()
    {
		new Thread(){
			public void run()
			{	Tournament.main(new String[]{"jdbc:mysql://localhost/TournamentJUnit"});	}
		}.start();


		players = new ArrayList<String>();
		for (int index = 0; index < 16; index++)
		{	players.add(name[index]);	}

		System.out.println("" + players.size() + "players");

		wait(5000);

		typeString("1");



		Iterator<String> i = players.iterator();
		while (i.hasNext())
		{

			typeString(i.next());
			typeEnter();
		}

		typeEnter();
		typeString("5");

		wait(6000);

		assertTrue(allPlayersInTournamentAsTyped(players));
		assertTrue(allPlayersPlayExactlyEightTimes(players));
		assertTrue(allMatchesHaveThreeOrFourPlayers());
		assertTrue(playerNeverScheduledAgainstSelf());
		assertTrue(noMatchupOccursMoreThanOnce());
	}

	private boolean noMatchupOccursMoreThanOnce()
	{
		Map<String, Integer> matchup = new  HashMap <String, Integer>();

		int matchNum;

		int[] player = new int[4];

		boolean success = true;

		try
		{
			ResultSet matchList = stmt.executeQuery("SELECT * FROM tblmatch;");
			while (matchList.next())
			{
				player[0] = -1; player[1] = -1; player[2] = -1; player[3] = -1;
				matchNum = matchList.getInt("_key");

				ResultSet playerInMatch = stmt2.executeQuery("SELECT p.name AS name, p._key FROM tblmatch m JOIN tblscore s ON m._fk_score_1 = s._key JOIN tblplayer p ON s._fk_player = p._key WHERE m._key = " +
					matchList.getInt("_key") + ";");
				playerInMatch.next();
				if (! (playerInMatch.getString("name").equals("bye")))
				{	player[0] = playerInMatch.getInt("_key");	}

				playerInMatch = stmt2.executeQuery("SELECT p.name AS name, p._key FROM tblmatch m JOIN tblscore s ON m._fk_score_2 = s._key JOIN tblplayer p ON s._fk_player = p._key WHERE m._key = " +
					matchList.getInt("_key") + ";");
				playerInMatch.next();
				if (! (playerInMatch.getString("name").equals("bye")))
				{	player[1] = playerInMatch.getInt("_key");	}

				playerInMatch = stmt2.executeQuery("SELECT p.name AS name, p._key FROM tblmatch m JOIN tblscore s ON m._fk_score_3 = s._key JOIN tblplayer p ON s._fk_player = p._key WHERE m._key = " +
				matchList.getInt("_key") + ";");
				playerInMatch.next();
				if (! (playerInMatch.getString("name").equals("bye")))
				{	player[2] = playerInMatch.getInt("_key");	}

				playerInMatch = stmt2.executeQuery("SELECT p.name AS name, p._key FROM tblmatch m JOIN tblscore s ON m._fk_score_4 = s._key JOIN tblplayer p ON s._fk_player = p._key WHERE m._key = " +
					matchList.getInt("_key") + ";");
				playerInMatch.next();
				if (! (playerInMatch.getString("name").equals("bye")))
				{	player[3] = playerInMatch.getInt("_key");	}


				for(int num = 0; num < (player.length - 1); num++)
				{
					if (player[num] != -1)
					{
						for (int num2 = num + 1; num2 < player.length; num2++)
						{
							if (player[num2] != -1)
							{
								if (matchup.containsKey("" +Math.min(player[num], player[num2]) + "," + Math.max(player[num], player[num2])))
								{
									System.out.println("player id #" + Math.min(player[num], player[num2]) + " scheduled to face player id # " + Math.max(player[num], player[num2]) +
										" in match #" + matchup.get("" + Math.min(player[num], player[num2]) + "," + Math.max(player[num], player[num2])) + " and in match #" + matchNum);
									success = false;
								}
								else
								{	matchup.put("" + Math.min(player[num], player[num2]) + "," + Math.max(player[num], player[num2]), matchNum);		}
							}
						}
					}
				}
			}
		}
		catch (Exception e)
		{	e.printStackTrace();
		return false;	}
		return success;
	}




	private boolean playerNeverScheduledAgainstSelf()
	{
		boolean success = true;

		try
		{
			ResultSet matchList = stmt.executeQuery("SELECT * FROM tblmatch;");
			while(matchList.next())
			{
				List<String> matchPlayerList = new ArrayList<String>();

				ResultSet playerInMatch = stmt2.executeQuery("SELECT p.name AS name FROM tblmatch m JOIN tblscore s ON m._fk_score_1 = s._key JOIN tblplayer p ON s._fk_player = p._key WHERE m._key = " +
					matchList.getInt("_key") + ";");
				playerInMatch.next();
				if (! (playerInMatch.getString("name").equals("bye")))
				{	matchPlayerList.add(playerInMatch.getString("name"));	}
				playerInMatch = stmt2.executeQuery("SELECT m._key, p._key, p.name AS name FROM tblmatch m JOIN tblscore s ON m._fk_score_2 = s._key JOIN tblplayer p ON s._fk_player = p._key WHERE m._key = " +
					matchList.getInt("_key") + ";");
				playerInMatch.next();
				if (! (playerInMatch.getString("name").equals("bye")))
				{
					if (matchPlayerList.contains(playerInMatch.getString("name")))
					{
						System.out.println("player id #" + playerInMatch.getInt("p._key") + " found more than once in match #" + playerInMatch.getInt("m._key")+ ".  Player not allowed to play against himself!");
						success = false;
					}
					else
					{	matchPlayerList.add(playerInMatch.getString("name"));	}
				}
				playerInMatch = stmt2.executeQuery("SELECT m._key, p._key, p.name AS name FROM tblmatch m JOIN tblscore s ON m._fk_score_3 = s._key JOIN tblplayer p ON s._fk_player = p._key WHERE m._key = " +
					matchList.getInt("_key") + ";");
				playerInMatch.next();
				if (! (playerInMatch.getString("name").equals("bye")))
				{
					if (matchPlayerList.contains(playerInMatch.getString("name")))
					{
						System.out.println("player id #" + playerInMatch.getInt("p._key") + " found more than once in match #" + playerInMatch.getInt("m._key"));
						success = false;
					}
					else
					{	matchPlayerList.add(playerInMatch.getString("name"));	}
				}
				playerInMatch = stmt2.executeQuery("SELECT m._key, p._key, p.name AS name FROM tblmatch m JOIN tblscore s ON m._fk_score_4 = s._key JOIN tblplayer p ON s._fk_player = p._key WHERE m._key = " +
					matchList.getInt("_key") + ";");
				playerInMatch.next();
				if (! (playerInMatch.getString("name").equals("bye")))
				{
					if (matchPlayerList.contains(playerInMatch.getString("name")))
					{
						System.out.println("player id #" + playerInMatch.getInt("p._key") + " found more than once in match #" + playerInMatch.getInt("m._key"));
						success = false;
					}
					else
					{	matchPlayerList.add(playerInMatch.getString("name"));	}
				}
			}

		}
		catch (Exception e)
		{	e.printStackTrace();
			return false;	}
		return success;
	}

	private boolean allMatchesHaveThreeOrFourPlayers()
	{
		boolean success = true;

		try
		{
			ResultSet matchList = stmt.executeQuery("SELECT * FROM tblmatch;");
			while(matchList.next())
			{
				int players = 0;
				ResultSet playerInMatch = stmt2.executeQuery("SELECT p.name AS name FROM tblmatch m JOIN tblscore s ON m._fk_score_1 = s._key JOIN tblplayer p ON s._fk_player = p._key WHERE m._key = " +
					matchList.getInt("_key") + ";");
				playerInMatch.next();
				if (! (playerInMatch.getString("name").equals("bye")))
				{	players++;	}
				playerInMatch = stmt2.executeQuery("SELECT p.name AS name FROM tblmatch m JOIN tblscore s ON m._fk_score_2 = s._key JOIN tblplayer p ON s._fk_player = p._key WHERE m._key = " +
					matchList.getInt("_key") + ";");
				playerInMatch.next();
				if (! (playerInMatch.getString("name").equals("bye")))
				{	players++;	}
				playerInMatch = stmt2.executeQuery("SELECT p.name AS name FROM tblmatch m JOIN tblscore s ON m._fk_score_3 = s._key JOIN tblplayer p ON s._fk_player = p._key WHERE m._key = " +
					matchList.getInt("_key") + ";");
				playerInMatch.next();
				if (! (playerInMatch.getString("name").equals("bye")))
				{	players++;	}
				playerInMatch = stmt2.executeQuery("SELECT p.name AS name FROM tblmatch m JOIN tblscore s ON m._fk_score_4 = s._key JOIN tblplayer p ON s._fk_player = p._key WHERE m._key = " +
					matchList.getInt("_key") + ";");
				playerInMatch.next();
				if (! (playerInMatch.getString("name").equals("bye")))
				{	players++;	}
				if (players < 3)
				{
					success = false;
					System.out.println("match #" + matchList.getInt("_key") + " has only " + players + " players, at least 3 required");
				}
			}
		}
		catch (Exception e)
		{	e.printStackTrace();
			return false;	}
		return success;
	}



	private boolean allPlayersInTournamentAsTyped(List<String> playerList)
	{
		boolean success = true;

		try
		{
			Iterator<String> i = playerList.iterator();

			while (i.hasNext())
			{
				String player = i.next();

				ResultSet name = stmt.executeQuery("SELECT name FROM tblplayer WHERE name = \"" + player + "\";");
				if (! name.next())
				{
					System.out.println("player " + player + " not found") ;
					success = false;
				}
				if (name.next())
				{
					System.out.println("player " + player + "found more than once");
					success = false;
				}
			}
		}
		catch (Exception e)
		{	return false;	}
		return success;
	}


	private boolean allPlayersPlayExactlyEightTimes(List<String> playerList)
	{
		boolean success = true;

		try
		{
			Iterator<String> i = playerList.iterator();
			while (i.hasNext())
			{
				String player = i.next();
				ResultSet name = stmt.executeQuery("SELECT count(*) as count from tblscore s join tblplayer p on s._fk_player = p._key where p.name = \"" + player + "\";");
				name.next(); int plays = name.getInt("count");
				if (plays != 8)
				{
					ResultSet key = stmt.executeQuery("SELECT _key from tblplayer where name = \"" + player + "\";");
					key.next(); int pkey = key.getInt("_key");
					System.out.println("player id " + pkey + " plays " + plays + " times, not 8");
					success = false;
				}
			}
			return success;
		}
		catch (Exception e)
		{	return false;	}
	}


	private void typeString(String inputString)
	{
		for (int index = 0; index < inputString.length(); index++)
		{
			if (Character.isUpperCase(inputString.charAt(index)))
			{
				r.keyPress(KeyEvent.VK_SHIFT);
				r.keyPress(inputString.charAt(index));
				r.keyRelease(inputString.charAt(index));
				r.keyRelease(KeyEvent.VK_SHIFT);
			}
			else
			{
				r.keyPress(inputString.toUpperCase().charAt(index));
				r.keyRelease(inputString.toUpperCase().charAt(index));
			}
		}
	}

	private void typeEnter()
	{
		r.keyPress(KeyEvent.VK_ENTER);
		r.keyRelease(KeyEvent.VK_ENTER);
	}


	public static void wait (int k){
		long time0, time1;
		time0 = System.currentTimeMillis();
		do{
		time1 = System.currentTimeMillis();
		}
		while ((time1 - time0) < k);
	}
}