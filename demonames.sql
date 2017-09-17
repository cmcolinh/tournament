insert into tblattributename(attrname, description, playernotnull) values ('playerName', 'Name of Player', 1);
insert into tblattributename(attrname, description, gamenotnull) values ('gameName', 'Name of Game', 1);
insert into tblattributename(attrname, description, competitionnotnull, regex) values ('gameInCompetition', 'Game is being used in the competition', 1, 'Y|N');

call addPlayer('Colin');
call addPlayer('David');
call addPlayer('James');
call addPlayer('Matthew');
call addPlayer('Mary');
call addPlayer('Pamela');
call addPlayer('Alpha');
call addPlayer('Beta');
call addPlayer('Gamma');
call addPlayer('Delta');
call addPlayer('Kappa');
call addPlayer('Theta');
call addPlayer('Sigma');
call addPlayer('Apple');
call addPlayer('Bacon');
call addPlayer('Copper');
call addPlayer('Dipper');
call addPlayer('Echo');
call addPlayer('Foxtrot');
call addPlayer('Golf');
call addPlayer('Hotel');
call addPlayer('India');
call addPlayer('Juliet');
call addPlayer('Kilo');
call addPlayer('Liger');
call addPlayer('November');
call addPlayer('Oscar');
call addPlayer('Peter');
call addPlayer('Quayle');
call addPlayer('Romeo');
call addPlayer('Sierra');
	
call addPlayer('Tango');
call addPlayer('Uniform');
call addPlayer('Victor');
call addPlayer('Wagon');
	
INSERT INTO tblscoringscheme (description) VALUES ('10-6-3-1 Scoring');
INSERT INTO tblscoring (_fk_scoringscheme, numplayers, rank, pointsforrank) VALUES ((SELECT _key from tblscoringscheme where description = '10-6-3-1 Scoring' LIMIT 1), 3, 1, 10);
INSERT INTO tblscoring (_fk_scoringscheme, numplayers, rank, pointsforrank) VALUES ((SELECT _key from tblscoringscheme where description = '10-6-3-1 Scoring' LIMIT 1), 3, 2, 5);
INSERT INTO tblscoring (_fk_scoringscheme, numplayers, rank, pointsforrank) VALUES ((SELECT _key from tblscoringscheme where description = '10-6-3-1 Scoring' LIMIT 1), 3, 3, 1);
INSERT INTO tblscoring (_fk_scoringscheme, numplayers, rank, pointsforrank) VALUES ((SELECT _key from tblscoringscheme where description = '10-6-3-1 Scoring' LIMIT 1), 4, 1, 10);
INSERT INTO tblscoring (_fk_scoringscheme, numplayers, rank, pointsforrank) VALUES ((SELECT _key from tblscoringscheme where description = '10-6-3-1 Scoring' LIMIT 1), 4, 2, 6);
INSERT INTO tblscoring (_fk_scoringscheme, numplayers, rank, pointsforrank) VALUES ((SELECT _key from tblscoringscheme where description = '10-6-3-1 Scoring' LIMIT 1), 4, 3, 3);
INSERT INTO tblscoring (_fk_scoringscheme, numplayers, rank, pointsforrank) VALUES ((SELECT _key from tblscoringscheme where description = '10-6-3-1 Scoring' LIMIT 1), 4, 4, 1);

INSERT INTO tblscoringscheme (description) VALUES ('FSPA Scoring');
INSERT INTO tblscoringscheme (description) VALUES ('FSPA Scoring Bonus Game');
INSERT INTO tblscoring (_fk_scoringscheme, numplayers, rank, pointsforrank) VALUES ((SELECT _key from tblscoringscheme where description = 'FSPA Scoring' LIMIT 1), 1, 1, 3);
INSERT INTO tblscoring (_fk_scoringscheme, numplayers, rank, pointsforrank) VALUES ((SELECT _key from tblscoringscheme where description = 'FSPA Scoring' LIMIT 1), 2, 1, 3);
INSERT INTO tblscoring (_fk_scoringscheme, numplayers, rank, pointsforrank) VALUES ((SELECT _key from tblscoringscheme where description = 'FSPA Scoring' LIMIT 1), 2, 2, 0);
INSERT INTO tblscoring (_fk_scoringscheme, numplayers, rank, pointsforrank) VALUES ((SELECT _key from tblscoringscheme where description = 'FSPA Scoring' LIMIT 1), 3, 1, 3);
INSERT INTO tblscoring (_fk_scoringscheme, numplayers, rank, pointsforrank) VALUES ((SELECT _key from tblscoringscheme where description = 'FSPA Scoring' LIMIT 1), 3, 2, 2);
INSERT INTO tblscoring (_fk_scoringscheme, numplayers, rank, pointsforrank) VALUES ((SELECT _key from tblscoringscheme where description = 'FSPA Scoring' LIMIT 1), 3, 3, 0);
INSERT INTO tblscoring (_fk_scoringscheme, numplayers, rank, pointsforrank) VALUES ((SELECT _key from tblscoringscheme where description = 'FSPA Scoring' LIMIT 1), 4, 1, 3);
INSERT INTO tblscoring (_fk_scoringscheme, numplayers, rank, pointsforrank) VALUES ((SELECT _key from tblscoringscheme where description = 'FSPA Scoring' LIMIT 1), 4, 2, 2);
INSERT INTO tblscoring (_fk_scoringscheme, numplayers, rank, pointsforrank) VALUES ((SELECT _key from tblscoringscheme where description = 'FSPA Scoring' LIMIT 1), 4, 3, 1);
INSERT INTO tblscoring (_fk_scoringscheme, numplayers, rank, pointsforrank) VALUES ((SELECT _key from tblscoringscheme where description = 'FSPA Scoring' LIMIT 1), 4, 4, 0);
INSERT INTO tblscoring (_fk_scoringscheme, numplayers, rank, pointsforrank) VALUES ((SELECT _key from tblscoringscheme where description = 'FSPA Scoring Bonus Game' LIMIT 1), 1, 1, 2);
INSERT INTO tblscoring (_fk_scoringscheme, numplayers, rank, pointsforrank) VALUES ((SELECT _key from tblscoringscheme where description = 'FSPA Scoring Bonus Game' LIMIT 1), 2, 1, 3);
INSERT INTO tblscoring (_fk_scoringscheme, numplayers, rank, pointsforrank) VALUES ((SELECT _key from tblscoringscheme where description = 'FSPA Scoring Bonus Game' LIMIT 1), 2, 2, 0);
INSERT INTO tblscoring (_fk_scoringscheme, numplayers, rank, pointsforrank) VALUES ((SELECT _key from tblscoringscheme where description = 'FSPA Scoring Bonus Game' LIMIT 1), 3, 1, 3);
INSERT INTO tblscoring (_fk_scoringscheme, numplayers, rank, pointsforrank) VALUES ((SELECT _key from tblscoringscheme where description = 'FSPA Scoring Bonus Game' LIMIT 1), 3, 2, 2);
INSERT INTO tblscoring (_fk_scoringscheme, numplayers, rank, pointsforrank) VALUES ((SELECT _key from tblscoringscheme where description = 'FSPA Scoring Bonus Game' LIMIT 1), 3, 3, 0);
INSERT INTO tblscoring (_fk_scoringscheme, numplayers, rank, pointsforrank) VALUES ((SELECT _key from tblscoringscheme where description = 'FSPA Scoring Bonus Game' LIMIT 1), 4, 1, 3);
INSERT INTO tblscoring (_fk_scoringscheme, numplayers, rank, pointsforrank) VALUES ((SELECT _key from tblscoringscheme where description = 'FSPA Scoring Bonus Game' LIMIT 1), 4, 2, 2);
INSERT INTO tblscoring (_fk_scoringscheme, numplayers, rank, pointsforrank) VALUES ((SELECT _key from tblscoringscheme where description = 'FSPA Scoring Bonus Game' LIMIT 1), 4, 3, 1);
INSERT INTO tblscoring (_fk_scoringscheme, numplayers, rank, pointsforrank) VALUES ((SELECT _key from tblscoringscheme where description = 'FSPA Scoring Bonus Game' LIMIT 1), 4, 4, 0);
INSERT INTO tblbonusscoring (_fk_scoring, bonuspoints, cond) VALUES ((SELECT _key from tblscoring where _fk_scoringscheme = (SELECT _key from tblscoringscheme where description = 'FSPA Scoring' LIMIT 1) and numplayers = 2 and rank = 1), 1, '(SELECT score FROM tempScore WHERE rank = 1) > 3 * (SELECT score FROM tempScore WHERE rank = 2)');
INSERT INTO tblbonusscoring (_fk_scoring, bonuspoints, cond) VALUES ((SELECT _key from tblscoring where _fk_scoringscheme = (SELECT _key from tblscoringscheme where description = 'FSPA Scoring' LIMIT 1) and numplayers = 2 and rank = 2), 1, '(SELECT score FROM tempScore WHERE rank = 1) <= 3 * (SELECT score FROM tempScore WHERE rank = 2)');
INSERT INTO tblbonusscoring (_fk_scoring, bonuspoints, cond) VALUES ((SELECT _key from tblscoring where _fk_scoringscheme = (SELECT _key from tblscoringscheme where description = 'FSPA Scoring' LIMIT 1) and numplayers = 3 and rank = 1), 1, '(SELECT score FROM tempScore WHERE rank = 1) > (SELECT score FROM tempScore WHERE rank = 2) + (SELECT score FROM tempScore WHERE rank = 3)');
INSERT INTO tblbonusscoring (_fk_scoring, bonuspoints, cond) VALUES ((SELECT _key from tblscoring where _fk_scoringscheme = (SELECT _key from tblscoringscheme where description = 'FSPA Scoring' LIMIT 1) and numplayers = 3 and rank = 3), 1, '(SELECT score FROM tempScore WHERE rank = 1) <= (SELECT score FROM tempScore WHERE rank = 2) + (SELECT score FROM tempScore WHERE rank = 3)');
INSERT INTO tblbonusscoring (_fk_scoring, bonuspoints, cond) VALUES ((SELECT _key from tblscoring where _fk_scoringscheme = (SELECT _key from tblscoringscheme where description = 'FSPA Scoring' LIMIT 1) and numplayers = 4 and rank = 1), 1, '(SELECT score FROM tempScore WHERE rank = 1) > (SELECT score FROM tempScore WHERE rank = 2) + (SELECT score FROM tempScore WHERE rank = 3)');
INSERT INTO tblbonusscoring (_fk_scoring, bonuspoints, cond) VALUES ((SELECT _key from tblscoring where _fk_scoringscheme = (SELECT _key from tblscoringscheme where description = 'FSPA Scoring' LIMIT 1) and numplayers = 4 and rank = 2), 1, '(SELECT score FROM tempScore WHERE rank = 2) > (SELECT score FROM tempScore WHERE rank = 3) + (SELECT score FROM tempScore WHERE rank = 4)');
INSERT INTO tblbonusscoring (_fk_scoring, bonuspoints, cond) VALUES ((SELECT _key from tblscoring where _fk_scoringscheme = (SELECT _key from tblscoringscheme where description = 'FSPA Scoring' LIMIT 1) and numplayers = 4 and rank = 3), 1, '(SELECT score FROM tempScore WHERE rank = 1) <= (SELECT score FROM tempScore WHERE rank = 2) + (SELECT score FROM tempScore WHERE rank = 3)');
INSERT INTO tblbonusscoring (_fk_scoring, bonuspoints, cond) VALUES ((SELECT _key from tblscoring where _fk_scoringscheme = (SELECT _key from tblscoringscheme where description = 'FSPA Scoring' LIMIT 1) and numplayers = 4 and rank = 4), 1, '(SELECT score FROM tempScore WHERE rank = 2) <= (SELECT score FROM tempScore WHERE rank = 3) + (SELECT score FROM tempScore WHERE rank = 4)');

INSERT INTO tblbonusscoring (_fk_scoring, bonuspoints, cond) VALUES ((SELECT _key from tblscoring where _fk_scoringscheme = (SELECT _key from tblscoringscheme where description = 'FSPA Scoring Bonus Game' LIMIT 1) and numplayers = 2 and rank = 1), 1, '(SELECT score FROM tempScore WHERE rank = 1) > 3 * (SELECT score FROM tempScore WHERE rank = 2)');
INSERT INTO tblbonusscoring (_fk_scoring, bonuspoints, cond) VALUES ((SELECT _key from tblscoring where _fk_scoringscheme = (SELECT _key from tblscoringscheme where description = 'FSPA Scoring Bonus Game' LIMIT 1) and numplayers = 2 and rank = 2), 1, '(SELECT score FROM tempScore WHERE rank = 1) <= 3 * (SELECT score FROM tempScore WHERE rank = 2)');
INSERT INTO tblbonusscoring (_fk_scoring, bonuspoints, cond) VALUES ((SELECT _key from tblscoring where _fk_scoringscheme = (SELECT _key from tblscoringscheme where description = 'FSPA Scoring Bonus Game' LIMIT 1) and numplayers = 3 and rank = 1), 1, '(SELECT score FROM tempScore WHERE rank = 1) > (SELECT score FROM tempScore WHERE rank = 2) + (SELECT score FROM tempScore WHERE rank = 3)');
INSERT INTO tblbonusscoring (_fk_scoring, bonuspoints, cond) VALUES ((SELECT _key from tblscoring where _fk_scoringscheme = (SELECT _key from tblscoringscheme where description = 'FSPA Scoring Bonus Game' LIMIT 1) and numplayers = 3 and rank = 3), 1, '(SELECT score FROM tempScore WHERE rank = 1) <= (SELECT score FROM tempScore WHERE rank = 2) + (SELECT score FROM tempScore WHERE rank = 3)');
INSERT INTO tblbonusscoring (_fk_scoring, bonuspoints, cond) VALUES ((SELECT _key from tblscoring where _fk_scoringscheme = (SELECT _key from tblscoringscheme where description = 'FSPA Scoring Bonus Game' LIMIT 1) and numplayers = 4 and rank = 1), 1, '(SELECT score FROM tempScore WHERE rank = 1) >= (SELECT score FROM tempScore WHERE rank = 2) + (SELECT score FROM tempScore WHERE rank = 3)');
INSERT INTO tblbonusscoring (_fk_scoring, bonuspoints, cond) VALUES ((SELECT _key from tblscoring where _fk_scoringscheme = (SELECT _key from tblscoringscheme where description = 'FSPA Scoring Bonus Game' LIMIT 1) and numplayers = 4 and rank = 2), 1, '(SELECT score FROM tempScore WHERE rank = 2) >= (SELECT score FROM tempScore WHERE rank = 3) + (SELECT score FROM tempScore WHERE rank = 4)');
INSERT INTO tblbonusscoring (_fk_scoring, bonuspoints, cond) VALUES ((SELECT _key from tblscoring where _fk_scoringscheme = (SELECT _key from tblscoringscheme where description = 'FSPA Scoring Bonus Game' LIMIT 1) and numplayers = 4 and rank = 3), 1, '(SELECT score FROM tempScore WHERE rank = 1) < (SELECT score FROM tempScore WHERE rank = 2) + (SELECT score FROM tempScore WHERE rank = 3)');
INSERT INTO tblbonusscoring (_fk_scoring, bonuspoints, cond) VALUES ((SELECT _key from tblscoring where _fk_scoringscheme = (SELECT _key from tblscoringscheme where description = 'FSPA Scoring Bonus Game' LIMIT 1) and numplayers = 4 and rank = 4), 1, '(SELECT score FROM tempScore WHERE rank = 2) < (SELECT score FROM tempScore WHERE rank = 3) + (SELECT score FROM tempScore WHERE rank = 4)');