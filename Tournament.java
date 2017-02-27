import java.util.*;
import java.io.*;
import javax.swing.*;
import java.sql.*;
import java.util.concurrent.ExecutionException;
import java.text.DecimalFormat;

public class Tournament
{
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	//static final String DB_URL = "jdbc:mysql://localhost/test";
	private String DB_URL;

	//  Database credentials
	static final String USER = "********";
	static final String PASS = "**********";

	private Connection conn = null;
	private Statement stmt = null;
	private Statement stmt2 = null;

	//private int[] timeGamePlayed;

	private List<String> games;
	private List<String> players;
	Map<String, Integer> numberedPlayers;

	@SuppressWarnings("unchecked")
	public static void main (String[] args)
	{
		Tournament t = new Tournament(args.length == 0? "jdbc:mysql://localhost/test" : args[0]);

		t.startDatabaseConnection();

		if (t.tournamentBegun())
		{
			t.runTournament();
			System.exit(0);
		}

		boolean done = false;
		String input;

		t.games = new ArrayList<String>()
		{
			public String toString()
			{
				StringBuilder returnValue = new StringBuilder("[");
				Iterator<String> i = iterator();
				while (i.hasNext())
				{
					returnValue.append(i.next());
					if (i.hasNext())
					{	returnValue.append("              " + i.next());	}
					if (i.hasNext())
					{	returnValue.append("\n");	}
				}
				return ("" + returnValue + "]");
			}
		};

		try
		{	t.players = (List<String>)t.games.getClass().newInstance();	}
		catch (Exception e) {	System.exit(0);	}

		t.games.addAll(t.loadGameListFromDatabase());
		t.players.addAll(t.loadPlayerListFromDatabase());

		//System.out.println(loading);
		List<String> options = new ArrayList<String>();
		options.add("1"); options.add("2"); options.add("3"); options.add("4"); options.add("5"); options.add("q"); options.add("Q");
		while (! done)
		{
			int choice = 0;
			System.out.println("Which action to take next?\n1: add players\n2: delete players\n3: add games\n4: delete games\n5: start tournament\nq: quit");
			Prompter response = ConsoleKeyPressPrompter.prompt(null, options);
			while(! response.isDone())
			{}
			try
			{
				if ((response.get().equals("q")) || (response.get().equals("Q")))
				{	choice = 10;		}
				else
				{	choice = new Integer("" + response.get()).intValue();	}
			}
			catch (InterruptedException e)
			{ 	choice = 0;	}
			catch (ExecutionException e)
			{ 	choice = 0;	}
			if (choice == 10)
			{	done = true;		}

			if (choice == 1)
			{
				boolean finished = false;
				while (! finished)
				{
					System.out.println(t.players);
					System.out.print("enter next player: ");
					Prompter getName = CommandLinePrompter.prompt("enter next player", "|[A-Za-z\\- ]{4,20}");while(! getName.isDone()){}
					//input = JOptionPane.showInputDialog("" + t.players + "\nenter next player");
					try
					{
						input = getName.get();
					}
					catch (InterruptedException e)
					{ 	input = "";	}
					catch (ExecutionException e)
					{ 	input = "";	}
					//input = JOptionPane.showInputDialog("" + t.players + "\nenter next player");
					if (((input == null) || input.trim().equals("")) ? finished = true : t.players.add(input));
				}
				t.savePlayerListToDatabase();
			}

			if (choice == 2)
			{
				boolean finished = false;
				while (! finished)
				{
					String playerList = "";
					Iterator<String> i = t.players.iterator();
					while(i.hasNext())
					{	playerList += "|" + i.next();	}

					System.out.println(t.players);
					System.out.print("enter next player: ");
					Prompter getName = CommandLinePrompter.prompt("enter next player", playerList);while(! getName.isDone()){}
					//input = JOptionPane.showInputDialog("" + t.players + "\nenter next player");
					try
					{
						input = getName.get();
					}
					catch (InterruptedException e)
					{ 	input = "";	}
					catch (ExecutionException e)
					{ 	input = "";	}

					if ((input == null) || input.trim().equals(""))
					{	finished = true;	}
					else
					{
						if(t.players.remove(input))
						{	t.removePlayerFromDatabase(input);	}
					}
				}
			}


			if (choice == 3)
			{
				boolean finished = false;
				while (! finished)
				{
					System.out.println(t.games);
					System.out.print("enter next game: ");

					Prompter getGame = CommandLinePrompter.prompt("enter next game", "|[A-Za-z0-9/&* ]{4,20}");while(! getGame.isDone()){}
					//input = JOptionPane.showInputDialog("" + t.players + "\nenter next player");
					try
					{
						input = getGame.get();
					}
					catch (InterruptedException e)
					{ 	input = "";	}
					catch (ExecutionException e)
					{ 	input = "";	}

					//input = JOptionPane.showInputDialog("" + t.games + "\nenter next game");
					if (((input == null) || input.trim().equals("")) ? finished = true : t.games.add(input));
				}
				t.saveGameListToDatabase();
			}

			if (choice == 4)
			{
				boolean finished = false;
				while (! finished)
				{
					String gameList = "";
					Iterator<String> i = t.games.iterator();
					while(i.hasNext())
					{	gameList += "|" + i.next();	}
					gameList = gameList.replaceAll("\\*", "\\\\*");

					System.out.println(t.games);
					System.out.print("enter next game: ");
					Prompter getGame = CommandLinePrompter.prompt("enter next game", gameList);while(! getGame.isDone()){}
					//input = JOptionPane.showInputDialog("" + t.players + "\nenter next player");
					try
					{
						input = getGame.get();
					}
					catch (InterruptedException e)
					{ 	input = "";	}
					catch (ExecutionException e)
					{ 	input = "";	}
					//input = JOptionPane.showInputDialog("" + t.games + "\nenter next game");
					if ((input == null) || input.trim().equals(""))
					{	finished = true;	}
					else
					{
						if(t.games.remove(input))
						{	t.removeGameFromDatabase(input);	}
					}
				}
			}

			if (choice == 5)
			{
				t.randomizePlayerNumbers();
				t.setupRounds();
				t.runTournament();
			}

		}


	}

	private Tournament (String databaseURL)
	{	DB_URL = databaseURL;		}


	public boolean tournamentBegun()
	{
		try{
			//STEP 4: Execute a query
			System.out.println("Creating statement...");
			stmt = conn.createStatement();
			if(stmt.executeQuery("SELECT _key FROM tblmatch").next())
			{	return true;	}
			return false;
		}
		catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}
		System.exit(0);
		return false;
	}


	public void runTournament()
	{
		//System.out.println(loading);
		List<String> options = new ArrayList<String>();
		options.add("1"); options.add("2"); options.add("3"); options.add("q"); options.add("Q"); options.add("s"); options.add("S");
		boolean done = false;
		while (! done)
		{
			int choice = 0;
			System.out.println("Which action to take next?\n1: assign a game to a match\n2: enter a match result\n3: write current state to html file\nq: quit");
			Prompter response = ConsoleKeyPressPrompter.prompt(null, options);
			while(! response.isDone())
			{}
			try
			{
				if ((response.get().equals("q")) || (response.get().equals("Q")))
				System.exit(0);
				if ((response.get() == "s") || (response.get() =="S"))
				choice = 4;
				else
				choice = new Integer("" + response.get()).intValue();
			}
			catch (InterruptedException e)
			{ 	choice = 0;	}
			catch (ExecutionException e)
			{ 	choice = 0;	}

			if (choice == 1)
			{
				doAssignAGameToAMatch();
			}

			if (choice == 2)
			{
				doEnterMatchResult();
			}


			if (choice == 3)
			{
				doWriteHTMLFile();
			}
		}

	}

	public void doAssignAGameToAMatch()
	{
		int matchNum;
		ResultSet r1;
		int key = 0;

		System.out.print("Enter Match #: ");

		String matchNumList = "";
		Iterator<String> i = loadMatchNumberListFromDatabase().iterator();
		while(i.hasNext())
		{	matchNumList += "|" + i.next();	}

		try
		{
			Prompter getMatchNum = CommandLinePrompter.prompt("Enter Match : ", matchNumList);while(! getMatchNum.isDone()){}
			//matchNum = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter Match #"));
			try
			{
				if (getMatchNum.get().equals(""))
				{	return;					}
				matchNum = Integer.parseInt(getMatchNum.get());
			}
			catch (InterruptedException e)
			{ 	return;	}
			catch (ExecutionException e)
			{ 	return;	}
		}
		catch(Exception e)
		{	return;	}
		try
		{
			String game = "";
			//STEP 4: Execute a query
			//System.out.println("Creating statement...");
			stmt = conn.createStatement();
			if (! stmt.executeQuery("SELECT _key FROM tblmatch WHERE _key = " + matchNum).next())
			{
				System.out.println("Match #" + matchNum + " not found.");
				return;
			}
			System.out.print("Which game will this group be assigned to?: ");
			String gameList = "";
			Iterator<String> it = loadGameListFromDatabase().iterator();
			while(it.hasNext())
			{	gameList += "|" + it.next();	}
			gameList = gameList.replaceAll("\\*", "\\\\*");

			Prompter getGame = CommandLinePrompter.prompt("Which game will this group be assigned to?", gameList);while(! getGame.isDone()){}
			//String game = JOptionPane.showInputDialog("which game will this group be assigned to?");
			try
			{
				game = getGame.get();
			}
			catch (InterruptedException e)
			{ 	game = "";	}
			catch (ExecutionException e)
			{ 	game = "";	}

			if (stmt.executeQuery("SELECT _key FROM tblgame WHERE gamename = \'" + game + "\'").next());
			{
				r1 = stmt.executeQuery("SELECT _key FROM tblgame WHERE gamename = \'" + game + "\'");
				r1.next(); key = r1.getInt("_key");
			}

			assignAGameToAMatch(matchNum, key);
		}
		catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
			System.exit(0);
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}
	}

	public void assignAGameToAMatch(int matchNum, int gameID) throws SQLException
	{
		stmt.executeUpdate("UPDATE tblmatch SET _fk_game = " + gameID + " WHERE _key = " + matchNum);
		stmt.executeUpdate("UPDATE tblscore SET _fk_game = " + gameID + " WHERE _key = " + ((matchNum * 10) + 1));
		stmt.executeUpdate("UPDATE tblscore SET _fk_game = " + gameID + " WHERE _key = " + ((matchNum * 10) + 2));
		stmt.executeUpdate("UPDATE tblscore SET _fk_game = " + gameID + " WHERE _key = " + ((matchNum * 10) + 3));
		stmt.executeUpdate("UPDATE tblscore SET _fk_game = " + gameID + " WHERE _key = " + ((matchNum * 10) + 4));
	}


	public String format(int num)
	{
		StringBuilder sb = new StringBuilder("" + num);
		for (int index = (sb.length() - 1); index > 0; index--)
		{
			if (((sb.length() - index) % 4) == 2)
			{
				sb = sb.insert(index, ',');
			}
		}
		return sb.toString();
	}



	public void doEnterMatchResult()
	{
		int matchNum;
		DecimalFormat df = new DecimalFormat("#,###");

		System.out.print("Enter Match #: ");

		String matchNumList = "";
		Iterator<String> i = loadMatchNumberListFromDatabase().iterator();
		while(i.hasNext())
		{	matchNumList += "|" + i.next();	}

		try
		{
			Prompter getMatchNum = CommandLinePrompter.prompt("Enter Match #: ", matchNumList);while(! getMatchNum.isDone()){}
			//matchNum = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter Match #"));
			try
			{
				if (getMatchNum.get().equals(""))
				{	return;					}
				matchNum = Integer.parseInt(getMatchNum.get());
			}
			catch (InterruptedException e)
			{ 	return;	}
			catch (ExecutionException e)
			{ 	return;	}
		}
		catch(Exception e)
		{	return;	}


		try
		{
			//STEP 4: Execute a query
			System.out.println("Creating statement...");
			stmt = conn.createStatement();
			if (! stmt.executeQuery("SELECT _key FROM tblmatch WHERE _key = " + matchNum).next())
			{
				System.out.println("Match #" + matchNum + " not found.");
				return;
			}
			ResultSet r1 = stmt.executeQuery("SELECT name, s._key FROM tblmatch m JOIN tblscore s on m._fk_score_1 = s._key JOIN tblplayer p ON s._fk_player = p._key WHERE m._key = " + matchNum);
			r1.next(); String player1 = r1.getString("name"); int score1 = r1.getInt("s._key");
			ResultSet r2 = stmt.executeQuery("SELECT name, s._key FROM tblmatch m JOIN tblscore s on m._fk_score_2 = s._key JOIN tblplayer p ON s._fk_player = p._key WHERE m._key = " + matchNum);
			r2.next(); String player2 = r2.getString("name"); int score2 = r2.getInt("s._key");
			ResultSet r3 = stmt.executeQuery("SELECT name, s._key FROM tblmatch m JOIN tblscore s on m._fk_score_3 = s._key JOIN tblplayer p ON s._fk_player = p._key WHERE m._key = " + matchNum);
			r3.next(); String player3 = r3.getString("name"); int score3 = r3.getInt("s._key");
			ResultSet r4 = stmt.executeQuery("SELECT name, s._key FROM tblmatch m JOIN tblscore s on m._fk_score_4 = s._key JOIN tblplayer p ON s._fk_player = p._key WHERE m._key = " + matchNum);
			r4.next(); String player4 = r4.getString("name"); int score4 = r4.getInt("s._key");

			if(! player1.equals("bye"))
			{	System.out.println(player1);	}
			if(! player2.equals("bye"))
			{	System.out.println(player2);	}
			if(! player3.equals("bye"))
			{	System.out.println(player3);	}
			if(! player4.equals("bye"))
			{	System.out.println(player4);	}
			System.out.println("Is this your group? (y/n)");
			List<String> options = new ArrayList<String>();
			options.add("y"); options.add("Y"); options.add("n"); options.add("N");

			Prompter response = ConsoleKeyPressPrompter.prompt(null, options);
			while(! response.isDone())
			{}
			{
				try
				{
					if ((response.get() == "n") || (response.get() =="N"))
					return;
				}
				catch (InterruptedException e)
				{ 	return;	}
				catch (ExecutionException e)
				{ 	return;	}
			}
			ResultSet ones = stmt.executeQuery("SELECT ones from tblgame g JOIN tblmatch m on m._fk_game = g._key where m._key = " + matchNum);
			ones.next(); boolean onesDigit = (ones.getInt("ones") == 1);
			String scoreFormat = (onesDigit ? "([1-9][0-9]{0,2},[0-9]{3})" : "([1-9],([0-9]{3},){2}[0-9]{2}0)|([1-9][0-9]{0,2},([0-9]{3},){0,1}[0-9]{2}0)");

			long player1Score = -1; int player1Points = 0;
			long player2Score = -1; int player2Points = 0;
			long player3Score = -1; int player3Points = 0;
			long player4Score = -1; int player4Points = 0;
			if(! player1.equals("bye"))
			{
				System.out.print("Enter score for " + player1 + ": ");
				try
				{
					Prompter getPlayer1Score = CommandLinePrompter.prompt("Enter score for " + player1 + ": ", scoreFormat);while(! getPlayer1Score.isDone()){}
					//player1Score = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter score for " + player1));
					try
					{
						if (getPlayer1Score.get().equals(""))
						{	return;					}
						player1Score = Long.parseLong(getPlayer1Score.get().replace(",",""));
					}
					catch (InterruptedException e)
					{ 	return;	}
					catch (ExecutionException e)
					{ 	return;	}
				}
				catch(Exception e)
				{	return;	}
			}
			if(! player2.equals("bye"))
			{
				System.out.print("Enter score for " + player2 + ": ");
				try
				{
					Prompter getPlayer2Score = CommandLinePrompter.prompt("Enter score for " + player2 + ": ", scoreFormat);while(! getPlayer2Score.isDone()){}
					//player2Score = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter score for " + player2));
					try
					{
						if (getPlayer2Score.get().equals(""))
						{	return;					}
						player2Score = Long.parseLong(getPlayer2Score.get().replace(",",""));
					}
					catch (InterruptedException e)
					{ 	return;	}
					catch (ExecutionException e)
					{ 	return;	}
				}
				catch(Exception e)
				{	return;	}
			}
			if(! player3.equals("bye"))
			{
				System.out.print("Enter score for " + player3 + ": ");
				try
				{
					Prompter getPlayer3Score = CommandLinePrompter.prompt("Enter score for " + player3 + ": ", scoreFormat);while(! getPlayer3Score.isDone()){}
					//player3Score = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter score for " + player3));
					try
					{
						if (getPlayer3Score.get().equals(""))
						{	return;					}
						player3Score = Long.parseLong(getPlayer3Score.get().replace(",",""));
					}
					catch (InterruptedException e)
					{ 	return;	}
					catch (ExecutionException e)
					{ 	return;	}
				}
				catch(Exception e)
				{	return;	}
			}
			if(! player4.equals("bye"))
			{
				System.out.print("Enter score for " + player4 + ": ");
				try
				{
					Prompter getPlayer4Score = CommandLinePrompter.prompt("Enter score for " + player4 + ": ", scoreFormat);while(! getPlayer4Score.isDone()){}
					//player4Score = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter score for " + player4));
					try
					{
						if (getPlayer4Score.get().equals(""))
						{	return;					}
						player4Score = Long.parseLong(getPlayer4Score.get().replace(",",""));
					}
					catch (InterruptedException e)
					{ 	return;	}
					catch (ExecutionException e)
					{ 	return;	}
				}
				catch(Exception e)
				{	return;	}
			}

			if(! player1.equals("bye"))
			{
				if ((! player2.equals("bye")) && (! player3.equals("bye")) && (! player4.equals("bye")))
				{	player1Points = getPoints(player1Score, player2Score, player3Score, player4Score);	}
				if (player2.equals("bye"))
				{	player1Points = getPoints(player1Score, player3Score, player4Score);	}
				if (player3.equals("bye"))
				{	player1Points = getPoints(player1Score, player2Score, player4Score);	}
				if (player4.equals("bye"))
				{	player1Points = getPoints(player1Score, player2Score, player3Score);	}
			}

			if(! player2.equals("bye"))
			{
				if ((! player1.equals("bye")) && (! player3.equals("bye")) && (! player4.equals("bye")))
				{	player2Points = getPoints(player2Score, player1Score, player3Score, player4Score);	}
				if (player1.equals("bye"))
				{	player2Points = getPoints(player2Score, player3Score, player4Score);	}
				if (player3.equals("bye"))
				{	player2Points = getPoints(player2Score, player1Score, player4Score);	}
				if (player4.equals("bye"))
				{	player2Points = getPoints(player2Score, player1Score, player3Score);	}
			}
			if(! player3.equals("bye"))
			{
				if ((! player1.equals("bye")) && (! player2.equals("bye")) && (! player4.equals("bye")))
				{	player3Points = getPoints(player3Score, player1Score, player2Score, player4Score);	}
				if (player1.equals("bye"))
				{	player3Points = getPoints(player3Score, player2Score, player4Score);	}
				if (player2.equals("bye"))
				{	player3Points = getPoints(player3Score, player1Score, player4Score);	}
				if (player4.equals("bye"))
				{	player3Points = getPoints(player3Score, player1Score, player2Score);	}
			}
			if(! player4.equals("bye"))
			{
				if ((! player1.equals("bye")) && (! player2.equals("bye")) && (! player3.equals("bye")))
				{	player4Points = getPoints(player4Score, player1Score, player2Score, player3Score);	}
				if (player1.equals("bye"))
				{	player4Points = getPoints(player4Score, player2Score, player3Score);	}
				if (player2.equals("bye"))
				{	player4Points = getPoints(player4Score, player1Score, player3Score);	}
				if (player3.equals("bye"))
				{	player4Points = getPoints(player4Score, player1Score, player2Score);	}
			}
			if(! player1.equals("bye"))
			{	System.out.println(player1 + " " + df.format(player1Score) + " " + player1Points + "pts");	}
			if(! player2.equals("bye"))
			{	System.out.println(player2 + " " + df.format(player2Score) + " " + player2Points + "pts");	}
			if(! player3.equals("bye"))
			{	System.out.println(player3 + " " + df.format(player3Score) + " " + player3Points + "pts");	}
			if(! player4.equals("bye"))
			{	System.out.println(player4 + " " + df.format(player4Score)/*format(player4Score)*/ + " " + player4Points + "pts");	}
			System.out.println("Is this information correct?");
			response = ConsoleKeyPressPrompter.prompt(null, options);
			while(! response.isDone())
			{}
			{
				try
				{
					if ((response.get() == "n") || (response.get() =="N"))
					return;
					System.out.println("confirmed");
				}
				catch (InterruptedException e)
				{ 	return;	}
				catch (ExecutionException e)
				{ 	return;	}
			}
			if(! player1.equals("bye"))
			{
				stmt.executeUpdate("UPDATE tblscore SET score = " + player1Score + " WHERE _key = " + ((matchNum * 10) + 1));
				stmt.executeUpdate("UPDATE tblscore SET points = " + player1Points + " WHERE _key = " + ((matchNum * 10) + 1));
			}
			if(! player2.equals("bye"))
			{
				stmt.executeUpdate("UPDATE tblscore SET score = " + player2Score + " WHERE _key = " + ((matchNum * 10) + 2));
				stmt.executeUpdate("UPDATE tblscore SET points = " + player2Points + " WHERE _key = " + ((matchNum * 10) + 2));
			}
			if(! player3.equals("bye"))
			{
				stmt.executeUpdate("UPDATE tblscore SET score = " + player3Score + " WHERE _key = " + ((matchNum * 10) + 3));
				stmt.executeUpdate("UPDATE tblscore SET points = " + player3Points + " WHERE _key = " + ((matchNum * 10) + 3));
			}
			if(! player4.equals("bye"))
			{
				stmt.executeUpdate("UPDATE tblscore SET score = " + player4Score + " WHERE _key = " + ((matchNum * 10) + 4));
				stmt.executeUpdate("UPDATE tblscore SET points = " + player4Points + " WHERE _key = " + ((matchNum * 10) + 4));
			}
		}
		catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
			System.exit(0);
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}



	}

	public int getPoints(long heroScore, long opponent1Score, long opponent2Score, long opponent3Score)
	{
		int opponentsDefeated = 0;
		if (heroScore > opponent1Score)
		{	opponentsDefeated++;	}
		if (heroScore > opponent2Score)
		{	opponentsDefeated++;	}
		if (heroScore > opponent3Score)
		{	opponentsDefeated++;	}

		if (opponentsDefeated == 3)
		{	return 10;	}
		if (opponentsDefeated == 2)
		{	return 6;	}
		if (opponentsDefeated == 1)
		{	return 3;	}
		return 1;
	}


	public int getPoints(long heroScore, long opponent1Score, long opponent2Score)
	{
		int opponentsDefeated = 0;
		if (heroScore > opponent1Score)
		{	opponentsDefeated++;	}
		if (heroScore > opponent2Score)
		{	opponentsDefeated++;	}

		if (opponentsDefeated == 2)
		{	return 10;	}
		if (opponentsDefeated == 1)
		{	return 5;	}
		return 1;
	}


	public void doWriteHTMLFile()
	{
		DecimalFormat df = new DecimalFormat("#,###");
		PrintWriter outputStream = null;
		final int NAMESPACE = 20;
		final int SCORESPACE = 14;
		StringBuilder space = new StringBuilder();
		StringBuilder scoreSpace = new StringBuilder();
		StringBuilder dash = new StringBuilder();
		Map<String, Integer> scores = new HashMap<String, Integer>();
		int rank = 1;
		for (int index = 0; index < NAMESPACE; index++)
		{
			space = space.append(" ");
			dash = dash.append("-");
		}
		for (int index = 0; index < SCORESPACE; index++)
		{
			scoreSpace = scoreSpace.append(" ");
		}
		boolean done = false;
		int round = 1;
		while (! done)
		{
			try{
				rank = 1;
				outputStream = new PrintWriter(new FileOutputStream("r" + round + ".html") );
				//STEP 4: Execute a query
				stmt2 = conn.createStatement();
				System.out.println("Creating statement...");
				if(! (stmt.executeQuery("SELECT _key FROM tblmatch WHERE _key LIKE \'" + round + "%\'")).next())
				{	done = true;			}
				else
				{
					outputStream.println("<html><head><title>2014 Maryland Pinball Championships Results for Round #" + round);
					outputStream.println("</title>");
					outputStream.println("<base href=\"http://mason.gmu.edu/~chorner1/mpc2014\">");
					outputStream.println("</head><body bgcolor=#f0f0f0><center><h3>2014 Maryland Pinball Championships Results for Round #" + round + "</h3></center>");
					outputStream.println("<hr>");
					outputStream.println("<pre>Overall Standings as of " + round + " Round" + (round == 1 ? "," : "s,"));
					outputStream.println();
					outputStream.println("Pos Player Name" + space.substring(11) + " POINTS");
					outputStream.println("--- " + dash + " ------");

					//STEP 4: Execute a query
					System.out.println("Creating statement...");

					ResultSet rs = stmt.executeQuery("SELECT name FROM tblplayer WHERE report = 1");
					while(rs.next()){
        				//Retrieve by column name
						String name = rs.getString("name");
						ResultSet total = stmt2.executeQuery("SELECT SUM(s.points) AS \'scoring\' FROM tblplayer p JOIN tblscore s ON p._key = s._fk_player WHERE p.name = \'" + name + "\' AND s._key < " + (1000 * (round + 1)));
						total.next();
						scores.put(name, total.getInt("scoring"));
					}
					Iterator<String> i = scores.keySet().iterator();
					Vector<String> v = new Vector<String>();
					while (i.hasNext())
					{
						boolean found = false;
						String next = i.next();
						for (int index = 0; index < v.size(); index++)
						{
							if(! found)
							{
								if (scores.get(next) > scores.get(v.elementAt(index)))
								{
									v.insertElementAt(next, index);
									found = true;
								}
							}
						}
						if (! found)
						{
							v.addElement(next);
						}
					}
					i = v.iterator();
					while(i.hasNext())
					{
						String next = i.next();
						outputStream.println("" + (rank >= 10 ? ("" + rank) : (" " + rank)) + "  " + next + (space.substring(next.length()) + "   " + (scores.get(next) < 10 ? (" " + scores.get(next)) : scores.get(next))));
						rank++;
					}
					outputStream.println();

					outputStream.println("Summary of all Matches in Round");

					rs = stmt.executeQuery("SELECT * FROM tblmatch m WHERE _key LIKE \'" + round + "%\'");
					while(rs.next()){
        				//Retrieve by column name
						int key = rs.getInt("_key");
						ResultSet game = stmt2.executeQuery("SELECT g.gamename AS \'game\' from tblgame g JOIN tblmatch m ON m._fk_game = g._key WHERE m._key = " + key);
						game.next(); String thisGame = game.getString("game");

						ResultSet p1Name = stmt2.executeQuery("SELECT name FROM tblmatch m JOIN tblscore s on m._fk_score_1 = s._key JOIN tblplayer p ON s._fk_player = p._key WHERE m._key = " + key);
						p1Name.next(); String name1 = p1Name.getString("name");
						ResultSet p1Score = stmt2.executeQuery("SELECT score FROM tblmatch m JOIN tblscore s on m._fk_score_1 = s._key WHERE m._key = " + key);
						p1Score.next(); String score1 = df.format(p1Score.getLong("score"));
						ResultSet p1Points = stmt2.executeQuery("SELECT points FROM tblmatch m JOIN tblscore s on m._fk_score_1 = s._key WHERE m._key = " + key);
						p1Points.next(); String points1 = "" + p1Points.getInt("points"); if (points1.length() == 1) points1 = " " + points1;

						ResultSet p2Name = stmt2.executeQuery("SELECT name FROM tblmatch m JOIN tblscore s on m._fk_score_2 = s._key JOIN tblplayer p ON s._fk_player = p._key WHERE m._key = " + key);
						p2Name.next(); String name2 = p2Name.getString("name");
						ResultSet p2Score = stmt2.executeQuery("SELECT score FROM tblmatch m JOIN tblscore s on m._fk_score_2 = s._key WHERE m._key = " + key);
						p2Score.next(); String score2 = df.format(p2Score.getLong("score"));
						ResultSet p2Points = stmt2.executeQuery("SELECT points FROM tblmatch m JOIN tblscore s on m._fk_score_2 = s._key WHERE m._key = " + key);
						p2Points.next(); String points2 = "" + p2Points.getInt("points"); if (points2.length() == 1) points2 = " " + points2;

						ResultSet p3Name = stmt2.executeQuery("SELECT name FROM tblmatch m JOIN tblscore s on m._fk_score_3 = s._key JOIN tblplayer p ON s._fk_player = p._key WHERE m._key = " + key);
						p3Name.next(); String name3 = p3Name.getString("name");
						ResultSet p3Score = stmt2.executeQuery("SELECT score FROM tblmatch m JOIN tblscore s on m._fk_score_3 = s._key WHERE m._key = " + key);
						p3Score.next(); String score3 = df.format(p3Score.getLong("score"));
						ResultSet p3Points = stmt2.executeQuery("SELECT points FROM tblmatch m JOIN tblscore s on m._fk_score_3 = s._key WHERE m._key = " + key);
						p3Points.next(); String points3 = "" + p3Points.getInt("points"); if (points3.length() == 1) points3 = " " + points3;

						ResultSet p4Name = stmt2.executeQuery("SELECT name FROM tblmatch m JOIN tblscore s on m._fk_score_4 = s._key JOIN tblplayer p ON s._fk_player = p._key WHERE m._key = " + key);
						p4Name.next(); String name4 = p4Name.getString("name");
						ResultSet p4Score = stmt2.executeQuery("SELECT score FROM tblmatch m JOIN tblscore s on m._fk_score_4 = s._key WHERE m._key = " + key);
						p4Score.next(); String score4 = df.format(p4Score.getLong("score"));
						ResultSet p4Points = stmt2.executeQuery("SELECT points FROM tblmatch m JOIN tblscore s on m._fk_score_4 = s._key WHERE m._key = " + key);
						p4Points.next(); String points4 = "" + p4Points.getInt("points"); if (points4.length() == 1) points4 = " " + points4;

						if (! ((name1.equals("bye")) && (name2.equals("bye")) && (name3.equals("bye")) && (name4.equals("bye"))))
						{
							outputStream.println("Match " + key + space.substring(("Match " + key).length()) + " " + thisGame);
							if (! name1.equals("bye"))
							{	outputStream.println(name1 + space.substring(name1.length()) + scoreSpace.substring(score1.length()) + score1 + " " + points1);	}
							if (! name2.equals("bye"))
							{	outputStream.println(name2 + space.substring(name2.length()) + scoreSpace.substring(score2.length()) + score2 + " " + points2);	}
							if (! name3.equals("bye"))
							{	outputStream.println(name3 + space.substring(name3.length()) + scoreSpace.substring(score3.length()) + score3 + " " + points3);	}
							if (! name4.equals("bye"))
							{	outputStream.println(name4 + space.substring(name4.length()) + scoreSpace.substring(score4.length()) + score4 + " " + points4);	}
						}
					}
					outputStream.println();

					if (round !=1)
					{	outputStream.println("<a href=\"r1.html\">Round 1</a>");	}
					else
					{	outputStream.println("Round 1");	}
					if (round !=2)
					{	outputStream.println("<a href=\"r2.html\">Round 2</a>");	}
					else
					{	outputStream.println("Round 2");	}
					if (round !=3)
					{	outputStream.println("<a href=\"r3.html\">Round 3</a>");	}
					else
					{	outputStream.println("Round 3");	}
					if (round !=4)
					{	outputStream.println("<a href=\"r4.html\">Round 4</a>");	}
					else
					{	outputStream.println("Round 4");	}
					if (round !=5)
					{	outputStream.println("<a href=\"r5.html\">Round 5</a>");	}
					else
					{	outputStream.println("Round 5");	}
					if (round !=6)
					{	outputStream.println("<a href=\"r6.html\">Round 6</a>");	}
					else
					{	outputStream.println("Round 6");	}
					if (round !=7)
					{	outputStream.println("<a href=\"r7.html\">Round 7</a>");	}
					else
					{	outputStream.println("Round 7");	}
					if (round !=8)
					{	outputStream.println("<a href=\"r8.html\">Round 8</a>");	}
					else
					{	outputStream.println("Round 8");	}

					outputStream.println("</pre><hr><p>");
					outputStream.println("</body>");
					outputStream.println("</html>");
					outputStream.close();
				}
			}
			catch(SQLException se){
				//Handle errors for JDBC
				se.printStackTrace();
				System.exit(0);
			}catch(Exception e){
				//Handle errors for Class.forName
				e.printStackTrace();
			}
			round++;
		}
	}


	public void setupRounds()
	{
		if ((players.size() > 28) && (players.size() <= 32))
		{
			//remove slots in order 7-9-18-32
			if (players.size() < 31)
			{	resetPlayerNumber(18,31);	}
			if (players.size() < 30)
			{	resetPlayerNumber(9, 30);	}

			makeMatch(101, 1, 2, 3, 4); makeMatch(102, 5, 6, 7, 8); makeMatch(103, 9, 10, 11, 12); makeMatch(104, 13, 14, 15, 16); makeMatch(105, 17, 18, 19, 20); makeMatch(106, 21, 22, 23, 24); makeMatch(107, 25, 26, 27, 28); makeMatch(108, 29, 30, 31, 32);
			makeMatch(201, 1, 5, 9, 13); makeMatch(202, 2, 6, 10, 14); makeMatch(203, 3, 7, 11, 15); makeMatch(204, 4, 8, 12, 16); makeMatch(205, 17, 21, 25, 29); makeMatch(206, 18, 22, 26, 30); makeMatch(207, 19, 23, 27, 31); makeMatch(208, 20, 24, 28, 32);
			makeMatch(301, 1, 6, 11, 16); makeMatch(302, 2, 5, 12, 15); makeMatch(303, 3, 8, 9, 14); makeMatch(304, 4, 7, 10, 13); makeMatch(305, 17, 22, 27, 32); makeMatch(306, 18, 21, 28, 31); makeMatch(307, 19, 24, 25, 30); makeMatch(308, 20, 23, 26, 29);
			makeMatch(401, 1, 7, 17, 23); makeMatch(402, 2, 8, 18, 24); makeMatch(403, 3, 5, 19, 21); makeMatch(404, 4, 6, 20, 22); makeMatch(405, 9, 15, 25, 31); makeMatch(406, 10, 16, 26, 32); makeMatch(407, 11, 13, 27, 29); makeMatch(408, 12, 14, 28, 30);
			makeMatch(501, 1, 8, 19, 22); makeMatch(502, 2, 7, 20, 21); makeMatch(503, 3, 6, 17, 24); makeMatch(504, 4, 5, 18, 23); makeMatch(505, 9, 16, 27, 30); makeMatch(506, 10, 15, 28, 29); makeMatch(507, 11, 14, 25, 32); makeMatch(508, 12, 13, 26, 31);
			makeMatch(601, 1, 10, 18, 25); makeMatch(602, 2, 9, 17, 26); makeMatch(603, 3, 12, 20, 27); makeMatch(604, 4, 11, 19, 28); makeMatch(605, 5, 14, 22, 29); makeMatch(606, 6, 13, 21, 30); makeMatch(607, 7, 16, 24, 31); makeMatch(608, 8, 15, 23, 32);
			makeMatch(701, 1, 12, 21, 32); makeMatch(702, 2, 11, 22, 31); makeMatch(703, 3, 10, 23, 30); makeMatch(704, 4, 9, 24, 29); makeMatch(705, 5, 16, 17, 28); makeMatch(706, 6, 15, 18, 27); makeMatch(707, 7, 14, 19, 26); makeMatch(708, 8, 13, 20, 25);
			makeMatch(801, 1, 14, 20, 31); makeMatch(802, 2, 13, 19, 32); makeMatch(803, 3, 16, 18, 29); makeMatch(804, 4, 15, 17, 30); makeMatch(805, 5, 10, 24, 27); makeMatch(806, 6, 9, 23, 28); makeMatch(807, 7, 12, 22, 25); makeMatch(808, 8, 11, 21, 26);
		}
		if ((players.size() > 32) && (players.size() <= 36))
		{
			//remove slots in the order 1-16-17-32
			if (players.size() < 35)
			{	resetPlayerNumber(17,35);	}
			if (players.size() < 34)
			{	resetPlayerNumber(16,34);	}

			makeMatch(101, 1, 2, 3, 4); makeMatch(102, 5, 6, 7, 8); makeMatch(103, 9, 10, 11, 12); makeMatch(104, 13, 14, 15, 16); makeMatch(105, 17, 18, 19, 20); makeMatch(106, 21, 22, 23, 24); makeMatch(107, 25, 26, 27, 28); makeMatch(108, 29, 30, 31, 32); makeMatch(109, 33, 34, 35, 36);
			makeMatch(201, 1, 5, 9, 13); makeMatch(202, 2, 6, 10, 17); makeMatch(203, 3, 7, 11, 21); makeMatch(204, 4, 8, 12, 25); makeMatch(205, 14, 18, 22, 29); makeMatch(206, 15, 19, 23, 33); makeMatch(207, 16, 26, 30, 34); makeMatch(208, 20, 27, 31, 35); makeMatch(209, 24, 28, 32, 36);
			makeMatch(301, 1, 6, 11, 25); makeMatch(302, 2, 7, 9, 16); makeMatch(303, 3, 5, 10, 20); makeMatch(304, 4, 14, 21, 33); makeMatch(305, 8, 15, 18, 32); makeMatch(306, 12, 13, 17, 22); makeMatch(307, 19, 27, 30, 36); makeMatch(308, 23, 28, 31, 34); makeMatch(309, 24, 26, 29, 35);
			makeMatch(401, 1, 10, 18, 21); makeMatch(402, 2, 8, 13, 29); makeMatch(403, 3, 14, 31, 36); makeMatch(404, 4, 9, 20, 23); makeMatch(405, 5, 15, 17, 28); makeMatch(406, 6, 12, 16, 19); makeMatch(407, 7, 26, 32, 33); makeMatch(408, 11, 24, 27, 34); makeMatch(409, 22, 25, 30, 35);
			makeMatch(501, 1, 14, 24, 30); makeMatch(502, 2, 12, 18, 34); makeMatch(503, 3, 9, 17, 32); makeMatch(504, 4, 6, 28, 35); makeMatch(505, 5, 21, 29, 36); makeMatch(506, 7, 10, 19, 25); makeMatch(507, 8, 16, 23, 27); makeMatch(508, 11, 13, 20, 33); makeMatch(509, 15, 22, 26, 31);
			makeMatch(601, 1, 12, 23, 26); makeMatch(602, 2, 11, 22, 36); makeMatch(603, 3, 6, 13, 24); makeMatch(604, 4, 10, 16, 32); makeMatch(605, 5, 18, 27, 33); makeMatch(606, 7, 20, 28, 30); makeMatch(607, 8, 14, 19, 35); makeMatch(608, 9, 15, 29, 34); makeMatch(609, 17, 21, 25, 31);
			makeMatch(701, 1, 19, 28, 29); makeMatch(702, 2, 5, 23, 30); makeMatch(703, 3, 12, 15, 27); makeMatch(704, 4, 11, 18, 31); makeMatch(705, 6, 9, 26, 36); makeMatch(706, 7, 14, 17, 34); makeMatch(707, 8, 10, 22, 33); makeMatch(708, 13, 21, 32, 35); makeMatch(709, 16, 20, 24, 25);
			makeMatch(801, 1, 7, 15, 35); makeMatch(802, 2, 14, 25, 32); makeMatch(803, 3, 16, 18, 28); makeMatch(804, 4, 17, 27, 29); makeMatch(805, 5, 11, 19, 26); makeMatch(806, 6, 20, 22, 34); makeMatch(807, 8, 9, 21, 30); makeMatch(808, 10, 13, 23, 36); makeMatch(809, 12, 24, 31, 33);
		}
		if ((players.size() > 36) && (players.size() <= 40))
		{
			//remove slots in the order 3-16-34-40
			if (players.size() < 39)
			{	resetPlayerNumber(34,39);	}
			if (players.size() < 38)
			{	resetPlayerNumber(16,38);	}

			makeMatch(101, 1, 2, 3, 4); makeMatch(102, 5, 6, 7, 8); makeMatch(103, 9, 10, 11, 12); makeMatch(104, 13, 14, 15, 16); makeMatch(105, 17, 18, 19, 20); makeMatch(106, 21, 22, 23, 24); makeMatch(107, 25, 26, 27, 28); makeMatch(108, 29, 30, 31, 32); makeMatch(109, 33, 34, 35, 36); makeMatch(110, 37, 38, 39, 40);
			makeMatch(201, 1, 5, 9, 13); makeMatch(202, 2, 6, 10, 17); makeMatch(203, 3, 7, 11, 21); makeMatch(204, 4, 8, 12, 25); makeMatch(205, 14, 18, 22, 29); makeMatch(206, 15, 19, 23, 33); makeMatch(207, 16, 20, 24, 37); makeMatch(208, 26, 30, 34, 38); makeMatch(209, 27, 31, 35, 39); makeMatch(210, 28, 32, 36, 40);
			makeMatch(301, 1, 6, 11, 25); makeMatch(302, 2, 7, 9, 29); makeMatch(303, 3, 5, 10, 15); makeMatch(304, 4, 13, 17, 24); makeMatch(305, 8, 14, 21, 34); makeMatch(306, 12, 16, 22, 39); makeMatch(307, 18, 23, 28, 37); makeMatch(308, 19, 26, 31, 36); makeMatch(309, 20, 32, 35, 38); makeMatch(310, 27, 30, 33, 40);
			makeMatch(401, 1, 12, 15, 29); makeMatch(402, 2, 22, 28, 34); makeMatch(403, 3, 6, 30, 35); makeMatch(404, 4, 9, 14, 36); makeMatch(405, 5, 25, 33, 37); makeMatch(406, 7, 10, 24, 32); makeMatch(407, 8, 13, 19, 27); makeMatch(408, 11, 17, 23, 40); makeMatch(409, 16, 18, 31, 38); makeMatch(410, 20, 21, 26, 39);
			makeMatch(501, 1, 8, 24, 28); makeMatch(502, 2, 5, 11, 39); makeMatch(503, 3, 19, 22, 25); makeMatch(504, 4, 15, 21, 38); makeMatch(505, 6, 14, 26, 32); makeMatch(506, 7, 18, 35, 40); makeMatch(507, 9, 20, 23, 27); makeMatch(508, 10, 30, 36, 37); makeMatch(509, 12, 13, 31, 34); makeMatch(510, 16, 17, 29, 33);
			makeMatch(601, 1, 14, 17, 39); makeMatch(602, 2, 8, 35, 37); makeMatch(603, 3, 18, 27, 32); makeMatch(604, 4, 22, 31, 33); makeMatch(605, 5, 16, 23, 26); makeMatch(606, 6, 12, 21, 36); makeMatch(607, 7, 13, 28, 38); makeMatch(608, 9, 15, 24, 30); makeMatch(609, 10, 20, 25, 40); makeMatch(610, 11, 19, 29, 34);
			makeMatch(701, 1, 22, 36, 38); makeMatch(702, 2, 23, 25, 32); makeMatch(703, 3, 26, 29, 37); makeMatch(704, 4, 5, 18, 34); makeMatch(705, 6, 13, 33, 39); makeMatch(706, 7, 15, 17, 27); makeMatch(707, 8, 11, 20, 30); makeMatch(708, 9, 21, 28, 31); makeMatch(709, 10, 16, 19, 35); makeMatch(710, 12, 14, 24, 40);
			makeMatch(801, 1, 19, 21, 40); makeMatch(802, 2, 12, 18, 33); makeMatch(803, 3, 8, 17, 31); makeMatch(804, 4, 7, 16, 30); makeMatch(805, 5, 20, 28, 29); makeMatch(806, 6, 24, 27, 34); makeMatch(807, 9, 22, 26, 35); makeMatch(808, 10, 14, 23, 38); makeMatch(809, 11, 13, 32, 37); makeMatch(810, 15, 25, 36, 39);
		}
		if ((players.size() > 41) && (players.size() <= 46))
		{
			if (players.size() == 46)
			{	resetPlayerNumber(21,47);	}
			if (players.size() == 45)
			{	resetPlayerNumber(38,46); resetPlayerNumber(44,47);	}
			if (players.size() == 44)
			{	resetPlayerNumber(21,47); resetPlayerNumber(23,46); resetPlayerNumber(36,45);		}
			if (players.size() == 42)
			{	resetPlayerNumber(38,43); resetPlayerNumber(16,45); resetPlayerNumber(17,46); resetPlayerNumber(21,47);		}


			makeMatch(801, 1, 2, 3, 4); makeMatch(802, 5, 6, 7, 8); makeMatch(803, 9, 10, 11, 12); makeMatch(804, 13, 14, 15, 16); makeMatch(805, 17, 18, 19, 20); makeMatch(806, 21, 22, 23, 24); makeMatch(807, 25, 26, 27, 28); makeMatch(808, 29, 30, 31, 32); makeMatch(809, 33, 34, 35, 36); makeMatch(810, 37, 38, 39, 40); makeMatch(811, 41, 42, 43, 44); makeMatch(812, 45, 46, 47, 48);
			makeMatch(201, 1, 11, 18, 23); makeMatch(202, 5, 3, 22, 15); makeMatch(203, 9, 7, 13, 19); makeMatch(204, 8, 17, 14, 10); makeMatch(205, 2, 12, 20, 21); makeMatch(206, 6, 4, 24, 16); makeMatch(207, 25, 35, 42, 47); makeMatch(208, 29, 27, 46, 39); makeMatch(209, 33, 31, 37, 43); makeMatch(210, 32, 41, 38, 34); makeMatch(211, 26, 36, 44, 45); makeMatch(212, 30, 28, 48, 40);
			makeMatch(301, 1, 13, 12, 24); makeMatch(302, 5, 18, 4, 14); makeMatch(303, 9, 22, 8, 20); makeMatch(304, 15, 2, 17, 7); makeMatch(305, 11, 6, 19, 21); makeMatch(306, 3, 10, 23, 16); makeMatch(307, 25, 37, 36, 48); makeMatch(308, 29, 42, 28, 38); makeMatch(309, 33, 46, 32, 44); makeMatch(310, 39, 26, 41, 31); makeMatch(311, 35, 30, 43, 45); makeMatch(312, 27, 34, 47, 40);
			makeMatch(401, 1, 8, 19, 16); makeMatch(402, 5, 12, 23, 17); makeMatch(403, 9, 4, 15, 21); makeMatch(404, 14, 11, 2, 22); makeMatch(405, 13, 6, 3, 20); makeMatch(406, 18, 10, 7, 24); makeMatch(407, 25, 32, 43, 40); makeMatch(408, 29, 36, 47, 41); makeMatch(409, 33, 28, 39, 45); makeMatch(410, 38, 35, 26, 46); makeMatch(411, 37, 30, 27, 44); makeMatch(412, 42, 34, 31, 48);
			makeMatch(501, 1, 15, 20, 10); makeMatch(502, 5, 19, 24, 2); makeMatch(503, 9, 23, 14, 6); makeMatch(504, 17, 13, 11, 4); makeMatch(505, 8, 3, 18, 21); makeMatch(506, 12, 7, 22, 16); makeMatch(507, 25, 39, 44, 34); makeMatch(508, 29, 43, 48, 26); makeMatch(509, 33, 47, 38, 30); makeMatch(510, 41, 37, 35, 28); makeMatch(511, 32, 27, 42, 45); makeMatch(512, 36, 31, 46, 40);
			makeMatch(601, 1, 14, 21, 7); makeMatch(602, 5, 20, 16, 11); makeMatch(603, 9, 24, 17, 3); makeMatch(604, 2, 8, 13, 23); makeMatch(605, 15, 6, 18, 12); makeMatch(606, 19, 10, 22, 4); makeMatch(607, 25, 38, 45, 31); makeMatch(608, 29, 44, 40, 35); makeMatch(609, 33, 48, 41, 27); makeMatch(610, 26, 32, 37, 47); makeMatch(611, 39, 30, 42, 36); makeMatch(612, 43, 34, 46, 28);
			makeMatch(701, 1, 17, 6, 22); makeMatch(702, 5, 21, 10, 13); makeMatch(703, 9, 16, 2, 18); makeMatch(704, 11, 15, 8, 24); makeMatch(705, 14, 3, 12, 19); makeMatch(706, 20, 7, 4, 23); makeMatch(707, 25, 41, 30, 46); makeMatch(708, 29, 45, 34, 37); makeMatch(709, 33, 40, 26, 42); makeMatch(710, 35, 39, 32, 48); makeMatch(711, 38, 27, 36, 43); makeMatch(712, 44, 31, 28, 47);

			if (players.size() == 42)
			{	makeMatch(101, 1, 5, 9, 0); makeMatch(102, 2, 6, 10, 0); makeMatch(103, 3, 7, 11, 0); makeMatch(104, 4, 8, 12, 0); makeMatch(105, 13, 18, 22, 0); makeMatch(106, 14, 20, 24, 0); makeMatch(107, 15, 19, 23, 0); makeMatch(108, 25, 29, 33, 0); makeMatch(109, 26, 30, 34, 0); makeMatch(110, 27, 31, 35, 0); makeMatch(111, 37, 42, 46, 0); makeMatch(112, 39, 43, 47, 0); makeMatch(113, 40, 41, 45, 0); makeMatch(114, 28, 32, 36, 0); 																}
			if (players.size() == 44)
			{	makeMatch(101, 1, 5, 9, 0); makeMatch(102, 2, 6, 10, 0); makeMatch(103, 3, 7, 11, 0); makeMatch(104, 4, 8, 12, 0); makeMatch(105, 13, 18, 22, 0); makeMatch(106, 14, 20, 24, 0); makeMatch(107, 15, 19, 28, 32); makeMatch(108, 16, 17, 38, 44); makeMatch(109, 25, 29, 33, 0); makeMatch(110, 26, 30, 34, 0); makeMatch(111, 27, 31, 35, 0); makeMatch(112, 37, 42, 46, 0); makeMatch(113, 39, 43, 47, 0); makeMatch(114, 40, 41, 45, 0);																}
			if (players.size() == 45)
			{	makeMatch(101, 1, 5, 9, 0); makeMatch(102, 2, 6, 10, 0); makeMatch(103, 3, 7, 11, 0); makeMatch(104, 4, 8, 12, 0); makeMatch(105, 13, 18, 22, 0); makeMatch(106, 14, 20, 24, 0); makeMatch(107, 15, 19, 23, 0); makeMatch(108, 16, 17, 21, 0); makeMatch(109, 25, 29, 33, 0); makeMatch(110, 26, 30, 34, 0); makeMatch(111, 27, 31, 35, 0); makeMatch(112, 37, 42, 46, 0); makeMatch(113, 39, 43, 47, 0); makeMatch(114, 40, 41, 45, 0); makeMatch(115, 28, 32, 36, 0); 								}
			if (players.size() == 46)
			{	makeMatch(101, 1, 5, 9, 0); makeMatch(102, 2, 6, 10, 0); makeMatch(103, 3, 7, 11, 0); makeMatch(104, 4, 8, 12, 0); makeMatch(105, 13, 18, 22, 0); makeMatch(106, 14, 20, 24, 0); makeMatch(107, 15, 19, 23, 0); makeMatch(108, 16, 17, 38, 44); makeMatch(109, 25, 29, 33, 0); makeMatch(110, 26, 30, 34, 0); makeMatch(111, 27, 31, 35, 0); makeMatch(112, 37, 42, 46, 0); makeMatch(113, 39, 43, 47, 0); makeMatch(114, 40, 41, 45, 0); makeMatch(115, 28, 32, 36, 0);									}
			//if (players.size() == 48)
			//{	makeMatch(801, 1, 5, 9, 0); makeMatch(802, 2, 6, 10, 0); makeMatch(803, 3, 7, 11, 0); makeMatch(804, 4, 8, 12, 0); makeMatch(805, 13, 18, 22, 0); makeMatch(806, 14, 20, 24, 0); makeMatch(807, 15, 19, 23, 0); makeMatch(808, 16, 17, 21, 0); makeMatch(809, 25, 29, 33, 0); makeMatch(810, 26, 30, 34, 0); makeMatch(811, 27, 31, 35, 0); makeMatch(812, 37, 42, 46, 0); makeMatch(813, 39, 43, 47, 0); makeMatch(814, 40, 41, 45, 0); makeMatch(815, 28, 32, 36, 0); makeMatch(816, 38, 44, 48, 0);	}
		}
		if ((players.size() > 46) && (players.size() <= 52))
		{
			//remove slots in the order 11-30-45-52
			if (players.size() < 49)
			{
				if (players.size() == 47)
				{	resetPlayerNumber(31, 48);	}
			}
			else
			{
				if (players.size() < 51)
				{	resetPlayerNumber(45,51);	}
				if (players.size() < 50)
				{	resetPlayerNumber(30,50);	}
			}

			makeMatch(101, 1, 2, 3, 4); makeMatch(102, 5, 6, 7, 8); makeMatch(103, 9, 10, 11, 12); makeMatch(104, 13, 14, 15, 16); makeMatch(105, 17, 18, 19, 20); makeMatch(106, 21, 22, 23, 24); makeMatch(107, 25, 26, 27, 28); makeMatch(108, 29, 30, 31, 32); makeMatch(109, 33, 34, 35, 36); makeMatch(110, 37, 38, 39, 40); makeMatch(111, 41, 42, 43, 44); makeMatch(112, 45, 46, 47, 48); if (players.size() > 48) {makeMatch(113, 49, 50, 51, 52);}
			makeMatch(201, 1, 6, 11, 16); makeMatch(202, 5, 10, 15, 20); makeMatch(203, 9, 14, 19, 24); makeMatch(204, 13, 18, 23, 28); makeMatch(205, 17, 22, 27, 32); makeMatch(206, 21, 26, 31, 36); makeMatch(207, 25, 30, 35, 40); makeMatch(208, 29, 34, 39, 44); makeMatch(209, 33, 38, 43, 48); makeMatch(210, 37, 42, 47, 52); makeMatch(211, 41, 46, 51, 4); makeMatch(212, 45, 50, 3, 8); makeMatch(213, 49, 2, 7, 12);
			makeMatch(301, 1, 10, 7, 24); makeMatch(302, 5, 14, 11, 28); makeMatch(303, 9, 18, 15, 32); makeMatch(304, 13, 22, 19, 36); makeMatch(305, 17, 26, 23, 40); makeMatch(306, 21, 30, 27, 44); makeMatch(307, 25, 34, 31, 48); makeMatch(308, 29, 38, 35, 52); makeMatch(309, 33, 42, 39, 4); makeMatch(310, 37, 46, 43, 8); makeMatch(311, 41, 50, 47, 12); makeMatch(312, 45, 2, 51, 16); makeMatch(313, 49, 6, 3, 20);
			makeMatch(401, 1, 18, 39, 12); makeMatch(402, 5, 22, 43, 16); makeMatch(403, 9, 26, 47, 20); makeMatch(404, 13, 30, 51, 24); makeMatch(405, 17, 34, 3, 28); makeMatch(406, 21, 38, 7, 32); makeMatch(407, 25, 42, 11, 36); makeMatch(408, 29, 46, 15, 40); makeMatch(409, 33, 50, 19, 44); makeMatch(410, 37, 2, 23, 48); makeMatch(411, 41, 6, 27, 52); makeMatch(412, 45, 10, 31, 4); makeMatch(413, 49, 14, 35, 8);
			makeMatch(501, 1, 22, 47, 40); makeMatch(502, 5, 26, 51, 44); makeMatch(503, 9, 30, 3, 48); makeMatch(504, 13, 34, 7, 52); makeMatch(505, 17, 38, 11, 4); makeMatch(506, 21, 42, 15, 8); makeMatch(507, 25, 46, 19, 12); makeMatch(508, 29, 50, 23, 16); makeMatch(509, 33, 2, 27, 20); makeMatch(510, 37, 6, 31, 24); makeMatch(511, 41, 10, 35, 28); makeMatch(512, 45, 14, 39, 32); makeMatch(513, 49, 18, 43, 36);
			makeMatch(601, 1, 26, 15, 52); makeMatch(602, 5, 30, 19, 4); makeMatch(603, 9, 34, 23, 8); makeMatch(604, 13, 38, 27, 12); makeMatch(605, 17, 42, 31, 16); makeMatch(606, 21, 46, 35, 20); makeMatch(607, 25, 50, 39, 24); makeMatch(608, 29, 2, 43, 28); makeMatch(609, 33, 6, 47, 32); makeMatch(610, 37, 10, 51, 36); makeMatch(611, 41, 14, 3, 40); makeMatch(612, 45, 18, 7, 44); makeMatch(613, 49, 22, 11, 48);
			makeMatch(701, 1, 30, 43, 20); makeMatch(702, 5, 34, 47, 24); makeMatch(703, 9, 38, 51, 28); makeMatch(704, 13, 42, 3, 32); makeMatch(705, 17, 46, 7, 36); makeMatch(706, 21, 50, 11, 40); makeMatch(707, 25, 2, 15, 44); makeMatch(708, 29, 6, 19, 48); makeMatch(709, 33, 10, 23, 52); makeMatch(710, 37, 14, 27, 4); makeMatch(711, 41, 18, 31, 8); makeMatch(712, 45, 22, 35, 12); makeMatch(713, 49, 26, 39, 16);
			makeMatch(801, 1, 34, 51, 32); makeMatch(802, 5, 38, 3, 36); makeMatch(803, 9, 42, 7, 40); makeMatch(804, 13, 46, 11, 44); makeMatch(805, 17, 50, 15, 48); makeMatch(806, 21, 2, 19, 52); makeMatch(807, 25, 6, 23, 4); makeMatch(808, 29, 10, 27, 8); makeMatch(809, 33, 14, 31, 12); makeMatch(810, 37, 18, 35, 16); makeMatch(811, 41, 22, 39, 20); makeMatch(812, 45, 26, 43, 24); makeMatch(813, 49, 30, 47, 28);
		}
		if ((players.size() > 53) && (players.size() <= 56))
		{


			if (players.size() == 54)
			{	resetPlayerNumber(28,55);	}
			makeMatch(101,1,2,3,4); makeMatch(102,5,6,7,8); makeMatch(103,9,10,11,12); makeMatch(104,13,14,15,16); makeMatch(105,17,18,19,20); makeMatch(106,21,22,23,24); makeMatch(107,25,26,27,28); makeMatch(108,29,30,31,32); makeMatch(109,33,34,35,36); makeMatch(110,37,38,39,40); makeMatch(111,41,42,43,44); makeMatch(112,45,46,47,48); makeMatch(113,49,50,51,52); makeMatch(114,53,54,55,56);
			makeMatch(201,1,5,21,25); makeMatch(202,2,6,13,17); makeMatch(203,14,18,22,26); makeMatch(204,7,9,19,27); makeMatch(205,8,10,15,23); makeMatch(206,3,11,16,28); makeMatch(207,4,12,20,24); makeMatch(208,29,33,49,53); makeMatch(209,30,34,41,45); makeMatch(210,42,46,50,54); makeMatch(211,35,37,47,55); makeMatch(212,36,38,43,51); makeMatch(213,31,39,44,56); makeMatch(214,32,40,48,52);
			makeMatch(301,1,6,24,28); makeMatch(302,2,5,15,19); makeMatch(303,16,20,23,27); makeMatch(304,8,11,17,26); makeMatch(305,7,12,13,22); makeMatch(306,3,9,14,25); makeMatch(307,4,10,18,21); makeMatch(308,29,34,52,56); makeMatch(309,30,33,43,47); makeMatch(310,44,48,51,55); makeMatch(311,36,39,45,54); makeMatch(312,35,40,41,50); makeMatch(313,31,37,42,53); makeMatch(314,32,38,46,49);
			makeMatch(401,1,9,17,23); makeMatch(402,2,10,14,28); makeMatch(403,5,11,13,24); makeMatch(404,6,12,18,27); makeMatch(405,16,19,21,26); makeMatch(406,3,7,15,20); makeMatch(407,4,8,22,25); makeMatch(408,29,37,45,51); makeMatch(409,30,38,42,56); makeMatch(410,33,39,41,52); makeMatch(411,34,40,46,55); makeMatch(412,44,47,49,54); makeMatch(413,31,35,43,48); makeMatch(414,32,36,50,53);
			makeMatch(501,1,7,16,18); makeMatch(502,2,8,21,27); makeMatch(503,5,12,14,23); makeMatch(504,15,17,22,28); makeMatch(505,6,11,20,25); makeMatch(506,3,10,19,24); makeMatch(507,4,9,13,26); makeMatch(508,29,35,44,46); makeMatch(509,30,36,49,55); makeMatch(510,33,40,42,51); makeMatch(511,43,45,50,56); makeMatch(512,34,39,48,53); makeMatch(513,31,38,47,52); makeMatch(514,32,37,41,54);
			makeMatch(601,1,11,19,22); makeMatch(602,2,12,16,25); makeMatch(603,6,9,15,21); makeMatch(604,5,10,20,26); makeMatch(605,14,17,24,27); makeMatch(606,3,8,13,18); makeMatch(607,4,7,23,28); makeMatch(608,29,39,47,50); makeMatch(609,30,40,44,53); makeMatch(610,34,37,43,49); makeMatch(611,33,38,48,54); makeMatch(612,42,45,52,55); makeMatch(613,31,36,41,46); makeMatch(614,32,35,51,56);
			makeMatch(701,1,8,14,20); makeMatch(702,2,7,24,26); makeMatch(703,6,10,16,22); makeMatch(704,13,19,23,25); makeMatch(705,5,9,18,28); makeMatch(706,3,12,17,21); makeMatch(707,4,11,15,27); makeMatch(708,29,36,42,48); makeMatch(709,30,35,52,54); makeMatch(710,34,38,44,50); makeMatch(711,41,47,51,53); makeMatch(712,33,37,46,56); makeMatch(713,31,40,45,49); makeMatch(714,32,39,43,55);
			makeMatch(801,1,12,15,26); makeMatch(802,2,11,18,23); makeMatch(803,7,10,17,25); makeMatch(804,13,20,21,28); makeMatch(805,8,9,16,24); makeMatch(806,3,5,22,27); makeMatch(807,4,6,14,19); makeMatch(808,29,40,43,54); makeMatch(809,30,39,46,51); makeMatch(810,35,38,45,53); makeMatch(811,41,48,49,56); makeMatch(812,36,37,44,52); makeMatch(813,31,33,50,55); makeMatch(814,32,34,42,47);
			//makeMatch(901,1,10,13,27); makeMatch(902,2,9,20,22); makeMatch(903,3,6,23,26); makeMatch(904,4,5,16,17); makeMatch(905,7,11,14,21); makeMatch(906,8,12,19,28); makeMatch(907,15,18,24,25); makeMatch(908,29,38,41,55); makeMatch(909,30,37,48,50); makeMatch(910,31,34,51,54); makeMatch(911,32,33,44,45); makeMatch(912,35,39,42,49); makeMatch(913,36,40,47,56); makeMatch(914,43,46,52,53);
		}
		if ((players.size() > 56) && (players.size() <= 64))
		{
			//to do: figure this out! remove slots in order 7-39 - 9-41 - 18-50 - 32-64
			if (players.size() < 63)
			{	resetPlayerNumber(32,63);	}
			if (players.size() < 62)
			{	resetPlayerNumber(50,62);	}
			if (players.size() < 61)
			{	resetPlayerNumber(18,61);	}
			if (players.size() < 60)
			{	resetPlayerNumber(41,60);	}
			if (players.size() < 59)
			{	resetPlayerNumber(9,59);	}
			if (players.size() < 58)
			{	resetPlayerNumber(39,58);	}

			makeMatch(101, 1, 2, 3, 4); makeMatch(102, 5, 6, 7, 8); makeMatch(103, 9, 10, 11, 12); makeMatch(104, 13, 14, 15, 16); makeMatch(105, 17, 18, 19, 20); makeMatch(106, 21, 22, 23, 24); makeMatch(107, 25, 26, 27, 28); makeMatch(108, 29, 30, 31, 32); makeMatch(109, 33, 34, 35, 36); makeMatch(110, 37, 38, 39, 40); makeMatch(111, 41, 42, 43, 44); makeMatch(112, 45, 46, 47, 48); makeMatch(113, 49, 50, 51, 52); makeMatch(114, 53, 54, 55, 56); makeMatch(115, 57, 58, 59, 60); makeMatch(116, 61, 62, 63, 64);
			makeMatch(201, 1, 5, 9, 13); makeMatch(202, 2, 6, 10, 14); makeMatch(203, 3, 7, 11, 15); makeMatch(204, 4, 8, 12, 16); makeMatch(205, 17, 21, 25, 29); makeMatch(206, 18, 22, 26, 30); makeMatch(207, 19, 23, 27, 31); makeMatch(208, 20, 24, 28, 32); makeMatch(209, 33, 37, 41, 45); makeMatch(210, 34, 38, 42, 46); makeMatch(211, 35, 39, 43, 47); makeMatch(212, 36, 40, 44, 48); makeMatch(213, 49, 53, 57, 61); makeMatch(214, 50, 54, 58, 62); makeMatch(215, 51, 55, 59, 63); makeMatch(216, 52, 56, 60, 64);
			makeMatch(301, 1, 6, 11, 16); makeMatch(302, 2, 5, 12, 15); makeMatch(303, 3, 8, 9, 14); makeMatch(304, 4, 7, 10, 13); makeMatch(305, 17, 22, 27, 32); makeMatch(306, 18, 21, 28, 31); makeMatch(307, 19, 24, 25, 30); makeMatch(308, 20, 23, 26, 29); makeMatch(309, 33, 38, 43, 48); makeMatch(310, 34, 37, 44, 47); makeMatch(311, 35, 40, 41, 46); makeMatch(312, 36, 39, 42, 45); makeMatch(313, 49, 54, 59, 64); makeMatch(314, 50, 53, 60, 63); makeMatch(315, 51, 56, 57, 62); makeMatch(316, 52, 55, 58, 61);
			makeMatch(401, 1, 7, 17, 23); makeMatch(402, 2, 8, 18, 24); makeMatch(403, 3, 5, 19, 21); makeMatch(404, 4, 6, 20, 22); makeMatch(405, 9, 15, 25, 31); makeMatch(406, 10, 16, 26, 32); makeMatch(407, 11, 13, 27, 29); makeMatch(408, 12, 14, 28, 30); makeMatch(409, 33, 39, 49, 55); makeMatch(410, 34, 40, 50, 56); makeMatch(411, 35, 37, 51, 53); makeMatch(412, 36, 38, 52, 54); makeMatch(413, 41, 47, 57, 63); makeMatch(414, 42, 48, 58, 64); makeMatch(415, 43, 45, 59, 61); makeMatch(416, 44, 46, 60, 62);
			makeMatch(501, 1, 8, 19, 22); makeMatch(502, 2, 7, 20, 21); makeMatch(503, 3, 6, 17, 24); makeMatch(504, 4, 5, 18, 23); makeMatch(505, 9, 16, 27, 30); makeMatch(506, 10, 15, 28, 29); makeMatch(507, 11, 14, 25, 32); makeMatch(508, 12, 13, 26, 31); makeMatch(509, 33, 40, 51, 54); makeMatch(510, 34, 39, 52, 53); makeMatch(511, 35, 38, 49, 56); makeMatch(512, 36, 37, 50, 55); makeMatch(513, 41, 48, 59, 62); makeMatch(514, 42, 47, 60, 61); makeMatch(515, 43, 46, 57, 64); makeMatch(516, 44, 45, 58, 63);
			makeMatch(601, 1, 10, 18, 25); makeMatch(602, 2, 9, 17, 26); makeMatch(603, 3, 12, 20, 27); makeMatch(604, 4, 11, 19, 28); makeMatch(605, 5, 14, 22, 29); makeMatch(606, 6, 13, 21, 30); makeMatch(607, 7, 16, 24, 31); makeMatch(608, 8, 15, 23, 32); makeMatch(609, 33, 42, 50, 57); makeMatch(610, 34, 41, 49, 58); makeMatch(611, 35, 44, 52, 59); makeMatch(612, 36, 43, 51, 60); makeMatch(613, 37, 46, 54, 61); makeMatch(614, 38, 45, 53, 62); makeMatch(615, 39, 48, 56, 63); makeMatch(616, 40, 47, 55, 64);
			makeMatch(701, 1, 12, 21, 32); makeMatch(702, 2, 11, 22, 31); makeMatch(703, 3, 10, 23, 30); makeMatch(704, 4, 9, 24, 29); makeMatch(705, 5, 16, 17, 28); makeMatch(706, 6, 15, 18, 27); makeMatch(707, 7, 14, 19, 26); makeMatch(708, 8, 13, 20, 25); makeMatch(709, 33, 44, 53, 64); makeMatch(710, 34, 43, 54, 63); makeMatch(711, 35, 42, 55, 62); makeMatch(712, 36, 41, 56, 61); makeMatch(713, 37, 48, 49, 60); makeMatch(714, 38, 47, 50, 59); makeMatch(715, 39, 46, 51, 58); makeMatch(716, 40, 45, 52, 57);
			makeMatch(801, 1, 14, 20, 31); makeMatch(802, 2, 13, 19, 32); makeMatch(803, 3, 16, 18, 29); makeMatch(804, 4, 15, 17, 30); makeMatch(805, 5, 10, 24, 27); makeMatch(806, 6, 9, 23, 28); makeMatch(807, 7, 12, 22, 25); makeMatch(808, 8, 11, 21, 26); makeMatch(809, 33, 46, 52, 63); makeMatch(810, 34, 45, 51, 64); makeMatch(811, 35, 48, 50, 61); makeMatch(812, 36, 47, 49, 62); makeMatch(813, 37, 42, 56, 59); makeMatch(814, 38, 41, 55, 60); makeMatch(815, 39, 44, 54, 57); makeMatch(816, 40, 43, 53, 58);
		}
		if ((players.size() > 64) && (players.size() <= 72))
		{
			//remove slots in the order 1-37 - 16-52 - 17-53 - 36-72
			if (players.size() < 71)
			{	resetPlayerNumber(36,71);	}
			if (players.size() < 70)
			{	resetPlayerNumber(53,70);	}
			if (players.size() < 69)
			{	resetPlayerNumber(17,69);	}
			if (players.size() < 68)
			{	resetPlayerNumber(52,68);	}
			if (players.size() < 67)
			{	resetPlayerNumber(16,67);	}
			if (players.size() < 66)
			{	resetPlayerNumber(37,66);	}

			makeMatch(101, 1, 2, 3, 4); makeMatch(102, 5, 6, 7, 8); makeMatch(103, 9, 10, 11, 12); makeMatch(104, 13, 14, 15, 16); makeMatch(105, 17, 18, 19, 20); makeMatch(106, 21, 22, 23, 24); makeMatch(107, 25, 26, 27, 28); makeMatch(108, 29, 30, 31, 32); makeMatch(109, 33, 34, 35, 36); makeMatch(110, 37, 38, 39, 40); makeMatch(111, 41, 42, 43, 44); makeMatch(112, 45, 46, 47, 48); makeMatch(113, 49, 50, 51, 52); makeMatch(114, 53, 54, 55, 56); makeMatch(115, 57, 58, 59, 60); makeMatch(116, 61, 62, 63, 64); makeMatch(117, 65, 66, 67, 68); makeMatch(118, 69, 70, 71, 72);
			makeMatch(201, 1, 5, 9, 13); makeMatch(202, 2, 6, 10, 17); makeMatch(203, 3, 7, 11, 21); makeMatch(204, 4, 8, 12, 25); makeMatch(205, 14, 18, 22, 29); makeMatch(206, 15, 19, 23, 33); makeMatch(207, 16, 26, 30, 34); makeMatch(208, 20, 27, 31, 35); makeMatch(209, 24, 28, 32, 36); makeMatch(210, 37, 41, 45, 49); makeMatch(211, 38, 42, 46, 53); makeMatch(212, 39, 43, 47, 57); makeMatch(213, 40, 44, 48, 61); makeMatch(214, 50, 54, 58, 65); makeMatch(215, 51, 55, 59, 69); makeMatch(216, 52, 62, 66, 70); makeMatch(217, 56, 63, 67, 71); makeMatch(218, 60, 64, 68, 72);
			makeMatch(301, 1, 6, 11, 25); makeMatch(302, 2, 7, 9, 16); makeMatch(303, 3, 5, 10, 20); makeMatch(304, 4, 14, 21, 33); makeMatch(305, 8, 15, 18, 32); makeMatch(306, 12, 13, 17, 22); makeMatch(307, 19, 27, 30, 36); makeMatch(308, 23, 28, 31, 34); makeMatch(309, 24, 26, 29, 35); makeMatch(310, 37, 42, 47, 61); makeMatch(311, 38, 43, 45, 52); makeMatch(312, 39, 41, 46, 56); makeMatch(313, 40, 50, 57, 69); makeMatch(314, 44, 51, 54, 68); makeMatch(315, 48, 49, 53, 58); makeMatch(316, 55, 63, 66, 72); makeMatch(317, 59, 64, 67, 70); makeMatch(318, 60, 62, 65, 71);
			makeMatch(401, 1, 10, 18, 21); makeMatch(402, 2, 8, 13, 29); makeMatch(403, 3, 14, 31, 36); makeMatch(404, 4, 9, 20, 23); makeMatch(405, 5, 15, 17, 28); makeMatch(406, 6, 12, 16, 19); makeMatch(407, 7, 26, 32, 33); makeMatch(408, 11, 24, 27, 34); makeMatch(409, 22, 25, 30, 35); makeMatch(410, 37, 46, 54, 57); makeMatch(411, 38, 44, 49, 65); makeMatch(412, 39, 50, 67, 72); makeMatch(413, 40, 45, 56, 59); makeMatch(414, 41, 51, 53, 64); makeMatch(415, 42, 48, 52, 55); makeMatch(416, 43, 62, 68, 69); makeMatch(417, 47, 60, 63, 70); makeMatch(418, 58, 61, 66, 71);
			makeMatch(501, 1, 14, 24, 30); makeMatch(502, 2, 12, 18, 34); makeMatch(503, 3, 9, 17, 32); makeMatch(504, 4, 6, 28, 35); makeMatch(505, 5, 21, 29, 36); makeMatch(506, 7, 10, 19, 25); makeMatch(507, 8, 16, 23, 27); makeMatch(508, 11, 13, 20, 33); makeMatch(509, 15, 22, 26, 31); makeMatch(510, 37, 50, 60, 66); makeMatch(511, 38, 48, 54, 70); makeMatch(512, 39, 45, 53, 68); makeMatch(513, 40, 42, 64, 71); makeMatch(514, 41, 57, 65, 72); makeMatch(515, 43, 46, 55, 61); makeMatch(516, 44, 52, 59, 63); makeMatch(517, 47, 49, 56, 69); makeMatch(518, 51, 58, 62, 67);
			makeMatch(601, 1, 12, 23, 26); makeMatch(602, 2, 11, 22, 36); makeMatch(603, 3, 6, 13, 24); makeMatch(604, 4, 10, 16, 32); makeMatch(605, 5, 18, 27, 33); makeMatch(606, 7, 20, 28, 30); makeMatch(607, 8, 14, 19, 35); makeMatch(608, 9, 15, 29, 34); makeMatch(609, 17, 21, 25, 31); makeMatch(610, 37, 48, 59, 62); makeMatch(611, 38, 47, 58, 72); makeMatch(612, 39, 42, 49, 60); makeMatch(613, 40, 46, 52, 68); makeMatch(614, 41, 54, 63, 69); makeMatch(615, 43, 56, 64, 66); makeMatch(616, 44, 50, 55, 71); makeMatch(617, 45, 51, 65, 70); makeMatch(618, 53, 57, 61, 67);
			makeMatch(701, 1, 19, 28, 29); makeMatch(702, 2, 5, 23, 30); makeMatch(703, 3, 12, 15, 27); makeMatch(704, 4, 11, 18, 31); makeMatch(705, 6, 9, 26, 36); makeMatch(706, 7, 14, 17, 34); makeMatch(707, 8, 10, 22, 33); makeMatch(708, 13, 21, 32, 35); makeMatch(709, 16, 20, 24, 25); makeMatch(710, 37, 55, 64, 65); makeMatch(711, 38, 41, 59, 66); makeMatch(712, 39, 48, 51, 63); makeMatch(713, 40, 47, 54, 67); makeMatch(714, 42, 45, 62, 72); makeMatch(715, 43, 50, 53, 70); makeMatch(716, 44, 46, 58, 69); makeMatch(717, 49, 57, 68, 71); makeMatch(718, 52, 56, 60, 61);
			makeMatch(801, 1, 7, 15, 35); makeMatch(802, 2, 14, 25, 32); makeMatch(803, 3, 16, 18, 28); makeMatch(804, 4, 17, 27, 29); makeMatch(805, 5, 11, 19, 26); makeMatch(806, 6, 20, 22, 34); makeMatch(807, 8, 9, 21, 30); makeMatch(808, 10, 13, 23, 36); makeMatch(809, 12, 24, 31, 33); makeMatch(810, 37, 43, 51, 71); makeMatch(811, 38, 50, 61, 68); makeMatch(812, 39, 52, 54, 64); makeMatch(813, 40, 53, 63, 65); makeMatch(814, 41, 47, 55, 62); makeMatch(815, 42, 56, 58, 70); makeMatch(816, 44, 45, 57, 66); makeMatch(817, 46, 49, 59, 72); makeMatch(818, 48, 60, 67, 69);
		}
		if ((players.size() > 72) && (players.size() <= 80))
		{
			//remove slots in the order 34-74 - 3-43 - 16-56 - 40-80
			if (players.size() < 79)
			{	resetPlayerNumber(40,79);	}
			if (players.size() < 78)
			{	resetPlayerNumber(56,78);	}
			if (players.size() < 77)
			{	resetPlayerNumber(16,77);	}
			if (players.size() < 76)
			{	resetPlayerNumber(43,76);	}
			if (players.size() < 75)
			{	resetPlayerNumber(3,75);	}


			makeMatch(101, 1, 2, 3, 4); makeMatch(102, 5, 6, 7, 8); makeMatch(103, 9, 10, 11, 12); makeMatch(104, 13, 14, 15, 16); makeMatch(105, 17, 18, 19, 20); makeMatch(106, 21, 22, 23, 24); makeMatch(107, 25, 26, 27, 28); makeMatch(108, 29, 30, 31, 32); makeMatch(109, 33, 34, 35, 36); makeMatch(110, 37, 38, 39, 40); makeMatch(111, 41, 42, 43, 44); makeMatch(112, 45, 46, 47, 48); makeMatch(113, 49, 50, 51, 52); makeMatch(114, 53, 54, 55, 56); makeMatch(115, 57, 58, 59, 60); makeMatch(116, 61, 62, 63, 64); makeMatch(117, 65, 66, 67, 68); makeMatch(118, 69, 70, 71, 72); makeMatch(119, 73, 74, 75, 76); makeMatch(120, 77, 78, 79, 80);
			makeMatch(201, 1, 5, 9, 13); makeMatch(202, 2, 6, 10, 17); makeMatch(203, 3, 7, 11, 21); makeMatch(204, 4, 8, 12, 25); makeMatch(205, 14, 18, 22, 29); makeMatch(206, 15, 19, 23, 33); makeMatch(207, 16, 20, 24, 37); makeMatch(208, 26, 30, 34, 38); makeMatch(209, 27, 31, 35, 39); makeMatch(210, 28, 32, 36, 40); makeMatch(211, 41, 45, 49, 53); makeMatch(212, 42, 46, 50, 57); makeMatch(213, 43, 47, 51, 61); makeMatch(214, 44, 48, 52, 65); makeMatch(215, 54, 58, 62, 69); makeMatch(216, 55, 59, 63, 73); makeMatch(217, 56, 60, 64, 77); makeMatch(218, 66, 70, 74, 78); makeMatch(219, 67, 71, 75, 79); makeMatch(220, 68, 72, 76, 80);
			makeMatch(301, 1, 6, 11, 25); makeMatch(302, 2, 7, 9, 29); makeMatch(303, 3, 5, 10, 15); makeMatch(304, 4, 13, 17, 24); makeMatch(305, 8, 14, 21, 34); makeMatch(306, 12, 16, 22, 39); makeMatch(307, 18, 23, 28, 37); makeMatch(308, 19, 26, 31, 36); makeMatch(309, 20, 32, 35, 38); makeMatch(310, 27, 30, 33, 40); makeMatch(311, 41, 46, 51, 65); makeMatch(312, 42, 47, 49, 69); makeMatch(313, 43, 45, 50, 55); makeMatch(314, 44, 53, 57, 64); makeMatch(315, 48, 54, 61, 74); makeMatch(316, 52, 56, 62, 79); makeMatch(317, 58, 63, 68, 77); makeMatch(318, 59, 66, 71, 76); makeMatch(319, 60, 72, 75, 78); makeMatch(320, 67, 70, 73, 80);
			makeMatch(401, 1, 12, 15, 29); makeMatch(402, 2, 22, 28, 34); makeMatch(403, 3, 6, 30, 35); makeMatch(404, 4, 9, 14, 36); makeMatch(405, 5, 25, 33, 37); makeMatch(406, 7, 10, 24, 32); makeMatch(407, 8, 13, 19, 27); makeMatch(408, 11, 17, 23, 40); makeMatch(409, 16, 18, 31, 38); makeMatch(410, 20, 21, 26, 39); makeMatch(411, 41, 52, 55, 69); makeMatch(412, 42, 62, 68, 74); makeMatch(413, 43, 46, 70, 75); makeMatch(414, 44, 49, 54, 76); makeMatch(415, 45, 65, 73, 77); makeMatch(416, 47, 50, 64, 72); makeMatch(417, 48, 53, 59, 67); makeMatch(418, 51, 57, 63, 80); makeMatch(419, 56, 58, 71, 78); makeMatch(420, 60, 61, 66, 79);
			makeMatch(501, 1, 8, 24, 28); makeMatch(502, 2, 5, 11, 39); makeMatch(503, 3, 19, 22, 25); makeMatch(504, 4, 15, 21, 38); makeMatch(505, 6, 14, 26, 32); makeMatch(506, 7, 18, 35, 40); makeMatch(507, 9, 20, 23, 27); makeMatch(508, 10, 30, 36, 37); makeMatch(509, 12, 13, 31, 34); makeMatch(510, 16, 17, 29, 33); makeMatch(511, 41, 48, 64, 68); makeMatch(512, 42, 45, 51, 79); makeMatch(513, 43, 59, 62, 65); makeMatch(514, 44, 55, 61, 78); makeMatch(515, 46, 54, 66, 72); makeMatch(516, 47, 58, 75, 80); makeMatch(517, 49, 60, 63, 67); makeMatch(518, 50, 70, 76, 77); makeMatch(519, 52, 53, 71, 74); makeMatch(520, 56, 57, 69, 73);
			makeMatch(601, 1, 14, 17, 39); makeMatch(602, 2, 8, 35, 37); makeMatch(603, 3, 18, 27, 32); makeMatch(604, 4, 22, 31, 33); makeMatch(605, 5, 16, 23, 26); makeMatch(606, 6, 12, 21, 36); makeMatch(607, 7, 13, 28, 38); makeMatch(608, 9, 15, 24, 30); makeMatch(609, 10, 20, 25, 40); makeMatch(610, 11, 19, 29, 34); makeMatch(611, 41, 54, 57, 79); makeMatch(612, 42, 48, 75, 77); makeMatch(613, 43, 58, 67, 72); makeMatch(614, 44, 62, 71, 73); makeMatch(615, 45, 56, 63, 66); makeMatch(616, 46, 52, 61, 76); makeMatch(617, 47, 53, 68, 78); makeMatch(618, 49, 55, 64, 70); makeMatch(619, 50, 60, 65, 80); makeMatch(620, 51, 59, 69, 74);
			makeMatch(701, 1, 22, 36, 38); makeMatch(702, 2, 23, 25, 32); makeMatch(703, 3, 26, 29, 37); makeMatch(704, 4, 5, 18, 34); makeMatch(705, 6, 13, 33, 39); makeMatch(706, 7, 15, 17, 27); makeMatch(707, 8, 11, 20, 30); makeMatch(708, 9, 21, 28, 31); makeMatch(709, 10, 16, 19, 35); makeMatch(710, 12, 14, 24, 40); makeMatch(711, 41, 62, 76, 78); makeMatch(712, 42, 63, 65, 72); makeMatch(713, 43, 66, 69, 77); makeMatch(714, 44, 45, 58, 74); makeMatch(715, 46, 53, 73, 79); makeMatch(716, 47, 55, 57, 67); makeMatch(717, 48, 51, 60, 70); makeMatch(718, 49, 61, 68, 71); makeMatch(719, 50, 56, 59, 75); makeMatch(720, 52, 54, 64, 80);
			makeMatch(801, 1, 19, 21, 40); makeMatch(802, 2, 12, 18, 33); makeMatch(803, 3, 8, 17, 31); makeMatch(804, 4, 7, 16, 30); makeMatch(805, 5, 20, 28, 29); makeMatch(806, 6, 24, 27, 34); makeMatch(807, 9, 22, 26, 35); makeMatch(808, 10, 14, 23, 38); makeMatch(809, 11, 13, 32, 37); makeMatch(810, 15, 25, 36, 39); makeMatch(811, 41, 59, 61, 80); makeMatch(812, 42, 52, 58, 73); makeMatch(813, 43, 48, 57, 71); makeMatch(814, 44, 47, 56, 70); makeMatch(815, 45, 60, 68, 69); makeMatch(816, 46, 64, 67, 74); makeMatch(817, 49, 62, 66, 75); makeMatch(818, 50, 54, 63, 78); makeMatch(819, 51, 53, 72, 77); makeMatch(820, 55, 65, 76, 79);
		}

	}

	public void makeMatch(int matchNum, int player1, int player2, int player3, int player4)
	{
		int[] randomPlayer = randomizer(player1, player2, player3, player4);
		player1 = randomPlayer[0]; player2 = randomPlayer[1]; player3 = randomPlayer[2]; player4 = randomPlayer[3];

		try{

			//STEP 4: Execute a query
			System.out.println("Creating statement...");
			stmt = conn.createStatement();
			if(! (stmt.executeQuery("SELECT _key FROM tblscore WHERE _key = " + ((10* matchNum) + 1) )).next())
			{
				if(! (stmt.executeQuery("SELECT _key FROM tblplayer WHERE _key = " + player1)).next())
				{	stmt.executeUpdate("INSERT INTO tblplayer VALUES (" + player1 + ", \'bye\', 0)");	}
				stmt.executeUpdate("INSERT INTO tblscore VALUES ( " + ((10 * matchNum) + 1) + ", " + player1 + ", 9999, -1, 0)");	}
			if(! (stmt.executeQuery("SELECT _key FROM tblscore WHERE _key = " + ((10* matchNum) + 2) )).next())
			{
				if(! (stmt.executeQuery("SELECT _key FROM tblplayer WHERE _key = " + player2)).next())
				{	stmt.executeUpdate("INSERT INTO tblplayer VALUES (" + player2 + ", \'bye\', 0)");	}
				stmt.executeUpdate("INSERT INTO tblscore VALUES ( " + ((10 * matchNum) + 2) + ", " + player2 + ", 9999, -1, 0)");	}
			if(! (stmt.executeQuery("SELECT _key FROM tblscore WHERE _key = " + ((10* matchNum) + 3) )).next())
			{
				if(! (stmt.executeQuery("SELECT _key FROM tblplayer WHERE _key = " + player3)).next())
				{	stmt.executeUpdate("INSERT INTO tblplayer VALUES (" + player3 + ", \'bye\', 0)");	}
				stmt.executeUpdate("INSERT INTO tblscore VALUES ( " + ((10 * matchNum) + 3) + ", " + player3 + ", 9999, -1, 0)");	}
			if(! (stmt.executeQuery("SELECT _key FROM tblscore WHERE _key = " + ((10* matchNum) + 4) )).next())
			{
				if(! (stmt.executeQuery("SELECT _key FROM tblplayer WHERE _key = " + player4)).next())
				{	stmt.executeUpdate("INSERT INTO tblplayer VALUES (" + player4 + ", \'bye\', 0)");	}
				stmt.executeUpdate("INSERT INTO tblscore VALUES ( " + ((10 * matchNum) + 4) + ", " + player4 + ", 9999, -1, 0)");	}

			if(! (stmt.executeQuery("SELECT _key FROM tblmatch WHERE _key = " + matchNum)).next())
			{	stmt.executeUpdate("INSERT INTO tblmatch VALUES ( " + matchNum + ", 9999, " + ((10 * matchNum) + 1) + ", " + ((10 * matchNum) + 2) + ", " + ((10 * matchNum) + 3) + ", " + ((10 * matchNum) + 4) + ")");	}
			return;
		}
		catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}
		System.out.println("something happened");
	}

	public int[] randomizer (int player1, int player2, int player3, int player4)
	{
		int[] destination = new int[4];

		double[] init  = new double[4];
		double[] rando = new double[4];

		init[0] = Math.random();
		init[1] = Math.random();
		init[2] = Math.random();
		init[3] = Math.random();

		rando[0] = init[0];
		rando[1] = init[1];
		rando[2] = init[2];
		rando[3] = init[3];

		Arrays.sort(rando);

		if (init[0] == rando[0])
		{	destination[0] = player1;	}
		if (init[0] == rando[1])
		{	destination[1] = player1;	}
		if (init[0] == rando[2])
		{	destination[2] = player1;	}
		if (init[0] == rando[3])
		{	destination[3] = player1;	}

		if (init[1] == rando[0])
		{	destination[0] = player2;	}
		if (init[1] == rando[1])
		{	destination[1] = player2;	}
		if (init[1] == rando[2])
		{	destination[2] = player2;	}
		if (init[1] == rando[3])
		{	destination[3] = player2;	}

		if (init[2] == rando[0])
		{	destination[0] = player3;	}
		if (init[2] == rando[1])
		{	destination[1] = player3;	}
		if (init[2] == rando[2])
		{	destination[2] = player3;	}
		if (init[2] == rando[3])
		{	destination[3] = player3;	}

		if (init[3] == rando[0])
		{	destination[0] = player4;	}
		if (init[3] == rando[1])
		{	destination[1] = player4;	}
		if (init[3] == rando[2])
		{	destination[2] = player4;	}
		if (init[3] == rando[3])
		{	destination[3] = player4;	}

		return destination;
	}

	public void resetPlayerNumber(int oldKey, int newKey)
	{
		try{
			//STEP 4: Execute a query
			System.out.println("Creating statement...");
			stmt = conn.createStatement();
			stmt.executeUpdate("UPDATE tblplayer SET _key = " + newKey + " WHERE _key = " + oldKey);
			return;
		}
		catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}
		System.exit(0);
	}

	public void addByePlayer(int num)
	{
		try{
			//STEP 4: Execute a query
			System.out.println("Creating statement...");
			stmt = conn.createStatement();
			if(! (stmt.executeQuery("SELECT _key FROM tblplayer WHERE _key = 0")).next())
			{	stmt.executeUpdate("INSERT INTO tblplayer VALUES ( " + num + ", 'bye', 0)");	}
			return;
		}
		catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}
	}


	public void addTBDGame()
	{
		try{
			//STEP 4: Execute a query
			System.out.println("Creating statement...");
			stmt = conn.createStatement();
			if(! (stmt.executeQuery("SELECT _key FROM tblgame WHERE _key = 9999")).next())
			{	stmt.executeUpdate("INSERT INTO tblgame VALUES ( 9999, 'TBD', 0)");	}
			return;
		}
		catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}
	}



	public void randomizePlayerNumbers()
	{
		undoExistingPlayerNumbersFromDatabase();
		int playerNum = 0;
		numberedPlayers = new HashMap<String, Integer>();
		Vector <String> shuffler = new Vector<String>(0);
		Iterator<String> i = players.iterator();
		while (i.hasNext())
		{	shuffler.addElement(i.next());	}
		while (! shuffler.isEmpty() )
		{
			int randomizer = (int)( (Math.random() * shuffler.size() )  );
			numberedPlayers.put(shuffler.elementAt(randomizer), ++playerNum);
			updateDatabasePlayerKey(shuffler.elementAt(randomizer), playerNum);
			shuffler.removeElementAt(randomizer);
		}
		//addByePlayer();
		addTBDGame();
	}

	public void updateDatabasePlayerKey(String name, int key)
	{
		try{
			//STEP 4: Execute a query
			System.out.println("Creating statement...");
			stmt = conn.createStatement();
			stmt.executeUpdate("UPDATE tblplayer SET _key = " + key + " WHERE name = \'" + name + "\'");
			return;
		}
		catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}
		System.exit(0);
	}

	public void undoExistingPlayerNumbersFromDatabase()
	{
		/*int*/ short num = /*Integer*/Short.MAX_VALUE - 1;
		try{
			//STEP 4: Execute a query
			System.out.println("Creating statement...");
			stmt = conn.createStatement();
			Iterator<String> i = players.iterator();
			while(i.hasNext())
			{
				String next = i.next();
				stmt.executeUpdate("UPDATE tblplayer SET _key = " + --num + " WHERE name = \'" + next.trim() + "\'");
			}
			return;
		}
		catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}
		System.exit(0);
	}

	public void removePlayerFromDatabase(String name)
	{
		try{
			//STEP 4: Execute a query
			System.out.println("Creating statement...");
			stmt = conn.createStatement();
			stmt.executeUpdate("DELETE FROM tblplayer WHERE name = \'" + name + "\'");
			return;
		}
		catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}
		System.exit(0);
	}

	public void removeGameFromDatabase(String name)
	{
		try{
			//STEP 4: Execute a query
			System.out.println("Creating statement...");
			stmt = conn.createStatement();
			stmt.executeUpdate("DELETE FROM tblgame WHERE gamename = \'" + name + "\'");
			return;
		}
		catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}
		System.exit(0);
	}



	public void saveGameListToDatabase()
	{
		try{
			//STEP 4: Execute a query
			System.out.println("Creating statement...");
			stmt = conn.createStatement();
			Iterator<String> i = games.iterator();
			while(i.hasNext())
			{
				String next = i.next();
				if(! (stmt.executeQuery("SELECT gamename FROM tblgame WHERE gamename = \'" + next.trim() + "\'").next()))
				{	stmt.executeUpdate("INSERT INTO tblgame VALUES (" + (next.hashCode() % 32768) + ", \'" + next.trim() + "\', 0)");	}
			}
			return;
		}
		catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}
		System.exit(0);
	}

	public void savePlayerListToDatabase()
	{
		try{
			//STEP 4: Execute a query
			//System.out.println("Creating statement...");
			stmt = conn.createStatement();
			Iterator<String> i = players.iterator();
			while(i.hasNext())
			{
				String next = i.next();
				if(! (stmt.executeQuery("SELECT name FROM tblplayer WHERE name = \'" + next.trim() + "\'").next()))
				{	stmt.executeUpdate("INSERT INTO tblplayer VALUES (" + (next.hashCode() % 32768) + ", \'" + next.trim() + "\', 1)");	}
			}
			return;
		}
		catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}
		System.exit(0);
	}

	public List<String> loadGameListFromDatabase()
	{
		try{
			List<String> result = new ArrayList<String>();
			//STEP 4: Execute a query
			//System.out.println("Creating statement...");
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT gamename FROM tblgame");
			while(rs.next()){
        		//Retrieve by column name
				result.add(rs.getString("gamename"));
			}
			return result;
		}
		catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}
		System.exit(0);
		return null;
	}

	public List<String> loadPlayerListFromDatabase()
	{
		try{
			List<String> result = new ArrayList<String>();
			//STEP 4: Execute a query
			System.out.println("Creating statement...");
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT name FROM tblplayer");
			while(rs.next()){
        		//Retrieve by column name
				result.add(rs.getString("name"));
			}
			return result;
		}
		catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}
		System.exit(0);
		return null;
	}


	public List<String> loadMatchNumberListFromDatabase()
	{
		try{
			List<String> result = new ArrayList<String>();
			//STEP 4: Execute a query
			//System.out.println("Creating statement...");
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT _key FROM tblmatch");
			while(rs.next()){
        		//Retrieve by column name
				result.add("" + rs.getInt("_key"));
			}
			return result;
		}
		catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}
		System.exit(0);
		return null;
	}

/*			finally{
			//finally block used to close resources
			try{
				if(stmt!=null)
					conn.close();
			}catch(SQLException se){
			}// do nothing
			try{
				if(conn!=null)
					conn.close();
			}catch(SQLException se){
				se.printStackTrace();
			}//end finally try
		}//end try*/



	public void startDatabaseConnection()
	{
		try{
			//STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			//STEP 3: Open a connection
			System.out.println("Connecting to a selected database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			System.out.println("Connected database successfully...");
			return;
		}
		catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}
		System.exit(0);
	}
}