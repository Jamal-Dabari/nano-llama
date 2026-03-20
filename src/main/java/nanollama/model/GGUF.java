package main.java.nanollama.model;

import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class GGUF implements Closeable {
  private int magic;
  private int version;
  private long tensorCount; // uint_64
  private long metadata_kv_count; // uint_64
  private Map<String, Object> metadata;
  private Arena arena = Arena.ofConfined();
  private MemorySegment segment;
  private long offset;

  public void load(RandomAccessFile file) throws IOException {
    offset = 0;
    try (FileChannel channel = file.getChannel()) {
      long channelSize = channel.size();
      segment = channel.map(FileChannel.MapMode.READ_ONLY, 0, channelSize, arena);

      magic = segment.get(ValueLayout.JAVA_INT, offset);
      readAndVerifyMagicNumber(magic);
      offset += 4;
      version = segment.get(ValueLayout.JAVA_INT, offset);
      readVersion(version);
      offset += 4;
      tensorCount = segment.get(ValueLayout.JAVA_LONG, offset);
      offset += 8;
      metadata_kv_count = segment.get(ValueLayout.JAVA_LONG, offset);
      offset += 8;
      parseMetadata();

      file.close();

    } catch (Exception e) {
      throw new IOException("Failed to read file", e);
    }
  }

  private void readAndVerifyMagicNumber(int magic) throws IOException {
    if (magic != 0x46554747) {
      throw new IOException("Invalid GGUF magic number");
    }

  }

  private void readVersion(int version) throws IOException {
    if (version > 3 || version < 1) {
      throw new IOException("Version not supported");
    }
  }

  private void parseMetadata() {
    metadata = new HashMap<>();
    for (long i = 0; i < metadata_kv_count; i++) {
      String key = readString();
      Object value = readValue();
      metadata.put(key, value);

    }
  }

  private String readString() {
    long length = segment.get(ValueLayout.JAVA_LONG, offset);
    offset += 8;
    byte[] bytes = new byte[(int) length];
    MemorySegment.copy(segment, ValueLayout.JAVA_BYTE, offset, bytes, 0, (int) length);
    String str = new String(bytes, StandardCharsets.UTF_8);
    offset += length;
    return str;
  }

  private Object readValue() {
    int typeInt = segment.get(ValueLayout.JAVA_INT, offset);
    offset += 4;
    GGUFValueType type = GGUFValueType.fromInt(typeInt);
    Object obj = null;

    switch (type) {
      case UINT8:
        obj = segment.get(ValueLayout.JAVA_BYTE, offset);
        offset += 1;
        break;
      case INT8:
        obj = segment.get(ValueLayout.JAVA_BYTE, offset);
        offset += 1;
        break;
      case UINT16:
        obj = segment.get(ValueLayout.JAVA_SHORT, offset);
        offset += 2;
        break;
      case INT16:
        obj = segment.get(ValueLayout.JAVA_SHORT, offset);
        offset += 2;
        break;
      case UINT32:
        obj = segment.get(ValueLayout.JAVA_INT, offset);
        offset += 4;
        break;
      case INT32:
        obj = segment.get(ValueLayout.JAVA_INT, offset);
        offset += 4;
        break;
      case UINT64:
        obj = segment.get(ValueLayout.JAVA_LONG, offset);
        offset += 8;
        break;
      case INT64:
        obj = segment.get(ValueLayout.JAVA_LONG, offset);
        offset += 8;
        break;
      case FLOAT32:
        obj = segment.get(ValueLayout.JAVA_FLOAT, offset);
        offset += 4;
        break;
      case FLOAT64:
        obj = segment.get(ValueLayout.JAVA_DOUBLE, offset);
        offset += 8;
        break;
      case BOOL:
        obj = segment.get(ValueLayout.JAVA_BOOLEAN, offset);
        offset += 1;
        break;
      case STRING:
        obj = readString();
        break;
      case ARRAY:
        int elementType = segment.get(ValueLayout.JAVA_INT, offset);
        offset += 4;
        long count = segment.get(ValueLayout.JAVA_LONG, offset);
        offset += 8;
        Object[] arr = new Object[(int) count];
        for (int i = 0; i < count; i++) {
          arr[i] = readValue();
          obj = arr;

        }
      default:
        break;
    }

    return obj;

  }

  private void parseTensorInfo() {
  }

  private void calcTensorDataOffset() {
  }

  @Override
  public void close() {
    arena.close();
  }

}
