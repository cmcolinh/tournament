import java.io.FileOutputStream;
//import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Tournament
{
  static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
  private String DB_URL;
  static final String USER = "********";
  static final String PASS = "**********";
  private Connection conn = null;
  private Statement stmt = null;
  //private Statement stmt2 = null;
  private List<String> games;
  private List<String> players;
  Map<String, Integer> numberedPlayers;
  private int competitionNumber = 1;

  public static void main(String[] args)
  {
    Tournament t = new Tournament(args.length == 0 ? "jdbc:mysql://192.168.1.2/test" : args[0]);

    t.startDatabaseConnection();
    if (t.tournamentBegun())
    {
      t.runTournament();
      System.exit(0);
    }
    int i = 0;


		t.games = new ArrayList<String>()
		{
			public static final long serialVersionUID = 0;
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
		{
			@SuppressWarnings("unchecked")
			List<String> players = (List<String>)t.games.getClass().newInstance();
			t.players = players;
			}
		catch (Exception e) {	System.exit(0);	}

    try
    {
      t.games.addAll(t.loadGameListFromDatabase());
      t.players.addAll(t.loadPlayerListFromDatabase());
    }
    catch (SQLException e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    ArrayList<String> options = new ArrayList<String>();
    options.add("1");options.add("2");options.add("3");options.add("4");options.add("5");options.add("q");options.add("Q");
    while (i == 0)
    {
      int j = 0;
      System.out.println("Which action to take next?\n1: add players\n2: delete players\n3: add games\n4: delete games\n5: start tournament\nq: quit");
      Prompter p1 = ConsoleKeyPressPrompter.prompt(null, options);
      while (!p1.isDone()) {}
      try
      {
        if ((p1.get().equals("q")) || (p1.get().equals("Q"))) {
          j = 10;
        } else {
          j = new Integer("" + p1.get()).intValue();
        }
      }
      catch (InterruptedException e)
      {
        j = 0;
      }
      catch (ExecutionException e)
      {
        j = 0;
      }
      if (j == 10) {
        i = 1;
      }
      int k;
      String str;
      if (j == 1)
      {
        k = 0;
        while (k == 0)
        {
          System.out.println(t.players);
          System.out.print("enter next player: ");
          Prompter p2 = CommandLinePrompter.prompt("enter next player", "|[A-Za-z\\- ]{4,20}");
          while (!p2.isDone()) {}
          try
          {
            str = p2.get();
          }
          catch (InterruptedException localInterruptedException2)
          {
            str = "";
          }
          catch (ExecutionException localExecutionException2)
          {
            str = "";
          }
          if ((str == null) || (str.trim().equals("")) ? (k = 1) == 0 : !t.players.add(str)) {}
        }
        try
        {
          t.savePlayerListToDatabase();
        }
        catch (SQLException e)
        {
          e.printStackTrace();
          System.exit(1);
        }
        catch (Exception e)
        {
          e.printStackTrace();
          System.exit(1);
        }
      }

      Prompter p3;
      if (j == 2)
      {
        k = 0;
        while (k == 0)
        {
          String option = "";
          Iterator<String> localIterator1 = t.players.iterator();
          while (localIterator1.hasNext()) {
            option = option + "|" + localIterator1.next();
          }
          System.out.println(t.players);
          System.out.print("enter next player: ");
          for (p3 = CommandLinePrompter.prompt("enter next player", options); !p3.isDone();) {}
          try
          {
            str = p3.get();
          }
          catch (InterruptedException e)
          {
            str = "";
          }
          catch (ExecutionException e)
          {
            str = "";
          }
          if ((str == null) || (str.trim().equals(""))) {
            k = 1;
          } else if (t.players.remove(str)) {
            t.removePlayerFromDatabase(str);
          }
        }
      }
      if (j == 3)
      {
        k = 0;
        while (k == 0)
        {
          System.out.println(t.games);
          System.out.print("enter next game: ");

          p3 = CommandLinePrompter.prompt("enter next game", "|[A-Za-z0-9/&* ]{4,20}");
          while (! p3.isDone()) {}
          try
          {
            str = p3.get();
          }
          catch (InterruptedException e)
          {
            str = "";
          }
          catch (ExecutionException e)
          {
            str = "";
          }
          if ((str == null) || (str.trim().equals("")) ? (k = 1) == 0 : !t.games.add(str)) {}
        }
        t.saveGameListToDatabase();
      }
      if (j == 4)
      {
        k = 0;
        while (k == 0)
        {
          String option = "";
          Iterator<String> localIterator2 = t.games.iterator();
          while (localIterator2.hasNext()) {
            option = option + "|" + localIterator2.next();
          }
          option = option.replaceAll("\\*", "\\\\*");

          System.out.println(t.games);
          System.out.print("enter next game: ");
          for (p3 = CommandLinePrompter.prompt("enter next game", option); !p3.isDone();) {}
          try
          {
            str = p3.get();
          }
          catch (InterruptedException e)
          {
            str = "";
          }
          catch (ExecutionException e)
          {
            str = "";
          }
          if ((str == null) || (str.trim().equals(""))) {
            k = 1;
          } else if (t.games.remove(str)) {
            t.removeGameFromDatabase(str);
          }
        }
      }
      if (j == 5)
      {
        t.setupRounds();
        t.runTournament();
      }
    }
  }

  private Tournament(String paramString)
  {
    this.DB_URL = paramString;
  }

  public boolean tournamentBegun()
  {
    try
    {
      System.out.println("Creating statement...");
      this.stmt = this.conn.createStatement();
      if (this.stmt.executeQuery("SELECT _key FROM tblmatch").next()) {
        return true;
      }
      return false;
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    System.exit(0);
    return false;
  }

  public void runTournament()
  {
    ArrayList<String> options = new ArrayList<String>();
    options.add("1");options.add("2");options.add("3");options.add("q");options.add("Q");options.add("s");options.add("S");
    int i = 0;
    while (i == 0)
    {
      int j = 0;
      System.out.println("Which action to take next?\n1: assign a game to a match\n2: enter a match result\n3: write current state to html file\nq: quit");
      Prompter p = ConsoleKeyPressPrompter.prompt(null, options);
      while (!p.isDone()) {}

      try
      {
		 System.out.println(p.get());


        if ((p.get().equals("q")) || (p.get().equals("Q"))) {
          System.exit(0);
        }
        if ((p.get() == "s") || (p.get() == "S")) {
          j = 4;
        } else {
          j = new Integer("" + p.get()).intValue();
        }
      }
      catch (InterruptedException e)
      {
        j = 0;
      }
      catch (ExecutionException e)
      {
        j = 0;
      }
      try
      {
        if (j == 1) {
          doAssignAGameToAMatch();
        }
        if (j == 2) {
          doEnterMatchResult();
        }
        if (j == 3) {
          doWriteHTMLFile();
        }
      }
      catch (SQLException e)
      {
        e.printStackTrace();
        System.exit(0);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
  }

  public void doAssignAGameToAMatch()
  {
    //int j = 0;

    System.out.print("Enter Match #: ");

    String str1 = "";
    Iterator<String> it1 = loadMatchNumberListFromDatabase().iterator();
    while (it1.hasNext()) {
      str1 = str1 + "|" + it1.next();
    }
    int i;
    try
    {
      Prompter p = CommandLinePrompter.prompt("Enter Match : ", str1);
      while (!p.isDone()) {}
      try
      {
        if (p.get().equals("")) {
          return;
        }
        i = Integer.parseInt(p.get());
      }
      catch (InterruptedException e)
      {
        return;
      }
      catch (ExecutionException e)
      {
        return;
      }
    }
    catch (Exception e)
    {
      return;
    }
    try
    {
      String str2 = "";


      this.stmt = this.conn.createStatement();
      if (!matchNumExists(i))
      {
        System.out.println("Match #" + i + " not found.");
        return;
      }
      System.out.print("Which game will this group be assigned to?: ");
      String str3 = "";
      Iterator<String> it2 = loadGameListFromDatabase().iterator();
      while (it2.hasNext()) {
        str3 = str3 + "|" + it2.next();
      }
      str3 = str3.replaceAll("\\*", "\\\\*");
      Prompter p2 = CommandLinePrompter.prompt("Which game will this group be assigned to?", str3);
      while (! p2.isDone()) {}
      try
      {
        str2 = p2.get();
      }
      catch (InterruptedException e)
      {
        str2 = "";
      }
      catch (ExecutionException e)
      {
        str2 = "";
      }
      if (gameNameExists(str2)) {
        assignAGameToAMatch(i, str2);
      } else {
        System.out.println("No game by that name");
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
      System.exit(0);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public boolean matchNumExists(int matchNum)
    throws SQLException, Exception
  {
    this.stmt = this.conn.createStatement();
    ResultSet localResultSet = this.stmt.executeQuery("SELECT matchNumExists(" + matchNum + ") as e");
    localResultSet.next();return localResultSet.getInt("e") == 1;
  }

  public boolean gameNameExists(String gameName)
    throws SQLException, Exception
  {
    this.stmt = this.conn.createStatement();
    ResultSet localResultSet = this.stmt.executeQuery("SELECT gameNameExists('" + gameName + "') as e");
    localResultSet.next();return localResultSet.getInt("e") == 1;
  }

  public int getKeyForGame(String paramString)
    throws SQLException, Exception
  {
    this.stmt = this.conn.createStatement();
    ResultSet rs = this.stmt.executeQuery("SELECT getGameNumberForGame('" + paramString + "') as ky");
    rs.next();return rs.getInt("ky");
  }

  public void assignAGameToAMatch(int paramInt, String paramString)
    throws SQLException, Exception
  {
    this.stmt.executeUpdate("CALL assignGameToMatch('" + paramInt + "', '" + paramString + "')");
  }

  public String format(int num)
  {
    StringBuilder sb = new StringBuilder("" + num);
    for (int i = sb.length() - 1; i > 0; i--) {
      if ((sb.length() - i) % 4 == 2) {
        sb = sb.insert(i, ',');
      }
    }
    return sb.toString();
  }

  public void doEnterMatchResult()
    throws SQLException, Exception
  {
    System.out.print("Enter Match #: ");

    String str1 = "";
    Iterator<String> localIterator = loadMatchNumberListFromDatabase().iterator();
    while (localIterator.hasNext()) {
      str1 = str1 + "|" + localIterator.next();
    }
    Prompter localPrompter1; for (localPrompter1 = CommandLinePrompter.prompt("Enter Match #: ", str1); !localPrompter1.isDone();) {}
    int i;
    try
    {
      if (localPrompter1.get().equals("")) {
        return;
      }
      i = Integer.parseInt(localPrompter1.get());
    }
    catch (InterruptedException e)
    {
      return;
    }
    catch (ExecutionException e)
    {
      return;
    }
    if (!matchNumExists(i))
    {
      System.out.println("Match #" + i + " not found.");
      return;
    }
    LinkedList<String> playerList = new LinkedList<String>();
    String[] playerArray = new String[0];
    LinkedList<Long> scoreList = new LinkedList<Long>();
    Long[] scoreArray = new Long[0];

    ResultSet localResultSet1 = this.stmt.executeQuery("CALL getPlayerNamesInMatch(" + i + ")");
    while (localResultSet1.next()) {
      playerList.add(localResultSet1.getString("name"));
    }
    for (Iterator<String> iter = playerList.iterator(); iter.hasNext();)
    {
      String play = iter.next();
      System.out.println(play);
    }
    System.out.println("Is this your group? (y/n)");
    List<String> options = new ArrayList<String>();
    options.add("y");options.add("Y");options.add("n");options.add("N");

    Prompter p = ConsoleKeyPressPrompter.prompt(null, options);
    while (! p.isDone()) {}
    try
    {
      if ((p.get() == "n") || (p.get() == "N")) {
        return;
      }
    }
    catch (InterruptedException e)
    {
      return;
    }
    catch (ExecutionException e)
    {
      return;
    }
    ResultSet rs2 = this.stmt.executeQuery("SELECT getScoreRegex(" + i + ") AS regex");


    rs2.next();String str2 = rs2.getString("regex");

    ListIterator<String> li = playerList.listIterator();
    while (li.hasNext())
    {
      System.out.print("Enter score for " + li.next() + ": ");
      try
      {
        Prompter p2 = CommandLinePrompter.prompt("Enter score: ", str2);
        while (!p2.isDone()) {}
        try
        {
          if (p2.get().equals("")) {
            return;
          }
          scoreList.add(Long.valueOf(Long.parseLong(p2.get().replace(",", ""))));
        }
        catch (InterruptedException e)
        {
          return;
        }
        catch (ExecutionException e)
        {
          return;
        }
      }
      catch (Exception e)
      {
        return;
      }
    }
    ResultSet rs3 = this.stmt.executeQuery("SELECT * from tblplayer WHERE 1=0");
    if (playerList.size() <= 4)
    {
      playerArray = new String[4];
      scoreArray = new Long[] {-1L, -1L, -1L, -1L};
      for (int j = 0; j < playerList.size(); j++)
      {
        playerArray[j] = playerList.get(j);
        scoreArray[j] = scoreList.get(j);
      }
      rs3 = this.stmt.executeQuery("CALL previewFourPlayerScoreEntry(\'" + i + "\', \'" + playerArray[0] + "\', \'" + scoreArray[0] + "\', \'" + playerArray[1] + "\', \'" + scoreArray[1] + "\', \'" + playerArray[2] + "\', \'" + scoreArray[2] + "\', \'" + playerArray[3] + "\', \'" + scoreArray[3] + "\')");
    }
    while (rs3.next()) {
      System.out.println(rs3.getString("name") + ": " + rs3.getString("score") + "  " + rs3.getString("points"));
    }
    System.out.println("Is this information correct?");
    Prompter p3 = ConsoleKeyPressPrompter.prompt(null, options);
    while (! p3.isDone()) {}
    try
    {
      if ((p3.get().equals("n")) || (p3.get().equals("N"))) {
        return;
      }
      System.out.println("confirmed");
    }
    catch (InterruptedException e)
    {
      return;
    }
    catch (ExecutionException e)
    {
      return;
    }
    for (int k = 0; k < playerArray.length; k++) {
      this.stmt.executeUpdate("CALL enterScoreByMatchNumberAndName(\'" + i + "\', \'" + playerArray[k] + "\', \'" + scoreArray[k] + "\')");
    }
  }

  public void doWriteHTMLFile()
  {
    PrintWriter out = null;
    try
    {
      this.stmt = this.conn.createStatement();
      ResultSet rs = this.stmt.executeQuery("CALL getFullWebsite(\'" + this.competitionNumber + "\')");
      while (rs.next())
      {
        String str1 = rs.getString("filename");
        String str2 = rs.getString("filetext");

        out = new PrintWriter(new FileOutputStream(str1));
        out.println(str2);

        out.close();
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public void setupRounds()
  {
    try
    {
      this.stmt = this.conn.createStatement();
      this.stmt.executeQuery("SELECT generateRandomMatchupTournament(\'Tournament\', 1)");
      return;
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public void removePlayerFromDatabase(String paramString)
  {
    try
    {
      this.stmt = this.conn.createStatement();
      this.stmt.executeUpdate("CALL removePlayer(\'" + paramString + "\')");
      return;
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    System.exit(0);
  }

  public void removeGameFromDatabase(String paramString)
  {
    try
    {
      this.stmt = this.conn.createStatement();
      this.stmt.executeUpdate("CALL removeGame(\'" + paramString + "\')");
      return;
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    System.exit(0);
  }

  public void addGame(String paramString)
    throws SQLException
  {
    this.stmt = this.conn.createStatement();
    this.stmt.executeUpdate("CALL addGame (\'" + paramString.trim() + "\')");
  }

  public void saveGameListToDatabase()
  {
    try
    {
      Iterator<String> i = this.games.iterator();
      while (i.hasNext())
      {
        String str = i.next();
        addGame(str);
      }
      return;
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    System.exit(0);
  }

  public void addPlayer(String paramString)
  {
    try
    {
      this.stmt = this.conn.createStatement();
      this.stmt.executeUpdate("CALL addPlayer (\'" + paramString.trim() + "\')");
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public void savePlayerListToDatabase()
    throws SQLException, Exception
  {
    Iterator<String> localIterator = this.players.iterator();
    while (localIterator.hasNext())
    {
      String str = localIterator.next();
      addPlayer(str);
    }
  }

  public List<String> loadGameListFromDatabase()
    throws SQLException
  {
    ArrayList<String> gameList = new ArrayList<String>();


    this.stmt = this.conn.createStatement();
    ResultSet localResultSet = this.stmt.executeQuery("CALL getGameNamesInCompetition(\'" + this.competitionNumber + "\')");
    while (localResultSet.next()) {
      gameList.add(localResultSet.getString("gamename"));
    }
    return gameList;
  }

  public List<String> loadPlayerListFromDatabase()
  {
    try
    {
      ArrayList<String> playerList = new ArrayList<String>();

      this.stmt = this.conn.createStatement();
      ResultSet localResultSet = this.stmt.executeQuery("CALL getPlayerNamesInCompetition(\'" + this.competitionNumber + "\')");
      while (localResultSet.next()) {
        playerList.add(localResultSet.getString("name"));
      }
      return playerList;
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    System.exit(0);
    return null;
  }

  public List<String> loadMatchNumberListFromDatabase()
  {
    try
    {
      ArrayList<String> matchNumList = new ArrayList<String>();


      this.stmt = this.conn.createStatement();
      ResultSet localResultSet = this.stmt.executeQuery("CALL getMatchNumbersInCompetition(\'" + this.competitionNumber + "\')");
      while (localResultSet.next()) {
        matchNumList.add("" + localResultSet.getInt("_key"));
      }
      return matchNumList;
    }
    catch (SQLException localSQLException)
    {
      localSQLException.printStackTrace();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    System.exit(0);
    return null;
  }

  public void startDatabaseConnection()
  {
    try
    {
      Class.forName("com.mysql.jdbc.Driver");


      System.out.println("Connecting to a selected database...");
      this.conn = DriverManager.getConnection(this.DB_URL, USER, PASS);
      System.out.println("Connected database successfully...");
      return;
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    System.exit(0);
  }
}
