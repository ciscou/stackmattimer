package com.rubikaz.cisco.tuenti;

public interface TuentiRequestListener {
  public void onComplete();
  public void onError(TuentiException te);
}
