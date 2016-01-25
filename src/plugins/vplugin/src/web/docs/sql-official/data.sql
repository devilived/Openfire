USE `tel`;
insert  into `v_lvl`(`name`,`money`,`during`,`remark`) values
('TRY',1000,604800,'10元/7天'),
('YEAR',15000,31536000,'150元/365天');

USE `openfire`;
INSERT INTO ofProperty (`name`,propValue)VALUES
('provider.admin.className','com.vidmt.of.plugin.sub.extdb.VAdminProvider'),
('provider.auth.className','com.vidmt.of.plugin.sub.extdb.VAuthProvider'),
('provider.user.className','com.vidmt.of.plugin.sub.extdb.VUserProvider'),
('xmpp.proxy.enabled','false'),

('database.defaultProvider.minConnections','50'),
('database.defaultProvider.maxConnections','150'),

('plugin.vplugin.telplugin.respath','/alidata1/data/html/telephone'),
('plugin.vplugin.telplugin.pay_notify_url','http://phonetest.vidmt.com/TelServer/api/1/pay/{paytype}/notify.api')
 ON DUPLICATE KEY UPDATE propValue=VALUES(propValue);

 
 insert into `v_user` (`id`, `pwd`, `name`, `nick`, `email`, `phone`, `status`, `lvl`, `lvl_end`, `sex`, `birth`, `signature`, `address`, `avatar_uri`, `photo_uri`, `loc_private`, `avoid_disturb`, `create_date`, `modify_date`) values('1','scram#OS6Tik088cWEay6@yzmXXHXOsntkkMEFEJLZbAT+R4c=@dfHCeSXPUeksyfjeXTFDd9VheDI=@9jbCHmwa88X8SR8q+VjAYbLlzxaMO6VFHk76oTKGnfo=@4096#ab1f89adda5fb50166b649d2daa2039c','admin',NULL,NULL,NULL,'NORMAL',NULL,NULL,'0','1995-12-20',NULL,NULL,NULL,NULL,'0','0','2015-12-15 18:06:48','2015-12-15 18:06:48');
