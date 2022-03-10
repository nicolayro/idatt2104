package no.ntnu.coderunnerbackend;

import java.io.*;

public class Command {

	public String run(String code) throws IOException, InterruptedException {
		BufferedWriter writer = new BufferedWriter(new FileWriter("Main.java", false));
		writer.write(code);
		writer.close();

		// Commands
		String[] buildDocker = {"/usr/local/bin/docker", "build", "-t", "code-runner", "."};
		String[] runDocker = {"/usr/local/bin/docker", "run", "--rm", "--name", "code-runner-container", "code-runner"};
		String[] cleanupDocker= {"/usr/local/bin/docker", "image", "rm", "code-runner"};

		// Build
		Runtime.getRuntime().exec(buildDocker).waitFor(); // Wait

		// Run
		Process run = Runtime.getRuntime().exec(runDocker);
		String runResults = printResults(run);
		run.waitFor(); // Wait

		// Delete
		Runtime.getRuntime().exec(cleanupDocker); // Don't wait

		return runResults;
	}

	public String printResults(Process process) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String result = "";
		String line;
		while ((line = reader.readLine()) != null) {
			result += line;
		}
		return result;
	}
}
