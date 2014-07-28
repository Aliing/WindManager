package com.ah.ui.actions.monitor.enrolledclients.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@Embeddable
@XStreamAlias("content")
public class RestrictionInfoForUI implements Serializable{

	private static final long serialVersionUID = 7365231209298321772L;
	
	@Transient
	@XStreamAlias("AllowSimple")
	private String allowSimple;
	
	@Transient
	@XStreamAlias("ForcePIN")
	private String forcePin;
	
	@Transient
	@XStreamAlias("MaxFailedAttempts")
	private String maxFailedAttempts;

	@Transient
	@XStreamAlias("MaxInactivity")
	private String maxInactivity;
	
	@Transient
	@XStreamAlias("MaxPINAgeInDays")
	private String maxPinAgeInDays;
	
	@Transient
	@XStreamAlias("MinComplexChars")
	private String minComplexChars;
	
	@Transient
	@XStreamAlias("MinLength")
	private String minLength;
	
	@Transient
	@XStreamAlias("RequireAlphanumeric")
	private String requireAlphanumeric;
	
	@Transient
	@XStreamAlias("PinHistory")
	private String pinHistory;
	
	@Transient
	@XStreamAlias("ManualFetchingWhenRoaming")
	private String manualFetchingWhenRoaming;
	
	@Transient
	@XStreamAlias("MaxGracePeriod")
	private String maxGracePeriod;
	
	@Transient
	@XStreamAlias("AllowAddingGameCenterFriends")
	private String allowAddingGameCenterFriends;
	
	@Transient
	@XStreamAlias("AllowAppInstallation")
	private String allowAppInstallation;
	
	@Transient
	@XStreamAlias("AllowAssistant")
	private String allowAssistant;
	
	@Transient
	@XStreamAlias("AllowAssistantWhileLocked")
	private String allowAssistantWhileLocked;
	
	@Transient
	@XStreamAlias("AllowCamera")
	private String allowCamera;
	
	@Transient
	@XStreamAlias("AllowExplicitContent")
	private String allowExplicitContent;
	
	@Transient
	@XStreamAlias("AllowScreenShot")
	private String allowScreenShot;
	
	@Transient
	@XStreamAlias("AllowYouTube")
	private String allowYouTuBe;
	
	@Transient
	@XStreamAlias("AllowiTunes")
	private String allowiTunes;
	
	@Transient
	@XStreamAlias("ForceITunesStorePasswordEntry")
	private String forceITunesStorePasswordEntry;
	
	@Transient
	@XStreamAlias("AllowSafari")
	private boolean allowSafari;
	
	@Transient
	@XStreamAlias("AllowUntrustedTLSPrompt")
	private String allowUntrustedTLSPrompt;
	
	@Transient
	@XStreamAlias("AllowCloudBackup")
	private String allowCloudBackup;
	
	@Transient
	@XStreamAlias("AllowCloudDocumentSync")
	private String allowCloudDocumentSync;
	
	@Transient
	@XStreamAlias("AllowPhotoStream")
	private String allowPhotoStream;
	
	@Transient
	@XStreamAlias("AllowGlobalBackgroundFetchWhenRoaming")
	private String allowGlobalBackgroundFetchWhenRoaming;
	
	@Transient
	@XStreamAlias("AllowVideoConferencing")
	private String allowVideoConferencing;
	
	@Transient
	@XStreamAlias("AllowInAppPurchases")
	private String allowInAppPurchases;
	
	@Transient
	@XStreamAlias("AllowMultiplayerGaming")
	private String allowMultiplayerGaming;
	
	@Transient
	@XStreamAlias("AllowVoiceDialing")
	private String allowVoiceDialing;
	
	@Transient
	@XStreamAlias("ForceEncryptedBackup")
	private String forceEncryptedBackup;
	
	@Transient
	@XStreamAlias("SafariAllowJavaScript")
	private String safariAllowJavaScript;
	
	@Transient
	@XStreamAlias("SafariAllowAutoFill")
	private String safariAllowAutoFill;
	
	@Transient
	@XStreamAlias("SafariAllowPopups")
	private String safariAllowPopups;
	
	@Transient
	@XStreamAlias("SafariForceFraudWarning")
	private String safariForceFraudWarning;
	
	@Transient
	@XStreamAlias("AllowDiagnosticSubmission")
	private String allowDiagnosticSubmission;
	
	@Transient
	@XStreamAlias("RatingApps")
	private String ratingApps;
	
	@Transient
	@XStreamAlias("RatingMovies")
	private String ratingMovies;
	
	@Transient
	@XStreamAlias("RatingTVShows")
	private String ratingTVShows;
	
	@Transient
	@XStreamAlias("SafariAcceptCookies")
	private String safariAcceptCookies;
	
	@Transient
	@XStreamAlias("RatingRegion")
	private String ratingRegion;

	public RestrictionInfoForUI() {
		super();
	}

	public RestrictionInfoForUI(String allowSimple, String forcePin,
			String maxFailedAttempts, String maxInactivity,
			String maxPinAgeInDays, String minComplexChars, String minLength,
			String requireAlphanumeri, String pinHistory,
			String manualFetchingWhenRoaming, String maxGracePeriod,
			String allowAddingGameCenterFriends, String allowAppInstallation,
			String allowAssistant, String allowAssistantWhileLocked,
			String allowCamera, String allowExplicitContent,
			String allowScreenShot, String allowYouTuBe, String allowiTunes,
			String forceITunesStorePasswordEntry, boolean allowSafari,
			String allowUntrustedTLSPrompt, String allowCloudBackup,
			String allowCloudDocumentSync, String allowPhotoStream,
			String allowGlobalBackgroundFetchWhenRoaming,
			String allowVideoConferencing, String allowInAppPurchases,
			String allowMultiplayerGaming, String allowVoiceDialing,
			String forceEncryptedBackup, String safariAllowJavaScript,
			String safariAllowAutoFill, String safariAllowPopups,
			String safariForceFraudWarning, String allowDiagnosticSubmission,
			String ratingApps, String ratingMovies, String ratingTVShows,
			String safariAcceptCookies, String ratingRegion) {
		super();
		this.allowSimple = allowSimple;
		this.forcePin = forcePin;
		this.maxFailedAttempts = maxFailedAttempts;
		this.maxInactivity = maxInactivity;
		this.maxPinAgeInDays = maxPinAgeInDays;
		this.minComplexChars = minComplexChars;
		this.minLength = minLength;
		this.requireAlphanumeric = requireAlphanumeri;
		this.pinHistory = pinHistory;
		this.manualFetchingWhenRoaming = manualFetchingWhenRoaming;
		this.maxGracePeriod = maxGracePeriod;
		this.allowAddingGameCenterFriends = allowAddingGameCenterFriends;
		this.allowAppInstallation = allowAppInstallation;
		this.allowAssistant = allowAssistant;
		this.allowAssistantWhileLocked = allowAssistantWhileLocked;
		this.allowCamera = allowCamera;
		this.allowExplicitContent = allowExplicitContent;
		this.allowScreenShot = allowScreenShot;
		this.allowYouTuBe = allowYouTuBe;
		this.allowiTunes = allowiTunes;
		this.forceITunesStorePasswordEntry = forceITunesStorePasswordEntry;
		this.allowSafari = allowSafari;
		this.allowUntrustedTLSPrompt = allowUntrustedTLSPrompt;
		this.allowCloudBackup = allowCloudBackup;
		this.allowCloudDocumentSync = allowCloudDocumentSync;
		this.allowPhotoStream = allowPhotoStream;
		this.allowGlobalBackgroundFetchWhenRoaming = allowGlobalBackgroundFetchWhenRoaming;
		this.allowVideoConferencing = allowVideoConferencing;
		this.allowInAppPurchases = allowInAppPurchases;
		this.allowMultiplayerGaming = allowMultiplayerGaming;
		this.allowVoiceDialing = allowVoiceDialing;
		this.forceEncryptedBackup = forceEncryptedBackup;
		this.safariAllowJavaScript = safariAllowJavaScript;
		this.safariAllowAutoFill = safariAllowAutoFill;
		this.safariAllowPopups = safariAllowPopups;
		this.safariForceFraudWarning = safariForceFraudWarning;
		this.allowDiagnosticSubmission = allowDiagnosticSubmission;
		this.ratingApps = ratingApps;
		this.ratingMovies = ratingMovies;
		this.ratingTVShows = ratingTVShows;
		this.safariAcceptCookies = safariAcceptCookies;
		this.ratingRegion = ratingRegion;
	}

	public String getAllowSimple() {
		return allowSimple;
	}

	public void setAllowSimple(String allowSimple) {
		this.allowSimple = allowSimple;
	}

	public String getForcePin() {
		return forcePin;
	}

	public void setForcePin(String forcePin) {
		this.forcePin = forcePin;
	}

	public String getMaxFailedAttempts() {
		return maxFailedAttempts;
	}

	public void setMaxFailedAttempts(String maxFailedAttempts) {
		this.maxFailedAttempts = maxFailedAttempts;
	}

	public String getMaxInactivity() {
		return maxInactivity;
	}

	public void setMaxInactivity(String maxInactivity) {
		this.maxInactivity = maxInactivity;
	}

	public String getMaxPinAgeInDays() {
		return maxPinAgeInDays;
	}

	public void setMaxPinAgeInDays(String maxPinAgeInDays) {
		this.maxPinAgeInDays = maxPinAgeInDays;
	}

	public String getMinComplexChars() {
		return minComplexChars;
	}

	public void setMinComplexChars(String minComplexChars) {
		this.minComplexChars = minComplexChars;
	}

	public String getMinLength() {
		return minLength;
	}

	public void setMinLength(String minLength) {
		this.minLength = minLength;
	}

	public String getRequireAlphanumeric() {
		return requireAlphanumeric;
	}

	public void setRequireAlphanumeric(String requireAlphanumeric) {
		this.requireAlphanumeric = requireAlphanumeric;
	}

	public String getPinHistory() {
		return pinHistory;
	}

	public void setPinHistory(String pinHistory) {
		this.pinHistory = pinHistory;
	}

	public String getManualFetchingWhenRoaming() {
		return manualFetchingWhenRoaming;
	}

	public void setManualFetchingWhenRoaming(String manualFetchingWhenRoaming) {
		this.manualFetchingWhenRoaming = manualFetchingWhenRoaming;
	}

	public String getMaxGracePeriod() {
		return maxGracePeriod;
	}

	public void setMaxGracePeriod(String maxGracePeriod) {
		this.maxGracePeriod = maxGracePeriod;
	}

	public String getAllowAddingGameCenterFriends() {
		return allowAddingGameCenterFriends;
	}

	public void setAllowAddingGameCenterFriends(String allowAddingGameCenterFriends) {
		this.allowAddingGameCenterFriends = allowAddingGameCenterFriends;
	}

	public String getAllowAppInstallation() {
		return allowAppInstallation;
	}

	public void setAllowAppInstallation(String allowAppInstallation) {
		this.allowAppInstallation = allowAppInstallation;
	}

	public String getAllowAssistant() {
		return allowAssistant;
	}

	public void setAllowAssistant(String allowAssistant) {
		this.allowAssistant = allowAssistant;
	}

	public String getAllowAssistantWhileLocked() {
		return allowAssistantWhileLocked;
	}

	public void setAllowAssistantWhileLocked(String allowAssistantWhileLocked) {
		this.allowAssistantWhileLocked = allowAssistantWhileLocked;
	}

	public String getAllowCamera() {
		return allowCamera;
	}

	public void setAllowCamera(String allowCamera) {
		this.allowCamera = allowCamera;
	}

	public String getAllowExplicitContent() {
		return allowExplicitContent;
	}

	public void setAllowExplicitContent(String allowExplicitContent) {
		this.allowExplicitContent = allowExplicitContent;
	}

	public String getAllowScreenShot() {
		return allowScreenShot;
	}

	public void setAllowScreenShot(String allowScreenShot) {
		this.allowScreenShot = allowScreenShot;
	}

	public String getAllowYouTuBe() {
		return allowYouTuBe;
	}

	public void setAllowYouTuBe(String allowYouTuBe) {
		this.allowYouTuBe = allowYouTuBe;
	}

	public String getAllowiTunes() {
		return allowiTunes;
	}

	public void setAllowiTunes(String allowiTunes) {
		this.allowiTunes = allowiTunes;
	}

	public String getForceITunesStorePasswordEntry() {
		return forceITunesStorePasswordEntry;
	}

	public void setForceITunesStorePasswordEntry(
			String forceITunesStorePasswordEntry) {
		this.forceITunesStorePasswordEntry = forceITunesStorePasswordEntry;
	}

	public boolean isAllowSafari() {
		return allowSafari;
	}

	public void setAllowSafari(boolean allowSafari) {
		this.allowSafari = allowSafari;
	}

	public String getAllowUntrustedTLSPrompt() {
		return allowUntrustedTLSPrompt;
	}

	public void setAllowUntrustedTLSPrompt(String allowUntrustedTLSPrompt) {
		this.allowUntrustedTLSPrompt = allowUntrustedTLSPrompt;
	}

	public String getAllowCloudBackup() {
		return allowCloudBackup;
	}

	public void setAllowCloudBackup(String allowCloudBackup) {
		this.allowCloudBackup = allowCloudBackup;
	}

	public String getAllowCloudDocumentSync() {
		return allowCloudDocumentSync;
	}

	public void setAllowCloudDocumentSync(String allowCloudDocumentSync) {
		this.allowCloudDocumentSync = allowCloudDocumentSync;
	}

	public String getAllowPhotoStream() {
		return allowPhotoStream;
	}

	public void setAllowPhotoStream(String allowPhotoStream) {
		this.allowPhotoStream = allowPhotoStream;
	}

	public String getAllowGlobalBackgroundFetchWhenRoaming() {
		return allowGlobalBackgroundFetchWhenRoaming;
	}

	public void setAllowGlobalBackgroundFetchWhenRoaming(
			String allowGlobalBackgroundFetchWhenRoaming) {
		this.allowGlobalBackgroundFetchWhenRoaming = allowGlobalBackgroundFetchWhenRoaming;
	}

	public String getAllowVideoConferencing() {
		return allowVideoConferencing;
	}

	public void setAllowVideoConferencing(String allowVideoConferencing) {
		this.allowVideoConferencing = allowVideoConferencing;
	}

	public String getAllowInAppPurchases() {
		return allowInAppPurchases;
	}

	public void setAllowInAppPurchases(String allowInAppPurchases) {
		this.allowInAppPurchases = allowInAppPurchases;
	}

	public String getAllowMultiplayerGaming() {
		return allowMultiplayerGaming;
	}

	public void setAllowMultiplayerGaming(String allowMultiplayerGaming) {
		this.allowMultiplayerGaming = allowMultiplayerGaming;
	}

	public String getAllowVoiceDialing() {
		return allowVoiceDialing;
	}

	public void setAllowVoiceDialing(String allowVoiceDialing) {
		this.allowVoiceDialing = allowVoiceDialing;
	}

	public String getForceEncryptedBackup() {
		return forceEncryptedBackup;
	}

	public void setForceEncryptedBackup(String forceEncryptedBackup) {
		this.forceEncryptedBackup = forceEncryptedBackup;
	}

	public String getSafariAllowJavaScript() {
		return safariAllowJavaScript;
	}

	public void setSafariAllowJavaScript(String safariAllowJavaScript) {
		this.safariAllowJavaScript = safariAllowJavaScript;
	}

	public String getSafariAllowAutoFill() {
		return safariAllowAutoFill;
	}

	public void setSafariAllowAutoFill(String safariAllowAutoFill) {
		this.safariAllowAutoFill = safariAllowAutoFill;
	}

	public String getSafariAllowPopups() {
		return safariAllowPopups;
	}

	public void setSafariAllowPopups(String safariAllowPopups) {
		this.safariAllowPopups = safariAllowPopups;
	}

	public String getSafariForceFraudWarning() {
		return safariForceFraudWarning;
	}

	public void setSafariForceFraudWarning(String safariForceFraudWarning) {
		this.safariForceFraudWarning = safariForceFraudWarning;
	}

	public String getAllowDiagnosticSubmission() {
		return allowDiagnosticSubmission;
	}

	public void setAllowDiagnosticSubmission(String allowDiagnosticSubmission) {
		this.allowDiagnosticSubmission = allowDiagnosticSubmission;
	}

	public String getRatingApps() {
		return ratingApps;
	}

	public void setRatingApps(String ratingApps) {
		this.ratingApps = ratingApps;
	}

	public String getRatingMovies() {
		return ratingMovies;
	}

	public void setRatingMovies(String ratingMovies) {
		this.ratingMovies = ratingMovies;
	}

	public String getRatingTVShows() {
		return ratingTVShows;
	}

	public void setRatingTVShows(String ratingTVShows) {
		this.ratingTVShows = ratingTVShows;
	}

	public String getSafariAcceptCookies() {
		return safariAcceptCookies;
	}

	public void setSafariAcceptCookies(String safariAcceptCookies) {
		this.safariAcceptCookies = safariAcceptCookies;
	}

	public String getRatingRegion() {
		return ratingRegion;
	}

	public void setRatingRegion(String ratingRegion) {
		this.ratingRegion = ratingRegion;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
