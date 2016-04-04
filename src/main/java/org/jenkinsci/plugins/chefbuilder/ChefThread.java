package org.jenkinsci.plugins.chefbuilder;

import java.io.IOException;

public class ChefThread implements Runnable {
	
	String node;
	String username;
	int port;
	String privatekey;
	String command;
	
	 
	ChefThread(String node, String username, int port, String privatekey, String command) {
		this.node = node;
		this.username = username;
		this.port = port;
		this.privatekey = privatekey;
		this.command = command;
		
	}
	
 /*public String run (String node, String username, int port, String privatekey, String command) throws IOException
	{
		ChefSshClient sch = new ChefSshClient();
		String output = sch.runchefclient(node, username, port, privatekey, command);
		
		return output;
		
	}
*/
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		ChefSshClient sch = new ChefSshClient();
		try {
			String output = sch.runchefclient(node, username, port, privatekey, command);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
