package com.vidmt.of.plugin.sub.test;

import java.io.File;

import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@AnnoPlugin
public class TestPlugin  implements Plugin{
	private static final Logger log = LoggerFactory.getLogger(TestPlugin.class);

	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		log.info("init TEST PLUGIN");
	}

	@Override
	public void destroyPlugin() {
		// TODO Auto-generated method stub
		
	}

}
