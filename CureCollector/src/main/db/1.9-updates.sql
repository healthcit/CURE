---------------------------------------------------------------------------------------------------
-- Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved-
-- Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
-- Proprietary and confidential
---------------------------------------------------------------------------------------------------

CREATE TABLE sys_variables
(
  schema_version bigint NOT NULL DEFAULT 0,
  system_update_version bigint NOT NULL DEFAULT 0 
)
WITH (
  OIDS=TRUE
);
ALTER TABLE sys_variables OWNER TO cacure;

INSERT INTO sys_variables VALUES('0','0');