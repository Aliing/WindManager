package com.ah.mdm.core.profile.payload;

import org.dom4j.Element;

import com.ah.mdm.core.profile.entity.AbstractProfileInfo;
import com.ah.mdm.core.profile.entity.RestrictionsProfileInfo;

public class RestrictionsPayload extends ProfilePayload
{

	public RestrictionsPayload(AbstractProfileInfo model)
	{
		super(model);
	}

	@Override
	protected void fillPayloadContent(Element parentNode)
	{
		super.fillPayloadContent(parentNode);
		RestrictionsProfileInfo m = (RestrictionsProfileInfo) model;
		addElement(parentNode, ALLOW_APP_INSTALLATION, m.isAllowAppInstallation());
		addElement(parentNode, ALLOW_ASSISTANT, m.isAllowAssistant());
		addElement(parentNode, ALLOW_CAMERA, m.isAllowCamera());
		addElement(parentNode, ALLOW_EXPLICITCONTENT, m.isAllowExplicitContent());
		addElement(parentNode, ALLOW_SCREEN_SHOT, m.isAllowScreenShot());
		addElement(parentNode, ALLOW_YOUTUBE, m.isAllowYouTube());
		addElement(parentNode, ALLOW_ITUNES, m.isAllowiTunes());
		addElement(parentNode, FORCE_ITUNES_STORE_PASSWORD_ENTRY, m.isForceITunesStorePasswordEntry());
		addElement(parentNode, ALLOW_SAFARI, m.isAllowSafari());
		addElement(parentNode, ALLOW_UNTRUSTED_TLS_PROMPT, m.isAllowUntrustedTLSPrompt());
		addElement(parentNode, ALLOW_CLOUD_BACKUP, m.isAllowCloudBackup());
		addElement(parentNode, ALLOW_CLOUD_DOCUMENT_SYNC, m.isAllowCloudDocumentSync());
		addElement(parentNode, ALLOW_PHOTO_STREAM, m.isAllowPhotoStream());
		addElement(parentNode, "allowAddingGameCenterFriends", m.isAllowAddingGameCenterFriends());
		addElement(parentNode, "allowAssistantWhileLocked", m.isAllowAssistantWhileLocked());
		addElement(parentNode, "allowDiagnosticSubmission", m.isAllowDiagnosticSubmission());
		addElement(parentNode, "allowGlobalBackgroundFetchWhenRoaming", m.isAllowGlobalBackgroundFetchWhenRoaming());
		addElement(parentNode, "allowInAppPurchases", m.isAllowInAppPurchases());
		addElement(parentNode, "allowMultiplayerGaming", m.isAllowMultiplayerGaming());
		addElement(parentNode, "allowVideoConferencing", m.isAllowVideoConferencing());
		addElement(parentNode, "allowVoiceDialing", m.isAllowVoiceDialing());
		addElement(parentNode, "forceEncryptedBackup", m.isForceEncryptedBackup());
		addElement(parentNode, "ratingApps", m.getRatingApps());
		addElement(parentNode, "ratingMovies", m.getRatingMovies());
		addElement(parentNode, "ratingRegion", m.getRatingRegion());
		addElement(parentNode, "ratingTVShows", m.getRatingTVShows());
		addElement(parentNode, "safariAcceptCookies", m.getSafariAcceptCookies());
		addElement(parentNode, "safariAllowAutoFill", m.isSafariAllowAutoFill());
		addElement(parentNode, "safariAllowJavaScript", m.isSafariAllowJavaScript());
		addElement(parentNode, "safariAllowPopups", m.isSafariAllowPopups());
		addElement(parentNode, "safariForceFraudWarning", m.isSafariForceFraudWarning());
	}

	@Override
	public AbstractProfileInfo parse(Element dictElement)
	{
		RestrictionsProfileInfo m = (RestrictionsProfileInfo) super.parse(dictElement);
		String allowAddingGameCenterFriends = getValue(dictElement, "allowAddingGameCenterFriends", true);
		m.setAllowAddingGameCenterFriends(allowAddingGameCenterFriends == null ? m.isAllowAddingGameCenterFriends() : Boolean
				.valueOf(allowAddingGameCenterFriends));

		String allowAppInstallation = getValue(dictElement, "allowAppInstallation", true);
		m.setAllowAppInstallation(allowAppInstallation == null ? m.isAllowAppInstallation() : Boolean.valueOf(allowAppInstallation));

		String allowAssistant = getValue(dictElement, "allowAssistant", true);
		m.setAllowAssistant(allowAssistant == null ? m.isAllowAssistant() : Boolean.valueOf(allowAssistant));

		String allowAssistantWhileLocked = getValue(dictElement, "allowAssistantWhileLocked", true);
		m.setAllowAssistantWhileLocked(allowAssistantWhileLocked == null ? m.isAllowAssistantWhileLocked() : Boolean
				.valueOf(allowAssistantWhileLocked));

		String allowCamera = getValue(dictElement, "allowCamera", true);
		m.setAllowCamera(allowCamera == null ? m.isAllowCamera() : Boolean.valueOf(allowCamera));

		String allowCloudBackup = getValue(dictElement, "allowCloudBackup", true);
		m.setAllowCloudBackup(allowCloudBackup == null ? m.isAllowCloudBackup() : Boolean.valueOf(allowCloudBackup));

		String allowCloudDocumentSync = getValue(dictElement, "allowCloudDocumentSync", true);
		m.setAllowCloudDocumentSync(allowCloudDocumentSync == null ? m.isAllowCloudDocumentSync() : Boolean.valueOf(allowCloudDocumentSync));

		String allowDiagnosticSubmission = getValue(dictElement, "allowDiagnosticSubmission", true);
		m.setAllowDiagnosticSubmission(allowDiagnosticSubmission == null ? m.isAllowDiagnosticSubmission() : Boolean
				.valueOf(allowDiagnosticSubmission));

		String allowExplicitContent = getValue(dictElement, "allowExplicitContent", true);
		m.setAllowExplicitContent(allowExplicitContent == null ? m.isAllowExplicitContent() : Boolean.valueOf(allowExplicitContent));

		String allowGlobalBackgroundFetchWhenRoaming = getValue(dictElement, "allowGlobalBackgroundFetchWhenRoaming", true);
		m.setAllowGlobalBackgroundFetchWhenRoaming(allowGlobalBackgroundFetchWhenRoaming == null ? m.isAllowGlobalBackgroundFetchWhenRoaming()
				: Boolean.valueOf(allowGlobalBackgroundFetchWhenRoaming));

		String allowInAppPurchases = getValue(dictElement, "allowInAppPurchases", true);
		m.setAllowInAppPurchases(allowInAppPurchases == null ? m.isAllowInAppPurchases() : Boolean.valueOf(allowInAppPurchases));

		String allowMultiplayerGaming = getValue(dictElement, "allowMultiplayerGaming", true);
		m.setAllowMultiplayerGaming(allowMultiplayerGaming == null ? m.isAllowMultiplayerGaming() : Boolean.valueOf(allowMultiplayerGaming));

		String allowPhotoStream = getValue(dictElement, "allowPhotoStream", true);
		m.setAllowPhotoStream(allowPhotoStream == null ? m.isAllowPhotoStream() : Boolean.valueOf(allowPhotoStream));

		String allowSafari = getValue(dictElement, "allowSafari", true);
		m.setAllowSafari(allowSafari == null ? m.isAllowSafari() : Boolean.valueOf(allowSafari));

		String allowScreenShot = getValue(dictElement, "allowScreenShot", true);
		m.setAllowScreenShot(allowScreenShot == null ? m.isAllowScreenShot() : Boolean.valueOf(allowScreenShot));

		String allowUntrustedTLSPrompt = getValue(dictElement, "allowUntrustedTLSPrompt", true);
		m.setAllowUntrustedTLSPrompt(allowUntrustedTLSPrompt == null ? m.isAllowUntrustedTLSPrompt() : Boolean.valueOf(allowUntrustedTLSPrompt));

		String allowVideoConferencing = getValue(dictElement, "allowVideoConferencing", true);
		m.setAllowVideoConferencing(allowVideoConferencing == null ? m.isAllowVideoConferencing() : Boolean.valueOf(allowVideoConferencing));

		String allowVoiceDialing = getValue(dictElement, "allowVoiceDialing", true);
		m.setAllowVoiceDialing(allowVoiceDialing == null ? m.isAllowVoiceDialing() : Boolean.valueOf(allowVoiceDialing));

		String allowYouTube = getValue(dictElement, "allowYouTube", true);
		m.setAllowYouTube(allowYouTube == null ? m.isAllowYouTube() : Boolean.valueOf(allowYouTube));

		String allowiTunes = getValue(dictElement, "allowiTunes", true);
		m.setAllowiTunes(allowiTunes == null ? m.isAllowiTunes() : Boolean.valueOf(allowiTunes));

		String forceEncryptedBackup = getValue(dictElement, "forceEncryptedBackup", true);
		m.setForceEncryptedBackup(forceEncryptedBackup == null ? m.isForceEncryptedBackup() : Boolean.valueOf(forceEncryptedBackup));

		String forceITunesStorePasswordEntry = getValue(dictElement, "forceITunesStorePasswordEntry", true);
		m.setForceITunesStorePasswordEntry(forceITunesStorePasswordEntry == null ? m.isForceITunesStorePasswordEntry() : Boolean
				.valueOf(forceITunesStorePasswordEntry));

		String ratingApps = getValue(dictElement, "ratingApps", false);
		m.setRatingApps(ratingApps == null ? m.getRatingApps() : Integer.valueOf(ratingApps));

		String ratingMovies = getValue(dictElement, "ratingMovies", false);
		m.setRatingMovies(ratingMovies == null ? m.getRatingMovies() : Integer.valueOf(ratingMovies));

		String ratingRegion = getValue(dictElement, "ratingRegion", false);
		m.setRatingRegion(ratingRegion == null ? m.getRatingRegion() : ratingRegion);

		String ratingTVShows = getValue(dictElement, "ratingTVShows", false);
		m.setRatingTVShows(ratingTVShows == null ? m.getRatingTVShows() : Integer.valueOf(ratingTVShows));

		String safariAcceptCookies = getValue(dictElement, "safariAcceptCookies", false);
		m.setSafariAcceptCookies(safariAcceptCookies == null ? m.getSafariAcceptCookies() : Integer.valueOf(safariAcceptCookies));

		String safariAllowAutoFill = getValue(dictElement, "safariAllowAutoFill", true);
		m.setSafariAllowAutoFill(safariAllowAutoFill == null ? m.isSafariAllowAutoFill() : Boolean.valueOf(safariAllowAutoFill));

		String safariAllowJavaScript = getValue(dictElement, "safariAllowJavaScript", true);
		m.setSafariAllowJavaScript(safariAllowJavaScript == null ? m.isSafariAllowJavaScript() : Boolean.valueOf(safariAllowJavaScript));

		String safariAllowPopups = getValue(dictElement, "safariAllowPopups", true);
		m.setSafariAllowPopups(safariAllowPopups == null ? m.isSafariAllowPopups() : Boolean.valueOf(safariAllowPopups));

		String safariForceFraudWarning = getValue(dictElement, "safariForceFraudWarning", true);
		m.setSafariForceFraudWarning(safariForceFraudWarning == null ? m.isSafariForceFraudWarning() : Boolean.valueOf(safariForceFraudWarning));
		return m;
	}
}
