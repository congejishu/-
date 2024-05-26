package com.ydd.zhichat.bean;

public class VipConfigApp implements java.io.Serializable {

    private Integer doubleRevoke;// 0, // 双向撤回
    private Integer nameLight;// 0, // 昵称高亮
    private Integer vipIcon;// 0, // 会员标志
    private Integer autoReply;// 0, // 自动回复
    private Integer superGroup;// 0, // 超级大群
    private Integer voiceVideo;// 0// 语音视频

    public Integer getAutoReply() {
        return autoReply;
    }

    public Integer getDoubleRevoke() {
        return doubleRevoke;
    }

    public Integer getNameLight() {
        return nameLight;
    }

    public Integer getSuperGroup() {
        return superGroup;
    }

    public Integer getVipIcon() {
        return vipIcon;
    }

    public Integer getVoiceVideo() {
        return voiceVideo;
    }

    public void setAutoReply(Integer autoReply) {
        this.autoReply = autoReply;
    }

    public void setDoubleRevoke(Integer doubleRevoke) {
        this.doubleRevoke = doubleRevoke;
    }

    public void setNameLight(Integer nameLight) {
        this.nameLight = nameLight;
    }

    public void setSuperGroup(Integer superGroup) {
        this.superGroup = superGroup;
    }

    public void setVipIcon(Integer vipIcon) {
        this.vipIcon = vipIcon;
    }

    public void setVoiceVideo(Integer voiceVideo) {
        this.voiceVideo = voiceVideo;
    }
}
