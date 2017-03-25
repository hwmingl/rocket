package rocket.external.socketio.config;

/**
 * Created by hanwm on 17/3/24.
 */
public class Configuration {

    private int                   bossThreads           = 0;                             // 0 = current_processors_amount * 2
    private int                   workerThreads         = 0;                             // 0 = current_processors_amount * 2
    private String                hostname;
    private int                   port                  = -1;
    private int                   sslPort               = -1;

    private SocketConfig          socketConfig          = new SocketConfig();

    public int getBossThreads() {
        return bossThreads;
    }

    public void setBossThreads(int bossThreads) {
        this.bossThreads = bossThreads;
    }

    public int getWorkerThreads() {
        return workerThreads;
    }

    public void setWorkerThreads(int workerThreads) {
        this.workerThreads = workerThreads;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getSslPort() {
        return sslPort;
    }

    public void setSslPort(int sslPort) {
        this.sslPort = sslPort;
    }

    public SocketConfig getSocketConfig() {
        return socketConfig;
    }

    public void setSocketConfig(SocketConfig socketConfig) {
        this.socketConfig = socketConfig;
    }
}
