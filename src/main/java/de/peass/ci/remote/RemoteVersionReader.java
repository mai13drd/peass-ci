package de.peass.ci.remote;

import java.io.File;
import java.io.IOException;

import org.jenkinsci.remoting.RoleChecker;

import de.peass.ci.JenkinsLogRedirector;
import de.peass.config.MeasurementConfiguration;
import de.peass.vcs.GitUtils;
import hudson.FilePath.FileCallable;
import hudson.model.TaskListener;
import hudson.remoting.VirtualChannel;

public class RemoteVersionReader implements FileCallable<MeasurementConfiguration> {

   private static final long serialVersionUID = -1266048917282327539L;

   private final MeasurementConfiguration measurementConfig;

   private final TaskListener listener;

   public RemoteVersionReader(final MeasurementConfiguration measurementConfig, final TaskListener listener) {
      this.measurementConfig = measurementConfig;
      this.listener = listener;
   }

   @Override
   public void checkRoles(final RoleChecker checker) throws SecurityException {
   }

   @Override
   public MeasurementConfiguration invoke(final File workspaceFolder, final VirtualChannel channel) throws IOException, InterruptedException {
      try (JenkinsLogRedirector redirector = new JenkinsLogRedirector(listener)) {
         final String version = GitUtils.getName("HEAD", workspaceFolder);
         measurementConfig.setVersion(version);
         final String versionOld = GitUtils.getName(measurementConfig.getVersionOld(), workspaceFolder);
         measurementConfig.setVersionOld(versionOld);
         return measurementConfig;
      } catch (Throwable e) {
         e.printStackTrace(listener.getLogger());
         e.printStackTrace();
         return null;
      }
   }
}
