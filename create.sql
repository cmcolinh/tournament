CREATE TABLE tblcompetition (
  _key smallint(6) NOT NULL AUTO_INCREMENT,
  name varchar(100) DEFAULT NULL,
  basepath varchar(100) DEFAULT NULL,
  PRIMARY KEY (_key),
  UNIQUE KEY (name));
  
CREATE TABLE tblgame (
  _key smallint(6) NOT NULL AUTO_INCREMENT,
  gamename varchar(40) DEFAULT NULL,
  ones tinyint(4) DEFAULT NULL,
  PRIMARY KEY (_key),
  UNIQUE KEY (gamename));
  
CREATE TABLE tblscoringscheme (
  _key smallint(6) NOT NULL AUTO_INCREMENT,
  description varchar(40) DEFAULT NULL,
  PRIMARY KEY (_key));

CREATE TABLE tblmatch (
  _key smallint(6) NOT NULL,
  _fk_game smallint(6) DEFAULT NULL,
  _fk_scoringscheme smallint(6) DEFAULT NULL,
  PRIMARY KEY (_key),
  FOREIGN KEY (_fk_game) REFERENCES tblgame (_key),
  FOREIGN KEY (_fk_scoringscheme) REFERENCES tblscoringscheme (_key));          

CREATE TABLE tblplayer (
  _key smallint(6) NOT NULL AUTO_INCREMENT,
  name varchar(40) DEFAULT NULL,
  report int(11) DEFAULT NULL,
  PRIMARY KEY (_key),
  UNIQUE KEY (_key));
  
CREATE TABLE tblscoreset (
  _key smallint(6) NOT NULL AUTO_INCREMENT,
  _fk_player smallint(6) DEFAULT NULL,
  _fk_competition smallint(6) DEFAULT NULL,
  PRIMARY KEY (_key),
  FOREIGN KEY (_fk_player) REFERENCES tblplayer (_key),
  FOREIGN KEY (_fk_competition) REFERENCES tblcompetition (_key));
 
CREATE TABLE tblscore (
  _key smallint(6) NOT NULL AUTO_INCREMENT,
  _fk_player smallint(6) DEFAULT NULL,
  _fk_match smallint(6) DEFAULT NULL,
  _fk_scoreset smallint(6) DEFAULT NULL,
  score bigint(20) DEFAULT NULL,
  rank smallint(6) DEFAULT NULL,
  points tinyint(4) DEFAULT NULL,
  PRIMARY KEY (_key),
  FOREIGN KEY (_fk_player) REFERENCES tblplayer (_key),
  FOREIGN KEY (_fk_match) REFERENCES tblmatch (_key),
  FOREIGN KEY (_fk_scoreset) REFERENCES tblscoreset (_key));
  
CREATE TABLE tblscoring (
  _key smallint(6) NOT NULL AUTO_INCREMENT,
  _fk_scoringscheme smallint(6) NOT NULL,
  numplayers smallint(6) NOT NULL,
  rank smallint(6) NOT NULL,
  pointsforrank smallint(6) NOT NULL,
  PRIMARY KEY (_key),
  FOREIGN KEY (_fk_scoringscheme) REFERENCES tblscoringscheme (_key));
  
CREATE TABLE tblbonusscoring (
  _key smallint(6) NOT NULL AUTO_INCREMENT,
  _fk_scoring smallint(6) NOT NULL,
  bonuspoints smallint(6) NOT NULL,
  cond varchar(10000) NOT NULL,
  PRIMARY KEY (_key),
  FOREIGN KEY (_fk_scoring) REFERENCES tblscoring(_key));
     
CREATE TABLE tblwebsitegenerator (
  _key smallint(6) NOT NULL AUTO_INCREMENT,
  _fk_competition smallint(6) DEFAULT NULL,
  filename varchar(40) DEFAULT NULL,
  filedef varchar(10000) DEFAULT NULL,
  PRIMARY KEY (_key),
  FOREIGN KEY (_fk_competition) REFERENCES tblcompetition (_key));
  
CREATE TABLE tblcompetitionenrollment (
  _key smallint(6) NOT NULL AUTO_INCREMENT,
  _fk_competition smallint(6) NOT NULL,
  _fk_player smallint(6) NOT NULL,
  seed smallint(6) DEFAULT NULL,
  report tinyint(1),
  PRIMARY KEY (_key),
  UNIQUE KEY (_fk_competition, _fk_player),
  FOREIGN KEY (_fk_competition) REFERENCES tblcompetition(_key),
  FOREIGN KEY (_fk_player) REFERENCES tblplayer(_key));
  
CREATE TABLE tblcompetitionadvancementscript (
  _key smallint(6) NOT NULL AUTO_INCREMENT,
  _fk_competition smallint(6) NOT NULL,
  step int(6) NOT NULL,
  command varchar(10000) DEFAULT NULL,
  active tinyint(1),
  PRIMARY KEY (_key),
  UNIQUE KEY (_fk_competition, step),
  FOREIGN KEY (_fk_competition) REFERENCES tblcompetition(_key));

CREATE TABLE tblattributename(
  _key smallint(6) NOT NULL AUTO_INCREMENT,
  varname varchar(40) NOT NULL,
  description varchar(40) NOT NULL,
  playernotnull tinyint(1) NOT NULL DEFAULT 0,
  gamenotnull tinyint(1) NOT NULL DEFAULT 0,
  competitionnotnull tinyint(1) NOT NULL DEFAULT 0,
  regex VARCHAR(100) NOT NULL DEFAULT '.{1,40}';
  PRIMARY KEY (_key),
  UNIQUE KEY (varname, playernotnull, gamenotnull, competitionnotnull));
  
CREATE TABLE tblattributevalue(
  _key smallint(6) NOT NULL AUTO_INCREMENT,
  _fk_player smallint (6) DEFAULT NULL,
  _fk_game smallint(6) DEFAULT NULL,
  _fk_competition smallint(6) DEFAULT NULL,
  _fk_attributename smallint(6) NOT NULL,
  attrvalue varchar(40) NOT NULL,
  startdate DATETIME NOT NULL,
  enddate DATETIME DEFAULT NULL,
  PRIMARY KEY (_key),
  FOREIGN KEY (_fk_attributename) REFERENCES tblattributename(_key),
  FOREIGN KEY (_fk_player) REFERENCES tblplayer(_key),
  FOREIGN KEY (_fk_game) REFERENCES tblgame(_key),
  FOREIGN KEY (_fk_competition) REFERENCES tblcompetition(_key)); 
  
CREATE TABLE tempScore (
  _key smallint(6) NOT NULL,
  _fk_player smallint(6) DEFAULT NULL,
  _fk_match smallint(6) DEFAULT NULL,
  _fk_scoreset smallint(6) DEFAULT NULL,
  score bigint(20) DEFAULT NULL,
  rank smallint(6) DEFAULT NULL,
  points tinyint(4) DEFAULT NULL,
  session_id int(4) NOT NULL,
  PRIMARY KEY (_key, session_id),
  FOREIGN KEY (_fk_player) REFERENCES tblplayer (_key),
  FOREIGN KEY (_fk_match) REFERENCES tblmatch (_key),
  FOREIGN KEY (_fk_scoreset) REFERENCES tblscoreset (_key));
  
 CREATE TABLE tempattributevalue(
  _fk_player smallint (6) DEFAULT NULL,
  _fk_game smallint(6) DEFAULT NULL,
  _fk_competition smallint(6) DEFAULT NULL,
  _fk_attributename smallint(6) NOT NULL,
  attrvalue varchar(40) NOT NULL,
  startdate DATETIME NOT NULL,
  enddate DATETIME DEFAULT NULL,
  session_id int(4) NOT NULL,
  FOREIGN KEY (_fk_attributename) REFERENCES tblattributename(_key),
  FOREIGN KEY (_fk_player) REFERENCES tblplayer(_key),
  FOREIGN KEY (_fk_game) REFERENCES tblgame(_key),
  FOREIGN KEY (_fk_competition) REFERENCES tblcompetition(_key)); 
