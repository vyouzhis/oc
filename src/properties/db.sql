 
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
-- Name: c_hdfgsdf; Type: TABLE; Schema: public; Owner: bi; Tablespace: 
--

CREATE TABLE c_hdfgsdf (
    t1 integer DEFAULT 0 NOT NULL,
    t2 character varying DEFAULT 'sdf'::character varying NOT NULL,
    t3 text NOT NULL,
    t4 date DEFAULT '2015-07-22'::date NOT NULL,
    t5 boolean DEFAULT true NOT NULL,
    t6 double precision DEFAULT 0.100000000000000006::double precision NOT NULL
);


ALTER TABLE public.c_hdfgsdf OWNER TO bi;

--
-- Name: c_mydata; Type: TABLE; Schema: public; Owner: bi; Tablespace: 
--

CREATE TABLE c_mydata (
    id integer DEFAULT 0 NOT NULL,
    name character varying DEFAULT '100'::character varying NOT NULL,
    ctime integer DEFAULT 0 NOT NULL,
    link text NOT NULL
);


ALTER TABLE public.c_mydata OWNER TO bi;

--
-- Name: hor_class; Type: TABLE; Schema: public; Owner: bi; Tablespace: 
--

CREATE TABLE hor_class (
    rule integer DEFAULT 0 NOT NULL,
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
    act_v10 text,
    act_v11 text,
    act_v12 text,
    act_v13 text,
    act_v14 text,
    act_v15 text,
    act_v16 text,
    act_v17 text,
    act_v18 text,
    act_v19 text,
    act_v1a text,
    act_v1b text,
    act_v1c text,
    act_v1d text
);


ALTER TABLE public.hor_class OWNER TO bi;

--
-- Name: TABLE hor_class; Type: COMMENT; Schema: public; Owner: bi
--

COMMENT ON TABLE hor_class IS '外部数据插入，比如CSV
rule 依赖于 classinfo 的 id 字段';


--
-- Name: eldata; Type: VIEW; Schema: public; Owner: bi
--

CREATE VIEW eldata AS
 SELECT hor_class.act_v0 AS "﻿deal",
    hor_class.act_v1 AS login,
    hor_class.act_v2 AS name,
    hor_class.act_v3 AS open_time,
    hor_class.act_v4 AS type,
    hor_class.act_v5 AS symbol,
    hor_class.act_v6 AS volume,
    hor_class.act_v7 AS open_price,
    hor_class.act_v8 AS close_time,
    hor_class.act_v9 AS close_price,
    hor_class.act_va AS commission,
    hor_class.act_vb AS taxes,
    hor_class.act_vc AS agent,
    hor_class.act_vd AS swap,
    hor_class.act_ve AS profit,
    hor_class.act_vf AS pips,
    hor_class.act_v10 AS comment
   FROM hor_class
  WHERE (hor_class.rule = 38);


ALTER TABLE public.eldata OWNER TO bi;

--
-- Name: hor_apisecret; Type: TABLE; Schema: public; Owner: bi; Tablespace: 
--

CREATE TABLE hor_apisecret (
    id integer NOT NULL,
    title character varying NOT NULL,
    username character varying NOT NULL,
    passwd character varying NOT NULL,
    ctime integer NOT NULL,
    idesc character varying DEFAULT ''::character varying NOT NULL,
    secret character varying(32) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE public.hor_apisecret OWNER TO bi;

--
-- Name: COLUMN hor_apisecret.secret; Type: COMMENT; Schema: public; Owner: bi
--

COMMENT ON COLUMN hor_apisecret.secret IS 'secret';


--
-- Name: hor_apisecret_id_seq; Type: SEQUENCE; Schema: public; Owner: bi
--

CREATE SEQUENCE hor_apisecret_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.hor_apisecret_id_seq OWNER TO bi;

--
-- Name: hor_apisecret_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: bi
--

ALTER SEQUENCE hor_apisecret_id_seq OWNED BY hor_apisecret.id;


--
-- Name: hor_cache; Type: TABLE; Schema: public; Owner: bi; Tablespace: 
--

CREATE TABLE hor_cache (
    md5 character varying NOT NULL,
    json text NOT NULL,
    ctime timestamp without time zone DEFAULT now(),
    title character varying NOT NULL
);


ALTER TABLE public.hor_cache OWNER TO bi;

--
-- Name: COLUMN hor_cache.title; Type: COMMENT; Schema: public; Owner: bi
--

COMMENT ON COLUMN hor_cache.title IS '名称';


--
-- Name: hor_classify; Type: TABLE; Schema: public; Owner: bi; Tablespace: 
--

CREATE TABLE hor_classify (
    id integer NOT NULL,
    pid integer DEFAULT 0 NOT NULL,
    name character varying NOT NULL,
    ctime integer NOT NULL,
    displays integer DEFAULT 0 NOT NULL
);


ALTER TABLE public.hor_classify OWNER TO bi;

--
-- Name: COLUMN hor_classify.displays; Type: COMMENT; Schema: public; Owner: bi
--

COMMENT ON COLUMN hor_classify.displays IS '0 显示，1 隐藏';


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
-- Name: hor_doc; Type: TABLE; Schema: public; Owner: bi; Tablespace: 
--

CREATE TABLE hor_doc (
    id integer NOT NULL,
    title character varying NOT NULL,
    ctime integer NOT NULL,
    doc text
);


ALTER TABLE public.hor_doc OWNER TO bi;

--
-- Name: hor_doc_id_seq; Type: SEQUENCE; Schema: public; Owner: bi
--

CREATE SEQUENCE hor_doc_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.hor_doc_id_seq OWNER TO bi;

--
-- Name: hor_doc_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: bi
--

ALTER SEQUENCE hor_doc_id_seq OWNED BY hor_doc.id;


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
    sqltmp text DEFAULT ''::character varying NOT NULL,
    ctime timestamp without time zone DEFAULT now(),
    etime integer DEFAULT 0 NOT NULL
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
    cid integer DEFAULT 0 NOT NULL,
    uid integer DEFAULT 0 NOT NULL
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
-- Name: COLUMN hor_usersql.uid; Type: COMMENT; Schema: public; Owner: bi
--

COMMENT ON COLUMN hor_usersql.uid IS '用户ID';


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
-- Name: p2p; Type: VIEW; Schema: public; Owner: bi
--

CREATE VIEW p2p AS
 SELECT hor_class.act_v0 AS risk,
    hor_class.act_v1 AS title,
    hor_class.act_v2 AS lowestamount,
    hor_class.act_v3 AS investcycle,
    hor_class.act_v4 AS investfield,
    hor_class.act_v5 AS riskscore,
    hor_class.act_v6 AS producttypelabel,
    hor_class.act_v7 AS expectedprofitrate
   FROM hor_class
  WHERE (hor_class.rule = 39);


ALTER TABLE public.p2p OWNER TO bi;

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
-- Name: testsdf; Type: TABLE; Schema: public; Owner: bi; Tablespace: 
--

CREATE TABLE testsdf (
    t1 integer DEFAULT 222 NOT NULL,
    t2 text NOT NULL,
    t3 date DEFAULT '2015-07-23'::date NOT NULL
);


ALTER TABLE public.testsdf OWNER TO bi;

--
-- Name: wine; Type: VIEW; Schema: public; Owner: bi
--

CREATE VIEW wine AS
 SELECT hor_class.act_v0 AS factory,
    hor_class.act_v1 AS addr,
    hor_class.act_v2 AS phome,
    hor_class.act_v3 AS burden,
    hor_class.act_v4 AS storage,
    hor_class.act_v5 AS quality,
    hor_class.act_v6 AS title,
    hor_class.act_v7 AS content,
    hor_class.act_v8 AS brand,
    hor_class.act_v9 AS series,
    hor_class.act_va AS set_size,
    hor_class.act_vb AS wine_class,
    hor_class.act_vc AS wine_grade,
    hor_class.act_vd AS area,
    hor_class.act_ve AS grapes,
    hor_class.act_vf AS flavour,
    hor_class.act_v10 AS drinking_occasion,
    hor_class.act_v11 AS origin,
    hor_class.act_v12 AS classification,
    hor_class.act_v13 AS packing,
    hor_class.act_v14 AS collocation,
    hor_class.act_v15 AS sleeping_time,
    hor_class.act_v16 AS bouquet,
    hor_class.act_v17 AS import_type,
    hor_class.act_v18 AS price,
    hor_class.act_v19 AS cost_price,
    hor_class.act_v1a AS sales,
    hor_class.act_v1b AS evaluate
   FROM hor_class
  WHERE (hor_class.rule = 40);


ALTER TABLE public.wine OWNER TO bi;

--
-- Name: id; Type: DEFAULT; Schema: public; Owner: bi
--

ALTER TABLE ONLY hor_apisecret ALTER COLUMN id SET DEFAULT nextval('hor_apisecret_id_seq'::regclass);


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

ALTER TABLE ONLY hor_doc ALTER COLUMN id SET DEFAULT nextval('hor_doc_id_seq'::regclass);


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
-- Name: hor_apisecret_pkey; Type: CONSTRAINT; Schema: public; Owner: bi; Tablespace: 
--

ALTER TABLE ONLY hor_apisecret
    ADD CONSTRAINT hor_apisecret_pkey PRIMARY KEY (id);


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
-- Name: hor_doc_pkey; Type: CONSTRAINT; Schema: public; Owner: bi; Tablespace: 
--

ALTER TABLE ONLY hor_doc
    ADD CONSTRAINT hor_doc_pkey PRIMARY KEY (id);


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
-- Name: md5_index; Type: INDEX; Schema: public; Owner: bi; Tablespace: 
--

CREATE INDEX md5_index ON hor_cache USING btree (md5);


--
-- Name: INDEX md5_index; Type: COMMENT; Schema: public; Owner: bi
--

COMMENT ON INDEX md5_index IS 'md5_index';


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