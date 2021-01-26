package de.peass.ci;

import hudson.model.Action;
import hudson.model.Project;

public class ProjectStatisticsAction implements Action {

   private Project project;

   public ProjectStatisticsAction(Project project) {
      this.project = project;
   }

   public int getBuildStepsCount() {
      return project.getBuilders().size();
   }

   public int getPostBuildStepsCount() {
      return project.getPublishersList().size();
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