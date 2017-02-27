CREATE TABLE tblplayer (
  _key smallint(6) NOT NULL,
  name varchar(40) DEFAULT NULL,
  report int(11) DEFAULT NULL,
  PRIMARY KEY (_key)
  
CREATE TABLE tblgame (
  _key smallint(6) NOT NULL,
  gamename varchar(40) DEFAULT NULL,
  ones int(11) DEFAULT NULL,
  PRIMARY KEY (_key);
  
CREATE TABLE tblscore (
    _key smallint(6) NOT NULL,
    _fk_player smallint(6) DEFAULT NULL,
    _fk_game smallint(6) DEFAULT NULL,
    score bigint(20) DEFAULT NULL,
    points tinyint(4) DEFAULT NULL,
    PRIMARY KEY (_key),
    KEY _fk_player (_fk_player),
    KEY _fk_game (_fk_game),
    FOREIGN KEY (_fk_player) REFERENCES tblplayer(_key),
   FOREIGN KEY (_fk_game) REFERENCES tblgame (_key);
   
   
CREATE TABLE `tblmatch` (
  `_key` smallint(6) NOT NULL,
  `_fk_game` smallint(6) DEFAULT NULL,
  `_fk_score_1` smallint(6) DEFAULT NULL,
  `_fk_score_2` smallint(6) DEFAULT NULL,
  `_fk_score_3` smallint(6) DEFAULT NULL,
  `_fk_score_4` smallint(6) DEFAULT NULL,
  PRIMARY KEY (`_key`),
  KEY `_fk_game` (`_fk_game`),
  KEY `_fk_score_1` (`_fk_score_1`),
  KEY `_fk_score_2` (`_fk_score_2`),
  KEY `_fk_score_3` (`_fk_score_3`),
  KEY `_fk_score_4` (`_fk_score_4`),
  FOREIGN KEY (`_fk_game`) REFERENCES tblgame(_key),
  FOREIGN KEY (_fk_score_1) REFERENCES tblscore(_key),
  FOREIGN KEY (_fk_score_2) REFERENCES tblscore(_key),
  FOREIGN KEY (_fk_score_3) REFERENCES tblscore(_key),
  FOREIGN KEY (_fk_score_4) REFERENCES tblscore(_key);