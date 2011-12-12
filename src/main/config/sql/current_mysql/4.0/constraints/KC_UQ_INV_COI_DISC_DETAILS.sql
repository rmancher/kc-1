DELIMITER /
ALTER TABLE INV_COI_DISC_DETAILS 
ADD CONSTRAINT UQ_INV_COI_DISC_DETAILS 
UNIQUE (COI_DISCLOSURE_NUMBER, ENTITY_NUMBER, ENTITY_SEQUENCE_NUMBER, SEQUENCE_NUMBER)
/
DELIMITER ;
