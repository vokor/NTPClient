import org.apache.commons.net.ntp.NtpV3Packet;
import org.apache.commons.net.ntp.TimeInfo;
import org.apache.commons.net.ntp.TimeStamp;

public class NTPClient {
    public static void processResponse(TimeInfo info) {
        NtpV3Packet message = info.getMessage();
        TimeStamp transmitTimeStamp = message.getTransmitTimeStamp();
        System.out.println(" Transmit Timestamp: " + transmitTimeStamp.toDateString());
    }
}