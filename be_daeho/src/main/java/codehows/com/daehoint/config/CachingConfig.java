package codehows.com.daehoint.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <b>CachingConfig 클래스</b><br>
 * 애플리케이션 전반에서 캐시를 관리하는 설정 클래스입니다.<br>
 * Spring Cache와 Caffeine 캐시 라이브러리를 사용하여 캐시를 설정하고 관리합니다.<br><br>
 *
 * <b>주요 기능:</b><br>
 * - `CacheManager`를 사용하여 캐시 전략을 설정.<br>
 * - 캐시 생명주기(TTL)와 최대 항목 수를 설정하여 성능 최적화.<br>
 * - 캐시를 단기 캐시와 장기 캐시로 나누어 각기 다른 데이터 요구에 맞게 설계.<br><br>
 *
 * <b>구성 요소:</b><br>
 * 1. `shortLivedCacheManager()`:<br>
 * - 2분의 TTL(Time-to-Live)과 최대 1000개의 항목을 가지는 단기 캐시를 생성.<br>
 * - 캐시 이름: `main_workerRetentions`, `main_processProductionReport`, `main_estimatedExpenses`, `process_productionPerformanceStatus`.<br>
 * 2. `longLivedCacheManager()`:<br>
 * - 60분의 TTL과 최대 1000개의 항목을 가지는 장기 캐시를 생성.<br>
 * - 캐시 이름: `report_mainProductionReport`, `processReportResponse`, `processStockResponse`, `lossData`, `flowData`, `performanceData`, `purchaseData`.<br><br>
 *
 * <b>주요 어노테이션:</b><br>
 * - `@EnableCaching`: 스프링 캐싱 기능을 활성화.<br>
 * - `@Configuration`: 스프링 설정 클래스를 나타냄.<br>
 * - `@Primary`: 기본 캐시 관리자로 설정.<br><br>
 *
 * <b>캐시 설정:</b><br>
 * - 단기 캐시:<br>
 * - TTL: 2분.<br>
 * - 최대 항목: 1000개.<br>
 * - 사용 사례: 빈번히 변경되거나 단기적으로 필요한 데이터.<br>
 * - 장기 캐시:<br>
 * - TTL: 60분.<br>
 * - 최대 항목: 1000개.<br>
 * - 사용 사례: 상대적으로 변경이 적고 장기적으로 유지되는 데이터.<br>
 */
@EnableCaching
@Configuration
public class CachingConfig {

    @Bean("shortLivedCacheManager")
    @Primary
    public CacheManager shortLivedCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("shortLivedCacheManager");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(2, TimeUnit.MINUTES)  // TTL 설정: 5분
                .maximumSize(1000));                   // 최대 캐시 항목 수: 1000
        cacheManager.setCacheNames(List.of("main_workerRetentions", "main_processProductionReport", "main_estimatedExpenses", "process_productionPerformanceStatus", "processReportResponse", "flux_soldering"));
        return cacheManager;
    }

    @Bean("longLivedCacheManager")
    public CacheManager longLivedCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("longLivedCache");
        cacheManager
                .setCaffeine(Caffeine.newBuilder()
                        .expireAfterWrite(60, TimeUnit.MINUTES)  // TTL: 60분
                        .maximumSize(1000)); //최대 항목 1000개
        cacheManager.setCacheNames(List.of("report_mainProductionReport", "processReportResponse", "processStockResponse", "lossData", "flowData", "performanceData", "purchaseData"));
        return cacheManager;
    }
}
