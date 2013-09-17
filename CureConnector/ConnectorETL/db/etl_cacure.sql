-- Function: set_permission(text, text, text)

-- DROP FUNCTION set_permission(text, text, text);

CREATE LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION set_permission(entityid text, perm text)
  RETURNS boolean AS
$BODY$BEGIN
  insert into entity_tag_permission (entity_id, tag_id, permission) select $1,f.tag_id,$2 from forms f;
  return true;
END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION set_permission(text, text) OWNER TO cacure;

