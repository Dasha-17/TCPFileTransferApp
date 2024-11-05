import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Random;

public class Client {
    public static void main(String[] args) {
        try (SocketChannel socketChannel = SocketChannel.open()) {
            socketChannel.connect(new InetSocketAddress("localhost", 12345));

            String fileName = "testFile.bin";
            byte[] fileContent = generate(1024 * 1024);//1

            String met = fileName + "\r\n" + fileContent.length + "\r\n";
            ByteBuffer metadataBuffer = ByteBuffer.wrap(met.getBytes());
            socketChannel.write(metadataBuffer);

            ByteBuffer fileBuffer = ByteBuffer.wrap(fileContent);
            while (fileBuffer.hasRemaining()) {
                socketChannel.write(fileBuffer);
            }
            System.out.println("Файл " + fileName + " отправлен");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static byte[] generate(int size) {
        byte[] content = new byte[size];
        new Random().nextBytes(content);
        return content;
    }
}
