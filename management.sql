DELIMITER //

CREATE PROCEDURE addPlayer(
 IN playerName VARCHAR(40))
BEGIN
  
 IF (playerNameExists(playerName) = 0)
 THEN
 INSERT INTO tblplayer(name, report)
 SELECT playerName, 1;
 END IF;
 
END//

DELETE FROM tempattributevalue WHERE session_id = connection_id();

CREATE TRIGGER forNewPlayers AFTER INSERT ON tblplayer
 FOR EACH ROW 
 BEGIN
 INSERT INTO tempattributevalue (_fk_player, _fk_attributename, attrvalue, startdate, enddate, session_id)
 VALUES (NEW._key, (SELECT _key FROM tblattributename WHERE varname = 'playerName'), NEW.name, CONVERT_TZ(NOW(), @@session.time_zone, '+00:00'), NULL, connection_id());

 END;//

 CREATE TRIGGER updateAttributeValues BEFORE INSERT ON tempattributevalue
 FOR EACH ROW
BEGIN
 UPDATE tblattributevalue SET _key = null WHERE _fk_player <=> NEW._fk_player AND _fk_game <=> NEW._fk_game AND _fk_competition <=> NEW._fk_competition AND _fk_attributename = NEW._fk_attributename AND attrvalue = NEW.attrvalue AND enddate IS NOT NULL;
 UPDATE tblattributevalue SET enddate = NEW.startdate WHERE _fk_player <=> NEW._fk_player AND _fk_game <=> NEW._fk_game AND _fk_competition <=> NEW._fk_competition AND _fk_attributename = NEW._fk_attributename;
 INSERT INTO tblattributevalue (_fk_player, _fk_game, _fk_competition, _fk_attributename, attrvalue, startdate, enddate)
 VALUES (NEW._fk_player, NEW._fk_game, NEW._fk_competition, NEW._fk_attributename, NEW.attrvalue, NEW.startdate, NEW.enddate);
 
 END;// 

DELIMITER ;