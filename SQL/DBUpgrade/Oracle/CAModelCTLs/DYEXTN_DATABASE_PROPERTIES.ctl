LOAD DATA INFILE 'H://caTissue//work//workspace//catissuecoreNew/SQL/DBUpgrade/Common/CAModelCSVs/DYEXTN_DATABASE_PROPERTIES.csv' 
APPEND 
INTO TABLE DYEXTN_DATABASE_PROPERTIES 
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
(IDENTIFIER NULLIF IDENTIFIER='\\N',NAME NULLIF NAME='\\N')