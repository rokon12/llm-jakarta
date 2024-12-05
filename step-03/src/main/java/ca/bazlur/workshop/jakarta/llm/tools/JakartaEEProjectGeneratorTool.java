package ca.bazlur.workshop.jakarta.llm.tools;


import ca.bazlur.workshop.jakarta.llm.util.InputNormalizer;
import ca.bazlur.workshop.jakarta.llm.util.MavenUtility;
import ca.bazlur.workshop.jakarta.llm.util.VersionInfo;
import ca.bazlur.workshop.jakarta.llm.util.ZipUtility;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Map.entry;

//This is a demo tool that generates a Jakarta EE project with customizable configurations.
//This can be furhter enhanced to include more options and configurations
@Slf4j
public class JakartaEEProjectGeneratorTool {
    private static final String DEFAULT_GROUPID = "org.eclipse";
    private static final String DEFAULT_ARTIFACTID = "jakartaee-hello-world";

    private static final Map<String, String> cache = new ConcurrentHashMap<>();

    @Tool("Generate a Jakarta EE project with customizable configurations. If any input is missing or unclear, the AI will request clarification step-by-step. After collecting and validating all inputs, the tool generates the project, zips it, and provides a download URL.")
    public String generate(
            @P("The Jakarta EE version (e.g., 'Jakarta EE 10', '10'). Extracts numerical values like '10'.") String jakartaVersion,
            @P("""
                    The Jakarta EE profile, mapped as follows:
                                       * 'Platform' -> 'full'
                                       * 'Web Profile' or 'Web' -> 'web'
                                       * 'Core Profile' or 'Core' -> 'core'
                                      Handles input variations like 'Web Profile' or 'Core' and maps them to their respective keys ('full', 'web', 'core')
                    """) String profile,
            @P("The Java SE version (e.g., 'Java SE 17', '17'). Extracts numerical values like '17'.") String javaVersion,
            @P("The runtime to use, mapped as follows: 'GlassFish' -> 'glassfish', 'Open Liberty' -> 'open-liberty', 'Payara' -> 'payara', 'TomEE' -> 'tomee', 'WildFly' -> 'wildfly'.") String runtime,
            @P("Whether Docker support is needed ('Yes', 'No'). Handles variations like 'docker support' or 'no docker'.") String docker,
            @P("The Maven group ID (default: 'org.eclipse'). Use default if not provided.") String groupId,
            @P("The Maven artifact ID (default: 'jakartaee-hello-world'). Use default if not provided. Normalizes spaces into hyphens.") String artifactId) {

        log.info("Generating Jakarta EE project with the following parameters: jakartaVersion={}, profile={}, javaVersion={}, runtime={}, dockerSupport={}",
                jakartaVersion, profile, javaVersion, runtime, docker);
        try {
            if (groupId == null || groupId.isEmpty()) {
                groupId = DEFAULT_GROUPID;
            }
            if (artifactId == null || artifactId.isEmpty()) {
                artifactId = DEFAULT_ARTIFACTID;
            }

            String missingInput = getMissingInputPrompt(jakartaVersion, profile, javaVersion, runtime, docker);
            if (missingInput != null) {
                return missingInput;
            }

            profile = InputNormalizer.normalizeProfile(profile);
            int javaVersionInt = InputNormalizer.normalizeJavaVersion(javaVersion);
            boolean dockerSupport = InputNormalizer.normalizeDocker(docker);
            runtime = InputNormalizer.normalizeRuntime(runtime);
            artifactId = InputNormalizer.normalizeArtifactId(artifactId);
            double jVersion = InputNormalizer.normalizeVersion(jakartaVersion);
            String path = generate(jVersion, profile, javaVersionInt, dockerSupport, runtime, DEFAULT_GROUPID, artifactId);

            return "Your Jakarta EE project has been generated! Download it here: http://localhost:8080/llm-jakarta/download/" + path + ".zip";
        } catch (Exception e) {
            log.error("Error generating Jakarta EE project", e);
            return "Error generating Jakarta EE project: " + e.getMessage();
        }
    }

    private String getMissingInputPrompt(String jakartaVersion, String profile, String javaVersion, String runtime, String docker) {
        if (jakartaVersion == null || jakartaVersion.isEmpty()) {
            return "Which Jakarta EE version do you want? (e.g., Jakarta EE 10, Jakarta EE 9, Jakarta EE 8)";
        }
        if (profile == null || profile.isEmpty()) {
            return "Which Jakarta EE profile do you want? (Platform, Web Profile, or Core Profile)";
        }
        if (javaVersion == null || javaVersion.isEmpty()) {
            return "Which Java version do you want to use? (e.g., Java SE 17, Java SE 11)";
        }
        if (runtime == null || runtime.isEmpty()) {
            return "Which runtime do you want? (None, WildFly, Open Liberty, Payara, TomEE, or GlassFish)";
        }
        if (docker == null || docker.isEmpty()) {
            return "Do you need Docker support? (Yes or No)";
        }
        return null;
    }

    public String generate(double jakartaVersion, String profile, int javaVersion, boolean dockerSupport, String runtime, String groupId, String artifactId) {
        try {
            log.info("Generating project - Jakarta EE version: {}, Jakarta EE profile: {}, Java SE version: {}, Docker: {}, runtime: {}, groupId: {}, artifactId: {}",
                    jakartaVersion, profile, javaVersion, dockerSupport, runtime, groupId, artifactId);

            String cachedDirectory = cache.get(getCacheKey(jakartaVersion, profile, javaVersion, dockerSupport, runtime, groupId, artifactId));

            if ((cachedDirectory == null) || (!new File(cachedDirectory).exists())) {
                File workingDirectory = Files.createTempDirectory("starter-output-").toFile();
                log.info("Working directory: {}", workingDirectory.getAbsolutePath());

                Properties properties = new Properties();
                properties.putAll(Map.ofEntries(
                        entry("jakartaVersion",
                                ((jakartaVersion % 1.0 != 0) ? String.format("%s", jakartaVersion)
                                        : String.format("%.0f", jakartaVersion))),
                        entry("profile", profile),
                        entry("javaVersion", javaVersion),
                        entry("docker", (dockerSupport ? "yes" : "no")),
                        entry("runtime", runtime),
                        entry("groupId", groupId),
                        entry("artifactId", artifactId),
                        entry("package", groupId)));

                MavenUtility.invokeMavenArchetype("org.eclipse.starter", "jakarta-starter", VersionInfo.ARCHETYPE_VERSION,
                        properties, workingDirectory);

                log.info("Creating zip file.");
                File directory = new File(workingDirectory, artifactId);
                ZipUtility.zipDirectory(directory, workingDirectory);

                String absolutePath = directory.getAbsolutePath();
                log.info("Zip file created: {}", absolutePath);

                if (groupId.equals(DEFAULT_GROUPID) && artifactId.equals(DEFAULT_ARTIFACTID)) {
                    log.info("Caching output.");
                    cache.put(getCacheKey(jakartaVersion, profile, javaVersion, dockerSupport, runtime, groupId, artifactId), workingDirectory.getAbsolutePath());
                }
                return Base64.getEncoder().encodeToString(absolutePath.getBytes());
            } else {
                log.info("Returning zip file from cached directory: {}", cachedDirectory);
                return Base64.getEncoder().encodeToString(cachedDirectory.getBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate zip.", e);
        }
    }

    private String getCacheKey(double jakartaVersion, String profile, int javaVersion, boolean docker, String runtime, String groupId, String artifactId) {
        return String.join(":",
                String.valueOf(jakartaVersion),
                profile,
                String.valueOf(javaVersion),
                String.valueOf(docker),
                runtime,
                groupId,
                artifactId);
    }
}
