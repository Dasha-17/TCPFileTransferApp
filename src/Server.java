import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.Scanner;

public class Server {
    private static volatile long totalBytesReceived = 0;
    private static volatile long startTime;

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Введите IP-адрес: ");
            String ipAddress = scanner.nextLine();
            System.out.print("Введите порт: ");
            int port = Integer.parseInt(scanner.nextLine());
            System.out.print("Введите директорию для сохранения файлов: ");
            String directoryPath = scanner.nextLine();

//            String ipAddress = "localhost";
//            int port = 12345;
//            String directoryPath = "C:\\Users\\dasha\\OneDrive\\Рабочий стол\\TCP";


            Path directory = Paths.get(directoryPath);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }

            startTime = System.currentTimeMillis();
            new Thread(Server::calculateSpeed).start();

            try (ServerSocketChannel serverChannel = ServerSocketChannel.open()) {
                serverChannel.bind(new InetSocketAddress(ipAddress, port));
                System.out.println("Сервер запущен и ожидает соединений...");

                while (true) {
                    try (SocketChannel clientChannel = serverChannel.accept()) {
                        System.out.println("Новое соединение установлено.");
                        ByteBuffer buffer = ByteBuffer.allocate(1024); // заголовки

                        int bytesRead = clientChannel.read(buffer);
                        if (bytesRead == -1) {
                            throw new IOException("Cоединение разорвано.");
                        }
                        buffer.flip();
                        FrameParser.FrameData frameData = FrameParser.parseFrame(buffer);
                        String fileName = frameData.fileName;
                        int fileLength = frameData.fileLength;
                        try (FileChannel fileChannel = FileChannel.open(directory.resolve(fileName), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                            ByteBuffer fileBuffer = ByteBuffer.allocate(1024);
                            long totalRead = 0;

                            while (totalRead < fileLength) {
                                fileBuffer.clear();
                                int read = clientChannel.read(fileBuffer);
                                if (read == -1) {
                                    throw new IOException("Cоединение разорвано.");
                                }
                                totalRead += read;
                                totalBytesReceived += read;
                                fileBuffer.flip();
                                fileChannel.write(fileBuffer);
                            }
                        }

                        System.out.println("Файл " + fileName + " успешно сохранён.");
                    } catch (IOException e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void calculateSpeed() {
        while (true) {
            try {
                Thread.sleep(1000);
                long timeElapsed = System.currentTimeMillis() - startTime;
                if (timeElapsed > 0) {
                    double speed = totalBytesReceived / (timeElapsed / 1000.0) / (1024 * 1024);
                    System.out.printf("Скорость передачи: %.2f МБ/с%n", speed);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
