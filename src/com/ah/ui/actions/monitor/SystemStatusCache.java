package com.ah.ui.actions.monitor;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.ah.be.app.HmBeTopoUtil;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.AhAlarm;
import com.ah.util.Tracer;

/*
 * @author Chris Scheers
 */

public class SystemStatusCache {

	private static final Tracer log = new Tracer(SystemStatusCache.class
			.getSimpleName());

	public synchronized static SystemStatusCache getInstance() {
		if (instance == null) {
			instance = new SystemStatusCache();
		}

		return instance;
	}

	private static SystemStatusCache instance;

	private SystemStatusCache() {
	}

	private Map<Long, ElementCounts> byDomainCounts;

	public void init(List<HmDomain> hmDomains) {
		try {
			byDomainCounts = new HashMap<Long, ElementCounts>();
			for (HmDomain hmDomain : hmDomains) {
				// initialize IDP count for domain
				HmBeTopoUtil.getIdpScheduledExecutor().getIdpCacheUpdateTask().idpChanged(hmDomain.getId());
			}
			initCacheForAllDomain(hmDomains);
			
			for (HmDomain hmDomain : hmDomains) {
				ElementCounts elementCounts = byDomainCounts.get(hmDomain
						.getId());
				log.info("init", "In domain: " + hmDomain.getDomainName()
						+ ", # Alarms count: "
						+ elementCounts.criticalAlarmCount + ", "
						+ elementCounts.majorAlarmCount + ", "
						+ elementCounts.minorAlarmCount + ", "
						+ elementCounts.clearedAlarmCount
						+ ", # Rogue Ap count: "
						+ elementCounts.innetRogueCount + ", "
						+ elementCounts.onmapRogueCount + ", "
						+ elementCounts.strongRogueCount + ", "
						+ elementCounts.weakRogueCount
						+ ", # New HiveAp count: "
						+ elementCounts.newHiveAPCount
						+ ", # Rogue Client count: "
						+ elementCounts.rogueClientCount);
			}
		} catch (Exception e) {
			log.error("init", "Failed to load domains.", e);
		}
	}

	public void initCacheForAllDomain(List<HmDomain> hmDomains) {
		if((hmDomains == null) || hmDomains.size() <=0 )
			return;
		for(HmDomain domain: hmDomains) {
			ElementCounts elementCounts = new ElementCounts();
			/* initial values is zero */
			elementCounts.innetRogueCount = new AtomicLong(0);
			elementCounts.onmapRogueCount = new AtomicLong(0);
			elementCounts.strongRogueCount = new AtomicLong(0);
			elementCounts.weakRogueCount = new AtomicLong(0);
			// Rogue client count
			elementCounts.rogueClientCount = new AtomicLong(0);
			
			elementCounts.criticalAlarmCount = new AtomicLong(0);
			elementCounts.majorAlarmCount = new AtomicLong(0);
			elementCounts.minorAlarmCount = new AtomicLong(0);
			elementCounts.clearedAlarmCount = new AtomicLong(0);
			elementCounts.newHiveAPCount = new AtomicLong(0);
			byDomainCounts.put(domain.getId(), elementCounts);
		}
		StringBuffer sql = new StringBuffer();
		sql.append("select severity, owner ,count(*) as count from ah_alarm group by owner , severity");
		List<?> lsResult = QueryUtil.executeNativeQuery(sql.toString());
		for(Object obj: lsResult) {
			Object[] objs = (Object[])obj;
			Short severity = (Short)objs[0];
			Long owner = ((BigInteger)objs[1]).longValue();
			Long count = ((BigInteger)objs[2]).longValue();;
			
			ElementCounts elementCounts = null;
			elementCounts = byDomainCounts.get(owner);
			if( null == elementCounts) {
				continue;
			}
			switch (severity) {
			case AhAlarm.AH_SEVERITY_CRITICAL:
				elementCounts.criticalAlarmCount = new AtomicLong(count);
				break;
			case AhAlarm.AH_SEVERITY_MAJOR:
				elementCounts.majorAlarmCount = new AtomicLong(count);
				break;
			case AhAlarm.AH_SEVERITY_MINOR:
				elementCounts.minorAlarmCount = new AtomicLong(count);
				break;
			case AhAlarm.AH_SEVERITY_UNDETERMINED:
				elementCounts.clearedAlarmCount = new AtomicLong(count);
				break;
			default:
				break;
			}
			byDomainCounts.put(owner, elementCounts);
		}
		sql = new StringBuffer();
		sql.append("select owner ,count(*) as count from hive_ap where managestatus=");
		sql.append(HiveAp.STATUS_NEW).append(" group by owner");
		lsResult = QueryUtil.executeNativeQuery(sql.toString());
		for(Object obj: lsResult) {
			Object[] objs = (Object[])obj;
			Long owner = ((BigInteger)objs[0]).longValue();
			Long count = ((BigInteger)objs[1]).longValue();
			
			ElementCounts elementCounts = null;
			elementCounts = byDomainCounts.get(owner);
			if( null == elementCounts) {
				continue;
			}
			elementCounts.newHiveAPCount = new AtomicLong(count);

			byDomainCounts.put(owner, elementCounts);
		}
	}
	
	public void initCacheByDomain(Long hmDomainId) {
		if (null == hmDomainId || byDomainCounts == null) {
			return;
		}
		ElementCounts elementCounts = new ElementCounts();
		elementCounts.criticalAlarmCount = new AtomicLong(QueryUtil
				.findRowCount(AhAlarm.class, new FilterParams(
						"owner.id = :s1 and severity = :s2", new Object[] {
								hmDomainId, AhAlarm.AH_SEVERITY_CRITICAL})));
		elementCounts.majorAlarmCount = new AtomicLong(QueryUtil.findRowCount(
				AhAlarm.class, new FilterParams(
						"owner.id = :s1 and severity = :s2", new Object[] {
								hmDomainId, AhAlarm.AH_SEVERITY_MAJOR})));
		elementCounts.minorAlarmCount = new AtomicLong(QueryUtil.findRowCount(
				AhAlarm.class, new FilterParams(
						"owner.id = :s1 and severity = :s2", new Object[] {
								hmDomainId, AhAlarm.AH_SEVERITY_MINOR})));
		elementCounts.clearedAlarmCount = new AtomicLong(
				QueryUtil.findRowCount(AhAlarm.class, new FilterParams(
						"owner.id = :s1 and severity = :s2", new Object[] {
								hmDomainId, AhAlarm.AH_SEVERITY_UNDETERMINED})));
		// AP counts
		elementCounts.newHiveAPCount = new AtomicLong(QueryUtil.findRowCount(
				HiveAp.class, new FilterParams(
						"owner.id = :s1 AND manageStatus = :s2", new Object[] {
								hmDomainId,HiveAp.STATUS_NEW })));
		/* initial values is zero */
		elementCounts.innetRogueCount = new AtomicLong(0);
		elementCounts.onmapRogueCount = new AtomicLong(0);
		elementCounts.strongRogueCount = new AtomicLong(0);
		elementCounts.weakRogueCount = new AtomicLong(0);
		// Rogue client count
		elementCounts.rogueClientCount = new AtomicLong(0);

		byDomainCounts.put(hmDomainId, elementCounts);
	}

	public void removeCacheByDomain(Long hmDomainId) {
		if (byDomainCounts==null) {
			return;
		}
		if (null == hmDomainId) {
			return;
		}
		byDomainCounts.remove(hmDomainId);
	}

	public void incrementNewHiveApCount(Long domainId) {
		if (byDomainCounts==null) {
			return;
		}
		ElementCounts counts = byDomainCounts.get(domainId);
		if (counts == null) {
			return;
		}
		counts.newHiveAPCount.incrementAndGet();
	}

	public void incrementNewHiveApCount(long delta, Long domainId) {
		if (byDomainCounts==null) {
			return;
		}
		ElementCounts counts = byDomainCounts.get(domainId);
		if (counts == null) {
			return;
		}
		counts.newHiveAPCount.addAndGet(delta);
	}

	public synchronized void decrementNewHiveApCount(Long domainId) {
		if (byDomainCounts==null) {
			return;
		}
		ElementCounts counts = byDomainCounts.get(domainId);
		if (counts == null) {
			return;
		}
		if (counts.newHiveAPCount.get() > 0) {
			counts.newHiveAPCount.decrementAndGet();
		}
	}

	/*- Unused functions.
	 public void incrementRougeClientCount(Long domainId) {
	 ElementCounts counts = byDomainCounts.get(domainId);
	 if (counts == null) {
	 return;
	 }
	 counts.rogueClientCount.incrementAndGet();
	 }

	 public void incrementRougeClientCount(long delta, Long domainId) {
	 ElementCounts counts = byDomainCounts.get(domainId);
	 if (counts == null) {
	 return;
	 }
	 counts.rogueClientCount.addAndGet(delta);
	 }

	 public synchronized void decrementRougeClientCount(Long domainId) {
	 ElementCounts counts = byDomainCounts.get(domainId);
	 if (counts == null) {
	 return;
	 }
	 if (counts.rogueClientCount.get() > 0) {
	 counts.rogueClientCount.decrementAndGet();
	 }
	 }
	 */
	public void updateInnetRogueCount(long value, Long domainId) {
		if (byDomainCounts==null) {
			return;
		}
		ElementCounts counts = byDomainCounts.get(domainId);
		if (counts == null) {
			return;
		}
		counts.innetRogueCount.set(value);
	}

	public void updateOnmapRogueCount(long value, Long domainId) {
		if (byDomainCounts==null) {
			return;
		}
		ElementCounts counts = byDomainCounts.get(domainId);
		if (counts == null) {
			return;
		}
		counts.onmapRogueCount.set(value);
	}

	public void updateStrongRogueCount(long value, Long domainId) {
		if (byDomainCounts==null) {
			return;
		}
		ElementCounts counts = byDomainCounts.get(domainId);
		if (counts == null) {
			return;
		}
		counts.strongRogueCount.set(value);
	}

	public void updateWeakRogueCount(long value, Long domainId) {
		if (byDomainCounts==null) {
			return;
		}
		ElementCounts counts = byDomainCounts.get(domainId);
		if (counts == null) {
			return;
		}
		counts.weakRogueCount.set(value);
	}

	public void updateRogueClientCount(long value, Long domainId) {
		if (byDomainCounts==null) {
			return;
		}
		ElementCounts counts = byDomainCounts.get(domainId);
		if (counts == null) {
			return;
		}
		counts.rogueClientCount.set(value);
	}

	public void incrementAlarmCount(short severity, Long domainId) {
		if (byDomainCounts==null) {
			return;
		}
		ElementCounts counts = byDomainCounts.get(domainId);
		if (counts == null) {
			return;
		}
		if (severity == AhAlarm.AH_SEVERITY_CRITICAL) {
			counts.criticalAlarmCount.incrementAndGet();
		} else if (severity == AhAlarm.AH_SEVERITY_MAJOR) {
			counts.majorAlarmCount.incrementAndGet();
		} else if (severity == AhAlarm.AH_SEVERITY_MINOR) {
			counts.minorAlarmCount.incrementAndGet();
		} else if (severity == AhAlarm.AH_SEVERITY_UNDETERMINED) {
			counts.clearedAlarmCount.incrementAndGet();
		}
	}

	public void incrementAlarmCount(int count, short severity, Long domainId) {
		for (int i = 0; i < count; i++) {
			incrementAlarmCount(severity, domainId);
		}
	}

	public synchronized void decrementAlarmCount(short severity, Long domainId) {
		if (byDomainCounts==null) {
			return;
		}
		ElementCounts counts = byDomainCounts.get(domainId);
		if (counts == null) {
			return;
		}
		boolean decrementCleared = false;
		if (severity == AhAlarm.AH_SEVERITY_CRITICAL) {
			if (counts.criticalAlarmCount.get() == 0) {
				decrementCleared = true;
			} else {
				counts.criticalAlarmCount.decrementAndGet();
			}
		} else if (severity == AhAlarm.AH_SEVERITY_MAJOR) {
			if (counts.majorAlarmCount.get() == 0) {
				decrementCleared = true;
			} else {
				counts.majorAlarmCount.decrementAndGet();
			}
		} else if (severity == AhAlarm.AH_SEVERITY_MINOR) {
			if (counts.minorAlarmCount.get() == 0) {
				decrementCleared = true;
			} else {
				counts.minorAlarmCount.decrementAndGet();
			}
		}
		if (severity == AhAlarm.AH_SEVERITY_UNDETERMINED || decrementCleared) {
			if (counts.clearedAlarmCount.get() > 0) {
				counts.clearedAlarmCount.decrementAndGet();
			}
		}
	}

	public void decrementAlarmCount(int count, short severity, Long domainId) {
		for (int i = 0; i < count; i++) {
			decrementAlarmCount(severity, domainId);
		}
	}

	public long getAlarmCount(short severity, HmUser user) {
		if (byDomainCounts==null) {
			return 0;
		}
		Long domainId = QueryUtil.getDomainFilter(user);
		if (domainId != null) {
			ElementCounts counts = byDomainCounts.get(domainId);
			if (severity == AhAlarm.AH_SEVERITY_CRITICAL) {
				return counts.criticalAlarmCount.get();
			} else if (severity == AhAlarm.AH_SEVERITY_MAJOR) {
				return counts.majorAlarmCount.get();
			} else if (severity == AhAlarm.AH_SEVERITY_MINOR) {
				return counts.minorAlarmCount.get();
			} else if (severity == AhAlarm.AH_SEVERITY_UNDETERMINED) {
				return counts.clearedAlarmCount.get();
			}
		}
		long count = 0;
		for (ElementCounts counts : byDomainCounts.values()) {
			if (severity == AhAlarm.AH_SEVERITY_CRITICAL) {
				count += counts.criticalAlarmCount.get();
			} else if (severity == AhAlarm.AH_SEVERITY_MAJOR) {
				count += counts.majorAlarmCount.get();
			} else if (severity == AhAlarm.AH_SEVERITY_MINOR) {
				count += counts.minorAlarmCount.get();
			} else if (severity == AhAlarm.AH_SEVERITY_UNDETERMINED) {
				count += counts.clearedAlarmCount.get();
			}
		}
		return count;
	}

	public long getNewHiveAPCount(HmUser user) {
		if (byDomainCounts==null) {
			return 0;
		}
		Long domainId = QueryUtil.getDomainFilter(user);
		if (domainId != null) {
			ElementCounts counts = byDomainCounts.get(domainId);
			return counts.newHiveAPCount.get();
		}
		long count = 0;
		for (ElementCounts counts : byDomainCounts.values()) {
			count += counts.newHiveAPCount.get();
		}
		return count;
	}

	public long getRogueClientCount(HmUser user) {
		if (byDomainCounts==null) {
			return 0;
		}
		Long domainId = QueryUtil.getDomainFilter(user);
		if (domainId != null) {
			ElementCounts counts = byDomainCounts.get(domainId);
			return counts.rogueClientCount.get();
		}
		long count = 0;
		for (ElementCounts counts : byDomainCounts.values()) {
			count += counts.rogueClientCount.get();
		}
		return count;
	}

	public long getInnetRogueCount(HmUser user) {
		if (byDomainCounts==null) {
			return 0;
		}
		Long domainId = QueryUtil.getDomainFilter(user);
		if (domainId != null) {
			ElementCounts counts = byDomainCounts.get(domainId);
			return counts.innetRogueCount.get();
		}
		long count = 0;
		for (ElementCounts counts : byDomainCounts.values()) {
			count += counts.innetRogueCount.get();
		}
		return count;

	}

	public long getOnmapRogueCount(HmUser user) {
		if (byDomainCounts==null) {
			return 0;
		}
		Long domainId = QueryUtil.getDomainFilter(user);
		if (domainId != null) {
			ElementCounts counts = byDomainCounts.get(domainId);
			return counts.onmapRogueCount.get();
		}
		long count = 0;
		for (ElementCounts counts : byDomainCounts.values()) {
			count += counts.onmapRogueCount.get();
		}
		return count;

	}

	public long getStrongRogueCount(HmUser user) {
		if (byDomainCounts==null) {
			return 0;
		}
		Long domainId = QueryUtil.getDomainFilter(user);
		if (domainId != null) {
			ElementCounts counts = byDomainCounts.get(domainId);
			return counts.strongRogueCount.get();
		}
		long count = 0;
		for (ElementCounts counts : byDomainCounts.values()) {
			count += counts.strongRogueCount.get();
		}
		return count;

	}

	public long getWeakRogueCount(HmUser user) {
		if (byDomainCounts==null) {
			return 0;
		}
		Long domainId = QueryUtil.getDomainFilter(user);
		if (domainId != null) {
			ElementCounts counts = byDomainCounts.get(domainId);
			return counts.weakRogueCount.get();
		}
		long count = 0;
		for (ElementCounts counts : byDomainCounts.values()) {
			count += counts.weakRogueCount.get();
		}
		return count;

	}

	protected class ElementCounts {
		AtomicLong criticalAlarmCount, majorAlarmCount, minorAlarmCount,
				clearedAlarmCount, newHiveAPCount, innetRogueCount,
				onmapRogueCount, strongRogueCount, weakRogueCount,
				rogueClientCount;
	}
}