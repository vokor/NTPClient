import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.net.InetAddress;

class Main {
    // Example of host name: clock.psu.edu
    public static void main(String[] args) throws IOException {
        if (args == null || args.length > 1) {
            System.err.println("Usage: NTPClient <hostname>");
            System.exit(1);
        }
        NTPUDPClient client = new NTPUDPClient();
        client.setDefaultTimeout(10000);
        InetAddress hostAddr = InetAddress.getByName(args[0]);
        TimeInfo info = client.getTime(hostAddr);
        NTPClient.processResponse(info);
        client.close();
    }
}