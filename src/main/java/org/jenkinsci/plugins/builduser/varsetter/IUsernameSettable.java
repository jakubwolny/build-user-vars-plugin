package org.jenkinsci.plugins.builduser.varsetter;

import hudson.EnvVars;
import hudson.model.Cause;
import hudson.model.Run;
import hudson.model.TaskListener;

/**
 * Interface declaring method for setting jenkins user build variables parametrized by
 * {@link Cause} subclasses.
 * <p>
 * User based {@link Cause} instance is the source of username data.
 *
 * <ul>
 * 	<li><b>BUILD_USER</b> -- full name of user started build,</li>
 * 	<li><b>BUILD_USER_FIRST_NAME</b> -- first name of user started build,</li>
 * 	<li><b>BUILD_USER_LAST_NAME</b> -- last name of user started build,</li>
 * 	<li><b>BUILD_USER_ID</b> -- id of user started build.</li>
 * </ul>
 *
 * @author GKonovalenko
 */
public interface IUsernameSettable<T extends Cause> {

    /** Full name of user started build */
    public static final String BUILD_USER_VAR_NAME = "BUILD_USER";
    /** Groups username belongs to **/
    public static final String BUILD_USER_VAR_GROUPS = "BUILD_USER_GROUPS";
    /** First name of user started build */
    public static final String BUILD_USER_FIRST_NAME_VAR_NAME = "BUILD_USER_FIRST_NAME";
    /** Last name of user started build */
    public static final String BUILD_USER_LAST_NAME_VAR_NAME = "BUILD_USER_LAST_NAME";
    /** Email of user started build */
    public static final String BUILD_USER_EMAIL = "BUILD_USER_EMAIL";
    /** Id of user started build */
    public static final String BUILD_USER_ID = "BUILD_USER_ID";
    /** Optional value for variable which value couldn't be defined. */
    public static final String UNDEFINED = "UNDEFINED";
    /** Slack name of the user who started build */
    public static final String BUILD_USER_SLACK = "BUILD_USER_SLACK";


    /**
     * Adds username build variables extracted from build cause to map of build variables.
     *
     * @param run
     * @param cause
     *              cause where to get username from.
     * @param envVars
     *              map of build variables, where to add username variables.
     * @param listener
     * @return
     *              <code>true</code> if username was determined and added to the passed map,
     * <code>false</code> otherwise.
     */
    boolean setJenkinsUserBuildVars(Run run, T cause, EnvVars envVars, TaskListener listener) throws Exception;

    /**
     * Returns {@link Cause} subclass used to determine user name.
     *
     * @return class used to determine user name.
     */
    Class<T> getUsedCauseClass();

}
