DELIMITER //



CREATE FUNCTION competitionNameExists(
 competitionName VARCHAR(100)) RETURNS tinyint(1)
BEGIN
 
 RETURN (SELECT (SELECT COUNT(*) AS my_bool FROM tblcompetition WHERE name = competitionName) = 1 as playerNameExists);

END//


CREATE FUNCTION createMatch(
 matchNum smallint, scoringScheme smallint) RETURNS smallint(6)
BEGIN
 
 INSERT INTO tblmatch(_key, _fk_game, _fk_scoringscheme) values (matchNum, getGameNumberForGame('TBD'), scoringScheme);
 
 RETURN matchNum;
 
END//



CREATE FUNCTION createScore(
 matchNum smallint, playerNum smallint) RETURNS smallint(6)
BEGIN
 
 INSERT INTO tblscore(_fk_match, _fk_player, score, rank, points ) VALUES (matchNum, playerNum, 0, 0, 0);
 
 RETURN matchNum;
 
END//



CREATE FUNCTION createScoreset(playerNum smallint, competitionNumber smallint) RETURNS smallint(6)
BEGIN
 
 INSERT INTO tblscoreset(_fk_player, _fk_competition) VALUES (playerNum, competitionNumber);
 
 RETURN playerNum;

END//


CREATE FUNCTION gameNameExists(
 name VARCHAR(40)) RETURNS tinyint(1)
BEGIN
 
 RETURN (SELECT (SELECT COUNT(*) AS my_bool FROM tblgame WHERE gamename = name) = 1 as gameNameExists);
 
END//

 
CREATE FUNCTION generateRandomMatchupTournament(competitionName VARCHAR(100), scoringScheme smallint) RETURNS smallint(6)
BEGIN
 
 SET SESSION group_concat_max_len = 1000000;
 
 INSERT INTO tblcompetition(name) VALUES (CONCAT(competitionName));
 SET @competitionNumber = LAST_INSERT_ID();
 
 
 DROP TEMPORARY TABLE IF EXISTS matchList;
 
 DROP TEMPORARY TABLE IF EXISTS ab;
 CREATE TEMPORARY TABLE IF NOT EXISTS ab AS
 (SELECT @row := @row + 1 as row, players.* FROM (SELECT * from tblplayer WHERE report = 1 ORDER BY RAND()) players, (SELECT @row := 0) r);
 
 UPDATE ab SET row = 31 WHERE row = 18 AND (SELECT COUNT(*) FROM tblplayer WHERE report = 1) < 31;
 
 UPDATE ab SET row = 30 WHERE row = 9 AND (SELECT COUNT(*) FROM tblplayer WHERE report = 1) < 30;
 
 UPDATE ab SET _key = getNewByePlayer() where _key = NULL;
 
 UPDATE ab SET _key = createScoreset(_key, @competitionNumber);
 
 DROP TEMPORARY TABLE IF EXISTS matchup;
 CREATE TEMPORARY TABLE IF NOT EXISTS matchup AS
 SELECT 101 as matchNum, 1 as playerNum UNION SELECT 101, 2 UNION SELECT 101, 3 UNION SELECT 101, 4
 UNION SELECT 102, 5 UNION SELECT 102, 6 UNION SELECT 102, 7 UNION SELECT 102, 8
 UNION SELECT 103, 9 UNION SELECT 103, 10 UNION SELECT 103, 11 UNION SELECT 103, 12
 UNION SELECT 104, 13 UNION SELECT 104, 14 UNION SELECT 104, 15 UNION SELECT 104, 16
 UNION SELECT 105, 17 UNION SELECT 105, 18 UNION SELECT 105, 19 UNION SELECT 105, 20
 UNION SELECT 106, 21 UNION SELECT 106, 22 UNION SELECT 106, 23 UNION SELECT 106, 24
 UNION SELECT 107, 25 UNION SELECT 107, 26 UNION SELECT 107, 27 UNION SELECT 107, 28
 UNION SELECT 108, 29 UNION SELECT 108, 30 UNION SELECT 108, 31 UNION SELECT 108, 32
 
 UNION SELECT 201, 1 UNION SELECT 201, 5 UNION SELECT 201, 9 UNION SELECT 201, 13
 UNION SELECT 202, 2 UNION SELECT 202, 6 UNION SELECT 202, 10 UNION SELECT 202, 14
 UNION SELECT 203, 3 UNION SELECT 203, 7 UNION SELECT 203, 11 UNION SELECT 203, 15
 UNION SELECT 204, 4 UNION SELECT 204, 8 UNION SELECT 204, 12 UNION SELECT 204, 16
 UNION SELECT 205, 17 UNION SELECT 205, 21 UNION SELECT 205, 25 UNION SELECT 205, 29
 UNION SELECT 206, 18 UNION SELECT 206, 22 UNION SELECT 206, 26 UNION SELECT 206, 30
 UNION SELECT 207, 19 UNION SELECT 207, 23 UNION SELECT 207, 27 UNION SELECT 207, 31
 UNION SELECT 208, 20 UNION SELECT 208, 24 UNION SELECT 208, 28 UNION SELECT 208, 32
 
 UNION SELECT 301, 1 UNION SELECT 301, 6 UNION SELECT 301, 11 UNION SELECT 301, 16
 UNION SELECT 302, 2 UNION SELECT 302, 5 UNION SELECT 302, 12 UNION SELECT 302, 15
 UNION SELECT 303, 3 UNION SELECT 303, 8 UNION SELECT 303, 9 UNION SELECT 303, 14
 UNION SELECT 304, 4 UNION SELECT 304, 7 UNION SELECT 304, 10 UNION SELECT 304, 13
 UNION SELECT 305, 17 UNION SELECT 305, 22 UNION SELECT 305, 27 UNION SELECT 305, 32
 UNION SELECT 306, 18 UNION SELECT 306, 21 UNION SELECT 306, 28 UNION SELECT 306, 31
 UNION SELECT 307, 19 UNION SELECT 307, 24 UNION SELECT 307, 25 UNION SELECT 307, 30
 UNION SELECT 308, 20 UNION SELECT 308, 23 UNION SELECT 308, 26 UNION SELECT 308, 29
 
 UNION SELECT 401, 1 UNION SELECT 401, 7 UNION SELECT 401, 17 UNION SELECT 401, 23
 UNION SELECT 402, 2 UNION SELECT 402, 8 UNION SELECT 402, 18 UNION SELECT 402, 24
 UNION SELECT 403, 3 UNION SELECT 403, 5 UNION SELECT 403, 19 UNION SELECT 403, 21
 UNION SELECT 404, 4 UNION SELECT 404, 6 UNION SELECT 404, 20 UNION SELECT 404, 22
 UNION SELECT 405, 9 UNION SELECT 405, 15 UNION SELECT 405, 25 UNION SELECT 405, 31
 UNION SELECT 406, 10 UNION SELECT 406, 16 UNION SELECT 406, 26 UNION SELECT 406, 32
 UNION SELECT 407, 11 UNION SELECT 407, 13 UNION SELECT 407, 27 UNION SELECT 407, 29
 UNION SELECT 408, 12 UNION SELECT 408, 14 UNION SELECT 408, 28 UNION SELECT 408, 30
 
 UNION SELECT 501, 1 UNION SELECT 501, 8 UNION SELECT 501, 19 UNION SELECT 501, 22
 UNION SELECT 502, 2 UNION SELECT 502, 7 UNION SELECT 502, 20 UNION SELECT 502, 21
 UNION SELECT 503, 3 UNION SELECT 503, 6 UNION SELECT 503, 17 UNION SELECT 503, 24
 UNION SELECT 504, 4 UNION SELECT 504, 5 UNION SELECT 504, 18 UNION SELECT 504, 23
 UNION SELECT 505, 9 UNION SELECT 505, 16 UNION SELECT 505, 27 UNION SELECT 505, 30
 UNION SELECT 506, 10 UNION SELECT 506, 15 UNION SELECT 506, 28 UNION SELECT 506, 29
 UNION SELECT 507, 11 UNION SELECT 507, 14 UNION SELECT 507, 25 UNION SELECT 507, 32
 UNION SELECT 508, 12 UNION SELECT 508, 13 UNION SELECT 508, 26 UNION SELECT 508, 31
 
 UNION SELECT 601, 1 UNION SELECT 601, 10 UNION SELECT 601, 18 UNION SELECT 601, 25
 UNION SELECT 602, 2 UNION SELECT 602, 9 UNION SELECT 602, 17 UNION SELECT 602, 26
 UNION SELECT 603, 3 UNION SELECT 603, 12 UNION SELECT 603, 20 UNION SELECT 603, 27
 UNION SELECT 604, 4 UNION SELECT 604, 11 UNION SELECT 604, 19 UNION SELECT 604, 28
 UNION SELECT 605, 5 UNION SELECT 605, 14 UNION SELECT 605, 22 UNION SELECT 605, 29
 UNION SELECT 606, 6 UNION SELECT 606, 13 UNION SELECT 606, 21 UNION SELECT 606, 30
 UNION SELECT 607, 7 UNION SELECT 607, 16 UNION SELECT 607, 24 UNION SELECT 607, 31
 UNION SELECT 608, 8 UNION SELECT 608, 15 UNION SELECT 608, 23 UNION SELECT 608, 32

 UNION SELECT 701, 1 UNION SELECT 701, 12 UNION SELECT 701, 21 UNION SELECT 701, 32
 UNION SELECT 702, 2 UNION SELECT 702, 11 UNION SELECT 702, 22 UNION SELECT 702, 31
 UNION SELECT 703, 3 UNION SELECT 703, 10 UNION SELECT 703, 23 UNION SELECT 703, 30
 UNION SELECT 704, 4 UNION SELECT 704, 9 UNION SELECT 704, 24 UNION SELECT 704, 29
 UNION SELECT 705, 5 UNION SELECT 705, 16 UNION SELECT 705, 17 UNION SELECT 705, 28
 UNION SELECT 706, 6 UNION SELECT 706, 15 UNION SELECT 706, 18 UNION SELECT 706, 27
 UNION SELECT 707, 7 UNION SELECT 707, 14 UNION SELECT 707, 19 UNION SELECT 707, 26
 UNION SELECT 708, 8 UNION SELECT 708, 13 UNION SELECT 708, 20 UNION SELECT 708, 25

 UNION SELECT 801, 1 UNION SELECT 801, 14 UNION SELECT 801, 20 UNION SELECT 801, 31
 UNION SELECT 802, 2 UNION SELECT 802, 13 UNION SELECT 802, 19 UNION SELECT 802, 32
 UNION SELECT 803, 3 UNION SELECT 803, 16 UNION SELECT 803, 18 UNION SELECT 803, 29
 UNION SELECT 804, 4 UNION SELECT 804, 15 UNION SELECT 804, 17 UNION SELECT 804, 30
 UNION SELECT 805, 5 UNION SELECT 805, 10 UNION SELECT 805, 24 UNION SELECT 805, 27
 UNION SELECT 806, 6 UNION SELECT 806, 9 UNION SELECT 806, 23 UNION SELECT 806, 28
 UNION SELECT 807, 7 UNION SELECT 807, 12 UNION SELECT 807, 22 UNION SELECT 807, 25
 UNION SELECT 808, 8 UNION SELECT 808, 11 UNION SELECT 808, 21 UNION SELECT 808, 26
 
 ORDER BY matchNum, RAND();

 CALL createTBDGame();
 
 DROP TEMPORARY TABLE IF EXISTS ac;
 CREATE TEMPORARY TABLE IF NOT EXISTS ac AS
 SELECT DISTINCT matchup.playerNum as row, ab._key as _key
 FROM matchup LEFT JOIN ab ON matchup.playerNum = ab.row ORDER BY row;
 
 UPDATE ac SET _key = getNewByePlayer() where _key IS NULL;
 
 DROP TEMPORARY TABLE IF EXISTS scores;
 CREATE TEMPORARY TABLE IF NOT EXISTS scores AS
 SELECT DISTINCT matchup.matchNum as matchNum, ac._key as playerNum
 FROM matchup JOIN ac on matchup.playerNum = ac.row; 
 
 DROP TEMPORARY TABLE IF EXISTS matches;
 CREATE TEMPORARY TABLE IF NOT EXISTS matches AS
 SELECT DISTINCT matchNum as matchNum from matchup;
 
 UPDATE matches SET matchNum = createMatch(10000 * @competitionNumber + matchNum, scoringScheme);
 
 UPDATE scores SET matchNum = createScore((10000 * @competitionNumber + matchNum), playerNum);
 
 UPDATE tblscore s SET _fk_scoreset = (SELECT _key FROM tblscoreset ss WHERE ss._fk_player = s._fk_player AND ss._fk_competition = @competitionNumber LIMIT 1) WHERE inCompetitionSpan(s._fk_match, @competitionNumber);

 INSERT INTO tblwebsitegenerator (_fk_competition, filename, filedef) VALUES (@competitionNumber, 'r1.html', CONCAT ('SELECT CONCAT(\'<html><head><html><head><title>Tournament Results for Round #1\n</title>\n</head><body bgcolor=#f0f0f0><center><h3>Tournament Results for Round #1,</h3></center>\n<hr>\n<pre>Overall Standings as of 1 round,</h3></center>\n\nPos Player Name          POINTS\n--- -------------------- ------\n\', (SELECT GROUP_CONCAT(line SEPARATOR \'\') FROM (SELECT CONCAT(LPAD(rank, 3, \' \'), \' \', RPAD(name, 20, \' \'), \'  \', LPAD(total, 3, \' \'), \'\n\') AS line FROM (SELECT _fk_player, total, CASE WHEN @l=total THEN @r ELSE @r:=@row + 1 END as rank, @l:=total, @row:=@row + 1 FROM (SELECT _fk_player, SUM(points) as total FROM tblscore JOIN tblplayer on tblscore._fk_player = tblplayer._key WHERE _fk_match < ', (SELECT 10000 * @competitionNumber + 200), ' AND _fk_match > ', (SELECT 10000 * @competitionNumber + 100) ,' AND report = 1 GROUP BY _fk_player ORDER BY total DESC) totals, (SELECT @r:=0, @row:=0, @l:=NULL) rank) summary JOIN tblplayer on tblplayer._key = summary._fk_player) a), \'\nSummary of all Matches in Round\n\', (SELECT GROUP_CONCAT(line SEPARATOR \'\n\') FROM (SELECT CONCAT(\'Match \',  m._key , \'         \', g.gamename, \'\n\', GROUP_CONCAT(CONCAT(CAST(RPAD(p.name, 20, \' \') AS CHAR CHARACTER SET utf8), \' \', CAST(LPAD(FORMAT(score, 0), 14, \' \')AS CHAR CHARACTER SET utf8), \' \', CAST(LPAD(s.points, 2, \' \') AS CHAR CHARACTER SET utf8)) SEPARATOR \'\n\')) AS line FROM tblmatch m JOIN tblscore s on s._fk_match = m._key JOIN tblgame g ON m._fk_game = g._key JOIN tblplayer p on p._key = s._fk_player WHERE m._key > ', (SELECT 10000 * @competitionNumber + 100), ' AND m._key < ', (SELECT 10000 * @competitionNumber + 200), ' AND p.name != \'bye\' GROUP BY m._key) a), \'\n\nRound 1\n<a href=\"r2.html\">Round 2</a>\n<a href=\"r3.html\">Round 3</a>\n<a href=\"r4.html\">Round 4</a>\n<a href=\"r5.html\">Round 5</a>\n<a href=\"r6.html\">Round 6</a>\n<a href=\"r7.html\">Round 7</a>\n<a href=\"r8.html\">Round 8</a>\n</pre><hr><p>\n</body>\n</html>\')'));
 INSERT INTO tblwebsitegenerator (_fk_competition, filename, filedef) VALUES (@competitionNumber, 'r2.html', CONCAT ('SELECT CONCAT(\'<html><head><html><head><title>Tournament Results for Round #2\n</title>\n</head><body bgcolor=#f0f0f0><center><h3>Tournament Results for Round #2,</h3></center>\n<hr>\n<pre>Overall Standings as of 2 rounds,</h3></center>\n\nPos Player Name          POINTS\n--- -------------------- ------\n\', (SELECT GROUP_CONCAT(line SEPARATOR \'\') FROM (SELECT CONCAT(LPAD(rank, 3, \' \'), \' \', RPAD(name, 20, \' \'), \'  \', LPAD(total, 3, \' \'), \'\n\') AS line FROM (SELECT _fk_player, total, CASE WHEN @l=total THEN @r ELSE @r:=@row + 1 END as rank, @l:=total, @row:=@row + 1 FROM (SELECT _fk_player, SUM(points) as total FROM tblscore JOIN tblplayer on tblscore._fk_player = tblplayer._key WHERE _fk_match < ', (SELECT 10000 * @competitionNumber + 300), ' AND _fk_match > ', (SELECT 10000 * @competitionNumber + 100) ,' AND report = 1 GROUP BY _fk_player ORDER BY total DESC) totals, (SELECT @r:=0, @row:=0, @l:=NULL) rank) summary JOIN tblplayer on tblplayer._key = summary._fk_player) a), \'\nSummary of all Matches in Round\n\', (SELECT GROUP_CONCAT(line SEPARATOR \'\n\') FROM (SELECT CONCAT(\'Match \',  m._key , \'         \', g.gamename, \'\n\', GROUP_CONCAT(CONCAT(CAST(RPAD(p.name, 20, \' \') AS CHAR CHARACTER SET utf8), \' \', CAST(LPAD(FORMAT(score, 0), 14, \' \')AS CHAR CHARACTER SET utf8), \' \', CAST(LPAD(s.points, 2, \' \') AS CHAR CHARACTER SET utf8)) SEPARATOR \'\n\')) AS line FROM tblmatch m JOIN tblscore s on s._fk_match = m._key JOIN tblgame g ON m._fk_game = g._key JOIN tblplayer p on p._key = s._fk_player WHERE m._key > ', (SELECT 10000 * @competitionNumber + 200), ' AND m._key < ', (SELECT 10000 * @competitionNumber + 300), ' AND p.name != \'bye\' GROUP BY m._key) a), \'\n\n<a href=\"r1.html\">Round 1</a>\nRound 2\n<a href=\"r3.html\">Round 3</a>\n<a href=\"r4.html\">Round 4</a>\n<a href=\"r5.html\">Round 5</a>\n<a href=\"r6.html\">Round 6</a>\n<a href=\"r7.html\">Round 7</a>\n<a href=\"r8.html\">Round 8</a>\n</pre><hr><p>\n</body>\n</html>\')'));
 INSERT INTO tblwebsitegenerator (_fk_competition, filename, filedef) VALUES (@competitionNumber, 'r3.html', CONCAT ('SELECT CONCAT(\'<html><head><html><head><title>Tournament Results for Round #3\n</title>\n</head><body bgcolor=#f0f0f0><center><h3>Tournament Results for Round #3,</h3></center>\n<hr>\n<pre>Overall Standings as of 3 rounds,</h3></center>\n\nPos Player Name          POINTS\n--- -------------------- ------\n\', (SELECT GROUP_CONCAT(line SEPARATOR \'\') FROM (SELECT CONCAT(LPAD(rank, 3, \' \'), \' \', RPAD(name, 20, \' \'), \'  \', LPAD(total, 3, \' \'), \'\n\') AS line FROM (SELECT _fk_player, total, CASE WHEN @l=total THEN @r ELSE @r:=@row + 1 END as rank, @l:=total, @row:=@row + 1 FROM (SELECT _fk_player, SUM(points) as total FROM tblscore JOIN tblplayer on tblscore._fk_player = tblplayer._key WHERE _fk_match < ', (SELECT 10000 * @competitionNumber + 400), ' AND _fk_match > ', (SELECT 10000 * @competitionNumber + 100) ,' AND report = 1 GROUP BY _fk_player ORDER BY total DESC) totals, (SELECT @r:=0, @row:=0, @l:=NULL) rank) summary JOIN tblplayer on tblplayer._key = summary._fk_player) a), \'\nSummary of all Matches in Round\n\', (SELECT GROUP_CONCAT(line SEPARATOR \'\n\') FROM (SELECT CONCAT(\'Match \',  m._key , \'         \', g.gamename, \'\n\', GROUP_CONCAT(CONCAT(CAST(RPAD(p.name, 20, \' \') AS CHAR CHARACTER SET utf8), \' \', CAST(LPAD(FORMAT(score, 0), 14, \' \')AS CHAR CHARACTER SET utf8), \' \', CAST(LPAD(s.points, 2, \' \') AS CHAR CHARACTER SET utf8)) SEPARATOR \'\n\')) AS line FROM tblmatch m JOIN tblscore s on s._fk_match = m._key JOIN tblgame g ON m._fk_game = g._key JOIN tblplayer p on p._key = s._fk_player WHERE m._key > ', (SELECT 10000 * @competitionNumber + 300), ' AND m._key < ', (SELECT 10000 * @competitionNumber + 400), ' AND p.name != \'bye\' GROUP BY m._key) a), \'\n\n<a href=\"r1.html\">Round 1</a>\n<a href=\"r2.html\">Round 2</a>\nRound 3\n<a href=\"r4.html\">Round 4</a>\n<a href=\"r5.html\">Round 5</a>\n<a href=\"r6.html\">Round 6</a>\n<a href=\"r7.html\">Round 7</a>\n<a href=\"r8.html\">Round 8</a>\n</pre><hr><p>\n</body>\n</html>\')'));
 INSERT INTO tblwebsitegenerator (_fk_competition, filename, filedef) VALUES (@competitionNumber, 'r4.html', CONCAT ('SELECT CONCAT(\'<html><head><html><head><title>Tournament Results for Round #4\n</title>\n</head><body bgcolor=#f0f0f0><center><h3>Tournament Results for Round #4,</h3></center>\n<hr>\n<pre>Overall Standings as of 4 rounds,</h3></center>\n\nPos Player Name          POINTS\n--- -------------------- ------\n\', (SELECT GROUP_CONCAT(line SEPARATOR \'\') FROM (SELECT CONCAT(LPAD(rank, 3, \' \'), \' \', RPAD(name, 20, \' \'), \'  \', LPAD(total, 3, \' \'), \'\n\') AS line FROM (SELECT _fk_player, total, CASE WHEN @l=total THEN @r ELSE @r:=@row + 1 END as rank, @l:=total, @row:=@row + 1 FROM (SELECT _fk_player, SUM(points) as total FROM tblscore JOIN tblplayer on tblscore._fk_player = tblplayer._key WHERE _fk_match < ', (SELECT 10000 * @competitionNumber + 500), ' AND _fk_match > ', (SELECT 10000 * @competitionNumber + 100) ,' AND report = 1 GROUP BY _fk_player ORDER BY total DESC) totals, (SELECT @r:=0, @row:=0, @l:=NULL) rank) summary JOIN tblplayer on tblplayer._key = summary._fk_player) a), \'\nSummary of all Matches in Round\n\', (SELECT GROUP_CONCAT(line SEPARATOR \'\n\') FROM (SELECT CONCAT(\'Match \',  m._key , \'         \', g.gamename, \'\n\', GROUP_CONCAT(CONCAT(CAST(RPAD(p.name, 20, \' \') AS CHAR CHARACTER SET utf8), \' \', CAST(LPAD(FORMAT(score, 0), 14, \' \')AS CHAR CHARACTER SET utf8), \' \', CAST(LPAD(s.points, 2, \' \') AS CHAR CHARACTER SET utf8)) SEPARATOR \'\n\')) AS line FROM tblmatch m JOIN tblscore s on s._fk_match = m._key JOIN tblgame g ON m._fk_game = g._key JOIN tblplayer p on p._key = s._fk_player WHERE m._key > ', (SELECT 10000 * @competitionNumber + 400), ' AND m._key < ', (SELECT 10000 * @competitionNumber + 500), ' AND p.name != \'bye\' GROUP BY m._key) a), \'\n\n<a href=\"r1.html\">Round 1</a>\n<a href=\"r2.html\">Round 2</a>\n<a href=\"r3.html\">Round 3</a>\nRound 4\n<a href=\"r5.html\">Round 5</a>\n<a href=\"r6.html\">Round 6</a>\n<a href=\"r7.html\">Round 7</a>\n<a href=\"r8.html\">Round 8</a>\n</pre><hr><p>\n</body>\n</html>\')'));
 INSERT INTO tblwebsitegenerator (_fk_competition, filename, filedef) VALUES (@competitionNumber, 'r5.html', CONCAT ('SELECT CONCAT(\'<html><head><html><head><title>Tournament Results for Round #5\n</title>\n</head><body bgcolor=#f0f0f0><center><h3>Tournament Results for Round #5,</h3></center>\n<hr>\n<pre>Overall Standings as of 5 rounds,</h3></center>\n\nPos Player Name          POINTS\n--- -------------------- ------\n\', (SELECT GROUP_CONCAT(line SEPARATOR \'\') FROM (SELECT CONCAT(LPAD(rank, 3, \' \'), \' \', RPAD(name, 20, \' \'), \'  \', LPAD(total, 3, \' \'), \'\n\') AS line FROM (SELECT _fk_player, total, CASE WHEN @l=total THEN @r ELSE @r:=@row + 1 END as rank, @l:=total, @row:=@row + 1 FROM (SELECT _fk_player, SUM(points) as total FROM tblscore JOIN tblplayer on tblscore._fk_player = tblplayer._key WHERE _fk_match < ', (SELECT 10000 * @competitionNumber + 600), ' AND _fk_match > ', (SELECT 10000 * @competitionNumber + 100) ,' AND report = 1 GROUP BY _fk_player ORDER BY total DESC) totals, (SELECT @r:=0, @row:=0, @l:=NULL) rank) summary JOIN tblplayer on tblplayer._key = summary._fk_player) a), \'\nSummary of all Matches in Round\n\', (SELECT GROUP_CONCAT(line SEPARATOR \'\n\') FROM (SELECT CONCAT(\'Match \',  m._key , \'         \', g.gamename, \'\n\', GROUP_CONCAT(CONCAT(CAST(RPAD(p.name, 20, \' \') AS CHAR CHARACTER SET utf8), \' \', CAST(LPAD(FORMAT(score, 0), 14, \' \')AS CHAR CHARACTER SET utf8), \' \', CAST(LPAD(s.points, 2, \' \') AS CHAR CHARACTER SET utf8)) SEPARATOR \'\n\')) AS line FROM tblmatch m JOIN tblscore s on s._fk_match = m._key JOIN tblgame g ON m._fk_game = g._key JOIN tblplayer p on p._key = s._fk_player WHERE m._key > ', (SELECT 10000 * @competitionNumber + 500), ' AND m._key < ', (SELECT 10000 * @competitionNumber + 600), ' AND p.name != \'bye\' GROUP BY m._key) a), \'\n\n<a href=\"r1.html\">Round 1</a>\n<a href=\"r2.html\">Round 2</a>\n<a href=\"r3.html\">Round 3</a>\n<a href=\"r4.html\">Round 4</a>\nRound 5\n<a href=\"r6.html\">Round 6</a>\n<a href=\"r7.html\">Round 7</a>\n<a href=\"r8.html\">Round 8</a>\n</pre><hr><p>\n</body>\n</html>\')'));
 INSERT INTO tblwebsitegenerator (_fk_competition, filename, filedef) VALUES (@competitionNumber, 'r6.html', CONCAT ('SELECT CONCAT(\'<html><head><html><head><title>Tournament Results for Round #6\n</title>\n</head><body bgcolor=#f0f0f0><center><h3>Tournament Results for Round #6,</h3></center>\n<hr>\n<pre>Overall Standings as of 6 rounds,</h3></center>\n\nPos Player Name          POINTS\n--- -------------------- ------\n\', (SELECT GROUP_CONCAT(line SEPARATOR \'\') FROM (SELECT CONCAT(LPAD(rank, 3, \' \'), \' \', RPAD(name, 20, \' \'), \'  \', LPAD(total, 3, \' \'), \'\n\') AS line FROM (SELECT _fk_player, total, CASE WHEN @l=total THEN @r ELSE @r:=@row + 1 END as rank, @l:=total, @row:=@row + 1 FROM (SELECT _fk_player, SUM(points) as total FROM tblscore JOIN tblplayer on tblscore._fk_player = tblplayer._key WHERE _fk_match < ', (SELECT 10000 * @competitionNumber + 700), ' AND _fk_match > ', (SELECT 10000 * @competitionNumber + 100) ,' AND report = 1 GROUP BY _fk_player ORDER BY total DESC) totals, (SELECT @r:=0, @row:=0, @l:=NULL) rank) summary JOIN tblplayer on tblplayer._key = summary._fk_player) a), \'\nSummary of all Matches in Round\n\', (SELECT GROUP_CONCAT(line SEPARATOR \'\n\') FROM (SELECT CONCAT(\'Match \',  m._key , \'         \', g.gamename, \'\n\', GROUP_CONCAT(CONCAT(CAST(RPAD(p.name, 20, \' \') AS CHAR CHARACTER SET utf8), \' \', CAST(LPAD(FORMAT(score, 0), 14, \' \')AS CHAR CHARACTER SET utf8), \' \', CAST(LPAD(s.points, 2, \' \') AS CHAR CHARACTER SET utf8)) SEPARATOR \'\n\')) AS line FROM tblmatch m JOIN tblscore s on s._fk_match = m._key JOIN tblgame g ON m._fk_game = g._key JOIN tblplayer p on p._key = s._fk_player WHERE m._key > ', (SELECT 10000 * @competitionNumber + 600), ' AND m._key < ', (SELECT 10000 * @competitionNumber + 700), ' AND p.name != \'bye\' GROUP BY m._key) a), \'\n\n<a href=\"r1.html\">Round 1</a>\n<a href=\"r2.html\">Round 2</a>\n<a href=\"r3.html\">Round 3</a>\n<a href=\"r4.html\">Round 4</a>\n<a href=\"r5.html\">Round 5</a>\nRound 6\n<a href=\"r7.html\">Round 7</a>\n<a href=\"r8.html\">Round 8</a>\n</pre><hr><p>\n</body>\n</html>\')'));
 INSERT INTO tblwebsitegenerator (_fk_competition, filename, filedef) VALUES (@competitionNumber, 'r7.html', CONCAT ('SELECT CONCAT(\'<html><head><html><head><title>Tournament Results for Round #7\n</title>\n</head><body bgcolor=#f0f0f0><center><h3>Tournament Results for Round #7,</h3></center>\n<hr>\n<pre>Overall Standings as of 7 rounds,</h3></center>\n\nPos Player Name          POINTS\n--- -------------------- ------\n\', (SELECT GROUP_CONCAT(line SEPARATOR \'\') FROM (SELECT CONCAT(LPAD(rank, 3, \' \'), \' \', RPAD(name, 20, \' \'), \'  \', LPAD(total, 3, \' \'), \'\n\') AS line FROM (SELECT _fk_player, total, CASE WHEN @l=total THEN @r ELSE @r:=@row + 1 END as rank, @l:=total, @row:=@row + 1 FROM (SELECT _fk_player, SUM(points) as total FROM tblscore JOIN tblplayer on tblscore._fk_player = tblplayer._key WHERE _fk_match < ', (SELECT 10000 * @competitionNumber + 800), ' AND _fk_match > ', (SELECT 10000 * @competitionNumber + 100) ,' AND report = 1 GROUP BY _fk_player ORDER BY total DESC) totals, (SELECT @r:=0, @row:=0, @l:=NULL) rank) summary JOIN tblplayer on tblplayer._key = summary._fk_player) a), \'\nSummary of all Matches in Round\n\', (SELECT GROUP_CONCAT(line SEPARATOR \'\n\') FROM (SELECT CONCAT(\'Match \',  m._key , \'         \', g.gamename, \'\n\', GROUP_CONCAT(CONCAT(CAST(RPAD(p.name, 20, \' \') AS CHAR CHARACTER SET utf8), \' \', CAST(LPAD(FORMAT(score, 0), 14, \' \')AS CHAR CHARACTER SET utf8), \' \', CAST(LPAD(s.points, 2, \' \') AS CHAR CHARACTER SET utf8)) SEPARATOR \'\n\')) AS line FROM tblmatch m JOIN tblscore s on s._fk_match = m._key JOIN tblgame g ON m._fk_game = g._key JOIN tblplayer p on p._key = s._fk_player WHERE m._key > ', (SELECT 10000 * @competitionNumber + 700), ' AND m._key < ', (SELECT 10000 * @competitionNumber + 800), ' AND p.name != \'bye\' GROUP BY m._key) a), \'\n\n<a href=\"r1.html\">Round 1</a>\n<a href=\"r2.html\">Round 2</a>\n<a href=\"r3.html\">Round 3</a>\n<a href=\"r4.html\">Round 4</a>\n<a href=\"r5.html\">Round 5</a>\n<a href=\"r6.html\">Round 6</a>\nRound 7\n<a href=\"r8.html\">Round 8</a>\n</pre><hr><p>\n</body>\n</html>\')'));
 INSERT INTO tblwebsitegenerator (_fk_competition, filename, filedef) VALUES (@competitionNumber, 'r8.html', CONCAT ('SELECT CONCAT(\'<html><head><html><head><title>Tournament Results for Round #8\n</title>\n</head><body bgcolor=#f0f0f0><center><h3>Tournament Results for Round #8,</h3></center>\n<hr>\n<pre>Overall Standings as of 8 rounds,</h3></center>\n\nPos Player Name          POINTS\n--- -------------------- ------\n\', (SELECT GROUP_CONCAT(line SEPARATOR \'\') FROM (SELECT CONCAT(LPAD(rank, 3, \' \'), \' \', RPAD(name, 20, \' \'), \'  \', LPAD(total, 3, \' \'), \'\n\') AS line FROM (SELECT _fk_player, total, CASE WHEN @l=total THEN @r ELSE @r:=@row + 1 END as rank, @l:=total, @row:=@row + 1 FROM (SELECT _fk_player, SUM(points) as total FROM tblscore JOIN tblplayer on tblscore._fk_player = tblplayer._key WHERE _fk_match < ', (SELECT 10000 * @competitionNumber + 900), ' AND _fk_match > ', (SELECT 10000 * @competitionNumber + 100) ,' AND report = 1 GROUP BY _fk_player ORDER BY total DESC) totals, (SELECT @r:=0, @row:=0, @l:=NULL) rank) summary JOIN tblplayer on tblplayer._key = summary._fk_player) a), \'\nSummary of all Matches in Round\n\', (SELECT GROUP_CONCAT(line SEPARATOR \'\n\') FROM (SELECT CONCAT(\'Match \',  m._key , \'         \', g.gamename, \'\n\', GROUP_CONCAT(CONCAT(CAST(RPAD(p.name, 20, \' \') AS CHAR CHARACTER SET utf8), \' \', CAST(LPAD(FORMAT(score, 0), 14, \' \')AS CHAR CHARACTER SET utf8), \' \', CAST(LPAD(s.points, 2, \' \') AS CHAR CHARACTER SET utf8)) SEPARATOR \'\n\')) AS line FROM tblmatch m JOIN tblscore s on s._fk_match = m._key JOIN tblgame g ON m._fk_game = g._key JOIN tblplayer p on p._key = s._fk_player WHERE m._key > ', (SELECT 10000 * @competitionNumber + 800), ' AND m._key < ', (SELECT 10000 * @competitionNumber + 900), ' AND p.name != \'bye\' GROUP BY m._key) a), \'\n\n<a href=\"r1.html\">Round 1</a>\n<a href=\"r2.html\">Round 2</a>\n<a href=\"r3.html\">Round 3</a>\n<a href=\"r4.html\">Round 4</a>\n<a href=\"r5.html\">Round 5</a>\n<a href=\"r6.html\">Round 6</a>\n<a href=\"r7.html\">Round 7</a>\nRound 8\n</pre><hr><p>\n</body>\n</html>\')'));
 
 RETURN @competitionNumber;

END//



CREATE FUNCTION getCompetitionNumberForCompetition(
 competitionName VARCHAR(100)) RETURNS smallint(6)
BEGIN
  
 RETURN (SELECT _key FROM tblcompetition WHERE name = competitionName LIMIT 1);
  
END//



CREATE FUNCTION getGameNumberForGame(
 name VARCHAR(40)) RETURNS smallint(6)
BEGIN
  
 RETURN (SELECT _key FROM tblgame WHERE gamename = name LIMIT 1);
  
END//


CREATE DEFINER=`root`@`localhost` FUNCTION `getNewByePlayer`() RETURNS int(11)
BEGIN

 INSERT INTO tblplayer(name, report)
 VALUES ('bye', 0);

 RETURN (SELECT LAST_INSERT_ID());

END//


CREATE FUNCTION getScoreRegex(
 matchNum int) RETURNS VARCHAR(100)
BEGIN
 
 SET @OnesRegex = '([1-9][0-9]{0,2},[0-9]{3})';
 SET @NoOnesRegex = '([1-9],([0-9]{3},){2}[0-9]{2}0)|([1-9][0-9]{0,2},([0-9]{3},){0,1}[0-9]{2}0)';
 
 SET @Ones = (SELECT g.ones FROM
 tblgame g JOIN tblmatch m ON m._fk_game = g._key
 WHERE m._key = matchNum);
 
 RETURN (SELECT 
    IF(@Ones = 1, @OnesRegex, @NoOnesRegex) AS regex);
 
END//



CREATE FUNCTION inCompetitionSpan(
 matchNum smallint, competitionNumber smallint) RETURNS tinyint(1)
BEGIN
 
 RETURN (SELECT (SELECT matchNum > competitionNumber * 10000) AND (SELECT matchNum < competitionNumber * 10000 + 10000));

END//


CREATE FUNCTION matchNumExists(
 matchNum int) RETURNS tinyint(1)
BEGIN
 
 RETURN (SELECT (SELECT COUNT(*) AS my_bool FROM tblmatch WHERE _key = matchNum) = 1 AS matchNumExists);
 
END //


CREATE FUNCTION playerNameExists(
 playerName VARCHAR(40)) RETURNS tinyint(1)
BEGIN
 
 RETURN (SELECT (SELECT COUNT(*) AS my_bool FROM tblplayer WHERE name = playerName) = 1 as competitionNameExists);
 
END//



CREATE FUNCTION playerNameExists(
 playerName VARCHAR(40)) RETURNS tinyint(1)
BEGIN
 
 RETURN (SELECT (SELECT COUNT(*) AS my_bool FROM tblplayer WHERE name = playerName) = 1 as competitionNameExists);
 
END// 



CREATE PROCEDURE addGame(
 IN name VARCHAR(40))
BEGIN
 
 IF (gameNameExists(name) = 0)
 THEN
 INSERT INTO tblgame(gamename, ones)
 SELECT name, 1;
 END IF;
 
END//


CREATE PROCEDURE addPlayer(
 IN playerName VARCHAR(40))
BEGIN
  
 IF (playerNameExists(playerName) = 0)
 THEN
 INSERT INTO tblplayer(name, report)
 SELECT playerName, 1;
 END IF;
 
END//


CREATE PROCEDURE assignGameToMatch(
 IN matchNum int, 
 IN name VARCHAR(40))
BEGIN
 
 UPDATE tblmatch SET _fk_game = getGameNumberForGame(name)
 WHERE _key = matchNum;
 
END//



CREATE PROCEDURE createTBDGame()

BEGIN
 
 INSERT INTO tblgame (_key, gamename, ones)
 SELECT * FROM (SELECT -1, 'TBD', 0) as tmp
 WHERE NOT EXISTS (SELECT -1, 'TBD', 0 from tblgame);

END//


CREATE PROCEDURE enterScore(
 IN scoreNum smallint, IN gameScore bigint)
BEGIN
 
 UPDATE tblscore
 SET score = gameScore
 WHERE _key = scoreNum;
 
 UPDATE tblscore
 SET rank = 0, points = 0
 WHERE _key = scoreNum;
 
 DROP TEMPORARY TABLE IF EXISTS rank;
 CREATE TEMPORARY TABLE IF NOT EXISTS rank AS
 (SELECT _key, rank FROM
 (SELECT _key, score, @curRank:=
 IF(@prevRank = score, @curRank, @incRank) AS rank, @incRank := @incRank + 1, @prevRank := score
 FROM tblscore s, (SELECT @curRank := 0, @prevRank := NULL, @incRank := 1 ) r
 WHERE s._fk_match = (select _fk_match from tblscore where _key = scoreNum)
 AND s.score > 0 ORDER BY s.score DESC) t);
 
 DROP TEMPORARY TABLE IF EXISTS numPlayers;
 CREATE TEMPORARY TABLE numPlayers (ct smallint); 
 INSERT INTO numPlayers SELECT COUNT(*) from rank;
 
 UPDATE tblscore as s JOIN rank r ON r._key = s._key SET s.rank = r.rank;
 
 UPDATE tblscore AS s
 JOIN tblmatch m ON s._fk_match = m._key
 JOIN tblscoring ts ON ts._fk_scoringscheme = m._fk_scoringscheme
 JOIN rank r on r._key = s._key
 JOIN tblplayer p ON s._fk_player = p._key
 SET s.points = ts.pointsforrank
 WHERE ts.rank = s.rank AND (ts.numplayers = 0 OR ts.numplayers = (SELECT * FROM numPlayers LIMIT 1));
 
END//


CREATE DEFINER=`root`@`localhost` PROCEDURE `enterScoreByMatchNumberAndName`(
 IN matchNum smallint, IN playerName VARCHAR(40), IN gameScore bigint)
BEGIN
 
 CALL enterScore ((SELECT s._key from tblscore s join tblplayer p on s._fk_player = p._key WHERE s._fk_match = matchNum AND p.name = playerName ORDER BY s._KEY DESC LIMIT 1), gameScore);
 
END//




CREATE PROCEDURE getFullWebsite(
 IN competitionNumber smallint)
BEGIN
 
 DECLARE done BOOLEAN DEFAULT 0;
 DECLARE o INT;
 
 DECLARE webgen CURSOR
 FOR
 SELECT _key FROM tblwebsitegenerator where _fk_competition = competitionNumber;
 
 DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done=1; 
 
 SET SESSION group_concat_max_len = 1000000;
 
 DROP TEMPORARY TABLE IF EXISTS site;
 CREATE TEMPORARY TABLE site (filename VARCHAR(40), filetext VARCHAR(10000));
  
 OPEN webgen;
 
 REPEAT
 
 FETCH webgen INTO o;
 
 SET @S = (SELECT filedef FROM tblwebsitegenerator WHERE _key = o);
 SET @ST = CONCAT(@S, ' INTO @OUT');
 
 PREPARE stmt FROM @ST;
 EXECUTE stmt;
 
 INSERT INTO site (filename, filetext) VALUES ((SELECT filename FROM tblwebsitegenerator WHERE _key = o), @OUT);
 
 SET @OUT = NULL;
 
 UNTIL done END REPEAT;
 
 CLOSE webgen;
 
 SELECT DISTINCT * from site;
 
 DROP TEMPORARY TABLE IF EXISTS site;
 

END//


CREATE PROCEDURE getGameNamesInCompetition(
 IN competitionNumber smallint)
BEGIN
 
 SELECT gamename from tblgame;

END//



CREATE PROCEDURE getMatchNumbersInCompetition(
 IN competitionNumber smallint)
BEGIN
 
 SELECT DISTINCT m._key from tblmatch m
 JOIN tblscore s on s._fk_match = m._key
 JOIN tblscoreset ss on s._fk_scoreset = ss._key
 WHERE ss._fk_competition = competitionNumber
 ORDER BY m._key;
 
END//




CREATE PROCEDURE getPlayerNamesInCompetition(
 IN competitionNumber smallint)
BEGIN
 
 SELECT DISTINCT name FROM tblplayer p
 join tblscoreset ss ON ss._fk_player = p._key
 WHERE p.report = 1 AND ss._fk_competition = competitionNumber;
 
END//





CREATE PROCEDURE getPlayerNamesInMatch(
 IN matchNum smallint)
BEGIN
 
 SELECT p.name AS name, s._key as scoreNum FROM tblplayer p
 JOIN tblscore s ON s._fk_player = p._key
 JOIN tblmatch m ON s._fk_match = m._key
 WHERE m._key = matchNum
 AND p.name != 'bye'
 ORDER BY s._key;

END//


CREATE PROCEDURE makeMatch(
 IN matchNum smallint,
 IN scoringScheme smallint)
BEGIN
 
 CALL createTBDGame();
 
 SELECT @TBD = (SELECT _key FROM tblgame where gamename = 'TBD');
 
 INSERT INTO tblmatch (_key, _fk_game, _fk_scoringScheme)
 VALUES (matchNum, @TBD, scoringScheme);

END//



CREATE PROCEDURE previewFourPlayerScoreEntry(
 IN matchNum smallint, IN player1Name VARCHAR(40), IN player1Score bigint, IN player2Name VARCHAR(40), IN player2Score bigint,
 IN player3Name VARCHAR(40), player3Score bigint, IN player4Name VARCHAR(40), IN player4Score bigint)
BEGIN
 
 DROP TEMPORARY TABLE IF EXISTS tempScore;
 
 CREATE TEMPORARY TABLE tempScore AS (SELECT * FROM tblscore s WHERE s._fk_match = matchNum);
 
 UPDATE tempScore ts JOIN tblplayer p on ts._fk_player = p._key 
 SET ts.score = player1Score WHERE name = player1Name;
 
 UPDATE tempScore ts JOIN tblplayer p on ts._fk_player = p._key 
 SET ts.score = player2Score WHERE name = player2Name;
 
 UPDATE tempScore ts JOIN tblplayer p on ts._fk_player = p._key 
 SET ts.score = player3Score WHERE name = player3Name;
 
 UPDATE tempScore ts JOIN tblplayer p on ts._fk_player = p._key 
 SET ts.score = player4Score WHERE name = player4Name;
 
 UPDATE tempScore
 SET rank = 0, points = 0;
 
 DROP TEMPORARY TABLE IF EXISTS rank;
 CREATE TEMPORARY TABLE IF NOT EXISTS rank AS
 (SELECT _key, rank FROM
 (SELECT _key, score, @curRank:=
 IF(@prevRank = score, @curRank, @incRank) AS rank, @incRank := @incRank + 1, @prevRank := score
 FROM tempScore s, (SELECT @curRank := 0, @prevRank := NULL, @incRank := 1 ) r
 WHERE s.score > 0 ORDER BY s.score DESC) t);
 
 DROP TEMPORARY TABLE IF EXISTS numPlayers;
 CREATE TEMPORARY TABLE numPlayers (ct smallint); 
 INSERT INTO numPlayers SELECT COUNT(*) from rank;
 
 UPDATE tempScore as s JOIN rank r ON r._key = s._key SET s.rank = r.rank;
 
 UPDATE tempScore AS s
 JOIN tblmatch m ON s._fk_match = m._key
 JOIN tblscoring ts ON ts._fk_scoringscheme = m._fk_scoringscheme
 JOIN rank r on r._key = s._key
 JOIN tblplayer p ON s._fk_player = p._key
 SET s.points = ts.pointsforrank
 WHERE ts.rank = s.rank AND (ts.numplayers = 0 OR ts.numplayers = (SELECT * FROM numPlayers LIMIT 1));
 
 SELECT p.name, FORMAT(s.score, 0) as score, s.points
 FROM tempScore s JOIN tblplayer p ON s._fk_player = p._key WHERE p.name != 'bye';
 
END//



CREATE PROCEDURE removeGame(
 IN name VARCHAR(40))
BEGIN
 
 DELETE FROM tblgame where gamename = name;
 
END//


CREATE PROCEDURE removePlayer(
 IN playerName VARCHAR(40))
BEGIN
 
 DELETE FROM tblplayer WHERE name = playerName;
 
END// 


DELIMITER ;
