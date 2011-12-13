DELIMITER /
  CREATE TABLE SUBAWARD_AMOUNT_INFO 
   (	SUBAWARD_AMOUNT_INFO_ID DECIMAL(12,0) NOT NULL, 
	SUBAWARD_ID DECIMAL(12,0) NOT NULL,
	OBLIGATED_AMOUNT DECIMAL(12,2), 
	OBLIGATED_CHANGE DECIMAL(12,2), 
	ANTICIPATED_AMOUNT DECIMAL(12,2), 
	ANTICIPATED_CHANGE DECIMAL(12,2), 
	EFFECTIVE_DATE DATE, 
	COMMENTS VARCHAR(300), 
	UPDATE_TIMESTAMP DATE NOT NULL, 
	UPDATE_USER VARCHAR(60) NOT NULL, 
	FILE_NAME VARCHAR(150), 
	DOCUMENT BLOB, 
	MIME_TYPE VARCHAR(100), 
	VER_NBR DECIMAL(8,0) DEFAULT 1 NOT NULL, 
	OBJ_ID VARCHAR(36) NOT NULL, 
	FILE_ID DECIMAL(22,0),
	SEQUENCE_NUMBER DECIMAL(4,0) NOT NULL,
	SUBAWARD_CODE VARCHAR(20) NOT NULL
  )
/
ALTER TABLE SUBAWARD_AMOUNT_INFO
  ADD CONSTRAINT PK_SUBAWARD_AMOUNT_INFO PRIMARY KEY (SUBAWARD_AMOUNT_INFO_ID)
/
DELIMITER ;
