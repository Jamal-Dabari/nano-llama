package dev.jamal.nanollama.model;

enum GGUFValueType {
  UINT8(0),
  INT8(1),
  UINT16(2),
  INT16(3),
  UINT32(4),
  INT32(5),
  FLOAT32(6),
  BOOL(7),
  STRING(8),
  ARRAY(9),
  UINT64(10),
  INT64(11),
  FLOAT64(12);

  final int value;

  GGUFValueType(int value) {
    this.value = value;
  }

  static GGUFValueType fromInt(int value) {
    for (GGUFValueType type : values()) {
      if (type.value == value)
        return type;
    }
    throw new IllegalArgumentException("Unkown GGUF value type: " + value);
  }

}
