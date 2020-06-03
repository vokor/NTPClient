import java.io.IOException;
import java.util.Date;

class Main {
    // Example of host name: clock.psu.edu
    public static void main(String[] args) throws IOException {
        if (args == null || args.length > 1) {
            System.err.println("Usage: SNTPClient <hostname>");
            System.exit(1);
        }
        byte leapIndicator = 0;
        byte mode = 3;
        byte version = 3;

        byte[] request = new byte[48];
        request[0] = (byte) (leapIndicator << 6 | version << 3 | mode);
        NTPClient client = new NTPClient(args[0]);
        Date date = new Date(client.processResponse(request));
        System.out.println(date.toString());
        client.close();
    }
}