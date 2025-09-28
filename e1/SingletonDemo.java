class Logger {
    private static Logger instance;

    private Logger() {}

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void log(String msg) {
        System.out.println("Log: " + msg);
    }
}

public class SingletonDemo {
    public static void main(String[] args) {
        Logger logger = Logger.getInstance();
        logger.log("System started.");
        logger.log("System running smoothly.");
    }
}
