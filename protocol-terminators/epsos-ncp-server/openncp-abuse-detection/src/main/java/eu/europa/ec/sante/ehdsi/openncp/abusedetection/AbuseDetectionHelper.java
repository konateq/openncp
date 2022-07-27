package eu.europa.ec.sante.ehdsi.openncp.abusedetection;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.com.srdc.epsos.util.Constants;

public class AbuseDetectionHelper {

    public static final String NAME_OF_JOB = "AbuseDetectionJob";
    public static final String NAME_OF_GROUP = "OpenNCP";
    private static final Logger LOGGER = LoggerFactory.getLogger(AbuseDetectionHelper.class);
    private static final String NAME_OF_TRIGGER = "triggerStart";

    //create variable scheduler of type Scheduler
    private static Scheduler scheduler;

    private AbuseDetectionHelper() {
    }

    public static void abuseDetectionShutdown() {
        boolean schedulerEnabled = Boolean.parseBoolean(Constants.ABUSE_SCHEDULER_ENABLE);
        if (schedulerEnabled) {
            LOGGER.info("Stopping AbuseDetectionServiceFactory Service...");
        }
    }

    public static void abuseDetectionInit() throws SchedulerException {
        boolean schedulerEnabled = Boolean.parseBoolean(Constants.ABUSE_SCHEDULER_ENABLE);
        if (schedulerEnabled) {
            LOGGER.info("Initializing AbuseDetectionServiceFactory Service...");

            //show message to know about the main thread
            LOGGER.info("The name of the QuartzScheduler main thread is: '{}'", Thread.currentThread().getName());

            //initialize scheduler instance from Quartz
            scheduler = new StdSchedulerFactory().getScheduler();

            //start scheduler
            scheduler.start();

            //create scheduler trigger based on the time interval
            Trigger triggerNew = createTrigger();

            //schedule trigger
            scheduleJob(triggerNew);
        } else {
            LOGGER.info("AbuseDetection Scheduler Disabled");
        }
    }

    //create scheduleJob() method to schedule a job
    private static void scheduleJob(Trigger triggerNew) throws SchedulerException {

        //create an instance of the JoDetails to connect Quartz job to the CreateQuartzJob
        JobDetail jobInstance = JobBuilder.newJob(AbuseDetectionService.class).withIdentity(NAME_OF_JOB, NAME_OF_GROUP).build();

        //invoke scheduleJob method to connect the Quartz scheduler to the jobInstance and the triggerNew
        scheduler.scheduleJob(jobInstance, triggerNew);

    }

    //create createTrigger() method that returns a trigger based on the time interval
    private static Trigger createTrigger() {

        //initialize time interval
        int triggerInterval = 60;

        if (!Constants.ABUSE_SCHEDULER_TIME_INTERVAL.isEmpty()) {
            int val = Integer.parseInt(Constants.ABUSE_SCHEDULER_TIME_INTERVAL);
            if (val >= 60) {
                triggerInterval = val;
            }
        }

        //create a trigger to be returned from the method
        return TriggerBuilder.newTrigger().withIdentity(NAME_OF_TRIGGER, NAME_OF_GROUP)
                .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(triggerInterval).repeatForever())
                .build();
    }
}
