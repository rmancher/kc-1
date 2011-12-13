DELIMITER /
DROP TABLE IF EXISTS COI_DISCLOSURE_DOCUMENT
/

CREATE TABLE COI_DISCLOSURE_DOCUMENT  ( 
	DOCUMENT_NUMBER 	VARCHAR(40) NOT NULL,
	VER_NBR         	DECIMAL(8,0) DEFAULT 1 NOT NULL,
	UPDATE_TIMESTAMP	DATE NOT NULL,
	UPDATE_USER     	VARCHAR(60) NOT NULL,
	OBJ_ID          	VARCHAR(36) NOT NULL)
/
ALTER TABLE COI_DISCLOSURE_DOCUMENT 
ADD CONSTRAINT PK_COI_DISCLOSURE_DOCUMENT 
PRIMARY KEY (DOCUMENT_NUMBER)
/

DELIMITER ;
