package org.jfrog.hudson.plugins.artifactory.release;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.BuildableItemWithBuildWrappers;
import hudson.model.User;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import org.jfrog.hudson.plugins.artifactory.action.ActionableHelper;
import org.jfrog.hudson.plugins.artifactory.config.ArtifactoryServer;
import org.jfrog.hudson.plugins.artifactory.config.Credentials;
import org.jfrog.hudson.plugins.artifactory.gradle.ArtifactoryGradleConfigurator;
import org.jfrog.hudson.plugins.artifactory.util.CredentialResolver;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * @author Mark Pope
 */
public class AutoPromotePostBuildAction extends Publisher {

    private String targetStatus;
    private String repositoryKey;
    private String comment;
    private boolean includeDependencies;
    private boolean useCopy;

    @DataBoundConstructor
    public AutoPromotePostBuildAction(String targetStatus, String repositoryKey, String comment, boolean includeDependencies, boolean useCopy) {
        this.targetStatus = targetStatus;
        this.repositoryKey = repositoryKey;
        this.comment = comment;
        this.includeDependencies = includeDependencies;
        this.useCopy = useCopy;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return null;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        ArtifactoryGradleConfigurator configurator = ActionableHelper.getBuildWrapper((BuildableItemWithBuildWrappers) build.getProject(), ArtifactoryGradleConfigurator.class);
        ArtifactoryServer artifactoryServer = configurator.getArtifactoryServer();
        Credentials deployer = CredentialResolver.getPreferredDeployer(artifactoryServer);
        String ciUser = getCiUser();

        PromotionConfig promotionConfig = new PromotionConfig(targetStatus, repositoryKey, comment, ciUser, useCopy, includeDependencies);
        ArtifactoryPromoter promoter = new ArtifactoryPromoter(build, null, promotionConfig, artifactoryServer, deployer);
        try {
            promoter.handlePromotion(listener);
            return true;
        } catch (Exception e) {
            e.printStackTrace(listener.error(e.getMessage()));
            return false;
        }
    }

    private String getCiUser() {
        User user = User.current();
        return (user == null) ? "anonymous" : user.getId();
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor {

        @Override
        public String getDisplayName() {
            return "Auto promote succesful builds";
        }

        @Override
        public boolean isApplicable(Class arg0) {
            return true;
        }
    }
}
