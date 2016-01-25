USE `tel`;
insert  into `v_lvl`(`name`,`money`,`during`,`remark`) values
('TRY',1,604800,'10元/7天'),
('YEAR',1,31536000,'150元/365天');

USE `openfire`;
INSERT INTO ofProperty (`name`,propValue)VALUES
('provider.admin.className','com.vidmt.of.plugin.sub.extdb.VAdminProvider'),
('provider.auth.className','com.vidmt.of.plugin.sub.extdb.VAuthProvider'),
('provider.user.className','com.vidmt.of.plugin.sub.extdb.VUserProvider'),
('xmpp.proxy.enabled','false'),

('plugin.vplugin.telplugin.respath','/home/pc1/bin/nginx-1.9.9/html/telephone'),
('plugin.vplugin.telplugin.pay_notify_url','http://phonetest.vidmt.com/TelServer/api/1/pay/{paytype}/notify.api')
 ON DUPLICATE KEY UPDATE propValue=VALUES(propValue);

