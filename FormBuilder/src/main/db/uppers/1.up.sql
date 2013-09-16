---------------------------------------------------------------------------------------------------
-- Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved-
-- Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
-- Proprietary and confidential
---------------------------------------------------------------------------------------------------

 CREATE TABLE preferences
(
  id INT PRIMARY KEY NOT NULL DEFAULT(1) CHECK (id = 1),
  show_please_select_option boolean NOT NULL DEFAULT(false)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE preferences OWNER TO fbdev;

INSERT INTO preferences(show_please_select_option) VALUES(false);

alter table "FormBuilder".module add column show_please_select_option boolean not null default(false);