//import jdk.internal.opt.CommandLine.Tokenizer;

record Llama(Configuration configuration, Tokenizer tokenizer, Weights weights) {

  public State createNewState(int batchSize) {
  }
}
