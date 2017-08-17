DELIMITER //

CREATE PROCEDURE enterScoreByMatchNumberAndName(
 IN matchNum smallint, IN playerName VARCHAR(40), IN gameScore bigint)
BEGIN
 
 CALL enterScore ((SELECT s._key from tblscore s join tblplayer p on s._fk_player = p._key WHERE s._fk_match = matchNum AND p.name = playerName ORDER BY s._KEY DESC LIMIT 1), gameScore);
 
END//


CREATE PROCEDURE enterScore(
 IN scoreNum smallint, IN gameScore bigint)
BEGIN
	
 DECLARE numPlayers int(4);
 DECLARE score int(4);
 DECLARE done BOOLEAN DEFAULT 0;
 
 DECLARE scoreInMatch CURSOR
 FOR
 SELECT _key FROM tblscore WHERE _fk_match = (SELECT _fk_match FROM tblscore WHERE _key = scoreNum LIMIT 1);
 DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done=1;
 
 DELETE FROM tempScore where session_id = connection_id();
 
 INSERT INTO tempScore(_key, _fk_player, _fk_match, _fk_scoreset, score, rank, points, session_id) 
 SELECT s._key, s._fk_player, s._fk_match, s._fk_scoreset, s.score, 0, 0, connection_id() from tblscore s where _fk_match = (SELECT _fk_match FROM tblscore WHERE _key = scoreNum LIMIT 1);
 
 UPDATE tempScore SET score = gameScore WHERE _key = scoreNum AND session_id = connection_id();
 
 DROP TEMPORARY TABLE IF EXISTS rank;
 CREATE TEMPORARY TABLE IF NOT EXISTS rank AS
 (SELECT _key, rank FROM
 (SELECT _key, score, @curRank:=
 IF(@prevRank = score, @curRank, @incRank) AS rank, @incRank := @incRank + 1, @prevRank := score
 FROM tempScore s, (SELECT @curRank := 0, @prevRank := NULL, @incRank := 1 ) r
 WHERE s.score > 0 ORDER BY s.score DESC) t);
 
 DROP TEMPORARY TABLE IF EXISTS numPlayersTbl;
 CREATE TEMPORARY TABLE numPlayersTbl (ct smallint); 
 INSERT INTO numPlayersTbl SELECT COUNT(*) from rank;
 
 UPDATE tempScore s JOIN rank r ON r._key = s._key SET s.rank = r.rank;
 
 SET numPlayers = (SELECT * from numPlayersTbl LIMIT 1);
 DROP TEMPORARY TABLE IF EXISTS numPlayersTbl;
 
 DROP TEMPORARY TABLE IF EXISTS bonusPoints;
 CREATE TEMPORARY TABLE bonusPoints (_key int(4), bonuspoints int(4)); 
 INSERT INTO bonusPoints SELECT s._key, 0 FROM tempScore s JOIN rank r ON r._key = s._key
 WHERE s.session_id = connection_id();
 
 OPEN scoreInMatch;
     REPEAT
     FETCH scoreInMatch INTO score;
        if (SELECT count(*)
            FROM tempScore s
  	        JOIN tblmatch m ON s._fk_match = m._key
  	        JOIN tblscoring sc ON sc._fk_scoringscheme = m._fk_scoringscheme
  	        JOIN tblbonusscoring bs ON bs._fk_scoring = sc._key
            WHERE s._key = score
  	        AND sc.rank = s.rank
  	        AND sc.numplayers = numPlayers > 0)
  	    THEN
     	    CALL getBonusPoints(score, numPlayers);
     	END IF;
     UNTIL done END REPEAT;  
 CLOSE scoreInMatch;
 
 UPDATE tempScore s
 JOIN tblmatch m ON s._fk_match = m._key
 JOIN tblscoring ts ON ts._fk_scoringscheme = m._fk_scoringscheme
 JOIN rank r on r._key = s._key
 JOIN tblplayer p ON s._fk_player = p._key
 JOIN bonusPoints bp ON bp._key = s._key
 SET s.points = ts.pointsforrank + bp.bonuspoints
 WHERE ts.rank = s.rank AND (ts.numplayers = 0 OR ts.numplayers = numPlayers)
 AND session_id = connection_id();
 
 UPDATE tblscore s
 JOIN tempScore ts ON s._key = ts._key
 SET s.points = ts.points,
     s.score = ts.score,
     s.rank = ts.rank
 WHERE ts.session_id = connection_id()
 AND s._key = ts._key;
 
 DELETE FROM tempScore where session_id = connection_id();
 
END//


CREATE PROCEDURE previewFourPlayerScoreEntry(
 IN matchNum smallint, IN player1Name VARCHAR(40), IN player1Score bigint, IN player2Name VARCHAR(40), IN player2Score bigint,
 IN player3Name VARCHAR(40), player3Score bigint, IN player4Name VARCHAR(40), IN player4Score bigint)
BEGIN
 
 DECLARE numPlayers int(4);
 DECLARE score int(4);
 DECLARE done BOOLEAN DEFAULT 0;
 
 DECLARE scoreInMatch CURSOR
 FOR
 SELECT _key FROM tblscore WHERE _fk_match = matchNum;
 DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done=1; 
	
 DELETE FROM tempScore where session_id = connection_id();
 
 INSERT INTO tempScore(_key, _fk_player, _fk_match, _fk_scoreset, score, rank, points, session_id) SELECT _key, _fk_player, _fk_match, _fk_scoreset, score, 0,
 0, connection_id() from tblscore where _fk_match = matchNum;

 UPDATE tempScore ts JOIN tblplayer p on ts._fk_player = p._key 
 SET ts.score = player1Score WHERE p.name = player1Name AND session_id = connection_id();
 
 UPDATE tempScore ts JOIN tblplayer p on ts._fk_player = p._key 
 SET ts.score = player2Score WHERE p.name = player2Name AND session_id = connection_id();
 
 UPDATE tempScore ts JOIN tblplayer p on ts._fk_player = p._key 
 SET ts.score = player3Score WHERE p.name = player3Name AND session_id = connection_id();
 
 UPDATE tempScore ts JOIN tblplayer p on ts._fk_player = p._key 
 SET ts.score = player4Score WHERE p.name = player4Name AND session_id = connection_id();
 
 DROP TEMPORARY TABLE IF EXISTS rank;
 CREATE TEMPORARY TABLE IF NOT EXISTS rank AS
 (SELECT _key, rank FROM
 (SELECT _key, score, @curRank:=
 IF(@prevRank = score, @curRank, @incRank) AS rank, @incRank := @incRank + 1, @prevRank := score
 FROM tempScore s, (SELECT @curRank := 0, @prevRank := NULL, @incRank := 1 ) r
 WHERE s.score > 0 ORDER BY s.score DESC) t);
 
 DROP TEMPORARY TABLE IF EXISTS numPlayersTbl;
 CREATE TEMPORARY TABLE numPlayersTbl (ct smallint); 
 INSERT INTO numPlayersTbl SELECT COUNT(*) from rank;
 
 UPDATE tempScore s JOIN rank r ON r._key = s._key SET s.rank = r.rank
 WHERE session_id = connection_id();
 
 SET numPlayers = (SELECT * from numPlayersTbl LIMIT 1);
 DROP TEMPORARY TABLE IF EXISTS numPlayersTbl;
 
 DROP TEMPORARY TABLE IF EXISTS bonusPoints;
 CREATE TEMPORARY TABLE bonusPoints (_key int(4), bonuspoints int(4)); 
 INSERT INTO bonusPoints SELECT s._key, 0 FROM tempScore s JOIN rank r ON r._key = s._key;
 
  OPEN scoreInMatch;
     REPEAT
     FETCH scoreInMatch INTO score;
        if (SELECT count(*)
            FROM tempScore s
  	        JOIN tblmatch m ON s._fk_match = m._key
  	        JOIN tblscoring sc ON sc._fk_scoringscheme = m._fk_scoringscheme
  	        JOIN tblbonusscoring bs ON bs._fk_scoring = sc._key
            WHERE s._key = score
  	        AND sc.rank = s.rank
  	        AND sc.numplayers = numPlayers > 0)
  	    THEN
     	    CALL getBonusPoints(score, numPlayers);
     	END IF; 
     UNTIL done END REPEAT;  
 CLOSE scoreInMatch; 
 
 
 UPDATE tempScore s
 JOIN tblmatch m ON s._fk_match = m._key
 JOIN tblscoring ts ON ts._fk_scoringscheme = m._fk_scoringscheme
 JOIN rank r on r._key = s._key
 JOIN tblplayer p ON s._fk_player = p._key
 JOIN bonusPoints bp ON bp._key = s._key
 SET s.points = ts.pointsforrank + bp.bonuspoints
 WHERE ts.rank = s.rank AND (ts.numplayers = 0 OR ts.numplayers = numPlayers)
 AND session_id = connection_id();
 
 SELECT p.name, FORMAT(s.score, 0) as score, s.points
 FROM tempScore s JOIN tblplayer p ON s._fk_player = p._key WHERE p.name != 'bye';
 
 DELETE FROM tempScore WHERE session_id = connection_id();
 
 END//
 
 
 CREATE PROCEDURE getBonusPoints (
 IN scoreNum int(4), IN numPlayers int(4))
BEGIN

 DECLARE done BOOLEAN DEFAULT 0;
 DECLARE matchNum int(4);
 DECLARE bonusCheck smallint(6);
 
 DECLARE bonus CURSOR
 FOR
 SELECT bs._key 
 FROM tempScore s
  	JOIN tblmatch m ON s._fk_match = m._key
  	JOIN tblscoring sc ON sc._fk_scoringscheme = m._fk_scoringscheme
  	JOIN tblbonusscoring bs ON bs._fk_scoring = sc._key
  WHERE s._key = scoreNum
  	AND sc.rank = s.rank
  	AND sc.numplayers = numPlayers;
 
 DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done=1; 
 
 UPDATE bonusPoints SET bonuspoints = 0 WHERE _key = scoreNum;
 
 SET matchNum = (SELECT _fk_match from tempScore where _key = scoreNum);
 SET SESSION group_concat_max_len = 1000000; 
 
  OPEN bonus;
 
 REPEAT
 
 FETCH bonus INTO bonusCheck;
 
     SET @S = (SELECT cond FROM tblbonusscoring WHERE _key = bonusCheck);
     SET @ST = CONCAT('SELECT ((SELECT bs.bonuspoints FROM tempScore s JOIN tblmatch m ON s._fk_match = m._key JOIN tblscoring sc ON sc._fk_scoringscheme = m._fk_scoringscheme JOIN tblbonusscoring bs ON bs._fk_scoring = sc._key WHERE bs._key = bonusCheck AND s._key = scoreNum) * (', @S, ')) INTO @OUT');

     SET @ST = REPLACE(@ST, 'bonusCheck', bonusCheck);
     SET @ST = REPLACE(@ST, 'scoreNum', scoreNum);
     SET @ST = REPLACE(@ST, 'matchNum', matchNum);
 
     PREPARE stmt FROM @ST;
     EXECUTE stmt;
 
     if (! done) THEN
         UPDATE bonusPoints SET bonuspoints = bonuspoints + @OUT WHERE _key = scoreNum;
     END IF;
     
 
  UNTIL done END REPEAT;
  
  CLOSE bonus;

END//

 DELIMITER ;