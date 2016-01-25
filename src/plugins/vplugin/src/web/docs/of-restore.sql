 INSERT INTO ofproperty (`name`,propValue)VALUES
('provider.admin.className','org.jivesoftware.openfire.admin.DefaultAdminProvider'),
('provider.auth.className','org.jivesoftware.openfire.auth.DefaultAuthProvider'),
('provider.user.className','org.jivesoftware.openfire.user.DefaultUserProvider')
 ON DUPLICATE KEY UPDATE propValue=VALUES(propValue);