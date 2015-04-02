 
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

DROP TABLE IF EXISTS role_user_info;
CREATE TABLE IF NOT EXISTS role_user_info (
  uid SERIAL ,
  name varchar NOT NULL  UNIQUE,
  passwd varchar NOT NULL,
  cm varchar NOT NULL ,
  nickname varchar NOT NULL ,
  email varchar NOT NULL ,
  ctime int NOT NULL DEFAULT '1' ,
  etime int  DEFAULT '0' ,  
  ltime int NOT NULL DEFAULT '1' ,  
  phone varchar NOT NULL DEFAULT '' ,
  status int NOT NULL DEFAULT '1' ,  
  isdelete int DEFAULT '0' ,
  gid int NOT NULL DEFAULT '0' ,
  cid int NOT NULL DEFAULT '0' ,
  error int NOT NULL DEFAULT '0' ,
  PRIMARY KEY (uid)
) ;


DROP TABLE IF EXISTS role_group;
CREATE TABLE IF NOT EXISTS role_group  (
  id SERIAL,
  gname varchar NOT NULL DEFAULT '' ,
  gdesc varchar NOT NULL DEFAULT '' ,
  position int DEFAULT '1' ,    
  mainrole text ,  
  subrole text ,
  status int NOT NULL DEFAULT '1' ,
  isdelete int DEFAULT '0' ,
  uid int NOT NULL DEFAULT '0' ,
  ctime int NOT NULL ,  
  etime int NOT NULL ,  
  PRIMARY KEY (id)
) ;


DROP TABLE IF EXISTS role_log;
CREATE TABLE IF NOT EXISTS  role_log (
  id SERIAL,
  lid varchar NOT NULL DEFAULT '' , 
  uid int NOT NULL  , 
  action int DEFAULT '0' ,
  ip varchar NOT NULL DEFAULT '' ,   
  ctime int NOT NULL DEFAULT '0' ,  
   PRIMARY KEY (id)   
) ;


DROP TABLE IF EXISTS hor_rule;
CREATE TABLE IF NOT EXISTS hor_rule (
  id serial NOT NULL,
  name character varying NOT NULL,
  collention character varying NOT NULL,
  qaction smallint NOT NULL DEFAULT 0::smallint,
  query text NOT NULL,
  field text NOT NULL,
  sort text NOT NULL,
  ctime integer NOT NULL,
  stime integer NOT NULL DEFAULT 0,
  etime integer NOT NULL DEFAULT 0,
  istop smallint NOT NULL DEFAULT 0, -- default 0 ,  1 is stop
  CONSTRAINT hor_rule_pkey PRIMARY KEY (id )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE hor_rule
  OWNER TO vyouzhi;
COMMENT ON TABLE hor_rule
  IS 'name 规则名称
	collention 集合名
	qaction 查询方式 
	query  查询条件
	field 查询条件
	sort 排序
	ctime 创建时间
	stime 开始时间
	etime 当前结束时间';

	
DROP TABLE IF EXISTS hor_mongodbcount;
CREATE TABLE IF NOT EXISTS hor_mongodbcount
(
  rule integer NOT NULL DEFAULT (0)::smallint,
  volume integer NOT NULL,
  ctime integer NOT NULL DEFAULT 0,
  CONSTRAINT rule_ctime UNIQUE (rule , ctime )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE hor_mongodbcount
  OWNER TO vyouzhi;
COMMENT ON TABLE hor_mongodbcount
  IS 'rule 规则ID
volume 数量
ctime 时间';

-- Index: rul_ctime

-- DROP INDEX rul_ctime;

CREATE INDEX rul_ctime
  ON hor_mongodbcount
  USING btree
  (rule , ctime );
