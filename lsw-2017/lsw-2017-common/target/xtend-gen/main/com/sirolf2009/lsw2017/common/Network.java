package com.sirolf2009.lsw2017.common;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.sirolf2009.lsw2017.common.ServerProxy;
import com.sirolf2009.lsw2017.common.model.PointRequest;

@SuppressWarnings("all")
public class Network {
  public static void register(final EndPoint endpoint) {
    final Kryo kryo = endpoint.getKryo();
    kryo.register(PointRequest.class);
    ObjectSpace.registerClasses(kryo);
    kryo.register(ServerProxy.class);
  }
}
