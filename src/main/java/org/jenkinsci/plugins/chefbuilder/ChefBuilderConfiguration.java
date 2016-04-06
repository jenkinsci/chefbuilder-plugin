package org.jenkinsci.plugins.chefbuilder;
import hudson.Launcher;
import hudson.Extension;
import hudson.FilePath;
import hudson.util.FormValidation;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.BuildListener;
import hudson.model.TaskListener;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.QueryParameter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChefBuilderConfiguration extends Builder implements SimpleBuildStep {

    private final String url;
    private final String filter;
    private final boolean parallel;
    private final boolean fail;
    private final int port;
    private final String username;
    private final String command;
    private final String privatekey;
    private static List<String> nodes = new ArrayList<String>();
    private String node;
    

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public ChefBuilderConfiguration(String url, String filter, boolean parallel, boolean fail, List<String> nodes, int port, String username, String privatekey, String command, String node) {
        this.url = url;
        this.filter = filter;
        this.parallel = parallel;
        this.fail = fail;
        this.nodes = nodes;
        this.username = username;
        this.port = port;
        this.privatekey = privatekey;
        this.command = command;
        this.node = node;
        
    }

    /**
     * We'll use this from the <tt>config.jelly</tt>.
     */
    public String getUrl() {
        return url;
    }
    
    public String getFilter() {
        return filter;
    }
    
    public boolean isParallel() {
        return parallel;
    }
    
    public boolean isFail() {
        return fail;
    }
    
    public String getUsername() {
        return username;
    }
    
    public int getPort() {
        return port;
    }
    
    public String getCommand() {
        return command;
    }
    
    public String getPrivatekey() {
        return privatekey;
    }
    

    @Override
    public boolean perform(AbstractBuild<?,?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
    	String output = null;      
    	listener.getLogger().println("Execute chef-client in parallel is set to : " + parallel);
              ChefXmlParser parser = new ChefXmlParser();
             
              ArrayList<Integer> exitValue = new ArrayList();
              List nodes = parser.getListofNodes(filter);
                       	        	 
         	listener.getLogger().println("The nodes are : " + nodes);
         	int MYTHREADS = nodes.size();
            
         	ExecutorService executor = Executors.newFixedThreadPool(MYTHREADS);
            List<Future<String>> list = new ArrayList<Future<String>>();
         	    	
         	for(int j=0;j<nodes.size();j++)
         	{
         		node = (String) nodes.get(j);
         		Callable<String> callable = new ChefThread(node, username, port, privatekey, command);
         		 Future<String> future = executor.submit(callable);
         		 list.add(future);
         	}
         	
         	 for(Future<String> fut : list){
                 try {
                     //print the return value of Future, notice the output delay in console
                     // because Future.get() waits for task to get completed
                	 listener.getLogger().println(new Date()+ "::"+fut.get());
                 } catch (Exception e) {
                     e.printStackTrace();
                     listener.getLogger().println(e);
                 }
             }
         		//Runnable worker = new ChefThread(node, username, port, privatekey, command);
         		//Runnable worker = t;
         		/*Callable worker = t;
         		executor.execute(worker);
*/         		//	output = t.getOutput();
         	//	listener.getLogger().println(output);
         	 executor.shutdown();
			return fail;
         	 
         	}
         	
           
    		/*// Wait until all threads are finish
    		while (!executor.isTerminated()) {
     
    		}
    		
    		if (executor.isTerminated())
    		{
    			output = t.getOutput();
    		}
    		
    		listener.getLogger().println("\nOutput is " + output);
    		
    		listener.getLogger().println("\nFinished all threads");
         		if (output .contains("ChefClientException"))
         		{
         			exitValue.add(-1);
         		}
         		else
         		{
         			exitValue.add(0);
         		}
         	
         	if ((exitValue.contains(-1)) && (fail == true))
         	{
         		return false;
         	}
         	else if ((exitValue.contains(-1)) && (fail == false))
         	{
         		return true;
         	}
         	else
         	{
         		return true;
         	}
			return fail;
			         	
         	
         	if ((exitValue.contains(-1)) && (fail == true))
         	{
         		listener.getLogger().println("in false");
         		return false;
         	}
         	else if ((exitValue.contains(-1)) && (fail == false))
         	{
         		return true;
         	}
         	{
         		listener.getLogger().println("in true");
         		return true;
         	}
*/         	
         
    
    private static String getStringFromInputStream(InputStream is) {

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();

	}


    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }
   
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
         @SuppressWarnings("unused")
		private static final Level SEVERE = null;

		public DescriptorImpl() {
            load();
        }

       
      /*  public FormValidation doCheckUrl(@QueryParameter String urlvalue)
                throws IOException, ServletException {
        		if (urlvalue.length() == 0)
        		{
        	   return FormValidation.error("Please set a name");
        		}
        		else if (urlvalue.length() != 0)
        		{  
                  return FormValidation.ok();
        		}
        		
        		return FormValidation.ok();
        		
        }*/
         
         public FormValidation doTestConnection(@QueryParameter String url
	        ) {
        	 
        	 try
        	 {
        		 URL url1 = new URL(url);
        		 HttpURLConnection connection = (HttpURLConnection)url1.openConnection();
        		 connection.setRequestMethod("GET");
        		 connection.connect();

        		 int code = connection.getResponseCode();
        		 if (code == 200)
        		 {
        			 return FormValidation.ok("SUCCESS");
        		 }
        		 else 
        		 {
        			 return FormValidation.error("connection failed");
        		 }
        	 }
        	 catch (Exception e)
        	 {
        		 return FormValidation.error("some issue");
        	 }
         }
         
         public FormValidation doValidate(@QueryParameter String filter) {
        	 try
        	 {
        		 String pattern = "(.*)(::)(.*),(.*)(::)(.*)";
        		 String pattern1 = "(.*)(::)(.*)";
        		        		 
            	 Pattern r = Pattern.compile(pattern);
            	 Pattern r1 = Pattern.compile(pattern1);
            	  System.out.println("the value of filter is :"+ filter);
     
            	 Matcher m = r.matcher(filter);
            	 Matcher m1 = r1.matcher(filter);
            	   if (m.find() ) {
            		   return FormValidation.ok("SUCCESS. however, there may be multiple strings matching same pattern. That is not correct. Be careful !!!");
            	   }
            	  
            	   else if (m1.find() ) {
            		   return FormValidation.ok("SUCCESS. however, there may be multiple strings matching same pattern. That is not correct. Be careful !!!");
            	   }
            	   else
            	   {
            	   
            		   return FormValidation.warning("seems the filter is not in correct format");
            	   }
        	 } catch (Exception e)
        	 {
        		 return FormValidation.error("some issue");
        	 }
        	
      }
         
         @SuppressWarnings({"unchecked" })
		public FormValidation doFetch(@QueryParameter String filter) {
        	
        	
        	 try
        	 {
        	 ChefXmlParser a = new ChefXmlParser();
        	 nodes = a.getListofNodes(filter);
        	 }
        	 catch (Exception e)
        	 {
        		 e.printStackTrace();
        		 return FormValidation.warning("Some issue while fetching nodes");
        	 }
        	 
        	 return FormValidation.ok("The nodes are " + nodes);
        	
      }
			  

        public boolean isApplicable(@SuppressWarnings("rawtypes") Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types 
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Execute chef client on selected nodes";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            // To persist global configuration information,
            // set that to properties and call save().
           // useFrench = formData.getBoolean("useFrench");
            // ^Can also use req.bindJSON(this, formData);
            //  (easier when there are many fields; need set* methods for this, like setUseFrench)
            save();
            return super.configure(req,formData);
        }
    }

	@Override
	public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener)
			throws InterruptedException, IOException {
		// TODO Auto-generated method stub
		
	}
}

