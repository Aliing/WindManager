package com.ah.be.log.clean.task;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

import com.ah.be.common.AhDirTools;
import com.ah.be.log.clean.LogCleanTaskable;
import com.ah.be.os.FileManager;
import com.ah.be.ts.hiveap.monitor.client.impl.ClientMonitorMgmtImpl;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.util.Tracer;

public class ClientMonitorLogCleanTask implements LogCleanTaskable {

	private static final Tracer log = new Tracer(ClientMonitorLogCleanTask.class.getSimpleName());

	private static long LOG_CLEAN_INTERVAL = 7 * 24 * 60 * 60 * 1000L;

	@Override
	public boolean clean() {
		long currTime = System.currentTimeMillis();

		// Delete log archives .
		deleteLogArchives(currTime);

		// Delete domain based logs.
		deleteDomainLogs(currTime);

		return true;
	}

	@Override
	public String getTaskName() {
		return "Client Monitor Log Clean Task";
	}

	private void deleteLogArchives(long currTime) {
		log.info("deleteLogArchives", "Executing client monitor log archives deletion task.");
		FileManager fileUtil = FileManager.getInstance();
		String cmTempDirPath = AhDirTools.getCmTempFileDir();
		File cmTempDir = new File(cmTempDirPath);
		File[] subFiles = cmTempDir.listFiles(new LogArchiveNameFilter());

		for (File subFile : subFiles) {
			if (currTime - subFile.lastModified() >= LOG_CLEAN_INTERVAL) {
				if (subFile.isFile()) {
					boolean deleted = subFile.delete();
					log.debug("deleteLogArchives", "File " + subFile.getPath() + (deleted ? " was deleted." : " wasn't deleted."));
				} else if (subFile.isDirectory()) {
					try {
						fileUtil.deleteDirectory(subFile.getPath());
						log.debug("deleteLogArchives", "Directory " + subFile.getPath() + " was deleted.");
					} catch (Exception e) {
						log.error("deleteLogArchives", "Error occurred while deleting directory " + subFile.getPath());
					}
				}
			}
		}
	}

	private void deleteDomainLogs(long currTime) {
		log.info("deleteDomainLogs", "Executing domain based client monitor logs deletion task.");
		List<?> domainNames = QueryUtil.executeQuery("select domainName from " + HmDomain.class.getSimpleName(), new SortParams("id"), new FilterParams("lower(domainName) != :s1", new Object[] { HmDomain.GLOBAL_DOMAIN.toLowerCase() }));

		for (Object obj : domainNames) {
			String domainName = (String) obj;
		   	deleteDomainLogs(currTime, domainName);
		}
	}

	private void deleteDomainLogs(long currTime, String domainName) {
		log.info("deleteDomainLogs", "Deleting client monitor logs for vHM " + domainName);
		String cmLogDirPath = AhDirTools.getCmDir(domainName);
		File cmLogDir = new File(cmLogDirPath);
		File[] logFiles = cmLogDir.listFiles(new LogNameFilter());

		for (File logFile : logFiles) {
			if (currTime - logFile.lastModified() >= LOG_CLEAN_INTERVAL) {
				if (logFile.isFile()) {
					boolean deleted = logFile.delete();
					log.debug("deleteDomainLogs", "File " + logFile.getPath() + (deleted ? " was deleted." : " wasn't deleted."));
				}
			}
		}
	}

	class LogArchiveNameFilter implements FilenameFilter {
		@Override
		public boolean accept(File dir, String name) {
			return name.startsWith(ClientMonitorMgmtImpl.CLIENT_LOG_ARCHIVE_DIR_NAME_PREFIX);
		}
	}

	class LogNameFilter implements FilenameFilter {
		@Override
		public boolean accept(File dir, String name) {
			return name.startsWith(ClientMonitorMgmtImpl.CLIENT_LOG_NAME_PREFIX) && name.length() >= ClientMonitorMgmtImpl.CLIENT_LOG_NAME_PREFIX.length() + 12 + ClientMonitorMgmtImpl.CLIENT_LOG_NAME_SUFFIX.length();
		}
	}

}