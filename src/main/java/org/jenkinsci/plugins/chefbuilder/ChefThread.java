package org.jenkinsci.plugins.chefbuilder;

import java.io.IOException;
import java.util.concurrent.Callable;

public class ChefThread implements Callable<String>  {
	
	 @Override
	    public String call() throws Exception {
	       // Thread.sleep(1000);
	    	ChefSshClient sch = new ChefSshClient();
			try {
				output = sch.runchefclient(node, username, port, privatekey, command);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        //return the thread name executing this callable task
	        //return Thread.currentThread().getName();
			return output;
	        
	    }
	
	String node;
	String username;
	int port;
	String privatekey;
	String command;
	String output;
	
	 
	ChefThread(String node, String username, int port, String privatekey, String command) {
		this.node = node;
		this.username = username;
		this.port = port;
		this.privatekey = privatekey;
		this.command = command;
		
	}
	
 	public void run() {
		// TODO Auto-generated method stub
		
		ChefSshClient sch = new ChefSshClient();
		try {
			output = sch.runchefclient(node, username, port, privatekey, command);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	

}
