---------------------------------------------------------------------------------------------------
-- Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved-
-- Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
-- Proprietary and confidential
---------------------------------------------------------------------------------------------------

alter table form_skip add column row_id character varying(150);

CREATE OR REPLACE FUNCTION from_hex(t text)
  RETURNS integer AS
$BODY$
DECLARE
    r RECORD;
BEGIN
    FOR r IN EXECUTE 'SELECT x'''||t||'''::integer AS hex' LOOP
        RETURN r.hex;
    END LOOP;
END
$BODY$
  LANGUAGE 'plpgsql' IMMUTABLE STRICT
  COST 100;
ALTER FUNCTION from_hex(text) OWNER TO cacure;

CREATE OR REPLACE FUNCTION generate_uuid_v3(namespace character varying, "name" character varying)
  RETURNS uuid AS
$BODY$
DECLARE
    value varchar(36);
    bytes varchar;
BEGIN
    bytes = md5(decode(namespace, 'hex') || decode(name, 'escape'));
    value = substr(bytes, 1+0, 8);
    value = value || '-';
    value = value || substr(bytes, 1+2*4, 4);
    value = value || '-';
    value = value || lpad(to_hex((from_hex(substr(bytes, 1+2*6, 2)) & 15) | 48), 2, '0');
    value = value || substr(bytes, 1+2*7, 2);
    value = value || '-';
    value = value || lpad(to_hex((from_hex(substr(bytes, 1+2*8, 2)) & 63) | 128), 2, '0');
    value = value || substr(bytes, 1+2*9, 2);
    value = value || '-';
    value = value || substr(bytes, 1+2*10, 12);
    return value::uuid;
END;
$BODY$
  LANGUAGE 'plpgsql' IMMUTABLE STRICT
  COST 100;
ALTER FUNCTION generate_uuid_v3(character varying, character varying) OWNER TO cacure;



CREATE TABLE sharing_group
(
  id character varying(100) NOT NULL,
  name character varying(500) NOT NULL,
  CONSTRAINT user_group_pk PRIMARY KEY (id)
);

ALTER TABLE entity_form
    RENAME TO sharing_group_form;

ALTER TABLE entity_module
    RENAME TO sharing_group_module;
    
alter table sharing_group_form add column sharing_group_id character varying(100);
alter table sharing_group_module add column sharing_group_id character varying(100);
alter table core_entity add column sharing_group_id character varying(100);

CREATE OR REPLACE FUNCTION convert_to_groups()
  RETURNS boolean AS
$BODY$
DECLARE
    q RECORD;
    a RECORD;
    group_id varchar;
    new_id bigint;
    fe_type varchar;
    e_type varchar;
begin
	
	FOR a IN SELECT * FROM core_entity  LOOP
	      group_id = generate_uuid_v3('6ba7b8119dad11d180b400c04fd430c8', 'http://www.heathcit.com/cacure/' || a.id);
          update core_entity set sharing_group_id = group_id
          where id=a.id;    
		  
		  update sharing_group_form set sharing_group_id = group_id where entity_id=a.id;
		  update sharing_group_module set sharing_group_id = group_id where entity_id=a.id;

          insert into sharing_group 
		  (id, name)
           values 
           (group_id, group_id);
		 
	END LOOP;
	RETURN true;
end
$BODY$
  LANGUAGE 'plpgsql' VOLATILE;

select convert_to_groups();

ALTER TABLE sharing_group_form DROP CONSTRAINT entity_form_pk;

ALTER TABLE sharing_group_form
  ADD CONSTRAINT entity_form_pk PRIMARY KEY(form_id, sharing_group_id);
    
ALTER TABLE sharing_group_module
  DROP CONSTRAINT entity_module_pk;

ALTER TABLE sharing_group_module
  ADD CONSTRAINT entity_module_pk PRIMARY KEY(module_id, sharing_group_id);
    
ALTER TABLE sharing_group_module ALTER COLUMN entity_id DROP NOT NULL;
ALTER TABLE sharing_group_form ALTER COLUMN entity_id DROP NOT NULL;