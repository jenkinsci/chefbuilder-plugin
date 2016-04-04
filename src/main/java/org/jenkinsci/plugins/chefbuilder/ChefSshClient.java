package org.jenkinsci.plugins.chefbuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringWriter;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

public class ChefSshClient {
	
	public ChefSshClient()
	{}
	
	public String runchefclient(String node, String username, int port, String privateKey, String command) throws IOException
	{
		//static int [] exitValue;
		StringWriter writer = new StringWriter();
		String finalOut = null;
		
		try
		{
 			JSch jsch = new JSch();
			jsch.addIdentity(privateKey);
			Session session = jsch.getSession(username, node, port);
			UserInfo ui = new MyUserInfo() {
				public boolean promptYesNo(String message) {
					return true;
				}
				
			};
			 session.setUserInfo(ui);
			 session.connect();
			 Channel channel=session.openChannel("exec");
			 ((ChannelExec)channel).setPty(true);
			  ((ChannelExec)channel).setCommand("sudo -S " + command);
			  
			  InputStream in=channel.getInputStream();
		      OutputStream out=channel.getOutputStream();
		      ((ChannelExec)channel).setErrStream(System.err);
		      channel.connect();
		      		      			
			byte[] bt = new byte[1024];
			 while(true){
			        while(in.available()>0){
			          int i=in.read(bt, 0, 1024);
			          if(i<0)break;
			         // listener.getLogger().println(new String(bt, 0, i));
			          writer.write(new String(bt, 0, i));
			          
			        }
			        if(channel.isClosed()){
			         // listener.getLogger().println("exit-status: "+channel.getExitStatus());
			       //   exitValue.add(channel.getExitStatus());
			     
			          break;
			        }
			        try{Thread.sleep(1000);}catch(Exception ee){}
			      }
			 	  finalOut = writer.toString();
			      channel.disconnect();
			      session.disconnect();
			    }
		catch (Exception e)
		{
			writer.write("ChefClientException: something went wrong for node" + node);
			writer.write(e.toString());
			finalOut = writer.toString();
		//	listener.getLogger().println("something went wrong for node" + node);
		//	listener.getLogger().println(e);
			 // exitValue.add(-1);
		}
		return finalOut;
	}
	
	public static abstract class MyUserInfo implements UserInfo, UIKeyboardInteractive {
		public String getPassword() {
			return null;
		}

		public boolean promptYesNo(String str) {
			return false;
		}

		public String getPassphrase() {
			return null;
		}

		public boolean promptPassphrase(String message) {
			return false;
		}

		public boolean promptPassword(String message) {
			return false;
		}

		public void showMessage(String message) {
		}

		public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt,
				boolean[] echo) {
			return null;
		}
	}
}