/*
SQLyog Ultimate v8.32 
MySQL - 5.5.5-10.1.9-MariaDB : Database - openfire
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`openfire` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `openfire`;

/*Table structure for table `ofExtComponentConf` */

CREATE TABLE `ofExtComponentConf` (
  `subdomain` varchar(255) NOT NULL,
  `wildcard` tinyint(4) NOT NULL,
  `secret` varchar(255) DEFAULT NULL,
  `permission` varchar(10) NOT NULL,
  PRIMARY KEY (`subdomain`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `ofExtComponentConf` */

/*Table structure for table `ofGroup` */

CREATE TABLE `ofGroup` (
  `groupName` varchar(50) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`groupName`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `ofGroup` */

/*Table structure for table `ofGroupProp` */

CREATE TABLE `ofGroupProp` (
  `groupName` varchar(50) NOT NULL,
  `name` varchar(100) NOT NULL,
  `propValue` text NOT NULL,
  PRIMARY KEY (`groupName`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `ofGroupProp` */

/*Table structure for table `ofGroupUser` */

CREATE TABLE `ofGroupUser` (
  `groupName` varchar(50) NOT NULL,
  `username` varchar(100) NOT NULL,
  `administrator` tinyint(4) NOT NULL,
  PRIMARY KEY (`groupName`,`username`,`administrator`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `ofGroupUser` */

/*Table structure for table `ofID` */

CREATE TABLE `ofID` (
  `idType` int(11) NOT NULL,
  `id` bigint(20) NOT NULL,
  PRIMARY KEY (`idType`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `ofID` */

insert  into `ofID`(`idType`,`id`) values (18,1),(19,1),(23,1),(26,2);

/*Table structure for table `ofMucAffiliation` */

CREATE TABLE `ofMucAffiliation` (
  `roomID` bigint(20) NOT NULL,
  `jid` text NOT NULL,
  `affiliation` tinyint(4) NOT NULL,
  PRIMARY KEY (`roomID`,`jid`(70))
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `ofMucAffiliation` */

/*Table structure for table `ofMucConversationLog` */

CREATE TABLE `ofMucConversationLog` (
  `roomID` bigint(20) NOT NULL,
  `sender` text NOT NULL,
  `nickname` varchar(255) DEFAULT NULL,
  `logTime` char(15) NOT NULL,
  `subject` varchar(255) DEFAULT NULL,
  `body` text,
  KEY `ofMucConversationLog_time_idx` (`logTime`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `ofMucConversationLog` */

/*Table structure for table `ofMucMember` */

CREATE TABLE `ofMucMember` (
  `roomID` bigint(20) NOT NULL,
  `jid` text NOT NULL,
  `nickname` varchar(255) DEFAULT NULL,
  `firstName` varchar(100) DEFAULT NULL,
  `lastName` varchar(100) DEFAULT NULL,
  `url` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `faqentry` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`roomID`,`jid`(70))
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `ofMucMember` */

/*Table structure for table `ofMucRoom` */

CREATE TABLE `ofMucRoom` (
  `serviceID` bigint(20) NOT NULL,
  `roomID` bigint(20) NOT NULL,
  `creationDate` char(15) NOT NULL,
  `modificationDate` char(15) NOT NULL,
  `name` varchar(50) NOT NULL,
  `naturalName` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `lockedDate` char(15) NOT NULL,
  `emptyDate` char(15) DEFAULT NULL,
  `canChangeSubject` tinyint(4) NOT NULL,
  `maxUsers` int(11) NOT NULL,
  `publicRoom` tinyint(4) NOT NULL,
  `moderated` tinyint(4) NOT NULL,
  `membersOnly` tinyint(4) NOT NULL,
  `canInvite` tinyint(4) NOT NULL,
  `roomPassword` varchar(50) DEFAULT NULL,
  `canDiscoverJID` tinyint(4) NOT NULL,
  `logEnabled` tinyint(4) NOT NULL,
  `subject` varchar(100) DEFAULT NULL,
  `rolesToBroadcast` tinyint(4) NOT NULL,
  `useReservedNick` tinyint(4) NOT NULL,
  `canChangeNick` tinyint(4) NOT NULL,
  `canRegister` tinyint(4) NOT NULL,
  PRIMARY KEY (`serviceID`,`name`),
  KEY `ofMucRoom_roomid_idx` (`roomID`),
  KEY `ofMucRoom_serviceid_idx` (`serviceID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `ofMucRoom` */

/*Table structure for table `ofMucRoomProp` */

CREATE TABLE `ofMucRoomProp` (
  `roomID` bigint(20) NOT NULL,
  `name` varchar(100) NOT NULL,
  `propValue` text NOT NULL,
  PRIMARY KEY (`roomID`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `ofMucRoomProp` */

/*Table structure for table `ofMucService` */

CREATE TABLE `ofMucService` (
  `serviceID` bigint(20) NOT NULL,
  `subdomain` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `isHidden` tinyint(4) NOT NULL,
  PRIMARY KEY (`subdomain`),
  KEY `ofMucService_serviceid_idx` (`serviceID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `ofMucService` */

insert  into `ofMucService`(`serviceID`,`subdomain`,`description`,`isHidden`) values (1,'conference',NULL,0);

/*Table structure for table `ofMucServiceProp` */

CREATE TABLE `ofMucServiceProp` (
  `serviceID` bigint(20) NOT NULL,
  `name` varchar(100) NOT NULL,
  `propValue` text NOT NULL,
  PRIMARY KEY (`serviceID`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `ofMucServiceProp` */

/*Table structure for table `ofOffline` */

CREATE TABLE `ofOffline` (
  `username` varchar(64) NOT NULL,
  `messageID` bigint(20) NOT NULL,
  `creationDate` char(15) NOT NULL,
  `messageSize` int(11) NOT NULL,
  `stanza` text NOT NULL,
  PRIMARY KEY (`username`,`messageID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `ofOffline` */

/*Table structure for table `ofPresence` */

CREATE TABLE `ofPresence` (
  `username` varchar(64) NOT NULL,
  `offlinePresence` text,
  `offlineDate` char(15) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `ofPresence` */

/*Table structure for table `ofPrivacyList` */

CREATE TABLE `ofPrivacyList` (
  `username` varchar(64) NOT NULL,
  `name` varchar(100) NOT NULL,
  `isDefault` tinyint(4) NOT NULL,
  `list` text NOT NULL,
  PRIMARY KEY (`username`,`name`),
  KEY `ofPrivacyList_default_idx` (`username`,`isDefault`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `ofPrivacyList` */

/*Table structure for table `ofPrivate` */

CREATE TABLE `ofPrivate` (
  `username` varchar(64) NOT NULL,
  `name` varchar(100) NOT NULL,
  `namespace` varchar(200) NOT NULL,
  `privateData` text NOT NULL,
  PRIMARY KEY (`username`,`name`,`namespace`(100))
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `ofPrivate` */

/*Table structure for table `ofProperty` */

CREATE TABLE `ofProperty` (
  `name` varchar(100) NOT NULL,
  `propValue` text NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `ofProperty` */

insert  into `ofProperty`(`name`,`propValue`) values ('adminConsole.port','9090'),('adminConsole.securePort','9091'),('connectionProvider.className','org.jivesoftware.database.DefaultConnectionProvider'),('database.defaultProvider.connectionTimeout','1.0'),('database.defaultProvider.driver','com.mysql.jdbc.Driver'),('database.defaultProvider.maxConnections','150'),('database.defaultProvider.minConnections','50'),('database.defaultProvider.password','87971fb784011fd56b59e63caa1dc222630b67fec4ff3ddcc9e97a2aa9594ad9c3a7488665dbaf60'),('database.defaultProvider.serverURL','jdbc:mysql://telephone.mysql.rds.aliyuncs.com:3306/openfire?rewriteBatchedStatements=true&useUnicode=true&characterEncoding=UTF-8'),('database.defaultProvider.testAfterUse','false'),('database.defaultProvider.testBeforeUse','false'),('database.defaultProvider.testSQL','select 1'),('database.defaultProvider.username','0f3300dbdfdbc4e0f11effe444dbc1049f20f8c374823603a75741a5fcb54c81'),('locale','zh_CN'),('passwordKey','OS6Tik088cWEay6'),('plugin.vplugin.telplugin.pay_notify_url','http://phonetest.vidmt.com/TelServer/api/1/pay/{paytype}/notify.api'),('plugin.vplugin.telplugin.respath','/alidata1/data/html/telephone'),('provider.admin.className','com.vidmt.of.plugin.sub.extdb.VAdminProvider'),('provider.auth.className','com.vidmt.of.plugin.sub.extdb.VAuthProvider'),('provider.group.className','org.jivesoftware.openfire.group.DefaultGroupProvider'),('provider.lockout.className','org.jivesoftware.openfire.lockout.DefaultLockOutProvider'),('provider.securityAudit.className','org.jivesoftware.openfire.security.DefaultSecurityAuditProvider'),('provider.user.className','com.vidmt.of.plugin.sub.extdb.VUserProvider'),('provider.vcard.className','org.jivesoftware.openfire.vcard.DefaultVCardProvider'),('sasl.scram-sha-1.iteration-count','4096'),('setup','true'),('stream.management.active','true'),('stream.management.requestFrequency','5'),('update.lastCheck','1451546636514'),('xmpp.auth.anonymous','true'),('xmpp.domain','vidmt.com'),('xmpp.proxy.enabled','false'),('xmpp.session.conflict-limit','0'),('xmpp.socket.ssl.active','true');

/*Table structure for table `ofPubsubAffiliation` */

CREATE TABLE `ofPubsubAffiliation` (
  `serviceID` varchar(100) NOT NULL,
  `nodeID` varchar(100) NOT NULL,
  `jid` varchar(255) NOT NULL,
  `affiliation` varchar(10) NOT NULL,
  PRIMARY KEY (`serviceID`,`nodeID`,`jid`(70))
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `ofPubsubAffiliation` */

insert  into `ofPubsubAffiliation`(`serviceID`,`nodeID`,`jid`,`affiliation`) values ('pubsub','','vidmt.com','owner');

/*Table structure for table `ofPubsubDefaultConf` */

CREATE TABLE `ofPubsubDefaultConf` (
  `serviceID` varchar(100) NOT NULL,
  `leaf` tinyint(4) NOT NULL,
  `deliverPayloads` tinyint(4) NOT NULL,
  `maxPayloadSize` int(11) NOT NULL,
  `persistItems` tinyint(4) NOT NULL,
  `maxItems` int(11) NOT NULL,
  `notifyConfigChanges` tinyint(4) NOT NULL,
  `notifyDelete` tinyint(4) NOT NULL,
  `notifyRetract` tinyint(4) NOT NULL,
  `presenceBased` tinyint(4) NOT NULL,
  `sendItemSubscribe` tinyint(4) NOT NULL,
  `publisherModel` varchar(15) NOT NULL,
  `subscriptionEnabled` tinyint(4) NOT NULL,
  `accessModel` varchar(10) NOT NULL,
  `language` varchar(255) DEFAULT NULL,
  `replyPolicy` varchar(15) DEFAULT NULL,
  `associationPolicy` varchar(15) NOT NULL,
  `maxLeafNodes` int(11) NOT NULL,
  PRIMARY KEY (`serviceID`,`leaf`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `ofPubsubDefaultConf` */

insert  into `ofPubsubDefaultConf`(`serviceID`,`leaf`,`deliverPayloads`,`maxPayloadSize`,`persistItems`,`maxItems`,`notifyConfigChanges`,`notifyDelete`,`notifyRetract`,`presenceBased`,`sendItemSubscribe`,`publisherModel`,`subscriptionEnabled`,`accessModel`,`language`,`replyPolicy`,`associationPolicy`,`maxLeafNodes`) values ('pubsub',0,0,0,0,0,1,1,1,0,0,'publishers',1,'open','English',NULL,'all',-1),('pubsub',1,1,5120,0,-1,1,1,1,0,1,'publishers',1,'open','English',NULL,'all',-1);

/*Table structure for table `ofPubsubItem` */

CREATE TABLE `ofPubsubItem` (
  `serviceID` varchar(100) NOT NULL,
  `nodeID` varchar(100) NOT NULL,
  `id` varchar(100) NOT NULL,
  `jid` varchar(255) NOT NULL,
  `creationDate` char(15) NOT NULL,
  `payload` mediumtext,
  PRIMARY KEY (`serviceID`,`nodeID`,`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `ofPubsubItem` */

/*Table structure for table `ofPubsubNode` */

CREATE TABLE `ofPubsubNode` (
  `serviceID` varchar(100) NOT NULL,
  `nodeID` varchar(100) NOT NULL,
  `leaf` tinyint(4) NOT NULL,
  `creationDate` char(15) NOT NULL,
  `modificationDate` char(15) NOT NULL,
  `parent` varchar(100) DEFAULT NULL,
  `deliverPayloads` tinyint(4) NOT NULL,
  `maxPayloadSize` int(11) DEFAULT NULL,
  `persistItems` tinyint(4) DEFAULT NULL,
  `maxItems` int(11) DEFAULT NULL,
  `notifyConfigChanges` tinyint(4) NOT NULL,
  `notifyDelete` tinyint(4) NOT NULL,
  `notifyRetract` tinyint(4) NOT NULL,
  `presenceBased` tinyint(4) NOT NULL,
  `sendItemSubscribe` tinyint(4) NOT NULL,
  `publisherModel` varchar(15) NOT NULL,
  `subscriptionEnabled` tinyint(4) NOT NULL,
  `configSubscription` tinyint(4) NOT NULL,
  `accessModel` varchar(10) NOT NULL,
  `payloadType` varchar(100) DEFAULT NULL,
  `bodyXSLT` varchar(100) DEFAULT NULL,
  `dataformXSLT` varchar(100) DEFAULT NULL,
  `creator` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `language` varchar(255) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `replyPolicy` varchar(15) DEFAULT NULL,
  `associationPolicy` varchar(15) DEFAULT NULL,
  `maxLeafNodes` int(11) DEFAULT NULL,
  PRIMARY KEY (`serviceID`,`nodeID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `ofPubsubNode` */

insert  into `ofPubsubNode`(`serviceID`,`nodeID`,`leaf`,`creationDate`,`modificationDate`,`parent`,`deliverPayloads`,`maxPayloadSize`,`persistItems`,`maxItems`,`notifyConfigChanges`,`notifyDelete`,`notifyRetract`,`presenceBased`,`sendItemSubscribe`,`publisherModel`,`subscriptionEnabled`,`configSubscription`,`accessModel`,`payloadType`,`bodyXSLT`,`dataformXSLT`,`creator`,`description`,`language`,`name`,`replyPolicy`,`associationPolicy`,`maxLeafNodes`) values ('pubsub','',0,'001451546573212','001451546573212',NULL,0,0,0,0,1,1,1,0,0,'publishers',1,0,'open','','','','vidmt.com','','English','',NULL,'all',-1);

/*Table structure for table `ofPubsubNodeGroups` */

CREATE TABLE `ofPubsubNodeGroups` (
  `serviceID` varchar(100) NOT NULL,
  `nodeID` varchar(100) NOT NULL,
  `rosterGroup` varchar(100) NOT NULL,
  KEY `ofPubsubNodeGroups_idx` (`serviceID`,`nodeID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `ofPubsubNodeGroups` */

/*Table structure for table `ofPubsubNodeJIDs` */

CREATE TABLE `ofPubsubNodeJIDs` (
  `serviceID` varchar(100) NOT NULL,
  `nodeID` varchar(100) NOT NULL,
  `jid` varchar(255) NOT NULL,
  `associationType` varchar(20) NOT NULL,
  PRIMARY KEY (`serviceID`,`nodeID`,`jid`(70))
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `ofPubsubNodeJIDs` */

/*Table structure for table `ofPubsubSubscription` */

CREATE TABLE `ofPubsubSubscription` (
  `serviceID` varchar(100) NOT NULL,
  `nodeID` varchar(100) NOT NULL,
  `id` varchar(100) NOT NULL,
  `jid` varchar(255) NOT NULL,
  `owner` varchar(255) NOT NULL,
  `state` varchar(15) NOT NULL,
  `deliver` tinyint(4) NOT NULL,
  `digest` tinyint(4) NOT NULL,
  `digest_frequency` int(11) NOT NULL,
  `expire` char(15) DEFAULT NULL,
  `includeBody` tinyint(4) NOT NULL,
  `showValues` varchar(30) DEFAULT NULL,
  `subscriptionType` varchar(10) NOT NULL,
  `subscriptionDepth` tinyint(4) NOT NULL,
  `keyword` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`serviceID`,`nodeID`,`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `ofPubsubSubscription` */

/*Table structure for table `ofRemoteServerConf` */

CREATE TABLE `ofRemoteServerConf` (
  `xmppDomain` varchar(255) NOT NULL,
  `remotePort` int(11) DEFAULT NULL,
  `permission` varchar(10) NOT NULL,
  PRIMARY KEY (`xmppDomain`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `ofRemoteServerConf` */

/*Table structure for table `ofRoster` */

CREATE TABLE `ofRoster` (
  `rosterID` bigint(20) NOT NULL,
  `username` varchar(64) NOT NULL,
  `jid` varchar(1024) NOT NULL,
  `sub` tinyint(4) NOT NULL,
  `ask` tinyint(4) NOT NULL,
  `recv` tinyint(4) NOT NULL,
  `nick` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`rosterID`),
  KEY `ofRoster_unameid_idx` (`username`),
  KEY `ofRoster_jid_idx` (`jid`(255))
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `ofRoster` */

/*Table structure for table `ofRosterGroups` */

CREATE TABLE `ofRosterGroups` (
  `rosterID` bigint(20) NOT NULL,
  `rank` tinyint(4) NOT NULL,
  `groupName` varchar(255) NOT NULL,
  PRIMARY KEY (`rosterID`,`rank`),
  KEY `ofRosterGroup_rosterid_idx` (`rosterID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `ofRosterGroups` */

/*Table structure for table `ofSASLAuthorized` */

CREATE TABLE `ofSASLAuthorized` (
  `username` varchar(64) NOT NULL,
  `principal` text NOT NULL,
  PRIMARY KEY (`username`,`principal`(200))
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `ofSASLAuthorized` */

/*Table structure for table `ofSecurityAuditLog` */

CREATE TABLE `ofSecurityAuditLog` (
  `msgID` bigint(20) NOT NULL,
  `username` varchar(64) NOT NULL,
  `entryStamp` bigint(20) NOT NULL,
  `summary` varchar(255) NOT NULL,
  `node` varchar(255) NOT NULL,
  `details` text,
  PRIMARY KEY (`msgID`),
  KEY `ofSecurityAuditLog_tstamp_idx` (`entryStamp`),
  KEY `ofSecurityAuditLog_uname_idx` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `ofSecurityAuditLog` */

/*Table structure for table `ofUser` */

CREATE TABLE `ofUser` (
  `username` varchar(64) NOT NULL,
  `storedKey` varchar(32) DEFAULT NULL,
  `serverKey` varchar(32) DEFAULT NULL,
  `salt` varchar(32) DEFAULT NULL,
  `iterations` int(11) DEFAULT NULL,
  `plainPassword` varchar(32) DEFAULT NULL,
  `encryptedPassword` varchar(255) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `creationDate` char(15) NOT NULL,
  `modificationDate` char(15) NOT NULL,
  PRIMARY KEY (`username`),
  KEY `ofUser_cDate_idx` (`creationDate`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `ofUser` */

insert  into `ofUser`(`username`,`storedKey`,`serverKey`,`salt`,`iterations`,`plainPassword`,`encryptedPassword`,`name`,`email`,`creationDate`,`modificationDate`) values ('admin','ZX3MQ4k24kqmU8s0DITketWIV6c=','BDHsm9zrMogJiBSAPYMs2bOnZ4s=','lLuYCmx+AVBTGXXl32ZUKjf1DIuEqy4o',4096,NULL,'24ed32a8fc1d7b709daffb29dfcc204b','Administrator','admin@example.com','001451546568726','0');

/*Table structure for table `ofUserFlag` */

CREATE TABLE `ofUserFlag` (
  `username` varchar(64) NOT NULL,
  `name` varchar(100) NOT NULL,
  `startTime` char(15) DEFAULT NULL,
  `endTime` char(15) DEFAULT NULL,
  PRIMARY KEY (`username`,`name`),
  KEY `ofUserFlag_sTime_idx` (`startTime`),
  KEY `ofUserFlag_eTime_idx` (`endTime`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `ofUserFlag` */

/*Table structure for table `ofUserProp` */

CREATE TABLE `ofUserProp` (
  `username` varchar(64) NOT NULL,
  `name` varchar(100) NOT NULL,
  `propValue` text NOT NULL,
  PRIMARY KEY (`username`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `ofUserProp` */

/*Table structure for table `ofVCard` */

CREATE TABLE `ofVCard` (
  `username` varchar(64) NOT NULL,
  `vcard` mediumtext NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `ofVCard` */

/*Table structure for table `ofVersion` */

CREATE TABLE `ofVersion` (
  `name` varchar(50) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `ofVersion` */

insert  into `ofVersion`(`name`,`version`) values ('openfire',22);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
