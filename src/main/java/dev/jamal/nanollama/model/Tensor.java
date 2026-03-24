package dev.jamal.nanollama.model;

public class Tensor {
  String name;
  int nDimensions;
  long[] dimensions;
  GGMLTYPE type;
  long offset;

  public Tensor(String name, int nDimensions, long[] dimensions, GGMLTYPE type, long offset) {

  }

  public Tensor() {
  }
}
