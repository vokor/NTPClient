import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class NTPClient {
    private static final long DAYS = 25567; // 1 Jan 1900 to 1 Jan 1970
    private static final long SECS = 60 * 60 * 24 * DAYS;
    private static final int ITERATION_COUNT = 10;
    private final static int PORT = 123; // SNTP UDP port
    private String HOST_ADDR;
    private final DatagramSocket socket;

    public NTPClient(String host) throws SocketException {
        this.HOST_ADDR = host;
        socket = new DatagramSocket();
    }

    public long processResponse(byte[] request) throws IOException {
        double sumClock = 0.0;
        for (int i = 0; i < ITERATION_COUNT; i++) {
            sumClock += getLocalClockOffset(socket, request.clone());
        }
        final double localClockOffset = sumClock / ITERATION_COUNT;
        return (long) (System.currentTimeMillis() + localClockOffset * 1000);
    }

    public void close() {
        socket.close();
    }

    private double getLocalClockOffset(DatagramSocket socket, byte[] request) throws IOException {
        InetAddress address = InetAddress.getByName(HOST_ADDR);

        DatagramPacket requestPacket = new DatagramPacket(request, request.length, address, PORT);
        encode(requestPacket.getData(), 40, now());
        socket.send(requestPacket);

        DatagramPacket packet = new DatagramPacket(request, request.length);
        socket.receive(packet);

        double destinationTimestamp = now();
        Message msg = new Message(packet.getData());
        final double originateTimestamp = msg.getOriginateTimestamp();
        final double receiveTimestamp = msg.getReceiveTimestamp();
        final double transmitTimestamp = msg.getTransmitTimestamp();
        return ((receiveTimestamp - originateTimestamp) + (transmitTimestamp - destinationTimestamp)) / 2;
    }

    private double now () {
        return System.currentTimeMillis() / 1000.0 + SECS;
    }

    private class Message {

        private byte[] data;
        private final double originateTimestamp;
        private final double receiveTimestamp;
        private final double transmitTimestamp;

        private final int originateOffset = 24;
        private final int receiveOffset = 32;
        private final int transmitOffset = 40;

        public Message(byte[] data) {
            this.data = data;
            this.originateTimestamp = decode(originateOffset);
            this.receiveTimestamp = decode(receiveOffset);
            this.transmitTimestamp = decode(transmitOffset);
        }

        public double getOriginateTimestamp() {
            return originateTimestamp;
        }

        public double getReceiveTimestamp() {
            return receiveTimestamp;
        }

        public double getTransmitTimestamp() {
            return transmitTimestamp;
        }

        public double decode(int pointer) {
            double r = 0.0;
            for (int i = 0; i < 8; i++) {
                r += unsignedByteToShort(data[pointer + i]) * Math.pow(2, (3 - i) * 8);
            }
            return r;
        }
    }

    public short unsignedByteToShort(byte b) {
        if ((b & 0x80) == 0x80) {
            return (short) (128 + (b & 0x7f));
        } else {
            return (short) b;
        }
    }

    public void encode(byte[] array, int pointer, double timestamp) {
        for (int i = 0; i < 8; i++) {
            double base = Math.pow(2, (3 - i) * 8);
            array[pointer + i] = (byte) (timestamp / base);
            timestamp = timestamp - (unsignedByteToShort(array[pointer + i]) * base);
        }
        array[7] = (byte) (Math.random() * 255.0);
    }
}
