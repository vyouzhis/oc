 
DROP TABLE IF EXISTS `role_user_info`;
CREATE TABLE IF NOT EXISTS `role_user_info` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT 'login name',
  `passwd` varchar(32) NOT NULL COMMENT 'login passwd only md5, if only one change code ,pls see here and use https to login',
  `cm` varchar(32) NOT NULL COMMENT 'login again change check md5',
  `nickname` varchar(255) NOT NULL COMMENT 'nick name',
  `email` varchar(255) NOT NULL COMMENT 'email',
  `ctime` int(11) NOT NULL DEFAULT '1' COMMENT 'create time',
  `etime` int(11)  DEFAULT '0' COMMENT 'edit time',  
  `ltime` int(11) NOT NULL DEFAULT '1' COMMENT 'last login time',  
  `phone` varchar(128) NOT NULL DEFAULT '' COMMENT 'user phone',
  `status` int(1) NOT NULL DEFAULT '1' COMMENT 'defaule 1 enable 0 disable',  
  `isdelete` tinyint(1) DEFAULT '0' COMMENT 'defaule 0 normal 1 delete',
  `gid` int(11) NOT NULL DEFAULT '0' COMMENT 'group id',
  `cid` int(11) NOT NULL DEFAULT '0' COMMENT 'creater uid',
  `error` int(1) NOT NULL DEFAULT '0' COMMENT 'passwd error count',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `role_group`;
CREATE TABLE IF NOT EXISTS `role_group`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `gname` varchar(30) NOT NULL DEFAULT '' COMMENT 'group name',
  `gdesc` varchar(200) NOT NULL DEFAULT '' COMMENT 'group desc',
  `position` int(11) DEFAULT '1' COMMENT 'lib position page order by',    
  `mainrole` text COMMENT 'main role json',  
  `subrole` text COMMENT 'sub role module json,eg. passwd diable',
  `status` int(11) NOT NULL DEFAULT '1' COMMENT 'defaule 1 enable 0 disable',
  `isdelete` tinyint(1) DEFAULT '0' COMMENT 'defaule 0 normal 1 delete',
  `uid` int(11) NOT NULL DEFAULT '0' COMMENT 'creater uid',
  `ctime` int(11) NOT NULL COMMENT 'create time',  
  `etime` int(11) NOT NULL COMMENT 'last edit time',  
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `role_log`;
CREATE TABLE IF NOT EXISTS  `role_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `lid` varchar(30) NOT NULL DEFAULT '' COMMENT 'lib name lib ppl/lib ', 
  `uid` int(11) NOT NULL  COMMENT 'user id ', 
  `action` tinyint(1) DEFAULT '0' COMMENT 'action: read-0  create-1 edit-2 remove-3 search-4',
  `ip` varchar(16) NOT NULL DEFAULT '' COMMENT 'client source IP',   
  `ctime` int(11) NOT NULL DEFAULT '0' COMMENT 'log time',  
   PRIMARY KEY (`id`)   
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `web_user`;
CREATE TABLE IF NOT EXISTS `web_user` (
 `id` int(11) NOT NULL AUTO_INCREMENT,
  `login` varchar(100) NOT NULL,
  `password` char(32) NOT NULL,
  `alias` varchar(45) NOT NULL,
  `email` varchar(100) NOT NULL,
  `ctime` int(11) NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `login` (`login`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `web_article`;
CREATE TABLE IF NOT EXISTS `web_article` (
 `id` int(11) NOT NULL AUTO_INCREMENT,
  `auth` varchar(32) NOT NULL COMMENT 'article auth',
  `title` char(100) NOT NULL COMMENT 'article title',
  `cont` text NOT NULL COMMENT 'cont',
  `img` varchar(100) NOT NULL,  
  `ctime` int(11) NULL DEFAULT '0' COMMENT 'create time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


----- postgresql
--
-- PostgreSQL database dump
--
--
-- PostgreSQL database dump
--

CREATE TABLE hor_doc
(
  id serial NOT NULL,
  title character varying NOT NULL,
  ctime integer NOT NULL,
  doc text,
  CONSTRAINT hor_doc_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE hor_doc
  OWNER TO bi;


SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: hor_class; Type: TABLE; Schema: public; Owner: bi; Tablespace: 
--

CREATE TABLE hor_class (
    rule integer,
    act_v0 text,
    act_v1 text,
    act_v2 text,
    act_v3 text,
    act_v4 text,
    act_v5 text,
    act_v6 text,
    act_v7 text,
    act_v8 text,
    act_v9 text,
    act_va text,
    act_vb text,
    act_vc text,
    act_vd text,
    act_ve text,
    act_vf text,
    modify_time timestamp without time zone DEFAULT now(),
    act_v10 text
);


ALTER TABLE public.hor_class OWNER TO bi;

--
-- Name: TABLE hor_class; Type: COMMENT; Schema: public; Owner: bi
--

COMMENT ON TABLE hor_class IS '外部数据插入，比如CSV
rule 依赖于 classinfo 的 id 字段';


--
-- Name: el_mt4; Type: VIEW; Schema: public; Owner: bi
--

CREATE VIEW el_mt4 AS
 SELECT hor_class.act_v0 AS profit,
    hor_class.act_v1 AS login,
    hor_class.act_v2 AS symbol,
    hor_class.act_v3 AS open_time,
    hor_class.act_v4 AS "timestamp",
    hor_class.act_v5 AS volume,
    hor_class.act_v6 AS close_time
   FROM hor_class
  WHERE (hor_class.rule = 77);


ALTER TABLE public.el_mt4 OWNER TO bi;

--
-- Name: game_kfd; Type: VIEW; Schema: public; Owner: bi
--

CREATE VIEW game_kfd AS
 SELECT hor_class.act_v0 AS account_name
   FROM hor_class
  WHERE (hor_class.rule = 75);


ALTER TABLE public.game_kfd OWNER TO bi;

--
-- Name: game_kfdmt4; Type: VIEW; Schema: public; Owner: bi
--

CREATE VIEW game_kfdmt4 AS
 SELECT hor_class.act_v0 AS profit,
    hor_class.act_v1 AS login,
    hor_class.act_v2 AS symbol,
    hor_class.act_v3 AS open_time,
    hor_class.act_v4 AS "timestamp",
    hor_class.act_v5 AS volume,
    hor_class.act_v6 AS close_time
   FROM hor_class
  WHERE (hor_class.rule = 76);


ALTER TABLE public.game_kfdmt4 OWNER TO bi;

--
-- Name: game_mt4; Type: VIEW; Schema: public; Owner: bi
--

CREATE VIEW game_mt4 AS
 SELECT hor_class.act_v0 AS login,
    hor_class.act_v1 AS symbol,
    hor_class.act_v2 AS open_time,
    hor_class.act_v3 AS "timestamp",
    hor_class.act_v4 AS volume,
    hor_class.act_v5 AS close_time
   FROM hor_class
  WHERE (hor_class.rule = 73);


ALTER TABLE public.game_mt4 OWNER TO bi;

--
-- Name: hor_classify; Type: TABLE; Schema: public; Owner: bi; Tablespace: 
--

CREATE TABLE hor_classify (
    id integer NOT NULL,
    pid integer DEFAULT 0 NOT NULL,
    name character varying NOT NULL,
    ctime integer NOT NULL
);


ALTER TABLE public.hor_classify OWNER TO bi;

--
-- Name: hor_classify_id_seq; Type: SEQUENCE; Schema: public; Owner: bi
--

CREATE SEQUENCE hor_classify_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.hor_classify_id_seq OWNER TO bi;

--
-- Name: hor_classify_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: bi
--

ALTER SEQUENCE hor_classify_id_seq OWNED BY hor_classify.id;


--
-- Name: hor_classinfo; Type: TABLE; Schema: public; Owner: bi; Tablespace: 
--

CREATE TABLE hor_classinfo (
    id integer NOT NULL,
    title character varying NOT NULL,
    idesc character varying DEFAULT ''::character varying NOT NULL,
    ctime integer DEFAULT 1 NOT NULL,
    view_name character varying(50),
    ctype integer DEFAULT 0 NOT NULL
);


ALTER TABLE public.hor_classinfo OWNER TO bi;

--
-- Name: TABLE hor_classinfo; Type: COMMENT; Schema: public; Owner: bi
--

COMMENT ON TABLE hor_classinfo IS 'ctime 创建时间
etime 这次完成的时间  
view_name  view 的名称
ctype  -- 0 -- csv , 1 -- sql';


--
-- Name: COLUMN hor_classinfo.view_name; Type: COMMENT; Schema: public; Owner: bi
--

COMMENT ON COLUMN hor_classinfo.view_name IS '保存 view 的名称';


--
-- Name: COLUMN hor_classinfo.ctype; Type: COMMENT; Schema: public; Owner: bi
--

COMMENT ON COLUMN hor_classinfo.ctype IS '0 -- csv , 1 -- sql';


--
-- Name: hor_classinfo_id_seq; Type: SEQUENCE; Schema: public; Owner: bi
--

CREATE SEQUENCE hor_classinfo_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.hor_classinfo_id_seq OWNER TO bi;

--
-- Name: hor_classinfo_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: bi
--

ALTER SEQUENCE hor_classinfo_id_seq OWNED BY hor_classinfo.id;


--
-- Name: hor_dbsource; Type: TABLE; Schema: public; Owner: bi; Tablespace: 
--

CREATE TABLE hor_dbsource (
    id integer NOT NULL,
    title character varying NOT NULL,
    dcname character varying NOT NULL,
    url character varying NOT NULL,
    username character varying NOT NULL,
    password character varying NOT NULL,
    ctime integer NOT NULL,
    modify_time timestamp without time zone DEFAULT now()
);


ALTER TABLE public.hor_dbsource OWNER TO bi;

--
-- Name: hor_dbsource_id_seq; Type: SEQUENCE; Schema: public; Owner: bi
--

CREATE SEQUENCE hor_dbsource_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.hor_dbsource_id_seq OWNER TO bi;

--
-- Name: hor_dbsource_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: bi
--

ALTER SEQUENCE hor_dbsource_id_seq OWNED BY hor_dbsource.id;


--
-- Name: hor_mongodbrule; Type: TABLE; Schema: public; Owner: bi; Tablespace: 
--

CREATE TABLE hor_mongodbrule (
    id integer NOT NULL,
    name character varying NOT NULL,
    collention character varying NOT NULL,
    qaction smallint DEFAULT (0)::smallint NOT NULL,
    query text NOT NULL,
    field text NOT NULL,
    sort text NOT NULL,
    ctime integer NOT NULL,
    stime integer NOT NULL,
    etime integer DEFAULT 0 NOT NULL,
    istop smallint DEFAULT 0 NOT NULL,
    snap integer DEFAULT 0 NOT NULL,
    cid integer DEFAULT 0 NOT NULL
);


ALTER TABLE public.hor_mongodbrule OWNER TO bi;

--
-- Name: COLUMN hor_mongodbrule.snap; Type: COMMENT; Schema: public; Owner: bi
--

COMMENT ON COLUMN hor_mongodbrule.snap IS '0 -- 可以在报表那儿显示菜单，1 需要二次运算开发';


--
-- Name: COLUMN hor_mongodbrule.cid; Type: COMMENT; Schema: public; Owner: bi
--

COMMENT ON COLUMN hor_mongodbrule.cid IS 'classify id';


--
-- Name: hor_mongodbrule_id_seq; Type: SEQUENCE; Schema: public; Owner: bi
--

CREATE SEQUENCE hor_mongodbrule_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.hor_mongodbrule_id_seq OWNER TO bi;

--
-- Name: hor_mongodbrule_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: bi
--

ALTER SEQUENCE hor_mongodbrule_id_seq OWNED BY hor_mongodbrule.id;


--
-- Name: hor_sqltmp; Type: TABLE; Schema: public; Owner: bi; Tablespace: 
--

CREATE TABLE hor_sqltmp (
    id integer NOT NULL,
    sid integer NOT NULL,
    name character varying NOT NULL,
    sqltmp character varying(256) DEFAULT ''::character varying NOT NULL,
    ctime timestamp without time zone DEFAULT now()
);


ALTER TABLE public.hor_sqltmp OWNER TO bi;

--
-- Name: hor_sqltmp_id_seq; Type: SEQUENCE; Schema: public; Owner: bi
--

CREATE SEQUENCE hor_sqltmp_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.hor_sqltmp_id_seq OWNER TO bi;

--
-- Name: hor_sqltmp_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: bi
--

ALTER SEQUENCE hor_sqltmp_id_seq OWNED BY hor_sqltmp.id;


--
-- Name: hor_usersql; Type: TABLE; Schema: public; Owner: bi; Tablespace: 
--

CREATE TABLE hor_usersql (
    id integer NOT NULL,
    sql text NOT NULL,
    name character varying NOT NULL,
    modify_time timestamp without time zone DEFAULT now(),
    dtype integer DEFAULT 0 NOT NULL,
    sql_type integer DEFAULT 0 NOT NULL,
    sqltmp character varying(256) DEFAULT ''::character varying NOT NULL,
    uview character varying(256) DEFAULT ''::character varying NOT NULL,
    input_data integer DEFAULT 0 NOT NULL,
    vtime integer DEFAULT 0 NOT NULL,
    cid integer DEFAULT 0 NOT NULL
);


ALTER TABLE public.hor_usersql OWNER TO bi;

--
-- Name: TABLE hor_usersql; Type: COMMENT; Schema: public; Owner: bi
--

COMMENT ON TABLE hor_usersql IS 'sql 语句
name 名称
modify_time 更新时间
dtype 数据类型
sql_type 0 代表一般的SQL 1 是模板
sqltmp 是记录模板对应的变量值
uview 记录视图名称
input_data 0 -- 0 不导入，1 导入数据';


--
-- Name: COLUMN hor_usersql.input_data; Type: COMMENT; Schema: public; Owner: bi
--

COMMENT ON COLUMN hor_usersql.input_data IS '0 不导入，1 导入数据';


--
-- Name: COLUMN hor_usersql.vtime; Type: COMMENT; Schema: public; Owner: bi
--

COMMENT ON COLUMN hor_usersql.vtime IS 'view 运行完成时间';


--
-- Name: COLUMN hor_usersql.cid; Type: COMMENT; Schema: public; Owner: bi
--

COMMENT ON COLUMN hor_usersql.cid IS 'classify id';


--
-- Name: hor_usersql_id_seq; Type: SEQUENCE; Schema: public; Owner: bi
--

CREATE SEQUENCE hor_usersql_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.hor_usersql_id_seq OWNER TO bi;

--
-- Name: hor_usersql_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: bi
--

ALTER SEQUENCE hor_usersql_id_seq OWNED BY hor_usersql.id;


--
-- Name: hor_webvisitcount; Type: TABLE; Schema: public; Owner: bi; Tablespace: 
--

CREATE TABLE hor_webvisitcount (
    rule integer DEFAULT (0)::smallint NOT NULL,
    volume integer NOT NULL,
    dial integer DEFAULT 0 NOT NULL,
    modify_time timestamp without time zone DEFAULT now(),
    val character varying
);


ALTER TABLE public.hor_webvisitcount OWNER TO bi;

--
-- Name: TABLE hor_webvisitcount; Type: COMMENT; Schema: public; Owner: bi
--

COMMENT ON TABLE hor_webvisitcount IS 'rule 规则ID
volume 数量
dial 刻度盘';


--
-- Name: COLUMN hor_webvisitcount.val; Type: COMMENT; Schema: public; Owner: bi
--

COMMENT ON COLUMN hor_webvisitcount.val IS '值';


--
-- Name: kfd_mt4_trades; Type: VIEW; Schema: public; Owner: bi
--

CREATE VIEW kfd_mt4_trades AS
 SELECT hor_class.act_v0 AS cmd,
    hor_class.act_v1 AS profit,
    hor_class.act_v2 AS close_price,
    hor_class.act_v3 AS login,
    hor_class.act_v4 AS open_price,
    hor_class.act_v5 AS ticket,
    hor_class.act_v6 AS symbol,
    hor_class.act_v7 AS comment,
    hor_class.act_v8 AS open_time,
    hor_class.act_v9 AS volume,
    hor_class.act_va AS "timestamp",
    hor_class.act_vb AS close_time
   FROM hor_class
  WHERE (hor_class.rule = 1);


ALTER TABLE public.kfd_mt4_trades OWNER TO bi;

--
-- Name: role_group; Type: TABLE; Schema: public; Owner: bi; Tablespace: 
--

CREATE TABLE role_group (
    id integer NOT NULL,
    gname character varying DEFAULT ''::character varying NOT NULL,
    gdesc character varying DEFAULT ''::character varying NOT NULL,
    "position" integer DEFAULT 1,
    mainrole text,
    subrole text,
    status integer DEFAULT 1 NOT NULL,
    isdelete integer DEFAULT 0,
    uid integer DEFAULT 0 NOT NULL,
    ctime integer NOT NULL,
    etime integer NOT NULL
);


ALTER TABLE public.role_group OWNER TO bi;

--
-- Name: role_group_id_seq; Type: SEQUENCE; Schema: public; Owner: bi
--

CREATE SEQUENCE role_group_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.role_group_id_seq OWNER TO bi;

--
-- Name: role_group_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: bi
--

ALTER SEQUENCE role_group_id_seq OWNED BY role_group.id;


--
-- Name: role_log; Type: TABLE; Schema: public; Owner: bi; Tablespace: 
--

CREATE TABLE role_log (
    id integer NOT NULL,
    lid character varying DEFAULT ''::character varying NOT NULL,
    uid integer NOT NULL,
    action integer DEFAULT 0,
    ip character varying DEFAULT ''::character varying NOT NULL,
    ctime integer DEFAULT 0 NOT NULL,
    data text DEFAULT ''::text NOT NULL
);


ALTER TABLE public.role_log OWNER TO bi;

--
-- Name: role_log_id_seq; Type: SEQUENCE; Schema: public; Owner: bi
--

CREATE SEQUENCE role_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.role_log_id_seq OWNER TO bi;

--
-- Name: role_log_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: bi
--

ALTER SEQUENCE role_log_id_seq OWNED BY role_log.id;


--
-- Name: role_user_info; Type: TABLE; Schema: public; Owner: bi; Tablespace: 
--

CREATE TABLE role_user_info (
    uid integer NOT NULL,
    name character varying NOT NULL,
    passwd character varying NOT NULL,
    cm character varying NOT NULL,
    nickname character varying NOT NULL,
    email character varying NOT NULL,
    ctime integer DEFAULT 1 NOT NULL,
    etime integer DEFAULT 0,
    ltime integer DEFAULT 1 NOT NULL,
    phone character varying DEFAULT ''::character varying NOT NULL,
    status integer DEFAULT 1 NOT NULL,
    isdelete integer DEFAULT 0,
    gid integer DEFAULT 0 NOT NULL,
    cid integer DEFAULT 0 NOT NULL,
    error integer DEFAULT 0 NOT NULL
);


ALTER TABLE public.role_user_info OWNER TO bi;

--
-- Name: role_user_info_uid_seq; Type: SEQUENCE; Schema: public; Owner: bi
--

CREATE SEQUENCE role_user_info_uid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.role_user_info_uid_seq OWNER TO bi;

--
-- Name: role_user_info_uid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: bi
--

ALTER SEQUENCE role_user_info_uid_seq OWNED BY role_user_info.uid;


--
-- Name: utest_viwes; Type: VIEW; Schema: public; Owner: bi
--

CREATE VIEW utest_viwes AS
 SELECT hor_class.act_v0 AS dial,
    hor_class.act_v1 AS account_name
   FROM hor_class
  WHERE (hor_class.rule = 78);


ALTER TABLE public.utest_viwes OWNER TO bi;

--
-- Name: id; Type: DEFAULT; Schema: public; Owner: bi
--

ALTER TABLE ONLY hor_classify ALTER COLUMN id SET DEFAULT nextval('hor_classify_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: bi
--

ALTER TABLE ONLY hor_classinfo ALTER COLUMN id SET DEFAULT nextval('hor_classinfo_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: bi
--

ALTER TABLE ONLY hor_dbsource ALTER COLUMN id SET DEFAULT nextval('hor_dbsource_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: bi
--

ALTER TABLE ONLY hor_mongodbrule ALTER COLUMN id SET DEFAULT nextval('hor_mongodbrule_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: bi
--

ALTER TABLE ONLY hor_sqltmp ALTER COLUMN id SET DEFAULT nextval('hor_sqltmp_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: bi
--

ALTER TABLE ONLY hor_usersql ALTER COLUMN id SET DEFAULT nextval('hor_usersql_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: bi
--

ALTER TABLE ONLY role_group ALTER COLUMN id SET DEFAULT nextval('role_group_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: bi
--

ALTER TABLE ONLY role_log ALTER COLUMN id SET DEFAULT nextval('role_log_id_seq'::regclass);


--
-- Name: uid; Type: DEFAULT; Schema: public; Owner: bi
--

ALTER TABLE ONLY role_user_info ALTER COLUMN uid SET DEFAULT nextval('role_user_info_uid_seq'::regclass);


--
-- Name: hor_classify_pkey; Type: CONSTRAINT; Schema: public; Owner: bi; Tablespace: 
--

ALTER TABLE ONLY hor_classify
    ADD CONSTRAINT hor_classify_pkey PRIMARY KEY (id);


--
-- Name: hor_classinfo_pkey; Type: CONSTRAINT; Schema: public; Owner: bi; Tablespace: 
--

ALTER TABLE ONLY hor_classinfo
    ADD CONSTRAINT hor_classinfo_pkey PRIMARY KEY (id);


--
-- Name: hor_mongodbrule_pkey; Type: CONSTRAINT; Schema: public; Owner: bi; Tablespace: 
--

ALTER TABLE ONLY hor_mongodbrule
    ADD CONSTRAINT hor_mongodbrule_pkey PRIMARY KEY (id);


--
-- Name: hor_sqltmp_pkey; Type: CONSTRAINT; Schema: public; Owner: bi; Tablespace: 
--

ALTER TABLE ONLY hor_sqltmp
    ADD CONSTRAINT hor_sqltmp_pkey PRIMARY KEY (id);


--
-- Name: role_group_pkey; Type: CONSTRAINT; Schema: public; Owner: bi; Tablespace: 
--

ALTER TABLE ONLY role_group
    ADD CONSTRAINT role_group_pkey PRIMARY KEY (id);


--
-- Name: role_log_pkey; Type: CONSTRAINT; Schema: public; Owner: bi; Tablespace: 
--

ALTER TABLE ONLY role_log
    ADD CONSTRAINT role_log_pkey PRIMARY KEY (id);


--
-- Name: role_user_info_name_key; Type: CONSTRAINT; Schema: public; Owner: bi; Tablespace: 
--

ALTER TABLE ONLY role_user_info
    ADD CONSTRAINT role_user_info_name_key UNIQUE (name);


--
-- Name: role_user_info_pkey; Type: CONSTRAINT; Schema: public; Owner: bi; Tablespace: 
--

ALTER TABLE ONLY role_user_info
    ADD CONSTRAINT role_user_info_pkey PRIMARY KEY (uid);


--
-- Name: rul_wvcdial; Type: INDEX; Schema: public; Owner: bi; Tablespace: 
--

CREATE INDEX rul_wvcdial ON hor_webvisitcount USING btree (rule, dial);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--