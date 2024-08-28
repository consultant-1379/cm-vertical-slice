package com.ericsson.nms.mediation.cm.vertical.slice.tests.templates;

import static org.junit.Assert.fail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.mediation.cm.vertical.slice.utility.TimeUtil;

/**
 * Template for set-up/clean-up of sync test cases.
 */
public abstract class SyncTestCaseSetup {

    public static final String SETUP_PHASE_NAME = "SETUP";
    public static final String CLEAN_UP_PHASE_NAME = "CLEAN-UP";

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncTestCaseSetup.class);

    private final String testSuiteName;
    private final String setupPhase;

    public SyncTestCaseSetup(final String testSuiteName, final String setupPhase) {
        this.testSuiteName = testSuiteName;
        this.setupPhase = setupPhase;
    }

    /**
     * Provides means to set-up/clean-up the test case.
     * <p>
     * This should be the starting point of each set-up/clean-up method.
     */
    public void start() {
        final long startTimestamp = System.currentTimeMillis();

        try {
            LOGGER.info("------ ###### -> Starting {} of a test in '{}' suite... <- ###### ------", setupPhase, testSuiteName);

            execute();

            LOGGER.info("------ ###### -> Finished {} of a test in '{}' suite. <- ###### ------", setupPhase, testSuiteName);

        } catch (final Exception exception) {
            LOGGER.error("!!!!!! ###### => Exception thrown during the {} of a test in '{}' suite! <= ###### !!!!!!", setupPhase, testSuiteName);
            LOGGER.error("       ------ -> Exception stack trace: ", exception);
            fail("Error in SyncTestCaseSetup, exception: " + exception);
        }

        final long timeTakenTimestamp = System.currentTimeMillis() - startTimestamp;
        LOGGER.info("       ------ -> {} of a test in '{}' suite took {}.\n", setupPhase, testSuiteName,
                TimeUtil.convertToMinSecMs(timeTakenTimestamp));
    }

    /**
     * Executes the set-up/clean-up of the test case.
     * <p>
     * Override this method and include all the set-up/clean-up logic here.
     *
     * @throws Exception
     */
    protected abstract void execute() throws Exception;

}
