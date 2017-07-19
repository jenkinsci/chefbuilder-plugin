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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

public class ChefBuilderConfiguration extends Builder implements SimpleBuildStep  {
	
	//private Run<?, ?> run;

    public final String url;
    public final String sinatraurl;
    public final String filter;
    public final boolean parallel;
    public final boolean fail;
    public final int port;
    public final String username;
    public final String command;
    public final String privatekey;
    public static List<String> nodes = new ArrayList<String>();
    public String node;
    

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public ChefBuilderConfiguration(String url, String sinatraurl, String filter, String username, int port, String command, String privatekey, boolean parallel, boolean fail ) {
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

    /**
     * We'll use this from the <tt>config.jelly</tt>.
     */
    public String getUrl() {
        return url;
    }
    
    public String getSinatraurl() {
        return sinatraurl;
    }
    
    public String getFilter() {
        return filter;
    }
    
    public boolean getParallel() {
        return parallel;
    }
    
    public boolean getFail() {
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
    	@SuppressWarnings("unused")
		String output = null;
    	//this.run = build;
    	
    	listener.getLogger().println("Execute chef-client in parallel is set to : " + parallel);
    	listener.getLogger().println("Fail the build if the command fails to run on any of the chef node is set to : " + fail);
              ChefXmlParser parser = new ChefXmlParser();
             
         //     ArrayList<Integer> exitValue = new ArrayList();
              @SuppressWarnings("rawtypes")
			List nodes = parser.getListofNodes(filter,sinatraurl);
                       	        	 
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
			return true;
         	 
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
         
    
/*    private static String getStringFromInputStream(InputStream is) {

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

	}*/


    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }
   
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
               
         public FormValidation doTestConnection(@QueryParameter String url, @QueryParameter String sinatraurl, String filter
	        ) throws IOException {
        	 
        	// System.out.println("value of url is " + url);
        //	 System.out.println("value of sinatraurl is " + sinatraurl);
        	 try
        	 {
        		 URL url1 = new URL(url);
        		 URL url2 = new URL(sinatraurl);
        		 HttpURLConnection connection = (HttpURLConnection)url1.openConnection();
        		 HttpURLConnection connection1 = (HttpURLConnection)url2.openConnection();
        		 connection.setRequestMethod("GET");
        		 connection1.setRequestMethod("GET");
        		 connection.connect();
        		 connection1.connect();

        		 int code = connection.getResponseCode();
        		 int sinatracode = connection1.getResponseCode();
        		 
        		 if ((code == 200) && (sinatracode == 200))
        		 {
        			 return FormValidation.ok("SUCCESS: able to connect chef server URL and sinatra URL");
        			 
        		 }
        		 else 
        		 {
        			 return FormValidation.error("connection to chef server web URL OR Sinatra web URL is failed. The HTTP URL connection code for chef server URL is" + code + " and connection code for sinatra web URL is " + sinatracode);
        		 }
        	 }
        	 catch (RuntimeException e)
        	 {
        		 throw e;
        	 }
        	 
        	 catch (Exception e)
        	 {
        		return FormValidation.error("some issue while connecting to chef OR sinatra web server URL");
        	 }
        	 
         }
         
                
         public FormValidation doValidate(@QueryParameter String filter) {
        	 try
        	 {
        		 String pattern = "(.*)(::)(.*),(.*)(::)(.*)";
        		 String pattern1 = "(.*)(::)(.*)";
        		        		 
            	 Pattern r = Pattern.compile(pattern);
            	 Pattern r1 = Pattern.compile(pattern1);
            	//  System.out.println("the value of filter is :"+ filter);
     
            	 Matcher m = r.matcher(filter);
            	 Matcher m1 = r1.matcher(filter);
            	   if (m.find() ) {
            		   return FormValidation.ok("SUCCESS. however, there may be multiple strings matching same pattern. That might be not correct. Be careful !!!");
            	   }
            	  
            	   else if (m1.find() ) {
            		   return FormValidation.ok("SUCCESS. however, there may be multiple strings matching same pattern. That might be not correct. Be careful !!!");
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
		public FormValidation doFetch(@QueryParameter String filter, @QueryParameter String sinatraurl) {
        	  
        //	 System.out.println("filter value in fetch is :" + filter  );
        //	 System.out.println("filter value in fetch is :" + sinatraurl  );
        	 try
        	 {
        	 ChefXmlParser a = new ChefXmlParser();
        	 nodes = a.getListofNodes(filter,sinatraurl);
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
	public void perform(Run<?, ?> arg0, FilePath arg1, Launcher arg2, TaskListener arg3)
			throws InterruptedException, IOException {
		// TODO Auto-generated method stub
		
	}

	
}

