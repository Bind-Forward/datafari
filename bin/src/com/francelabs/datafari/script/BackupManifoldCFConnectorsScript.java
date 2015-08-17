package com.francelabs.datafari.script;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONException;
import org.json.JSONObject;

import com.francelabs.manifoldcf.configuration.api.JSONUtils;
import com.francelabs.manifoldcf.configuration.api.ManifoldAPI;

public class BackupManifoldCFConnectorsScript {

	private static String configPropertiesFileName = "config/log4j.properties";

	private final static Logger LOGGER = Logger
			.getLogger(BackupManifoldCFConnectorsScript.class.getName());

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) {

		PropertyConfigurator.configure(configPropertiesFileName);

		if (args.length < 1) {
			LOGGER.fatal("No argument");
			return;
		}

		String backupDirectory;
		if (args.length < 2) {
			System.out.println("Backup directory : ");
			Scanner input = new Scanner(System.in);
			backupDirectory = input.nextLine();
		} else {
			backupDirectory = args[1];
		}

		try {

			File outputConnectionsDir = new File(backupDirectory,
					ManifoldAPI.COMMANDS.OUTPUTCONNECTIONS);

			File repositoryConnectionsDir = new File(backupDirectory,
					ManifoldAPI.COMMANDS.REPOSITORYCONNECTIONS);

			File authorityConnectionsDir = new File(backupDirectory,
					ManifoldAPI.COMMANDS.AUTHORITYCONNECTIONS);

			File jobsDir = new File(backupDirectory, ManifoldAPI.COMMANDS.JOBS);

			if (args[0].equals("BACKUP")) {

				ManifoldAPI.waitUntilManifoldIsStarted();
				
				prepareDirectory(outputConnectionsDir);
				saveAllConnections(
						ManifoldAPI
								.getConnections(ManifoldAPI.COMMANDS.OUTPUTCONNECTIONS),
						outputConnectionsDir);

				prepareDirectory(repositoryConnectionsDir);
				saveAllConnections(
						ManifoldAPI
								.getConnections(ManifoldAPI.COMMANDS.REPOSITORYCONNECTIONS),
						repositoryConnectionsDir);

				prepareDirectory(authorityConnectionsDir);
				saveAllConnections(
						ManifoldAPI
								.getConnections(ManifoldAPI.COMMANDS.AUTHORITYCONNECTIONS),
						authorityConnectionsDir);

				prepareDirectory(jobsDir);
				saveAllConnections(
						ManifoldAPI.getConnections(ManifoldAPI.COMMANDS.JOBS),
						jobsDir);

				LOGGER.info("Connectors Saved");

			}

			if (args[0].equals("RESTORE")) {
				ManifoldAPI.waitUntilManifoldIsStarted();
				ManifoldAPI.cleanAll();

				restoreAllConnections(outputConnectionsDir,
						ManifoldAPI.COMMANDS.OUTPUTCONNECTIONS);

				restoreAllConnections(repositoryConnectionsDir,
						ManifoldAPI.COMMANDS.REPOSITORYCONNECTIONS);

				restoreAllConnections(authorityConnectionsDir,
						ManifoldAPI.COMMANDS.AUTHORITYCONNECTIONS);

				restoreAllConnections(jobsDir, ManifoldAPI.COMMANDS.JOBS);

				LOGGER.info("Connectors Restored");
			}

		} catch (Exception e) {
			LOGGER.fatal(e.getMessage());
			e.printStackTrace();
		}
	}

	private static void prepareDirectory(File directory) {
		directory.mkdirs();
		for (File file : directory.listFiles()) {
			file.delete();
		}
	}

	private static void restoreAllConnections(File directory, String command)
			throws Exception {

		File[] connectorFiles = directory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				name.endsWith(".json");
				return true;
			}
		});
		for (File connectorFile : connectorFiles) {
			restoreConnection(connectorFile, command);
		}

	}

	private static void restoreConnection(File connectorFile, String command)
			throws Exception {

		JSONObject jsonObject = JSONUtils.readJSON(connectorFile);
		String name = connectorFile.getName();
		ManifoldAPI.putConfig(command, name.substring(0, name.length() - 5),
				jsonObject);

	}

	private static void saveAllConnections(Map<String, JSONObject> connections,
			File directory) throws IOException, JSONException {

		for (Entry<String, JSONObject> connection : connections.entrySet()) {
			saveConnection(connection, directory);
		}
	}

	private static void saveConnection(
			Entry<String, JSONObject> outputConnection, File directory)
			throws IOException, JSONException {

		JSONUtils.saveJSON(outputConnection.getValue(), new File(directory,
				outputConnection.getKey() + ".json"));
		File connectorFile = new File(directory, outputConnection.getKey()
				+ ".json");

	}

}
