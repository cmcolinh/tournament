DELIMITER //

CREATE PROCEDURE addPlayer(
 IN playerName VARCHAR(40))
BEGIN
  
 IF (playerNameExists(playerName) = 0)
 THEN
 INSERT INTO tblplayer(name, report)
 SELECT playerName, 1;
 ELSE
 UPDATE tblplayer SET report = 1 WHERE NAME = playerName;
 END IF;
 
 
END//


CREATE PROCEDURE removePlayer(
 IN playerName VARCHAR(40))
BEGIN
 
 UPDATE tblplayer SET report = 0 WHERE name = playerName;
 
END// 


CREATE PROCEDURE addGame(
 IN name VARCHAR(40))
BEGIN
 
 IF (gameNameExists(name) = 0)
 THEN
 INSERT INTO tblgame(gamename, ones)
 SELECT name, 0;
 END IF;
 
END//
 
CREATE PROCEDURE getGameNamesInCompetition(
 IN competitionNumber smallint)
BEGIN
 
 SELECT g.gamename from tblgame g JOIN tblattributevalue av ON g._key = av._fk_game WHERE av.enddate IS NULL and av._fk_competition = competitionNumber AND av.attrname = 'gameInCompetition' AND av.attrvalue = 'Y';

END//

CREATE TRIGGER forNewPlayers AFTER INSERT ON tblplayer
 FOR EACH ROW 
 BEGIN
 INSERT INTO tempattributevalue (_fk_player, _fk_attributename, attrvalue, startdate, enddate, session_id)
 VALUES (NEW._key, (SELECT _key FROM tblattributename WHERE attrname = 'playerName' AND playernotnull = 1 AND gamenotnull = 0 AND competitionnotnull = 0), NEW.name, CONVERT_TZ(NOW(), @@session.time_zone, '+00:00'), NULL, connection_id());
 INSERT INTO tempattributevalue (_fk_player, _fk_attributename, attrvalue, startdate, enddate, session_id)
 VALUES (NEW._key, (SELECT _key FROM tblattributename WHERE attrname = 'playerActive' AND playernotnull = 1 AND gamenotnull = 0 AND competitionnotnull = 0), CASE NEW.report WHEN 1 THEN 'Y' ELSE 'N' END, CONVERT_TZ(NOW(), @@session.time_zone, '+00:00'), NULL, connection_id()); 
 
 DELETE FROM tempattributevalue WHERE session_id = connection_id();
 END;//

CREATE TRIGGER forAlteredPlayers BEFORE UPDATE ON tblplayer
 FOR EACH ROW
 BEGIN
 INSERT INTO tempattributevalue (_fk_player, _fk_attributename, attrvalue, startdate, enddate, session_id)
 VALUES (NEW._key, (SELECT _key FROM tblattributename WHERE attrname = 'playerName' AND playernotnull = 1 AND gamenotnull = 0 AND competitionnotnull = 0), NEW.name, CONVERT_TZ(NOW(), @@session.time_zone, '+00:00'), NULL, connection_id());
 INSERT INTO tempattributevalue (_fk_player, _fk_attributename, attrvalue, startdate, enddate, session_id)
 VALUES (NEW._key, (SELECT _key FROM tblattributename WHERE attrname = 'playerActive' AND playernotnull = 1 AND gamenotnull = 0 AND competitionnotnull = 0), CASE NEW.report WHEN 1 THEN 'Y' ELSE 'N' END, CONVERT_TZ(NOW(), @@session.time_zone, '+00:00'), NULL, connection_id());  
 DELETE FROM tempattributevalue WHERE session_id = connection_id();
 END;//
 
 
CREATE TRIGGER forNewGames BEFORE INSERT ON tblgame
 FOR EACH ROW 
 BEGIN
 INSERT INTO tempattributevalue (_fk_game, _fk_attributename, attrvalue, startdate, enddate, session_id)
 VALUES (NEW._key, (SELECT _key FROM tblattributename WHERE attrname = 'gameName' AND playernotnull = 0 AND gamenotnull = 1 AND competitionnotnull = 0), NEW.gamename, CONVERT_TZ(NOW(), @@session.time_zone, '+00:00'), NULL, connection_id());
 DELETE FROM tempattributevalue WHERE session_id = connection_id();
 END;//
 
CREATE TRIGGER forAlteredGames BEFORE UPDATE ON tblgame
 FOR EACH ROW 
 BEGIN
 INSERT INTO tempattributevalue (_fk_game, _fk_attributename, attrvalue, startdate, enddate, session_id)
 VALUES (NEW._key, (SELECT _key FROM tblattributename WHERE attrname = 'gameName' AND playernotnull = 0 AND gamenotnull = 1 AND competitionnotnull = 0), NEW.gamename, CONVERT_TZ(NOW(), @@session.time_zone, '+00:00'), NULL, connection_id());
 DELETE FROM tempattributevalue WHERE session_id = connection_id();
 END;//



 CREATE TRIGGER updateAttributeValues AFTER INSERT ON tempattributevalue
 FOR EACH ROW
 BEGIN
 IF (!   (NEW.attrvalue IS NOT NULL 
     AND (NEW._fk_player IS NOT NULL) = (SELECT playernotnull FROM tblattributename WHERE _key = NEW._fk_attributename) 
     AND (NEW._fk_game IS NOT NULL) = (SELECT gamenotnull FROM tblattributename WHERE _key = NEW._fk_attributename)
     AND (NEW._fk_game IS NOT NULL) = (SELECT gamenotnull FROM tblattributename WHERE _key = NEW._fk_attributename)
     AND (NEW._fk_competition IS NOT NULL) = (SELECT competitionnotnull FROM tblattributename WHERE _key = NEW._fk_attributename) ) )
 THEN
      SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'the types of entities associated with the attribute value do not match the specification of the attribute name';
 END IF;
 
 IF (NEW.attrvalue NOT RLIKE (SELECT regex FROM tblattributename WHERE _key = NEW._fk_attributename))
 THEN
      SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'attempting to set a value that is invalid according to the specification';
 END IF;
 
 UPDATE tblattributevalue SET enddate = NEW.startdate WHERE _fk_player <=> NEW._fk_player AND _fk_game <=> NEW._fk_game AND _fk_competition <=> NEW._fk_competition AND _fk_attributename = NEW._fk_attributename AND attrvalue != NEW.attrvalue AND enddate IS NULL;
 
 IF (SELECT NOT EXISTS (SELECT * FROM tblattributevalue WHERE _fk_player <=> NEW._fk_player AND _fk_game <=> NEW._fk_game AND _fk_competition <=> NEW._fk_competition AND _fk_attributename = NEW._fk_attributename AND attrvalue = NEW.attrvalue AND enddate IS NULL) = 1) 
 THEN
     INSERT INTO tblattributevalue (_fk_player, _fk_game, _fk_competition, _fk_attributename, attrvalue, startdate, enddate)
         VALUES (NEW._fk_player, NEW._fk_game, NEW._fk_competition, NEW._fk_attributename, NEW.attrvalue, NEW.startdate, NEW.enddate);
 END IF;
 END;//
 
 CREATE TRIGGER guardAttributeValueInserts BEFORE INSERT ON tblattributevalue
 FOR EACH ROW
 BEGIN
	 IF (SELECT NOT EXISTS (SELECT _fk_player, _fk_game, _fk_competition, _fk_attributename, attrvalue, startdate, enddate
	     FROM tempattributevalue 
	     WHERE _fk_player <=> NEW._fk_player 
	     AND _fk_game <=> NEW._fk_game
	     AND _fk_competition <=> NEW._fk_competition
	     AND _fk_attributename <=> NEW._fk_attributename
	     AND attrvalue <=> NEW.attrvalue
	     AND startdate <=> NEW.startdate
	     AND enddate <=> NEW.enddate) = 1)
	 THEN
          SIGNAL SQLSTATE '45000'
          SET MESSAGE_TEXT = 'not setting this attribute value in the proper manner';
     END IF;     	     
	 
 END;//

DELIMITER ;