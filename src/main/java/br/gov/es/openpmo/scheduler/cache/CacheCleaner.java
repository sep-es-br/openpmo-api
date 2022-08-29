package br.gov.es.openpmo.scheduler.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CacheCleaner {

  private static final Logger LOGGER = LoggerFactory.getLogger(CacheCleaner.class);
  private final CacheManager cacheManager;

  @Autowired
  public CacheCleaner(final CacheManager cacheManager) {this.cacheManager = cacheManager;}

  public void clearAllCache() {
    this.cacheManager.getCacheNames()
      .forEach(this::clearCache);
  }

  private void clearCache(final String cacheName) {
    final Cache cache = this.cacheManager.getCache(cacheName);
    if(Objects.isNull(cache)) return;
    LOGGER.info("Limpando cache {}", cacheName);
    cache.clear();
  }


}
