import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class FrameParser {
    public static class FrameData {
        public String fileName;
        public int fileLength;

        public FrameData(String fileName, int fileLength) {
            this.fileName = fileName;
            this.fileLength = fileLength;
        }
    }

    public static FrameData parseFrame(ByteBuffer buffer) throws IOException {
        String frame = StandardCharsets.UTF_8.decode(buffer).toString();
        int fileNameEndIndex = frame.indexOf("\r\n");
        if (fileNameEndIndex == -1) {//нет разделителя
            throw new IOException("Ошибка: неверный формат");
        }
        String fileName = frame.substring(0, fileNameEndIndex);

        int lengthStartIndex = fileNameEndIndex + 2;
        int lengthEndIndex = frame.indexOf("\r\n", lengthStartIndex);
        if (lengthEndIndex == -1) {
            throw new IOException("Ошибка: не найдена длина файла");
        }
        int fileLength = Integer.parseInt(frame.substring(lengthStartIndex, lengthEndIndex));

        return new FrameData(fileName, fileLength);
    }
}
