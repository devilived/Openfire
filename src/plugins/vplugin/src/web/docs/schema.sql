SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS  `v_location`;
CREATE TABLE `v_location` (
  `uid` bigint(20) NOT NULL,
  `lat` double DEFAULT NULL COMMENT '经度',
  `lon` double DEFAULT NULL COMMENT '纬度',
  `time` datetime DEFAULT NULL COMMENT '时间',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS  `v_location_nearby`;
CREATE TABLE `v_location_nearby` (
  `uid` bigint(20) NOT NULL COMMENT 'uid',
  `lat` double NOT NULL COMMENT '纬度',
  `lon` double NOT NULL COMMENT '经度',
  `time` datetime NOT NULL COMMENT '时间',
  `loc_private` tinyint(1) DEFAULT NULL COMMENT '是否保密（冗余为了查询）',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别(冗余)',
  `birth` date DEFAULT NULL COMMENT '生日(冗余)',
  PRIMARY KEY (`uid`),
  KEY `idx_locationnearby_birth` (`birth`),
  KEY `idx_locationnearby_time` (`time`),
  KEY `idx_locationnearby_locprivate` (`loc_private`) USING HASH,
  KEY `idx_locationnearby_sex` (`sex`) USING HASH
) ENGINE=InnoDB ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS  `v_log`;
CREATE TABLE `v_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` varchar(10) NOT NULL COMMENT '时间类型',
  `time` datetime DEFAULT NULL COMMENT '日志发生时间',
  `create_by` int(20) DEFAULT NULL COMMENT '日志操作者',
  `tgt_uid` int(20) DEFAULT NULL COMMENT '操作对象',
  `content` text COMMENT '日志内容',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '日志记录时间',
  PRIMARY KEY (`id`),
  KEY `idx_log_type` (`type`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS  `v_lvl`;
CREATE TABLE `v_lvl` (
  `name` varchar(10) NOT NULL COMMENT '会员类型',
  `money` int(11) NOT NULL COMMENT '会员费用(分)',
  `during` int(11) NOT NULL COMMENT '会员时长(s)',
  `remark` varchar(64) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS  `v_order`;
CREATE TABLE `v_order` (
  `id` char(64) NOT NULL COMMENT '订单号',
  `uid` bigint(20) NOT NULL,
  `subject` varchar(128) DEFAULT NULL COMMENT '商品名称',
  `attach` varchar(128) DEFAULT NULL COMMENT '商品附加信息',
  `total_fee` float DEFAULT NULL COMMENT '总金额',
  `pay_type` varchar(5) DEFAULT NULL COMMENT '支付类型',
  `trade_no` varchar(64) DEFAULT NULL COMMENT '支付交易号',
  `pay_acc` varchar(64) DEFAULT NULL COMMENT '支付账号',
  `status` varchar(10) NOT NULL COMMENT '状态',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_paytype` (`pay_type`) USING HASH,
  KEY `idx_order_status` (`status`) USING HASH,
  KEY `idx_order_uid` (`uid`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS  `v_paylog`;
CREATE TABLE `v_paylog` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` bigint(20) DEFAULT NULL,
  `pay_event` varchar(10) NOT NULL COMMENT '支付或者退款',
  `pay_type` varchar(10) DEFAULT NULL COMMENT '商品名称->会员类型',
  `pay_acc` varchar(100) DEFAULT NULL COMMENT '支付宝账号或银行',
  `total_fee` int(11) DEFAULT NULL COMMENT '交易金额(分)',
  `trade_no` varchar(64) DEFAULT NULL COMMENT '订单号',
  `pay_time` datetime DEFAULT NULL COMMENT '交易付款时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '会员是否有效(未过期/被升级替换)',
  `content` text,
  PRIMARY KEY (`id`),
  KEY `uid` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='此表只是用来记录支付通知流水，不做他用';

DROP TABLE IF EXISTS  `v_trace`;
CREATE TABLE `v_trace` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` varchar(15) NOT NULL COMMENT 'uid',
  `points` mediumtext NOT NULL COMMENT '足迹点的json串',
  `date` date NOT NULL COMMENT '创建/更新足迹时间',
  `modify_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `hash` char(32) NOT NULL COMMENT '唯一索引',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unq_trace_hash` (`hash`) USING HASH,
  KEY `idx_trace_date` (`date`),
  KEY `idx_trace_uid` (`uid`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS  `v_user`;
CREATE TABLE `v_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `pwd` varchar(512) CHARACTER SET utf8 DEFAULT NULL COMMENT '密码',
  `name` varchar(15) CHARACTER SET utf8 DEFAULT NULL COMMENT '姓名',
  `nick` varchar(50) DEFAULT NULL COMMENT '昵称',
  `email` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT 'email',
  `phone` varchar(15) CHARACTER SET utf8 DEFAULT NULL COMMENT '电话',
  `status` varchar(10) CHARACTER SET utf8 NOT NULL COMMENT '状态',
  `lvl` char(5) CHARACTER SET utf8 DEFAULT NULL COMMENT '会员级别',
  `lvl_end` datetime DEFAULT NULL COMMENT '会员过期时间',
  `sex` int(1) DEFAULT NULL COMMENT '性别',
  `birth` date DEFAULT NULL COMMENT '年龄',
  `signature` varchar(50) DEFAULT NULL COMMENT '签名',
  `address` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '地址',
  `avatar_uri` varchar(128) CHARACTER SET utf8 DEFAULT NULL COMMENT '头像',
  `photo_uri` varchar(550) CHARACTER SET utf8 DEFAULT NULL COMMENT '相片',
  `loc_private` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'vip位置保密',
  `avoid_disturb` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'vip防骚扰',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `modify_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unq_user_phone` (`phone`) USING HASH,
  UNIQUE KEY `unq_user_email` (`email`) USING HASH,
  KEY `idx_user_name` (`name`) USING HASH,
  KEY `idx_user_sex` (`sex`) USING HASH,
  KEY `idx_user_nick` (`nick`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;

