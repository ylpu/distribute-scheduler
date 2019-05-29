package com.yl.distribute.scheduler.core.resource.mesos;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mesos.MesosSchedulerDriver;
import org.apache.mesos.Protos;
import org.apache.mesos.Scheduler;

public class MesosDistributeSchedulerDriver {

	private final static Log LOGGER = LogFactory.getLog(MesosDistributeSchedulerDriver.class);

	public static void main(String[] args) {
		if (args.length < 1 || args.length > 3) {
			usage();
			System.exit(1);
		}

		String path = System.getProperty("user.dir") + "/pinspider-1.0-SNAPSHOT-jar-with-dependencies.jar";

		Protos.CommandInfo.URI uri = Protos.CommandInfo.URI.newBuilder().setValue(path).setExtract(false).build();
		String commandUserProfile = "java -cp pinspider-1.0-SNAPSHOT-jar-with-dependencies.jar com.geekz.anon.mesos.framework.PinUserProfileExecutor";
		Protos.CommandInfo commandInfoUserProfile = Protos.CommandInfo.newBuilder()
				.setValue(commandUserProfile)
				.addUris(uri)	
				.build();

		String commandUserBoard   = "java -cp pinspider-1.0-SNAPSHOT-jar-with-dependencies.jar com.geekz.anon.mesos.framework.PinUserBoardExecutor";
		Protos.CommandInfo commandInfoUserBoard = Protos.CommandInfo.newBuilder()
				.setValue(commandUserBoard)
				.addUris(uri)
				.build();

		Protos.ExecutorInfo userProfileExecutorInfo = Protos.ExecutorInfo.newBuilder()
				.setExecutorId(Protos.ExecutorID.newBuilder().setValue("PinUserProfileExecutor"))
				.setCommand(commandInfoUserProfile)
		        .setName("PinUserProfileExecutor Java")
		        .build();

		Protos.ExecutorInfo userBoardExecutorInfo = Protos.ExecutorInfo.newBuilder()
				.setExecutorId(Protos.ExecutorID.newBuilder().setValue("PinUserBoardExecutor"))
				.setCommand(commandInfoUserBoard)
				.setName("PinUserBoardExecutor Java")
				.build();

		Protos.FrameworkInfo.Builder frameworkBuilder = Protos.FrameworkInfo.newBuilder()
				.setFailoverTimeout(120000)
				.setUser("")
				.setName("Pinspider Framework");

		if (System.getenv("MESOS_CHECKPOINT") != null) {
			System.out.println("Enabling checkpoint for the framework");
			frameworkBuilder.setCheckpoint(true);
		}

		Scheduler scheduler = args.length == 1 ?
				new MesosDistributeScheduler(userProfileExecutorInfo, userBoardExecutorInfo) :
				new MesosDistributeScheduler(userProfileExecutorInfo, userBoardExecutorInfo, Integer.parseInt(args[1]), args[2]);

		MesosSchedulerDriver schedulerDriver = null;

		if (System.getenv("MESOS_AUTHENTICATE") != null) {
			LOGGER.info("Enabling authentication for the framework");

			if (System.getenv("DEFAULT_PRINCIPAL") == null) {
				LOGGER.error("Expecting authentication principal");
				System.exit(1);
			}

			if (System.getenv("DEFAULT_SECRET") == null) {
				LOGGER.error("Expecting authentication secret");
				System.exit(1);
			}

			Protos.Credential credential = Protos.Credential.newBuilder()
				  .setPrincipal(System.getenv("DEFAULT_PRINCIPAL"))
				  .setSecret(System.getenv("DEFAULT_SECRET")).build();

			frameworkBuilder.setPrincipal(System.getenv("DEFAULT_PRINCIPAL"));

			schedulerDriver = new MesosSchedulerDriver(scheduler, frameworkBuilder.build(), args[0], credential);
		} else {
			frameworkBuilder.setPrincipal("test-framework-java");
			schedulerDriver = new MesosSchedulerDriver(scheduler, frameworkBuilder.build(), args[0]);
		}

		int status = schedulerDriver.run() == Protos.Status.DRIVER_STOPPED ? 0 : 1;
		schedulerDriver.stop();
		System.exit(status);
	}

	private static void usage() {
		String name = MesosDistributeScheduler.class.getName();
		System.err.println("Usage : " + name + "master <tasks> <pinterest_user_URL>");
		System.err.println("<pinterest_user_URL> example: http://www.pinterest.com/techcrunch");
	}
}