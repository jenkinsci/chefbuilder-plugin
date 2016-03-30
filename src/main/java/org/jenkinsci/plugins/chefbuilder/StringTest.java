package org.jenkinsci.plugins.chefbuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;


public class StringTest {
public static void main(String[] args) throws JSchException, IOException{
	
	StringWriter writer = new StringWriter();
	
	try
	{
		
		JSch jsch = new JSch();
		String privateKey = "C:\\test\\key";
		jsch.addIdentity(privateKey);
		String host = null;
		host = "192.168.102.211";
				
		Session session = jsch.getSession("chef-admin", host, 22);
			
		UserInfo ui = new MyUserInfo() {
			public boolean promptYesNo(String message) {
				return true;
			}
			
		};
		 session.setUserInfo(ui);
		 session.connect();

		String command = "ls -l /tmp";
		 // String sudo_pass=null;
		  Channel channel=session.openChannel("exec");
		  //String pass= "chef123";
		  
		  ((ChannelExec)channel).setPty(true);
		  ((ChannelExec)channel).setCommand("sudo -S " + command);
		  
		  InputStream in=channel.getInputStream();
	      OutputStream out=channel.getOutputStream();
	      ((ChannelExec)channel).setErrStream(System.err);
	      channel.connect();
	      
	      //out.write((sudo_pass+"\n").getBytes());
	      //out.flush();
	      
		
		byte[] bt = new byte[1024];
		 while(true){
		        while(in.available()>0){
		          int i=in.read(bt, 0, 1024);
		          if(i<0)break;
		       //   System.out.print(new String(bt, 0, i));
		          writer.write(new String(bt, 0, i));
		          System.out.println(writer.toString());
		          
		        }
		        if(channel.isClosed()){
		          System.out.println("exit-status: "+channel.getExitStatus());
		          break;
		        }
		        try{Thread.sleep(1000);}catch(Exception ee){}
		      }
		// String s = writer.toString();
		 //System.out.println(s);
		      channel.disconnect();
		      session.disconnect();
		    }
	catch (Exception e)
	{
		e.printStackTrace();
	}
	
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


