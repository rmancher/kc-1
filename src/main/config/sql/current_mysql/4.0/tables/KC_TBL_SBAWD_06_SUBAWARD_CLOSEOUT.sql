DELIMITER /
CREATE TABLE SUBAWARD_CLOSEOUT 
   (	SUBAWARD_CLOSEOUT_ID DECIMAL(12,0) NOT NULL, 
	SUBAWARD_ID DECIMAL(12,0) NOT NULL, 
	CLOSEOUT_NUMBER DECIMAL(3,0), 
	CLOSEOUT_TYPE_CODE DECIMAL(3,0) NOT NULL, 
	DATE_REQUESTED DATE, 
	DATE_FOLLOWUP DATE, 
	DATE_RECEIVED DATE, 
	COMMENTS VARCHAR(300), 
	UPDATE_TIMESTAMP DATE NOT NULL, 
	UPDATE_USER VARCHAR(60) NOT NULL, 
	VER_NBR DECIMAL(8,0) DEFAULT 1 NOT NULL, 
	OBJ_ID VARCHAR(36) NOT NULL,
	SEQUENCE_NUMBER DECIMAL(4,0) NOT NULL,
	SUBAWARD_CODE VARCHAR(20) NOT NULL
  )
/
ALTER TABLE SUBAWARD_CLOSEOUT
	ADD CONSTRAINT PK_SUBAWARD_CLOSEOUT PRIMARY KEY (SUBAWARD_CLOSEOUT_ID)
/
DELIMITER ;
