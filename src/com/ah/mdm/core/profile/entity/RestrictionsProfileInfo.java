package com.ah.mdm.core.profile.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_profile_restrictions")
public class RestrictionsProfileInfo extends AbstractProfileInfo
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long	restrictionsId;

	private boolean	allowAddingGameCenterFriends;
	private boolean	allowAppInstallation;
	private boolean	allowAssistant;
	private boolean	allowAssistantWhileLocked;
	private boolean	allowCamera;
	private boolean	allowExplicitContent;
	private boolean	allowScreenShot;
	private boolean	allowYouTube;
	private boolean	allowiTunes;
	private boolean	forceITunesStorePasswordEntry;
	private boolean	allowSafari;
	private boolean	allowUntrustedTLSPrompt;
	private boolean	allowCloudBackup;
	private boolean	allowCloudDocumentSync;
	private boolean	allowPhotoStream;
	private boolean	allowGlobalBackgroundFetchWhenRoaming;
	private boolean	allowInAppPurchases;
	private boolean	allowMultiplayerGaming;
	private boolean	allowVideoConferencing;
	private boolean	allowVoiceDialing;
	private boolean	forceEncryptedBackup;
	private boolean	safariAllowAutoFill;
	private boolean	safariAllowJavaScript;
	private boolean	safariAllowPopups;
	private boolean	safariForceFraudWarning;
	private boolean	allowGameCenter;
	private boolean	allowBookstore;
	private boolean	allowBookstoreErotica;
	private boolean	allowPassbookWhileLocked;
	private boolean	allowSharedStream;
	private boolean	allowUIConfigurationProfileInstallation;

	private boolean	allowDiagnosticSubmission;
	private int		ratingApps;
	private int		ratingMovies;
	private String	ratingRegion;
	private int		ratingTVShows;
	private int		safariAcceptCookies;

	public long getRestrictionsId()
	{
		return restrictionsId;
	}

	public void setRestrictionsId(long restrictionsId)
	{
		this.restrictionsId = restrictionsId;
	}

	public RestrictionsProfileInfo()
	{
		super(ProfilePayloadType.PAYLOAD_TYPE_RESTRICTIONS);
	}

	public boolean isAllowAddingGameCenterFriends()
	{
		return allowAddingGameCenterFriends;
	}

	public void setAllowAddingGameCenterFriends(boolean allowAddingGameCenterFriends)
	{
		this.allowAddingGameCenterFriends = allowAddingGameCenterFriends;
	}

	public boolean isAllowAppInstallation()
	{
		return allowAppInstallation;
	}

	public void setAllowAppInstallation(boolean allowAppInstallation)
	{
		this.allowAppInstallation = allowAppInstallation;
	}

	public boolean isAllowAssistant()
	{
		return allowAssistant;
	}

	public void setAllowAssistant(boolean allowAssistant)
	{
		this.allowAssistant = allowAssistant;
	}

	public boolean isAllowAssistantWhileLocked()
	{
		return allowAssistantWhileLocked;
	}

	public void setAllowAssistantWhileLocked(boolean allowAssistantWhileLocked)
	{
		this.allowAssistantWhileLocked = allowAssistantWhileLocked;
	}

	public boolean isAllowCamera()
	{
		return allowCamera;
	}

	public void setAllowCamera(boolean allowCamera)
	{
		this.allowCamera = allowCamera;
	}

	public boolean isAllowExplicitContent()
	{
		return allowExplicitContent;
	}

	public void setAllowExplicitContent(boolean allowExplicitContent)
	{
		this.allowExplicitContent = allowExplicitContent;
	}

	public boolean isAllowScreenShot()
	{
		return allowScreenShot;
	}

	public void setAllowScreenShot(boolean allowScreenShot)
	{
		this.allowScreenShot = allowScreenShot;
	}

	public boolean isAllowYouTube()
	{
		return allowYouTube;
	}

	public void setAllowYouTube(boolean allowYouTube)
	{
		this.allowYouTube = allowYouTube;
	}

	public boolean isAllowiTunes()
	{
		return allowiTunes;
	}

	public void setAllowiTunes(boolean allowiTunes)
	{
		this.allowiTunes = allowiTunes;
	}

	public boolean isForceITunesStorePasswordEntry()
	{
		return forceITunesStorePasswordEntry;
	}

	public void setForceITunesStorePasswordEntry(boolean forceITunesStorePasswordEntry)
	{
		this.forceITunesStorePasswordEntry = forceITunesStorePasswordEntry;
	}

	public boolean isAllowSafari()
	{
		return allowSafari;
	}

	public void setAllowSafari(boolean allowSafari)
	{
		this.allowSafari = allowSafari;
	}

	public boolean isAllowUntrustedTLSPrompt()
	{
		return allowUntrustedTLSPrompt;
	}

	public void setAllowUntrustedTLSPrompt(boolean allowUntrustedTLSPrompt)
	{
		this.allowUntrustedTLSPrompt = allowUntrustedTLSPrompt;
	}

	public boolean isAllowCloudBackup()
	{
		return allowCloudBackup;
	}

	public void setAllowCloudBackup(boolean allowCloudBackup)
	{
		this.allowCloudBackup = allowCloudBackup;
	}

	public boolean isAllowCloudDocumentSync()
	{
		return allowCloudDocumentSync;
	}

	public void setAllowCloudDocumentSync(boolean allowCloudDocumentSync)
	{
		this.allowCloudDocumentSync = allowCloudDocumentSync;
	}

	public boolean isAllowPhotoStream()
	{
		return allowPhotoStream;
	}

	public void setAllowPhotoStream(boolean allowPhotoStream)
	{
		this.allowPhotoStream = allowPhotoStream;
	}

	public boolean isAllowGlobalBackgroundFetchWhenRoaming()
	{
		return allowGlobalBackgroundFetchWhenRoaming;
	}

	public void setAllowGlobalBackgroundFetchWhenRoaming(boolean allowGlobalBackgroundFetchWhenRoaming)
	{
		this.allowGlobalBackgroundFetchWhenRoaming = allowGlobalBackgroundFetchWhenRoaming;
	}

	public boolean isAllowInAppPurchases()
	{
		return allowInAppPurchases;
	}

	public void setAllowInAppPurchases(boolean allowInAppPurchases)
	{
		this.allowInAppPurchases = allowInAppPurchases;
	}

	public boolean isAllowMultiplayerGaming()
	{
		return allowMultiplayerGaming;
	}

	public void setAllowMultiplayerGaming(boolean allowMultiplayerGaming)
	{
		this.allowMultiplayerGaming = allowMultiplayerGaming;
	}

	public boolean isAllowVideoConferencing()
	{
		return allowVideoConferencing;
	}

	public void setAllowVideoConferencing(boolean allowVideoConferencing)
	{
		this.allowVideoConferencing = allowVideoConferencing;
	}

	public boolean isAllowVoiceDialing()
	{
		return allowVoiceDialing;
	}

	public void setAllowVoiceDialing(boolean allowVoiceDialing)
	{
		this.allowVoiceDialing = allowVoiceDialing;
	}

	public boolean isForceEncryptedBackup()
	{
		return forceEncryptedBackup;
	}

	public void setForceEncryptedBackup(boolean forceEncryptedBackup)
	{
		this.forceEncryptedBackup = forceEncryptedBackup;
	}

	public boolean isSafariAllowAutoFill()
	{
		return safariAllowAutoFill;
	}

	public void setSafariAllowAutoFill(boolean safariAllowAutoFill)
	{
		this.safariAllowAutoFill = safariAllowAutoFill;
	}

	public boolean isSafariAllowJavaScript()
	{
		return safariAllowJavaScript;
	}

	public void setSafariAllowJavaScript(boolean safariAllowJavaScript)
	{
		this.safariAllowJavaScript = safariAllowJavaScript;
	}

	public boolean isSafariAllowPopups()
	{
		return safariAllowPopups;
	}

	public void setSafariAllowPopups(boolean safariAllowPopups)
	{
		this.safariAllowPopups = safariAllowPopups;
	}

	public boolean isSafariForceFraudWarning()
	{
		return safariForceFraudWarning;
	}

	public void setSafariForceFraudWarning(boolean safariForceFraudWarning)
	{
		this.safariForceFraudWarning = safariForceFraudWarning;
	}

	public boolean isAllowGameCenter()
	{
		return allowGameCenter;
	}

	public void setAllowGameCenter(boolean allowGameCenter)
	{
		this.allowGameCenter = allowGameCenter;
	}

	public boolean isAllowBookstore()
	{
		return allowBookstore;
	}

	public void setAllowBookstore(boolean allowBookstore)
	{
		this.allowBookstore = allowBookstore;
	}

	public boolean isAllowBookstoreErotica()
	{
		return allowBookstoreErotica;
	}

	public void setAllowBookstoreErotica(boolean allowBookstoreErotica)
	{
		this.allowBookstoreErotica = allowBookstoreErotica;
	}

	public boolean isAllowPassbookWhileLocked()
	{
		return allowPassbookWhileLocked;
	}

	public void setAllowPassbookWhileLocked(boolean allowPassbookWhileLocked)
	{
		this.allowPassbookWhileLocked = allowPassbookWhileLocked;
	}

	public boolean isAllowSharedStream()
	{
		return allowSharedStream;
	}

	public void setAllowSharedStream(boolean allowSharedStream)
	{
		this.allowSharedStream = allowSharedStream;
	}

	public boolean isAllowUIConfigurationProfileInstallation()
	{
		return allowUIConfigurationProfileInstallation;
	}

	public void setAllowUIConfigurationProfileInstallation(boolean allowUIConfigurationProfileInstallation)
	{
		this.allowUIConfigurationProfileInstallation = allowUIConfigurationProfileInstallation;
	}

	public boolean isAllowDiagnosticSubmission()
	{
		return allowDiagnosticSubmission;
	}

	public void setAllowDiagnosticSubmission(boolean allowDiagnosticSubmission)
	{
		this.allowDiagnosticSubmission = allowDiagnosticSubmission;
	}

	public int getRatingApps()
	{
		return ratingApps;
	}

	public void setRatingApps(int ratingApps)
	{
		this.ratingApps = ratingApps;
	}

	public int getRatingMovies()
	{
		return ratingMovies;
	}

	public void setRatingMovies(int ratingMovies)
	{
		this.ratingMovies = ratingMovies;
	}

	public String getRatingRegion()
	{
		return ratingRegion;
	}

	public void setRatingRegion(String ratingRegion)
	{
		this.ratingRegion = ratingRegion;
	}

	public int getRatingTVShows()
	{
		return ratingTVShows;
	}

	public void setRatingTVShows(int ratingTVShows)
	{
		this.ratingTVShows = ratingTVShows;
	}

	public int getSafariAcceptCookies()
	{
		return safariAcceptCookies;
	}

	public void setSafariAcceptCookies(int safariAcceptCookies)
	{
		this.safariAcceptCookies = safariAcceptCookies;
	}
}
