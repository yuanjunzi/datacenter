package com.git.service.yuanjunzi.datacenter.constants;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * Created by yuanjunzi on 2018/11/26.
 */
public enum RiskParamEnum {
    PLATFORM(ImmutableMap.<String, String>builder()
            .put("0", "未知平台")
            .put("1", "电脑浏览器")
            .put("2", "手机浏览器（m版）")
            .put("3", "手机浏览器打开的i版")
            .put("4", "安卓(包括公司app中的内嵌i版)")
            .put("5", "iPhone(包括公司app中的内嵌i版)")
            .put("6", "Windows Phone")
            .put("7", "苹果平板")
            .put("8", "安卓平板")
            .put("9", "Win8平板")
            .put("10", "安卓TV")
            .put("11", "微信app-h5浏览器")
            .put("12", "机浏览器WAP")
            .put("13", "微信小程序")
            .put("14", "手机qq-app-h5浏览器")
            .put("15", "工行app-h5浏览器")
            .put("16", "猫眼闸机")
            .put("17", "猫眼 pda")
            .put("18", "百度小程序")
            .put("19", "头条小程序")
            .put("1000", "反爬mtsi")
            .build()
    ),

    PARTNER(ImmutableMap.<String, String>builder()
            .put("-1", "未知")
            .put("0", "团购/团购商户开店宝")
            .put("1", "猫眼")
            .put("2", "酒店")
            .put("3", "云平台，美团云，mos")
            .put("4", "外卖")
            .put("5", "ecom商家后台")
            .put("6", "早餐")
            .put("7", "KTV")
            .put("8", "商户通")
            .put("9", "买单")
            .put("10", "上门服务")
            .put("11", "旅游（门票）")
            .put("12", "支付")
            .put("13", "火车票")
            .put("14", "美团票务")
            .put("15", "猫眼竞拍")
            .put("16", "点菜")
            .put("17", "外卖商超")
            .put("18", "支付抽奖")
            .put("19", "外卖商家")
            .put("20", "猫眼商户后台")

            .put("21", "美团商城-开店宝端")
            .put("22", "钱包绑卡")
            .put("23", "美团拍店")
            .put("24", "猫眼片方")
            .put("25", "国内机票")
            .put("26", "点评账户")
            .put("27", "点评ktv")
            .put("28", "点评团购")
            .put("29", "酒景/旅游预订")
            .put("30", "综合品类")
            .put("31", "美团旅行app")
            .put("32", "猫眼演出")
            .put("33", "金融")
            .put("34", "闪惠（买单）")
            .put("35", "酒店房惠（买单）")
            .put("36", "跟团游")
            .put("37", "犀牛云盘")
            .put("38", "点评电影")
            .put("39", "点评休闲娱乐")
            .put("40", "点评亲子")

            .put("41", "物料采购")
            .put("42", "境外度假")
            .put("43", "点评酒店预订")
            .put("44", "点评餐饮预订")
            .put("45", "购物频道")
            .put("46", "一见发")
            .put("47", "推广通")
            .put("48", "广告商业活动")
            .put("49", "酷讯")
            .put("50", "点评外卖")
            .put("51", "支付手机话费充值（直充）")
            .put("52", "外卖配送、美团骑手")
            .put("53", "点评商企通")
            .put("54", "国际机票")
            .put("55", "点评结婚")
            .put("56", "风控运营平台")
            .put("57", "结婚品类（DP）")
            .put("58", "上海电影演出")
            .put("59", "鲜花配送")
            .put("60", "上海到家")

            .put("61", "上海丽人")
            .put("62", "点评点菜")
            .put("63", "智能住宿")
            .put("64", "商户账号")
            .put("65", "VPN")
            .put("66", "保险")
            .put("67", "教育培训")
            .put("68", "B2B（快驴）")
            .put("69", "点评1分钱抽奖活动")
            .put("70", "广告平台-销售系统")
            .put("71", "餐饮生态")
            .put("72", "二销当地游")
            .put("73", "B端手艺人")
            .put("74", "船票")
            .put("75", "点评到店综合营销")
            .put("76", "UGC")
            .put("77", "外卖打赏")
            .put("78", "智能POS机")
            .put("79", "餐饮ERP-POS")
            .put("80", "齿科预定")

            .put("81", "到餐变现")
            .put("82", "猫眼观影套餐")
            .put("83", "生活缴费")
            .put("84", "点评二手交易")
            .put("85", "打车-司机端-专快车")
            .put("86", "火车票抢票")
            .put("87", "猫眼商城")
            .put("88", "运动健身")
            .put("89", "点评搬家")
            .put("90", "大象红包")
            .put("91", "扫码付（智能支付）")
            .put("92", "闪付")
            .put("93", "酒旅凤凰（民宿）")
            .put("94", "爱车/乐车帮")
            .put("95", "信用付（还款）")
            .put("96", "即刻达")
            .put("97", "美甲款式一口价/泛商品交易")

            .put("100", "QA测试")
            .put("101", "海外酒店")
            .put("102", "其他")
            .put("103", "跑腿业务")
            .put("104", "天天掌柜、俏鱼")
            .put("105", "猫眼电影专业影人")
            .put("106", "HR系统")
            .put("107", "点评有礼")
            .put("108", "酒店营销类商品售卖业务")
            .put("109", "小象生鲜")
            .put("110", "汽车票")
            .put("111", "点评全家")
            .put("112", "彩票")
            .put("113", "商家钱包")
            .put("114", "二维码支付")
            .put("115", "打车-乘客端")
            .put("116", "打车-司机端-出租车")
            .put("117", "机酒")
            .put("118", "亲子业务")
            .put("119", "美团服务员")
            .put("120", "deep会员、美团优选")

            .put("121", "旅游商家后台")
            .put("122", "点评管家")
            .put("123", "点评美食")
            .put("124", "客服系统")
            .put("125", "餐饮生态M端")
            .put("126", "美团美食")
            .put("127", "兼职管理系统")
            .put("128", "通用预定")
            .put("129", "亚食联")
            .put("130", "美酒社区")
            .put("131", "定金业务")
            .put("132", "餐饮学院")
            .put("133", "公交卡业务")
            .put("134", "旅游用车")
            .put("135", "打车太仆系统")
            .put("136", "境内度假")
            .put("137", "消费金融")
            .put("138", "猫眼微信站")
            .put("139", "霸王餐")
            .put("140", "现金贷")

            .put("141", "信用住")
            .put("142", "车+X")
            .put("143", "捐款平台")
            .put("144", "打车发单")
            .put("145", "商家折扣卡")
            .put("146", "美业大学")
            .put("147", "点餐")
            .put("148", "拼团")
            .put("149", "信用卡还款")
            .put("150", "点评平台")
            .put("151", "联名卡")
            .put("152", "商场")
            .put("153", "美团管家")
            .put("154", "秒付")
            .put("155", "到餐平台B端")
            .put("156", "美团自建商城（SAAS）")
            .put("157", "小微金融")
            .put("158", "保理")
            .put("159", "小猫")
            .put("160", "租车")

            .put("161", "闪付-地铁卡")
            .put("162", "外卖红包")
            .put("163", "酒店商户第三方平台叮当")
            .put("164", "北极星开放平台(点综)")
            .put("165", "小美垫付")
            .put("166", "旅游文创")
            .put("167", "长租")
            .put("168", "社区论坛")
            .put("169", "自研小白盒")
            .put("170", "支付签到")
            .put("171", "扫码购")
            .put("172", "点评社区生活")
            .put("173", "同舟商家后台")
            .put("174", "智能音响")
            .put("175", "美团优选会员卡")
            .put("176", "安全运维平台")
            .put("177", "秒批妙用")
            .put("178", "服务商司机")
            .put("179", "商户智能支付")
            .put("180", "加盟代理物料商城")

            .put("181", "智能支付")
            .put("182", "酒旅直销")
            .put("183", "跨境业务")
            .put("184", "招财猫")
            .put("185", "点评家装")
            .put("186", "点评统计平台")
            .put("187", "高星酒店")
            .put("188", "点评婚庆小程序")
            .put("189", "馒头直聘")
            .put("190", "点评秒付点餐")
            .put("191", "点评新零售")
            .put("192", "1分钱拼团")
            .put("193", "美团众包")
            .put("194", "配送接驳系统")
            .put("195", "sso登录")
            .put("196", "集团采购供应商")
            .put("197", "速购拣货")
            .put("198", "美团平台")
            .put("199", "到综SAAS")

            .put("200", "开普勒电子发票系统")
            .put("201", "美团收银")
            .put("202", "速购事业部")
            .put("203", "点评平台小程序")
            .put("204", "极速开票")
            .put("205", "智能支付买单业务")
            .put("206", "小美智能助理")
            .put("207", "烽火台")
            .put("208", "理财业务")
            .put("209", "用户增长")
            .put("210", "行业二维码")
            .put("211", "自动绑卡")
            .put("212", "热点通产品")
            .put("213", "云店助手")
            .put("214", "闪购")
            .put("215", "酒店DNA信息采集")
            .put("216", "小美收银机")
            .put("217", "食安项目")
            .put("218", "到店餐饮")
            .put("219", "现金贷分期")
            .put("220", "到家商家会员")

            .put("221", "墨镜项目")
            .put("222", "美团自主收银")
            .put("223", "美团电子发票")
            .put("224", "扫码支付（到综）")
            .put("226", "别样红酒店")
            .put("227", "美团自建商城")
            .put("228", "美团旅行上海后台境外")
            .put("229", "餐电商")
            .put("230", "点评吃喝玩乐小程序")
            .put("231", "非合作教培商户小程序")
            .put("232", "闪购POS后台")
            .put("233", "智能门锁")
            .put("234", "宠物电商")
            .put("235", "快驴")
            .put("236", "无卡生码")
            .put("237", "服务体验")
            .put("238", "广告平台-品牌广告")
            .put("239", "结婚业务")
            .put("240", "美团排队")

            .put("241", "广告平台")
            .put("242", "美团生活费-信用付")
            .put("243", "骑手自强学堂")
            .put("244", "酒店直销")
            .put("245", "点评市场营销")
            .put("246", "到餐点餐")
            .put("247", "加盟商学院培训")
            .put("248", "海屯")
            .put("249", "全渠道会员")
            .put("250", "刷脸")
            .put("251", "生态金融营销系统")
            .put("252", "美团公益")
            .put("253", "打车红包")
            .put("254", "开电宝小程序开发者平台")
            .put("255", "美团头条")
            .put("256", "外卖智能助手")
            .put("257", "扫码购开发者中心")
            .put("258", "美团内容开发平台")
            .put("259", "金融开发者后台")
            .put("260", "餐饮开放平台-开发者中心")

            .put("261", "美团云内部员工专享")
            .put("262", "招聘官网门户")
            .put("263", "美团移动联盟")
            .put("264", "畅玩可退款")
            .put("265", "充电宝")
            .put("266", "扫码购商家端")
            .put("267", "小额储值卡")
            .put("268", "智能支付储值卡")
            .put("269", "声纹验证")
            .put("270", "小象买菜")
            .put("271", "预约订座")
            .put("272", "酒店别样红PMS系统")
            .put("273", "到餐预定")
            .put("274", "美团轻收银")
            .put("275", "美团收银青春版")
            .put("276", "美团收银零售版")
            .put("277", "美团收银专业版")
            .put("278", "美团收银茶饮版")
            .put("279", "美团收银烘焙版")
            .put("280", "金蝉系统商圈分期")

            .put("281", "加盟代理物料商城")
            .put("282", "开放平台")
            .put("283", "美团收银零售管家")
            .put("284", "红心系统")
            .put("285", "小美智能助理")
            .put("286", "外卖商家券售卖")
            .put("287", "点评侧营销短信售卖")
            .put("288", "点评琳琅悟空活动平台")
            .put("289", "外卖-海外版")
            .put("290", "盘古")

            .put("1000", "反扒mtsi")
            .build()
    ),

    APPNM(ImmutableMap.<String, String>builder()
            .put("-1", "无法获取app信息")
            .put("0", "美团主app")
            .put("1", "猫眼app")
            .put("2", "酒店app")
            .put("4", "外卖app")
            .put("5", "开店宝")
            .put("10", "美团拍店")
            .put("11", "点评主app")
            .put("12", "点评团app")
            .put("13", "酷讯机票app")
            .put("14", "犀牛网盘")
            .put("15", "快驴")
            .put("16", "美团众包app")
            .put("17", "旅游")
            .put("18", "超级火车票")
            .put("19", "手机qq")
            .put("20", "美团收银pos")

            .put("21", "天天掌柜-俏鱼")
            .put("23", "美团钱包")
            .put("24", "餐饮ERP-POS")
            .put("25", "点评管家")
            .put("26", "外卖自营加盟")
            .put("27", "打车-司机端-出租车")
            .put("28", "大象")
            .put("29", "猫眼专业版")
            .put("30", "榛果民宿")
            .put("31", "小象生鲜")
            .put("32", "跑腿业务B端APP")
            .put("33", "美团外卖商家版")
            .put("34", "彩票")
            .put("35", "打车-乘客端")
            .put("36", "打车-司机端-专快车")
            .put("37", "美团服务员")
            .put("38", "向日葵")
            .put("39", "美团旅行")
            .put("40", "美团旅行商家版")

            .put("41", "美团跑腿")
            .put("42", "海鸥APP")
            .put("43", "格瓦拉")
            .put("44", "小猫")
            .put("45", "轻收银")
            .put("46", "租车")
            .put("47", "美团骑手")
            .put("48", "外卖蜜蜂")
            .put("49", "服务商司机")
            .put("50", "美团智能收银")
            .put("51", "速购拣货")
            .put("52", "美团收银零售版")
            .put("53", "美团过机神器")
            .put("54", "LQK零售管家")
            .put("55", "LQK零售POS")
            .put("56", "LQK餐饮老板APP")
            .put("57", "LQK餐饮POS")
            .put("58", "LQK餐饮服务员")
            .put("59", "科闪零售管家")
            .put("60", "科闪零售POS")

            .put("61", "科闪餐饮老板APP")
            .put("62", "科闪餐饮POS")
            .put("63", "科闪餐饮服务员")
            .put("64", "林诺零售管家")
            .put("65", "林诺零售POS")
            .put("66", "林诺餐饮老板APP")
            .put("67", "林诺餐饮POS")
            .put("68", "林诺餐饮服务员")
            .put("69", "珍珠零售管家")
            .put("70", "珍珠零售POS")
            .put("71", "珍珠餐饮老板APP")
            .put("72", "珍珠餐饮POS")
            .put("73", "珍珠餐饮服务员")
            .put("74", "商场")
            .put("75", "热点通")
            .put("76", "美团收银管家")
            .put("77", "美团零售管家")
            .put("78", "美团轻收银管家")
            .put("79", "客满满")
            .put("80", "小美收银机")

            .put("81", "馒头直聘")
            .put("82", "闪购")
            .put("83", "VPN")
            .put("84", "火车票抢票小程序")
            .put("85", "钱进管家")
            .put("86", "钱进零售版")
            .put("87", "点评结婚")
            .put("88", "美团电子发票")
            .put("89", "电影演出赛事")
            .put("90", "美团排队")
            .put("91", "hotel_rc_checkin")
            .put("92", "美团酒店商家")
            .put("93", "海星")
            .put("94", "海屯")
            .put("95", "小象买菜")
            .put("96", "驼铃")
            .put("97", "招财树POS机")
            .put("98", "盘古")
            .build()
    );

    private Map<String, String> content;


    RiskParamEnum(ImmutableMap<String, String> content) {
        this.content = content;
    }

    public Map<String, String> getContent() {
        return content;
    }
}
