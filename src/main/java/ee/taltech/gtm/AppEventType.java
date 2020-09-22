package ee.taltech.gtm;

public enum AppEventType {
    RUN("-run"),
    BUILD("-build");

    AppEventType(String command) {
        this.command = command;
    }

    private final String command;

    public String getCommand() {
        return command;
    }
}

