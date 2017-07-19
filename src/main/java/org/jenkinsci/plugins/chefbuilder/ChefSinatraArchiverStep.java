package org.jenkinsci.plugins.chefbuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nonnull;


import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.kohsuke.stapler.DataBoundConstructor;
import com.google.inject.Inject;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Run;
import hudson.model.TaskListener;

@SuppressWarnings("deprecation")
public class ChefSinatraArchiverStep extends AbstractStepImpl {
	private static final Logger logger = Logger.getLogger(ChefSinatraArchiverStep.class.getName());
	 public static String url = null;
	    public  static String sinatraurl = null;
	    public static String filter = null;
	    public static boolean parallel = false;
	    public static boolean fail = false;
	    public static int port = 22;
	    public static String username = null;
	    public static String command = null;
	    public static String privatekey = null;
	    public static List<String> nodes = new ArrayList<String>();
	    public static String node = null;
	   
	

	private static final long serialVersionUID = -2711052677399057398L;
	@SuppressWarnings("static-access")
	@DataBoundConstructor
	    public ChefSinatraArchiverStep(String url, String sinatraurl, String filter, String username, int port, String command, String privatekey, boolean parallel, boolean fail) {
		
		 this.url = url;
	        this.sinatraurl = sinatraurl;
	        this.filter = filter;
	        this.username = username;
	        this.port = port;
	        this.command = command;
	        this.privatekey = privatekey;
	        this.parallel = parallel;
	        this.fail = fail;
	}
	
	   
    public static class ChefSinatraStepExecution extends AbstractSynchronousStepExecution<Void> {
    	
    	 @Inject private transient ChefSinatraArchiverStep step; 
    	 @StepContextParameter private transient AbstractBuild<?,?> build; 
    	 @StepContextParameter private transient BuildListener listener;
    	 @StepContextParameter
    	    private transient Launcher launcher;
		@Override
		protected Void run() throws Exception {
			
			 listener.getLogger().println("Running Chef Sinatra step.");
		    ChefBuilderConfiguration Builder = new ChefBuilderConfiguration (url,sinatraurl,filter,username,port,command,privatekey, parallel, fail);
		    
			   Builder.perform(build,launcher, listener);
			 
			   
			return null;
		} 

    }
    

	    @Extension
	    public static class DescriptorImpl extends AbstractStepDescriptorImpl  {
	    	@SuppressWarnings("deprecation")
			public DescriptorImpl() { super(ChefSinatraStepExecution.class); }
			

			@Override
	        public String getFunctionName() {
	            return "chefSinatraStep";
	        }

	        @Nonnull
	        @Override
	        public String getDisplayName() {
	            return "Execute chef-client on remote nodes";
	        }	
	    }
		}
