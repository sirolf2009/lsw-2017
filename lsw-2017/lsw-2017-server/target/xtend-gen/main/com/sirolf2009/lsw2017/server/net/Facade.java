package com.sirolf2009.lsw2017.server.net;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.sirolf2009.lsw2017.common.ServerProxy;
import com.sirolf2009.lsw2017.common.model.PointRequest;
import io.reactivex.subjects.PublishSubject;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;

@SuppressWarnings("all")
public class Facade extends Connection implements ServerProxy {
  @Accessors
  private final PublishSubject<PointRequest> subject;
  
  public Facade() {
    new ObjectSpace(this).register(1, this);
    this.subject = PublishSubject.<PointRequest>create();
  }
  
  @Override
  public void requestPoints(final PointRequest request) {
    this.subject.onNext(request);
  }
  
  @Pure
  public PublishSubject<PointRequest> getSubject() {
    return this.subject;
  }
}
