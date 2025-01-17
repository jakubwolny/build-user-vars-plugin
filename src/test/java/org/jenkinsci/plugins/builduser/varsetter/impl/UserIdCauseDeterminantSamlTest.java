package org.jenkinsci.plugins.builduser.varsetter.impl;

import hudson.EnvVars;
import hudson.model.Cause.UserIdCause;

import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.security.ChainedServletFilter;
import hudson.security.SecurityRealm;
import hudson.util.LogTaskListener;
import jenkins.model.IdStrategy;
import org.easymock.EasyMock;
import org.jenkinsci.plugins.saml.SamlSecurityRealm;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import javax.servlet.FilterConfig;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.easymock.EasyMock.anyObject;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UserIdCauseDeterminantSamlTest {

    @Rule
    public JenkinsRule r = new JenkinsRule();

    public EnvVars runSamlSecurityRealmTest(JenkinsRule r, String userid, String caseConversion) throws IOException {
        ArrayList<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        User user =
                new User(
                        userid,
                        "password123",
                        true,
                        true,
                        true,
                        true,
                        grantedAuthorities);
        SamlSecurityRealm realm = EasyMock.mock(SamlSecurityRealm.class);

        IdStrategy strategy = new IdStrategy.CaseSensitive();
        EasyMock.expect(realm.getUserIdStrategy()).andReturn(strategy).anyTimes();
        EasyMock.expect(realm.getSecurityComponents()).andReturn(new SecurityRealm.SecurityComponents());
        EasyMock.expect(realm.createFilter(anyObject(FilterConfig.class))).andReturn(new ChainedServletFilter());
        EasyMock.expect(realm.getUsernameCaseConversion()).andReturn(caseConversion);
        EasyMock.expect(realm.loadUserByUsername2(userid)).andReturn(user).anyTimes();

        EasyMock.replay(realm);

        hudson.model.User.getById(userid, true);
        r.jenkins.setSecurityRealm(realm);
        EnvVars outputVars = new EnvVars();
        TaskListener taskListener = new LogTaskListener(Logger.getLogger("test-logger"), Level.INFO);
        Run run = r.createFreeStyleProject().getBuild("0");
        UserIdCause cause = new UserIdCause(userid);
        UserIdCauseDeterminant determinant = new UserIdCauseDeterminant();
        determinant.setJenkinsUserBuildVars(run, cause, outputVars, taskListener);
        return outputVars;
    }

    @Test
    public void testSetJenkinsUserBuildVarsSamlUpperCase() throws IOException {
        EnvVars outputVars = runSamlSecurityRealmTest(r, "Testuser", "uppercase");
        assertThat(outputVars.get("BUILD_USER_ID"), is(equalTo("TESTUSER")));
    }

    @Test
    public void testSetJenkinsUserBuildVarsSamlLowerCase() throws IOException {
        EnvVars outputVars = runSamlSecurityRealmTest(r, "Testuser", "lowercase");
        assertThat(outputVars.get("BUILD_USER_ID"), is(equalTo("testuser")));
    }

    @Test
    public void testSetJenkinsUserBuildVarsSamlNoCase() throws IOException {
        EnvVars outputVars = runSamlSecurityRealmTest(r, "Testuser", "none");
        assertThat(outputVars.get("BUILD_USER_ID"), is(equalTo("Testuser")));
    }
}
