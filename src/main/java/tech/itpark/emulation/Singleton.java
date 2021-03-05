package tech.itpark.emulation;

// anti-pattern, объект создаёт сам себя
// 100 вариантов создания Singleton'а
public class Singleton {
  // eager
  private static final Singleton instance = new Singleton();

  private Singleton() {}

  public static Singleton getInstance() {
    return instance;
  }
}
