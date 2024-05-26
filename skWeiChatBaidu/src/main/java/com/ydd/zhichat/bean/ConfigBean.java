package com.ydd.zhichat.bean;

import com.alibaba.fastjson.JSON;
import com.ydd.zhichat.Reporter;

/**
 * @编写人： 未知
 * @时间： 2016/4/28 10:43
 * @说明： tanx补注释
 * @功能： 在所有的接口初始化之前，会向服务器获取接口配置，该类保存获取的配置
 **/
public class ConfigBean {
    private String ftpHost;    // ftp(无用)
    private String ftpUsername;// ftp用户名(无用)
    private String ftpPassword;// ftp密码(无用)
    private String androidAppUrl;// AndroidApp下载地址
    private String androidExplain;
    private String androidVersion;// 版本号
    private String androidDisable;// 禁用版本号，包括这个和更低版本，
    private String apiUrl;// Api的服务器地址
    private String uploadUrl;// 上传的服务器地址
    private String downloadUrl;// 头像以外的东西的下载地址
    private String downloadAvatarUrl;// 下载头像的前缀
    private String XMPPHost;  // xmpp主机
    private String XMPPDomain;// xmpp群聊的域名
    private int xmppPingTime; // 每隔xmppPingTime秒ping一次服务器
    private int XMPPTimeout;  // Xmpp超时时长(服务器针对客户端的超时时长)
    private int isOpenCluster;    // 是否开启集群
    private int isOpenReceipt = 1;// 是否请求回执
    private int hideSearchByFriends = 1;// 是否隐藏好友搜索功能 0:隐藏 1：开启
    /**
     * 注册邀请码   registerInviteCode
     * 0:关闭
     * 1:开启一对一邀请（一码一用，且必填）
     * <p>
     * 2:开启一对多邀请（一码多用，选填项），该模式下客户端需要把用户自己的邀请码显示出来
     */
    private String address;
    private int registerInviteCode;
    private int nicknameSearchUser = 2; //昵称搜索用户  0 :关闭       1:精确搜索    2:模糊搜索   默认模糊搜索
    private int regeditPhoneOrName;// 0：使用手机号注册，1：使用用户名注册
    private int isCommonFindFriends = 0;// 普通用户是否能搜索好友 0:允许 1：不允许
    private int isCommonCreateGroup = 0;// 普通用户是否能建群 0:允许 1：不允许
    private int displayRedPacket;//是否开启红包功能 0:隐藏 1：开启
    private int isOpenPositionService = 0;// 是否开启位置相关服务 0：开启 1：关闭
    private int isOpenYouke;
    private String language;
    private Integer isRestore;
    private int selectAvatarMode;
    private int isOpenGoogleFCM = 0;// 是否打开Android Google推送 1：开启 0：关闭
    private String popularAPP;// 热门应用  lifeCircle  生活圈，  videoMeeting 视频会议，  liveVideo 视频直播，  shortVideo 短视频， peopleNearby 附近的人
    private String isOpenRegister;// 是否开放注册，
    private String isOpenSMSCode; // 是否需要短信验证码，
    private String jitsiServer;// jitsi的前缀地址
    // 关于页面的信息，
    private String companyName;
    private String copyright;
    private String website = "http://1.14.160.11/im-download.html";
    private String headBackgroundImg;
    private String privacyPolicyPrefix; // 隐私政策的地址前缀,
    private int fileValidTime = -1;// 文件保存时长，默认永久
    private int isOpenRoomSearch = 1;// 是否开启群组搜索 0：开启 1：关闭
    private int isOpenOnlineStatus = 0;// 是否在聊天界面显示好友在线/离线 0：关闭 1：开启
    private double payRate = 0.01;// 充值手续费率
    private double drawRate = 0.01;// 提现手续费率
    private int isChatOpen = 0;
    private int isRoomMode = 0;
    private int isFlagSecure = 0;
    private String vipConfigApp = "";
    private VipConfigApp vipConfig = null;
    private Integer vipYearCost;
    private Integer vipSeasonCost;
    private Integer vipMoonCost;
    private int isAudiovisual = 0;
    private Integer isVipCenter = 0;
    private Integer isUidShop = 0;

    private String vip1TextColo;
    private String vip2TextColo;
    private String vip3TextColo;

    private String vip1ImgUpdate;
    private String vip2ImgUpdate;
    private String vip3ImgUpdate;

    private String lhaoImgUpdate;
    private String viplevel1ImgUpdate;
    private String viplevel2ImgUpdate;
    private String viplevel3ImgUpdate;
    private Integer isCirclePay = 0;
    private Integer circleType = 0;
    private Integer circlePrice = 0;
    private String zhifuStr = "";//支付通知
    private Integer redRatio = 0;

    public Integer getRedRatio() {
        return redRatio;
    }

    public void setRedRatio(Integer redRatio) {
        this.redRatio = redRatio;
    }

    public String getZhifuStr() {
        return zhifuStr;
    }

    public void setZhifuStr(String zhifuStr) {
        this.zhifuStr = zhifuStr;
    }

    public Integer getCirclePrice() {
        return circlePrice;
    }

    public Integer getCircleType() {
        return circleType;
    }

    public Integer getIsCirclePay() {
        return isCirclePay;
    }

    public String getLhaoImgUpdate() {
        return lhaoImgUpdate;
    }

    public String getVip1ImgUpdate() {
        return vip1ImgUpdate;
    }

    public String getVip1TextColo() {
        return vip1TextColo;
    }

    public String getVip2ImgUpdate() {
        return vip2ImgUpdate;
    }

    public String getVip2TextColo() {
        return vip2TextColo;
    }

    public String getVip3ImgUpdate() {
        return vip3ImgUpdate;
    }

    public String getVip3TextColo() {
        return vip3TextColo;
    }

    public String getViplevel1ImgUpdate() {
        return viplevel1ImgUpdate;
    }

    public void setCirclePrice(Integer circlePrice) {
        this.circlePrice = circlePrice;
    }

    public void setCircleType(Integer circleType) {
        this.circleType = circleType;
    }

    public void setIsCirclePay(Integer isCirclePay) {
        this.isCirclePay = isCirclePay;
    }

    public void setLhaoImgUpdate(String lhaoImgUpdate) {
        this.lhaoImgUpdate = lhaoImgUpdate;
    }

    public void setVip1ImgUpdate(String vip1ImgUpdate) {
        this.vip1ImgUpdate = vip1ImgUpdate;
    }

    public void setVip1TextColo(String vip1TextColo) {
        this.vip1TextColo = vip1TextColo;
    }

    public void setVip2ImgUpdate(String vip2ImgUpdate) {
        this.vip2ImgUpdate = vip2ImgUpdate;
    }

    public void setVip2TextColo(String vip2TextColo) {
        this.vip2TextColo = vip2TextColo;
    }

    public void setVip3ImgUpdate(String vip3ImgUpdate) {
        this.vip3ImgUpdate = vip3ImgUpdate;
    }

    public void setVip3TextColo(String vip3TextColo) {
        this.vip3TextColo = vip3TextColo;
    }

    public void setViplevel1ImgUpdate(String viplevelImgUpdate) {
        this.viplevel1ImgUpdate = viplevelImgUpdate;
    }

    public String getViplevel2ImgUpdate() {
        return viplevel2ImgUpdate;
    }

    public String getViplevel3ImgUpdate() {
        return viplevel3ImgUpdate;
    }

    public void setViplevel2ImgUpdate(String viplevel2ImgUpdate) {
        this.viplevel2ImgUpdate = viplevel2ImgUpdate;
    }

    public void setViplevel3ImgUpdate(String viplevel3ImgUpdate) {
        this.viplevel3ImgUpdate = viplevel3ImgUpdate;
    }

    public Integer getIsUidShop() {
        return isUidShop;
    }

    public void setIsUidShop(Integer isUidShop) {
        this.isUidShop = isUidShop;
    }

    public void setIsVipCenter(Integer isVipCenter) {
        this.isVipCenter = isVipCenter;
    }

    public Integer getIsVipCenter() {
        return isVipCenter;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setIsAudiovisual(int isAudiovisual) {
        this.isAudiovisual = isAudiovisual;
    }

    public int getIsAudiovisual() {
        return isAudiovisual;
    }

    public Integer getVipMoonCost() {
        return vipMoonCost;
    }

    public Integer getVipSeasonCost() {
        return vipSeasonCost;
    }

    public Integer getVipYearCost() {
        return vipYearCost;
    }

    public void setVipMoonCost(Integer vipMoonCost) {
        this.vipMoonCost = vipMoonCost;
    }

    public void setVipSeasonCost(Integer vipSeasonCost) {
        this.vipSeasonCost = vipSeasonCost;
    }

    public void setVipYearCost(Integer vipYearCost) {
        this.vipYearCost = vipYearCost;
    }

    public VipConfigApp getVipConfig() {
        return vipConfig;
    }

    public void setVipConfig(VipConfigApp vipConfig) {
        this.vipConfig = vipConfig;
    }

    public String getVipConfigApp() {
        return vipConfigApp;
    }

    public void setVipConfigApp(String vipConfigApp) {
        this.vipConfigApp = vipConfigApp;
    }

    public int getIsFlagSecure() {
        return isFlagSecure;
    }

    public void setIsFlagSecure(int isFlagSecure) {
        this.isFlagSecure = isFlagSecure;
    }


    public int getSelectAvatarMode() {
        return selectAvatarMode;
    }

    public void setSelectAvatarMode(int selectAvatarMode) {
        this.selectAvatarMode = selectAvatarMode;
    }

    public int getIsRoomMode() {
        return isRoomMode;
    }

    public void setIsRoomMode(int isRoomMode) {
        this.isRoomMode = isRoomMode;
    }

    public int getIsChatOpen(){
        return isChatOpen;
    }

    public void setIsChatOpen(int isChatOpen) {
        this.isChatOpen = isChatOpen;
    }

    public double getPayRate() {
        return payRate;
    }

    public void setPayRate(double payRate) {
        this.payRate = payRate;
    }

    public double getDrawRate() {
        return drawRate;
    }

    public void setDrawRate(double drawRate) {
        this.drawRate = drawRate;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getFtpHost() {
        return ftpHost;
    }

    public void setFtpHost(String ftpHost) {
        this.ftpHost = ftpHost;
    }

    public String getFtpUsername() {
        return ftpUsername;
    }

    public void setFtpUsername(String ftpUsername) {
        this.ftpUsername = ftpUsername;
    }

    public String getFtpPassword() {
        return ftpPassword;
    }

    public void setFtpPassword(String ftpPassword) {
        this.ftpPassword = ftpPassword;
    }

    public String getAndroidAppUrl() {
        return androidAppUrl;
    }

    public void setAndroidAppUrl(String androidAppUrl) {
        this.androidAppUrl = androidAppUrl;
    }

    public String getAndroidExplain() {
        return androidExplain;
    }

    public void setAndroidExplain(String androidExplain) {
        this.androidExplain = androidExplain;
    }

    public String getAndroidVersion() {
        return androidVersion;
    }

    public void setAndroidVersion(String androidVersion) {
        this.androidVersion = androidVersion;
    }

    public String getAndroidDisable() {
        return androidDisable;
    }

    public void setAndroidDisable(String androidDisable) {
        this.androidDisable = androidDisable;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getDownloadAvatarUrl() {
        return downloadAvatarUrl;
    }

    public void setDownloadAvatarUrl(String downloadAvatarUrl) {
        this.downloadAvatarUrl = downloadAvatarUrl;
    }

    public String getXMPPHost() {
        return XMPPHost;
    }

    public void setXMPPHost(String xMPPHost) {
        XMPPHost = xMPPHost;
    }

    public String getXMPPDomain() {
        return XMPPDomain;
    }

    public void setXMPPDomain(String xMPPDomain) {
        XMPPDomain = xMPPDomain;
    }

    public int getXmppPingTime() {
        return xmppPingTime;
    }

    public void setXmppPingTime(int xmppPingTime) {
        this.xmppPingTime = xmppPingTime;
    }

    public int getXMPPTimeout() {
        return XMPPTimeout;
    }

    public void setXMPPTimeout(int XMPPTimeout) {
        this.XMPPTimeout = XMPPTimeout;
    }

    public int getIsOpenCluster() {
        return isOpenCluster;
    }

    public void setIsOpenCluster(int isOpenCluster) {
        this.isOpenCluster = isOpenCluster;
    }

    public int getIsOpenReceipt() {
        return isOpenReceipt;
    }

    public void setIsOpenReceipt(int isOpenReceipt) {
        this.isOpenReceipt = isOpenReceipt;
    }

    public String getIsOpenRegister() {
        return isOpenRegister;
    }

    public void setIsOpenRegister(String isOpenRegister) {
        this.isOpenRegister = isOpenRegister;
    }

    public String getIsOpenSMSCode() {
        return isOpenSMSCode;
    }

    public void setIsOpenSMSCode(String isOpenSMSCode) {
        this.isOpenSMSCode = isOpenSMSCode;
    }

    public String getJitsiServer() {
        return jitsiServer;
    }

    public void setJitsiServer(String jitsiServer) {
        this.jitsiServer = jitsiServer;
    }

    public int getFileValidTime() {
        return fileValidTime;
    }

    public void setFileValidTime(int fileValidTime) {
        this.fileValidTime = fileValidTime;
    }

    public int getHideSearchByFriends() {
        return hideSearchByFriends;
    }

    public void setHideSearchByFriends(int hideSearchByFriends) {
        this.hideSearchByFriends = hideSearchByFriends;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getRegisterInviteCode() {
        return registerInviteCode;
    }

    public void setRegisterInviteCode(int registerInviteCode) {
        this.registerInviteCode = registerInviteCode;
    }

    public int getNicknameSearchUser() {
        return nicknameSearchUser;
    }

    public void setNicknameSearchUser(int nicknameSearchUser) {
        this.nicknameSearchUser = nicknameSearchUser;
    }

    public int getRegeditPhoneOrName() {
        return regeditPhoneOrName;
    }

    public void setRegeditPhoneOrName(int regeditPhoneOrName) {
        this.regeditPhoneOrName = regeditPhoneOrName;
    }

    public int getIsCommonFindFriends() {
        return isCommonFindFriends;
    }

    public void setIsCommonFindFriends(int isCommonFindFriends) {
        this.isCommonFindFriends = isCommonFindFriends;
    }

    public int getIsCommonCreateGroup() {
        return isCommonCreateGroup;
    }

    public void setIsCommonCreateGroup(int isCommonCreateGroup) {
        this.isCommonCreateGroup = isCommonCreateGroup;
    }

    public int getDisplayRedPacket() {
        return displayRedPacket;
    }

    public void setDisplayRedPacket(int displayRedPacket) {
        this.displayRedPacket = displayRedPacket;
    }

    public int getIsOpenPositionService() {
        return isOpenPositionService;
    }

    public Integer getIsRestore() {
        return isRestore;
    }

    public void setIsRestore(Integer isRestore) {
        this.isRestore = isRestore;
    }

    public void setIsOpenYouke(int isOpenYouke) {
        this.isOpenYouke = isOpenYouke;
    }

    public int getIsOpenYouke(){
        return isOpenYouke;
    }

    public void setIsOpenPositionService(int isOpenPositionService) {
        this.isOpenPositionService = isOpenPositionService;
    }

    public String getHeadBackgroundImg() {
        return headBackgroundImg;
    }

    public void setHeadBackgroundImg(String headBackgroundImg) {
        this.headBackgroundImg = headBackgroundImg;
    }

    public int getIsOpenGoogleFCM() {
        return isOpenGoogleFCM;
    }

    public void setIsOpenGoogleFCM(int isOpenGoogleFCM) {
        this.isOpenGoogleFCM = isOpenGoogleFCM;
    }

    public String getPopularAPP() {
        return popularAPP;
    }

    public void setPopularAPP(String popularAPP) {
        this.popularAPP = popularAPP;
    }

    public PopularApp getPopularAPPBean() {
        PopularApp popularAppBean = null;
        try {
            popularAppBean = JSON.parseObject(popularAPP, PopularApp.class);
        } catch (Exception e) {
            Reporter.unreachable(e);
        }
        if (popularAppBean == null) {
            popularAppBean = new PopularApp();
        }
        return popularAppBean;
    }

    public String getPrivacyPolicyPrefix() {
        return privacyPolicyPrefix;
    }

    public void setPrivacyPolicyPrefix(String privacyPolicyPrefix) {
        this.privacyPolicyPrefix = privacyPolicyPrefix;
    }

    public int getIsOpenRoomSearch() {
        return isOpenRoomSearch;
    }

    public void setIsOpenRoomSearch(int isOpenRoomSearch) {
        this.isOpenRoomSearch = isOpenRoomSearch;
    }

    public int getIsOpenOnlineStatus() {
        return isOpenOnlineStatus;
    }

    public void setIsOpenOnlineStatus(int isOpenOnlineStatus) {
        this.isOpenOnlineStatus = isOpenOnlineStatus;
    }

    // 热门应用  lifeCircle  生活圈，  videoMeeting 视频会议，  liveVideo 视频直播，  shortVideo 短视频， peopleNearby 附近的人
    public static class PopularApp {
        public int lifeCircle = 1;
        public int videoMeeting = 1;
        public int liveVideo = 1;
        public int shortVideo = 1;
        public int peopleNearby = 1;
        public int scan = 1;
    }
}