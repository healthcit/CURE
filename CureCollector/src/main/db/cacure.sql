--
-- PostgreSQL database dump
--

-- Dumped from database version 8.4.4
-- Dumped by pg_dump version 9.1.3
-- Started on 2012-06-01 16:09:03

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- TOC entry 7 (class 2615 OID 16492)
-- Name: cacure; Type: SCHEMA; Schema: -; Owner: cacure
--

CREATE SCHEMA cacure;


ALTER SCHEMA cacure OWNER TO cacure;

SET search_path = cacure, pg_catalog;

--
-- TOC entry 169 (class 1255 OID 16769)
-- Dependencies: 7 503
-- Name: convertskips(); Type: FUNCTION; Schema: cacure; Owner: cacure
--

CREATE FUNCTION convertskips() RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
    r form_skip%rowtype;
BEGIN
    FOR r IN SELECT * FROM form_skip where ans_value is not null
    LOOP
	insert into skip_parts (id, parent_id, answer_value)
		values (nextval('"form_skip_id_seq"'), r.id, r.ans_value);

    END LOOP;
    RETURN 1;
END
$$;


ALTER FUNCTION cacure.convertskips() OWNER TO cacure;

--
-- TOC entry 170 (class 1255 OID 41553)
-- Dependencies: 503 7
-- Name: delete_module(integer); Type: FUNCTION; Schema: cacure; Owner: cacure
--

CREATE FUNCTION delete_module(mod_id integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
    this_form RECORD;
    this_skip RECORD;
begin
	FOR this_form IN SELECT * FROM "cacure".forms WHERE module_id = mod_id LOOP

		-- delete skips
		FOR this_skip IN SELECT * FROM "cacure".form_skip WHERE form_id = this_form.id LOOP
			
			delete from "cacure".skip_parts where parent_id =this_skip.id;

		END LOOP;
		delete from "cacure".form_skip where form_id = this_form.id;
		
		-- delete entity forms
		DELETE FROM "cacure".entity_form WHERE form_id = this_form.id;

	END LOOP; -- forms loop
	-- delete all forms
	DELETE FROM "cacure".forms WHERE module_id = mod_id;

	-- delete module
	DELETE FROM "cacure".entity_module WHERE module_id = mod_id;
	DELETE FROM "cacure".modules WHERE id = mod_id;
	
	
	RETURN 1;
end
$$;


ALTER FUNCTION cacure.delete_module(mod_id integer) OWNER TO cacure;

--
-- TOC entry 171 (class 1255 OID 41556)
-- Dependencies: 7 503
-- Name: delete_module(character varying); Type: FUNCTION; Schema: cacure; Owner: cacure
--

CREATE FUNCTION delete_module(mod_id character varying) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
    this_form RECORD;
    this_skip RECORD;
begin
	FOR this_form IN SELECT * FROM "cacure".forms WHERE module_id = mod_id LOOP

		-- delete skips
		FOR this_skip IN SELECT * FROM "cacure".form_skip WHERE form_id = this_form.id LOOP
			
			delete from "cacure".skip_parts where parent_id =this_skip.id;

		END LOOP;
		delete from "cacure".form_skip where form_id = this_form.id;
		
		-- delete entity forms
		DELETE FROM "cacure".entity_form WHERE form_id = this_form.id;

	END LOOP; -- forms loop
	-- delete all forms
	DELETE FROM "cacure".forms WHERE module_id = mod_id;

	-- delete module
	DELETE FROM "cacure".entity_module WHERE module_id = mod_id;
	DELETE FROM "cacure".modules WHERE id = mod_id;
	
	
	RETURN 1;
end
$$;


ALTER FUNCTION cacure.delete_module(mod_id character varying) OWNER TO cacure;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 141 (class 1259 OID 16594)
-- Dependencies: 7
-- Name: core_entity; Type: TABLE; Schema: cacure; Owner: cacure; Tablespace: 
--

CREATE TABLE core_entity (
    id character varying(100) NOT NULL,
    sharing_group_id character varying(100)
);


ALTER TABLE cacure.core_entity OWNER TO cacure;

--
-- TOC entry 155 (class 1259 OID 115972)
-- Dependencies: 7
-- Name: entity_tag_permission; Type: TABLE; Schema: cacure; Owner: cacure; Tablespace: 
--

CREATE TABLE entity_tag_permission (
    entity_id character varying(40),
    tag_id character varying(500),
    permission character varying(10)
);


ALTER TABLE cacure.entity_tag_permission OWNER TO cacure;

--
-- TOC entry 144 (class 1259 OID 16603)
-- Dependencies: 7
-- Name: form_skip; Type: TABLE; Schema: cacure; Owner: cacure; Tablespace: 
--

CREATE TABLE form_skip (
    id bigint NOT NULL,
    form_id character varying(100) NOT NULL,
    question_id character varying(100) NOT NULL,
    rule character varying(50) NOT NULL,
    logical_op character varying(3),
    question_owner_form_id character varying(150) NOT NULL,
    row_id character varying(150)
);


ALTER TABLE cacure.form_skip OWNER TO cacure;

--
-- TOC entry 145 (class 1259 OID 16609)
-- Dependencies: 7
-- Name: form_skip_id_seq; Type: SEQUENCE; Schema: cacure; Owner: cacure
--

CREATE SEQUENCE form_skip_id_seq
    START WITH 4
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE cacure.form_skip_id_seq OWNER TO cacure;

--
-- TOC entry 146 (class 1259 OID 16611)
-- Dependencies: 7
-- Name: forms; Type: TABLE; Schema: cacure; Owner: cacure; Tablespace: 
--

CREATE TABLE forms (
    id character varying(100) NOT NULL,
    description character varying(300),
    author character varying(50),
    question_count integer NOT NULL,
    xform_location character varying(400) NOT NULL,
    module_id character varying(100) NOT NULL,
    form_order integer NOT NULL,
    form_name character varying(100) NOT NULL,
    tag_id character varying(500)
);


ALTER TABLE cacure.forms OWNER TO cacure;

--
-- TOC entry 147 (class 1259 OID 16617)
-- Dependencies: 1834 7
-- Name: modules; Type: TABLE; Schema: cacure; Owner: cacure; Tablespace: 
--

CREATE TABLE modules (
    id character varying(100) NOT NULL,
    name character varying(100) NOT NULL,
    description character varying(300),
    status character varying(10) NOT NULL,
    deploy_date timestamp with time zone DEFAULT now() NOT NULL,
    estimated_completion_time character varying(500),
    context character varying(20)
);


ALTER TABLE cacure.modules OWNER TO cacure;

--
-- TOC entry 148 (class 1259 OID 16623)
-- Dependencies: 1835 7
-- Name: patients; Type: TABLE; Schema: cacure; Owner: cacure; Tablespace: 
--

CREATE TABLE patients (
    modified_date date,
    additional_info character varying(4000),
    phr_first_time_completed_date date,
    user_id bigint NOT NULL,
    status character varying(50) DEFAULT 'NOT SUBMITTED'::character varying,
    id bytea NOT NULL
);


ALTER TABLE cacure.patients OWNER TO cacure;

--
-- TOC entry 149 (class 1259 OID 16630)
-- Dependencies: 7
-- Name: security_questions; Type: TABLE; Schema: cacure; Owner: cacure; Tablespace: 
--

CREATE TABLE security_questions (
    value character varying(60),
    id bigint NOT NULL
);


ALTER TABLE cacure.security_questions OWNER TO cacure;

--
-- TOC entry 150 (class 1259 OID 16633)
-- Dependencies: 149 7
-- Name: security_questions_id_seq; Type: SEQUENCE; Schema: cacure; Owner: cacure
--

CREATE SEQUENCE security_questions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE cacure.security_questions_id_seq OWNER TO cacure;

--
-- TOC entry 1874 (class 0 OID 0)
-- Dependencies: 150
-- Name: security_questions_id_seq; Type: SEQUENCE OWNED BY; Schema: cacure; Owner: cacure
--

ALTER SEQUENCE security_questions_id_seq OWNED BY security_questions.id;


--
-- TOC entry 154 (class 1259 OID 58963)
-- Dependencies: 7
-- Name: sharing_group; Type: TABLE; Schema: cacure; Owner: cacure; Tablespace: 
--

CREATE TABLE sharing_group (
    id character varying(100) NOT NULL,
    name character varying(500) NOT NULL
);


ALTER TABLE cacure.sharing_group OWNER TO cacure;

--
-- TOC entry 142 (class 1259 OID 16597)
-- Dependencies: 7
-- Name: sharing_group_form; Type: TABLE; Schema: cacure; Owner: cacure; Tablespace: 
--

CREATE TABLE sharing_group_form (
    form_id character varying(100) NOT NULL,
    entity_id character varying(100),
    status character varying(20) NOT NULL,
    lastupdated timestamp with time zone,
    sharing_group_id character varying(100) NOT NULL
);


ALTER TABLE cacure.sharing_group_form OWNER TO cacure;

--
-- TOC entry 143 (class 1259 OID 16600)
-- Dependencies: 7
-- Name: sharing_group_module; Type: TABLE; Schema: cacure; Owner: cacure; Tablespace: 
--

CREATE TABLE sharing_group_module (
    module_id character varying(100) NOT NULL,
    entity_id character varying(100),
    status character varying(20) NOT NULL,
    datesubmitted timestamp with time zone,
    sharing_group_id character varying(100) NOT NULL
);


ALTER TABLE cacure.sharing_group_module OWNER TO cacure;

--
-- TOC entry 153 (class 1259 OID 16764)
-- Dependencies: 7
-- Name: skip_parts; Type: TABLE; Schema: cacure; Owner: cacure; Tablespace: 
--

CREATE TABLE skip_parts (
    id bigint NOT NULL,
    parent_id bigint NOT NULL,
    answer_value character varying(150) NOT NULL
);


ALTER TABLE cacure.skip_parts OWNER TO cacure;

--
-- TOC entry 156 (class 1259 OID 115978)
-- Dependencies: 7
-- Name: tag; Type: TABLE; Schema: cacure; Owner: cacure; Tablespace: 
--

CREATE TABLE tag (
    tag_id character varying(500)
);


ALTER TABLE cacure.tag OWNER TO cacure;

--
-- TOC entry 151 (class 1259 OID 16635)
-- Dependencies: 1837 1839 7
-- Name: users; Type: TABLE; Schema: cacure; Owner: cacure; Tablespace: 
--

CREATE TABLE users (
    username character varying(25),
    password character varying(130),
    password_hint character varying(50),
    last_login_date date,
    mustchangepassword boolean DEFAULT false,
    system_usage_consent boolean DEFAULT false,
    system_usage_consent_date date,
    email_addr character varying(25),
    security_question_id bigint,
    security_question_answer character varying(50),
    id bigint NOT NULL,
    role character varying(25)
);


ALTER TABLE cacure.users OWNER TO cacure;

--
-- TOC entry 152 (class 1259 OID 16638)
-- Dependencies: 7 151
-- Name: users_id_seq; Type: SEQUENCE; Schema: cacure; Owner: cacure
--

CREATE SEQUENCE users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE cacure.users_id_seq OWNER TO cacure;

--
-- TOC entry 1875 (class 0 OID 0)
-- Dependencies: 152
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: cacure; Owner: cacure
--

ALTER SEQUENCE users_id_seq OWNED BY users.id;


--
-- TOC entry 1836 (class 2604 OID 16640)
-- Dependencies: 150 149
-- Name: id; Type: DEFAULT; Schema: cacure; Owner: cacure
--

ALTER TABLE ONLY security_questions ALTER COLUMN id SET DEFAULT nextval('security_questions_id_seq'::regclass);


--
-- TOC entry 1838 (class 2604 OID 16641)
-- Dependencies: 152 151
-- Name: id; Type: DEFAULT; Schema: cacure; Owner: cacure
--

ALTER TABLE ONLY users ALTER COLUMN id SET DEFAULT nextval('users_id_seq'::regclass);


--
-- TOC entry 1841 (class 2606 OID 16643)
-- Dependencies: 141 141
-- Name: core_entity_pk; Type: CONSTRAINT; Schema: cacure; Owner: cacure; Tablespace: 
--

ALTER TABLE ONLY core_entity
    ADD CONSTRAINT core_entity_pk PRIMARY KEY (id);


--
-- TOC entry 1843 (class 2606 OID 59011)
-- Dependencies: 142 142 142
-- Name: entity_form_pk; Type: CONSTRAINT; Schema: cacure; Owner: cacure; Tablespace: 
--

ALTER TABLE ONLY sharing_group_form
    ADD CONSTRAINT entity_form_pk PRIMARY KEY (form_id, sharing_group_id);


--
-- TOC entry 1845 (class 2606 OID 59013)
-- Dependencies: 143 143 143
-- Name: entity_module_pk; Type: CONSTRAINT; Schema: cacure; Owner: cacure; Tablespace: 
--

ALTER TABLE ONLY sharing_group_module
    ADD CONSTRAINT entity_module_pk PRIMARY KEY (module_id, sharing_group_id);


--
-- TOC entry 1849 (class 2606 OID 16649)
-- Dependencies: 146 146
-- Name: forms_pkey; Type: CONSTRAINT; Schema: cacure; Owner: cacure; Tablespace: 
--

ALTER TABLE ONLY forms
    ADD CONSTRAINT forms_pkey PRIMARY KEY (id);


--
-- TOC entry 1851 (class 2606 OID 16651)
-- Dependencies: 147 147
-- Name: module_pkey; Type: CONSTRAINT; Schema: cacure; Owner: cacure; Tablespace: 
--

ALTER TABLE ONLY modules
    ADD CONSTRAINT module_pkey PRIMARY KEY (id);


--
-- TOC entry 1854 (class 2606 OID 16653)
-- Dependencies: 148 148
-- Name: patients_pkey; Type: CONSTRAINT; Schema: cacure; Owner: cacure; Tablespace: 
--

ALTER TABLE ONLY patients
    ADD CONSTRAINT patients_pkey PRIMARY KEY (id);


--
-- TOC entry 1847 (class 2606 OID 16655)
-- Dependencies: 144 144
-- Name: pk_id; Type: CONSTRAINT; Schema: cacure; Owner: cacure; Tablespace: 
--

ALTER TABLE ONLY form_skip
    ADD CONSTRAINT pk_id PRIMARY KEY (id);


--
-- TOC entry 1856 (class 2606 OID 16657)
-- Dependencies: 149 149
-- Name: pk_security_questions_pkey; Type: CONSTRAINT; Schema: cacure; Owner: cacure; Tablespace: 
--

ALTER TABLE ONLY security_questions
    ADD CONSTRAINT pk_security_questions_pkey PRIMARY KEY (id);


--
-- TOC entry 1859 (class 2606 OID 16659)
-- Dependencies: 151 151
-- Name: pk_users_pkey; Type: CONSTRAINT; Schema: cacure; Owner: cacure; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT pk_users_pkey PRIMARY KEY (id);


--
-- TOC entry 1861 (class 2606 OID 16768)
-- Dependencies: 153 153
-- Name: skip_parts_pkey; Type: CONSTRAINT; Schema: cacure; Owner: cacure; Tablespace: 
--

ALTER TABLE ONLY skip_parts
    ADD CONSTRAINT skip_parts_pkey PRIMARY KEY (id);


--
-- TOC entry 1863 (class 2606 OID 58970)
-- Dependencies: 154 154
-- Name: user_group_pk; Type: CONSTRAINT; Schema: cacure; Owner: cacure; Tablespace: 
--

ALTER TABLE ONLY sharing_group
    ADD CONSTRAINT user_group_pk PRIMARY KEY (id);


--
-- TOC entry 1857 (class 1259 OID 16660)
-- Dependencies: 151
-- Name: fki_security_questions_fkey; Type: INDEX; Schema: cacure; Owner: cacure; Tablespace: 
--

CREATE INDEX fki_security_questions_fkey ON users USING btree (security_question_id);


--
-- TOC entry 1852 (class 1259 OID 16661)
-- Dependencies: 148
-- Name: fki_users_fkey; Type: INDEX; Schema: cacure; Owner: cacure; Tablespace: 
--

CREATE INDEX fki_users_fkey ON patients USING btree (user_id);


--
-- TOC entry 1866 (class 2606 OID 16662)
-- Dependencies: 141 143 1840
-- Name: core_entity_fk; Type: FK CONSTRAINT; Schema: cacure; Owner: cacure
--

ALTER TABLE ONLY sharing_group_module
    ADD CONSTRAINT core_entity_fk FOREIGN KEY (entity_id) REFERENCES core_entity(id) ON DELETE CASCADE;


--
-- TOC entry 1864 (class 2606 OID 16667)
-- Dependencies: 1840 141 142
-- Name: core_entity_fk; Type: FK CONSTRAINT; Schema: cacure; Owner: cacure
--

ALTER TABLE ONLY sharing_group_form
    ADD CONSTRAINT core_entity_fk FOREIGN KEY (entity_id) REFERENCES core_entity(id) ON DELETE CASCADE;


--
-- TOC entry 1870 (class 2606 OID 115981)
-- Dependencies: 1840 141 155
-- Name: entity_tag_permission_fk; Type: FK CONSTRAINT; Schema: cacure; Owner: cacure
--

ALTER TABLE ONLY entity_tag_permission
    ADD CONSTRAINT entity_tag_permission_fk FOREIGN KEY (entity_id) REFERENCES core_entity(id) ON DELETE CASCADE;


--
-- TOC entry 1869 (class 2606 OID 16672)
-- Dependencies: 151 149 1855
-- Name: fk_security_questions_fkey; Type: FK CONSTRAINT; Schema: cacure; Owner: cacure
--

ALTER TABLE ONLY users
    ADD CONSTRAINT fk_security_questions_fkey FOREIGN KEY (security_question_id) REFERENCES security_questions(id);


--
-- TOC entry 1868 (class 2606 OID 16677)
-- Dependencies: 1858 151 148
-- Name: fk_users_fkey; Type: FK CONSTRAINT; Schema: cacure; Owner: cacure
--

ALTER TABLE ONLY patients
    ADD CONSTRAINT fk_users_fkey FOREIGN KEY (user_id) REFERENCES users(id);


--
-- TOC entry 1865 (class 2606 OID 16682)
-- Dependencies: 142 1848 146
-- Name: forms_pk; Type: FK CONSTRAINT; Schema: cacure; Owner: cacure
--

ALTER TABLE ONLY sharing_group_form
    ADD CONSTRAINT forms_pk FOREIGN KEY (form_id) REFERENCES forms(id) ON DELETE CASCADE;


--
-- TOC entry 1867 (class 2606 OID 16687)
-- Dependencies: 143 1850 147
-- Name: modules_pk; Type: FK CONSTRAINT; Schema: cacure; Owner: cacure
--

ALTER TABLE ONLY sharing_group_module
    ADD CONSTRAINT modules_pk FOREIGN KEY (module_id) REFERENCES modules(id) ON DELETE CASCADE;


--
-- TOC entry 1873 (class 0 OID 0)
-- Dependencies: 7
-- Name: cacure; Type: ACL; Schema: -; Owner: cacure
--

REVOKE ALL ON SCHEMA cacure FROM PUBLIC;
REVOKE ALL ON SCHEMA cacure FROM cacure;
GRANT ALL ON SCHEMA cacure TO cacure;
GRANT ALL ON SCHEMA cacure TO PUBLIC;


-- Completed on 2012-06-01 16:09:04

--
-- PostgreSQL database dump complete
--


--
-- Create admin user
--
ALTER TABLE users
   ALTER COLUMN mustchangepassword SET DEFAULT false;
ALTER TABLE users
   ALTER COLUMN system_usage_consent SET DEFAULT false;
ALTER TABLE users ADD COLUMN "role" character varying(25);
INSERT INTO users(
            username, "password", mustchangepassword, 
            system_usage_consent, system_usage_consent_date, id, "role")
    VALUES ('admin', '0b14d501a594442a01c6859541bcb3e8164d183d32937b851835442f69d5c94e',
	    FALSE, TRUE, now(), nextval('users_id_seq'), 'ROLE_ADMIN');
INSERT INTO tag values('TAG1');
UPDATE forms set tag_id='TAG1';
INSERT INTO entity_tag_permission (entity_id, tag_id, permission) select id, 'TAG1', 'READ' from core_entity;
INSERT INTO entity_tag_permission (entity_id, tag_id, permission) select id, 'TAG1', 'WRITE' from core_entity;
INSERT INTO entity_tag_permission (entity_id, tag_id, permission) select id, 'TAG1', 'APPROVE' from core_entity;
