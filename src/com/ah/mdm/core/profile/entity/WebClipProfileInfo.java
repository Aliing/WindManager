package com.ah.mdm.core.profile.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "t_profile_web_clip")
public class WebClipProfileInfo extends AbstractProfileInfo
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long	webClipId;

	private String	url;
	private String	label;
	private String	iconFileName;
	private byte[]	icon;
	private boolean	removable;
	private boolean	fullScreen;
	private boolean	precomposed;
	@Transient
	private String iconStr;
	public WebClipProfileInfo()
	{
		super(ProfilePayloadType.PAYLOAD_TYPE_WEP_CLIP);
	}
	
	public String getIconStr() {
		return iconStr;
	}

	public void setIconStr(String iconStr) {
		this.iconStr = iconStr;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	public String getIconFileName()
	{
		return iconFileName;
	}

	public void setIconFileName(String iconFileName)
	{
		this.iconFileName = iconFileName;
	}

	public byte[] getIcon()
	{
		return icon;
	}

	public void setIcon(byte[] icon)
	{
		this.icon = icon;
	}

	public boolean isRemovable()
	{
		return removable;
	}

	public void setRemovable(boolean removable)
	{
		this.removable = removable;
	}

	public boolean isFullScreen()
	{
		return fullScreen;
	}

	public void setFullScreen(boolean fullScreen)
	{
		this.fullScreen = fullScreen;
	}

	public boolean isPrecomposed()
	{
		return precomposed;
	}

	public long getWebClipId()
	{
		return webClipId;
	}

	public void setWebClipId(long webClipId)
	{
		this.webClipId = webClipId;
	}

	public void setPrecomposed(boolean precomposed)
	{
		this.precomposed = precomposed;
	}

}
