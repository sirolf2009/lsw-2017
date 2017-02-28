package com.sirolf2009.lsw2017.server.net;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.sirolf2009.lsw2017.common.ServerProxy;
import com.sirolf2009.lsw2017.common.model.PointRequest;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;
import rx.subjects.PublishSubject;

@SuppressWarnings("all")
public class Facade extends Connection implements ServerProxy {
  @Accessors
  private final PublishSubject<PointRequest> subject;
  
  public Facade() {
    ObjectSpace _objectSpace = new ObjectSpace(this);
    _objectSpace.register(1, this);
    PublishSubject<PointRequest> _create = PublishSubject.<PointRequest>create();
    this.subject = _create;
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
