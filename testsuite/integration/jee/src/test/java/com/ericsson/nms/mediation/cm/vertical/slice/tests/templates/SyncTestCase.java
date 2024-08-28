package com.ericsson.nms.mediation.cm.vertical.slice.tests.templates;

import static org.junit.Assert.fail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.mediation.cm.vertical.slice.utility.TimeUtil;

/**
 * Template for sync test cases.
 */
public abstract class SyncTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncTestCase.class);

    private final String name;

    public SyncTestCase(final String name) {
        this.name = name;
    }

    /**
     * Starts the execution of the test case.
     * <p>
     * This should be the starting point of each test case.
     */
    public void start() {
        final long startTimestamp = System.currentTimeMillis();

        try {
            LOGGER.info("------ ####################{}################### ------", getHashCharacterLine());
            LOGGER.info("------ ###### => Starting '{}' test... <= ###### ------", name);
            LOGGER.info("------ ####################{}################### ------", getHashCharacterLine());

            execute();

            LOGGER.info("++++++ ###########{}####################################### ++++++", getHashCharacterLine());
            LOGGER.info("++++++ ###### => '{}' test finished successfully. <= ###### ++++++", name);
            LOGGER.info("++++++ ###########{}####################################### ++++++", getHashCharacterLine());

        } catch (final Exception exception) {
            LOGGER.error("!!!!!! #######################################{}################# !!!!!!", getHashCharacterLine());
            LOGGER.error("!!!!!! ###### => Exception thrown during the '{}' test! <= ###### !!!!!!", name);
            LOGGER.error("!!!!!! #######################################{}################# !!!!!!", getHashCharacterLine());
            LOGGER.error("       ------ -> Exception stack trace: ", exception);
            fail("Error in SyncTestCase, exception: " + exception);
        }

        final long timeTakenTimestamp = System.currentTimeMillis() - startTimestamp;
        LOGGER.info("       ------ -> Test took {}.\n", TimeUtil.convertToMinSecMs(timeTakenTimestamp));
    }

    /**
     * Executes the test case flow.
     * <p>
     * Override this method and include all the test logic here.
     *
     * @throws Exception
     */
    protected abstract void execute() throws Exception;

    private String getHashCharacterLine() {
        final StringBuilder hashLine = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            hashLine.append("#");
        }

        return hashLine.toString();
    }

}
