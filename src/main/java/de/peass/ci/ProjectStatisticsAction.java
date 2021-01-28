package de.peass.ci;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import hudson.model.Action;
import hudson.model.Project;

public class ProjectStatisticsAction implements Action {

   private Project<?,?> project;

   public ProjectStatisticsAction(Project<?,?> project) {
      this.project = project;
   }

   public int getBuildStepsCount() {
      return project.getBuilders().size();
   }

   public int getPostBuildStepsCount() {
      return project.getPublishersList().size();
   }

   public Map<Integer, String> getTvaluesMap() {

      final Map<Integer, String> tValuesMap = new HashMap<Integer, String>();
      final String[] directories = getBuilds();

      for (final String buildNumber : directories) {
         try {
            final String value = getValueFromXML("de.peass.analysis.changes.Change", "tvalue", buildNumber);
            tValuesMap.put(Integer.parseInt(buildNumber), value);
         } catch (ParserConfigurationException | SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }

      return tValuesMap;
   }

   private String getValueFromXML(final String tagName, final String subTagName, final String buildNumber) throws ParserConfigurationException, SAXException {
      final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      final DocumentBuilder builder = factory.newDocumentBuilder();

      final String pathToXml = project.getBuildDir() + "/" + buildNumber + "/build.xml";

      Document doc = null;
      try {
         doc = builder.parse(new File(pathToXml));
      } catch (SAXException | IOException e1) {
         // TODO Auto-generated catch block
         e1.printStackTrace();
      }
      final NodeList nodes = doc.getElementsByTagName(tagName);
      final Node node = nodes.item(0);
      final Element element = (Element) node;
      final NodeList subnodes;
      try {
         subnodes = element.getElementsByTagName(subTagName).item(0).getChildNodes();
      } catch (NullPointerException e) {
         System.out.println("Element is null!");
         return null;
      }
      final Node subnode = (Node) subnodes.item(0);

      return subnode.getNodeValue();
   }

   private String[] getBuilds() {
      File file = new File("/home/noname/.jenkins/jobs/demo-project/builds");
      String[] directories = file.list(new FilenameFilter() {
         @Override
         public boolean accept(File current, String name) {
            return new File(current, name).isDirectory();
         }
      });
      return directories;
   }

   @Override
   public String getIconFileName() {
      return "/plugin/peass-ci/images/jenkins-redbg.png";
   }

   @Override
   public String getDisplayName() {
      return "Project Statistics";
   }

   @Override
   public String getUrlName() {
      return "stats";
   }
}