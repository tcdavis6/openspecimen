LOAD DATA INFILE 'H://caTissue//work//workspace//catissuecoreNew/SQL/DBUpgrade/Common/CAModelCSVs/DYEXTN_ATTRIBUTE_TYPE_INFO.csv' 
APPEND 
INTO TABLE DYEXTN_ATTRIBUTE_TYPE_INFO 
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
(IDENTIFIER NULLIF IDENTIFIER='\\N',PRIMITIVE_ATTRIBUTE_ID NULLIF PRIMITIVE_ATTRIBUTE_ID='\\N')