package com.sirolf2009.lsw2017.common;

import com.sirolf2009.lsw2017.common.model.PointRequest;

@SuppressWarnings("all")
public interface ServerProxy {
  public abstract void requestPoints(final PointRequest request);
}
