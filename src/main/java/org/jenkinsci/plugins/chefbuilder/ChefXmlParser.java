package org.jenkinsci.plugins.chefbuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
 
public class ChefXmlParser 
{ 
	public String filter;
	public String sinatraurl;
public ChefXmlParser()
{
	}

@SuppressWarnings("rawtypes")
public List getListofNodes(String filter, String sinatraurl)
{
	this.filter = filter;
	this.sinatraurl = sinatraurl;
	 List<String> nodes = new ArrayList<String>();
	 String[] actRec = null;
	 String[] dvdfilter = null;

	 try {
   	  
		// System.out.println("the value of sinatra url in xml is :"+ sinatraurl);
		// System.out.println("the value of filter in xml is :"+ filter);
         URL oracle = new URL(sinatraurl);
         BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream(),Charset.defaultCharset()));

         File file = new File("/tmp/nodesxml.xml");
    //     File file1 = new File("/tmp/project.dtd");
         
     	if (!file.exists()) {
			file.createNewFile();
		}
		
	/*	if (!file1.exists()) {
			file1.createNewFile();
		}*/
		
//		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		Writer fw = null;
		fw = new OutputStreamWriter(new FileOutputStream(file.getAbsoluteFile()),"UTF-8");
		BufferedWriter bw = new BufferedWriter(fw);

        String inputLine;
        while ((inputLine = in.readLine()) != null)
        {
         //   System.out.println(inputLine);
        	bw.write(inputLine);
        	bw.write("\n");
        }
        bw.close();	
        in.close();
 
        DocumentBuilderFactory dbFactory 
           = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;

        dBuilder = dbFactory.newDocumentBuilder();

        Document doc = dBuilder.parse(file);
        doc.getDocumentElement().normalize();

        XPath xPath =  XPathFactory.newInstance().newXPath();
        Map<String, String> map = new HashMap<String, String>();
       

        String expression = "/project/node";	        
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
      //  System.out.println("the value of nodelist is :" + nodeList.getLength());
        for (int i = 0; i < nodeList.getLength(); i++) {
        	//  System.out.println("the value of i is " + i);
           Node nNode = nodeList.item(i);
           Element eElement = (Element) nNode;
        //   System.out.println("in the for block");
        //   System.out.println("\nCurrent Element :" + nNode.getNodeName());
          /* if (nNode.getNodeType() == Node.ELEMENT_NODE) {
            
              System.out.println("Nodes Name : " 
                 + eElement.getAttribute("name"));
             
              
              System.out.println("Nodes receips : " 
                      + eElement.getAttribute("recipes"));        	
           }
*/           
           map.put(eElement.getAttribute("name"), eElement.getAttribute("recipes"));
          
        }
        List<String> keys = new ArrayList<String>(map.keySet());
        dvdfilter = filter.split(",");
        for (int l=0;l<dvdfilter.length;l++)
        {
        
        for (String key: keys) {
     	  // System.out.println("key is " + key + " value is :" + map.get(key) );
     	   
     	  if (map.get(key).equals(""))
          { //System.out.println("reciep is null"); 
     		  }
          
          else if (map.get(key).matches("(.*)(::)(.*),(.*)(::)(.*)"))
       	   {
        	//  System.out.println("in the for1");
        	 // System.out.println("the value of filter" + filter);
       		   actRec = map.get(key).split(",");
       		   for(int k =0; k < actRec.length; k++)
                  {
       			 //  System.out.println("first value is " + actRec[k] + "for the filter" + );
       			   if (actRec[k].equals(dvdfilter[l]))
       			   {
       			//	System.out.println("in this time");
       				   nodes.add(key);
       			   }
       			   else
       			   {
       				//   System.out.println("not this time");
       			   }
                  }
       		   
       	   }
       	   else if (map.get(key).matches("(.*),(.*)(::)(.*)"))
       	   {
       		 //  System.out.println("in my case" + map.get(key));
       		   actRec = map.get(key).split(",");
       		 //  System.out.println("the value of actRec is in our case" + actRec.length );
       		   for(int j =0; j < actRec.length; j++)
                  {
       			   if ((actRec[j].matches("(.*)(::)(.*)") && actRec[j].equals(dvdfilter[l]) ))
       			   {
       			//	   System.out.println("how many times");
       				  nodes.add(key);
       			   }
       			   else
       			   {
       				   
       			   }
                  }
       	   }
       	    else
       	   {
       		   
       	   }
       	   
      }
        }   
       // System.out.println("total size is "+ nodes.size());
        Logger.getLogger(filter).warning("the nodes are " + nodes);
	
}
 catch (ParserConfigurationException e) {
    e.printStackTrace();
 } catch (SAXException e) {
    e.printStackTrace();
 } catch (IOException e) {
    e.printStackTrace();
 } catch (XPathExpressionException e) {
    e.printStackTrace();
 }
	return nodes;
}
}
