package ca.bazlur.workshop.jakarta.llm.util;

public class InputNormalizer {

    public static double normalizeVersion(String input) {
        String normalized = normalizeJakartaVersion(input);
        return Double.parseDouble(normalized);
    }

    public static String normalizeJakartaVersion(String input) {
        if (input.toLowerCase().contains("8")) return "8";
        if (input.toLowerCase().contains("9.1")) return "9.1";
        if (input.toLowerCase().contains("9")) return "9";
        if (input.toLowerCase().contains("10")) return "10";
        if (input.toLowerCase().contains("11")) return "11";
        throw new IllegalArgumentException("Unsupported Jakarta EE version: " + input);
    }

    public static String normalizeProfile(String input) {
        if (input.toLowerCase().contains("platform")) return "full";
        if (input.toLowerCase().contains("web")) return "web";
        if (input.toLowerCase().contains("core")) return "core";
        return input;
    }

    public static int normalizeJavaVersion(String input) {
        if (input.contains("8")) return 8;
        if (input.contains("11")) return 11;
        if (input.contains("17")) return 17;
        if (input.contains("21")) return 21;
        throw new IllegalArgumentException("Unsupported Java version: " + input);
    }

    public static boolean normalizeDocker(String input) {
        if (input.toLowerCase().contains("yes") || input.toLowerCase().contains("true") || input.toLowerCase().contains("docker")) {
            return true;
        }
        if (input.toLowerCase().contains("no") || input.toLowerCase().contains("false")) {
            return false;
        }
        throw new IllegalArgumentException("Unsupported Docker flag: " + input);
    }

    public static String normalizeRuntime(String input) {
        if (input.toLowerCase().contains("glassfish")) return "glassfish";
        if (input.toLowerCase().contains("open liberty") || input.toLowerCase().contains("liberty"))
            return "open-liberty";
        if (input.toLowerCase().contains("payara")) return "payara";
        if (input.toLowerCase().contains("tomee")) return "tomee";
        if (input.toLowerCase().contains("wildfly") || input.toLowerCase().contains("wild-fly")) return "wildfly";
        if (input.toLowerCase().contains("none")) return "none";
        throw new IllegalArgumentException("Unsupported runtime: " + input);
    }

    public static String normalizeArtifactId(String input) {
        return input.toLowerCase().replace(" ", "-");
    }
}
