package com.sirolf2009.lsw2017.server.net;

import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.bucket.BucketManager;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.xtext.xbase.lib.InputOutput;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observables.BlockingObservable;

@SuppressWarnings("all")
public class Database implements Closeable {
  private final static Logger log = LogManager.getLogger();
  
  private final CouchbaseCluster cluster;
  
  private final Bucket bucket;
  
  public Database() {
    CouchbaseCluster _create = CouchbaseCluster.create("localhost");
    this.cluster = _create;
    Bucket _openBucket = this.cluster.openBucket("lsw-2017", "iwanttodie123");
    this.bucket = _openBucket;
    BucketManager _bucketManager = this.bucket.bucketManager();
    _bucketManager.createN1qlPrimaryIndex(true, false);
    Database.log.info("Database connection initialized");
  }
  
  public void awardPoints(final String teamName, final int points, final long time) {
    AsyncBucket _async = this.bucket.async();
    Observable<JsonDocument> _get = _async.get(teamName);
    final Func1<JsonDocument, JsonDocument> _function = (JsonDocument it) -> {
      JsonObject _content = it.content();
      JsonObject _content_1 = it.content();
      Integer _int = _content_1.getInt("points");
      int _plus = ((_int).intValue() + points);
      _content.put("points", _plus);
      JsonObject _content_2 = it.content();
      _content_2.put("lastCheckedIn", time);
      return it;
    };
    Observable<JsonDocument> _map = _get.<JsonDocument>map(_function);
    final Func1<JsonDocument, JsonDocument> _function_1 = (JsonDocument it) -> {
      return this.bucket.<JsonDocument>replace(it, 1, TimeUnit.SECONDS);
    };
    Observable<JsonDocument> _map_1 = _map.<JsonDocument>map(_function_1);
    BlockingObservable<JsonDocument> _blocking = _map_1.toBlocking();
    final Action1<JsonDocument> _function_2 = (JsonDocument it) -> {
      InputOutput.<JsonDocument>println(it);
    };
    final Action1<Throwable> _function_3 = (Throwable it) -> {
      Database.log.error("Failed to award points", it);
    };
    _blocking.subscribe(_function_2, _function_3);
  }
  
  public Integer getPoints(final String teamName) {
    JsonDocument _team = this.getTeam(teamName);
    return this.getPoints(_team);
  }
  
  public JsonDocument createNewTeam(final String teamName) {
    JsonDocument _xblockexpression = null;
    {
      JsonObject _create = JsonObject.create();
      JsonObject _put = _create.put("points", 0);
      final JsonObject team = _put.put("lastCheckedIn", 0);
      JsonDocument _create_1 = JsonDocument.create(teamName, team);
      _xblockexpression = this.bucket.<JsonDocument>upsert(_create_1);
    }
    return _xblockexpression;
  }
  
  public boolean doesTeamExist(final String teamName) {
    return this.bucket.exists(teamName);
  }
  
  public JsonDocument getTeam(final String teamName) {
    return this.bucket.get(teamName);
  }
  
  public Integer getPoints(final JsonDocument document) {
    JsonObject _content = document.content();
    Object _get = _content.get("points");
    return ((Integer) _get);
  }
  
  @Override
  public void close() throws IOException {
    this.bucket.close();
    this.cluster.disconnect();
  }
}
