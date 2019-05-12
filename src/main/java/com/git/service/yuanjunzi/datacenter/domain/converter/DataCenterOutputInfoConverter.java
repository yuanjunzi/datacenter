package com.git.service.yuanjunzi.datacenter.domain.converter;

import com.alibaba.fastjson.JSONObject;
import com.git.service.yuanjunzi.datacenter.constants.ActionEnum;
import com.git.service.yuanjunzi.datacenter.constants.ErrorMsgEnum;
import com.git.service.yuanjunzi.datacenter.constants.RiskParamEnum;
import com.git.service.yuanjunzi.datacenter.domain.dto.CriticalLogTermsDTO;
import com.git.service.yuanjunzi.datacenter.infrastructure.dao.po.DataSourcePO;
import com.git.service.yuanjunzi.datacenter.presentation.vo.DataCenterVisualDataVO;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static com.git.service.yuanjunzi.datacenter.application.service.DataProcessFacade.QUERY_DETAIL_OR_STATISTIC;

/**
 * Created by yuanjunzi on 2019/4/26.
 * 数据中心查询结果翻译、分类，转化为可在前端展示的数据形式.
 */
public class DataCenterOutputInfoConverter extends Converter<DataCenterVisualDataVO, DataSourcePO> {

    private static final String TITLE = "titleFlow";

    private static final String RESULT = "dataFlow";

    private static final String RESULT_LOGIN = "dataLogin";

    private static final String RESULT_SIGNUP = "dataSignUp";

    private static final String RESULT_USER_INFO = "dataUserInfo";

    private static final String RESULT_USER_STATUS = "dataUserStatus";

    private static final String RESULT_MOBILE = "dataMobile";

    private static final String RESULT_DETAIL = "dataDetail";

    private static final String TYPE = "queryType";

    private static final String STATUS = "status";

    private static final String DATA = "data";

    private static final String ERR_MSG = "errMsg";

    public DataCenterOutputInfoConverter() {
        super(new DataCenterVisualDataVOFunction(), new DataSourcePOFunction());
    }

    static class DataCenterVisualDataVOFunction implements Function<DataCenterVisualDataVO, DataSourcePO> {

        @Override
        @SuppressWarnings("unchecked")
        public DataSourcePO apply(DataCenterVisualDataVO vo) {
            DataSourcePO po = new DataSourcePO();
            JSONObject json = vo.getJsonObject();
            po.setTitle(json.getObject(TITLE, List.class));
            po.setResult(json.getObject(RESULT, List.class));
            return po;
        }
    }

    static class DataSourcePOFunction implements Function<DataSourcePO, DataCenterVisualDataVO> {

        private final static Set<String> actionAboutLogin = ImmutableSet.<String>builder()
                .add(ActionEnum.LOGIN.name())
                .add(ActionEnum.LOGOUT.name())
                .add(ActionEnum.KICKOUT.name())
                .build();

        private final static Set<String> actionAboutSignUp = ImmutableSet.<String>builder()
                .add(ActionEnum.SIGNUP.name())
                .build();

        private final static Set<String> actionAboutModifyUserInfo = ImmutableSet.<String>builder()
                .add(ActionEnum.SET_PASSWORD.name())
                .add(ActionEnum.SET_PASSWORD_STRONG.name())
                .add(ActionEnum.RESET_PASSWORD.name())
                .add(ActionEnum.CHANGE_USERNAME.name())
                .add(ActionEnum.CHANGE_AVATAR.name())
                .add(ActionEnum.BIND_MOBILE.name())
                .add(ActionEnum.REBIND_MOBILE.name())
                .add(ActionEnum.UNBIND_MOBILE.name())
                .add(ActionEnum.BIND_EMAIL.name())
                .add(ActionEnum.REBIND_EMAIL.name())
                .add(ActionEnum.UNBIND_EMAIL.name())
                .add(ActionEnum.BIND_THIRD.name())
                .add(ActionEnum.UNBIND_THIRD.name())
                .build();

        private final static Set<String> actionAboutUserStatusChange = ImmutableSet.<String>builder()
                .add(ActionEnum.CANCEL.name())
                .add(ActionEnum.CANCEL_USER.name())
                .add(ActionEnum.UNCANCEL.name())
                .add(ActionEnum.FROZEN.name())
                .add(ActionEnum.UNFROZEN.name())
                .add(ActionEnum.LOCK.name())
                .add(ActionEnum.UNLOCK.name())
                .add(ActionEnum.BAN.name())
                .add(ActionEnum.UNBAN.name())
                .add(ActionEnum.SET_STATUS.name())
                .build();

        private final static Set<String> actionAboutUserMobileChange = ImmutableSet.<String>builder()
                .add(ActionEnum.BIND_MOBILE.name())
                .add(ActionEnum.REBIND_MOBILE.name())
                .add(ActionEnum.UNBIND_MOBILE.name())
                .build();

        @Override
        public DataCenterVisualDataVO apply(DataSourcePO po) {
            DataCenterVisualDataVO vo = new DataCenterVisualDataVO();

            JSONObject jsonObject = new JSONObject();
            JSONObject data = new JSONObject();
            jsonObject.put(STATUS, po.isSuccess() ? 1 : 0);

            if (!po.isSuccess()) {
                data.put(ERR_MSG, po.getErrMsg());
                jsonObject.put(DATA, data);
                vo.setJsonObject(jsonObject);
                return vo;
            }

            if (po.getResult() instanceof List
                    && !((List) po.getResult()).isEmpty()
                    && ((List) po.getResult()).get(0) instanceof CriticalLogTermsDTO) {
                for (int cnt = 0; cnt < ((List) po.getResult()).size(); cnt++) {
                    CriticalLogTermsDTO item = (CriticalLogTermsDTO) ((List) po.getResult()).get(cnt);
                    riskParamSolveAndClassify(item, vo);
                }
            }
            data.put(TITLE, po.getTitle());
            data.put(RESULT, po.getResult());
            data.put(RESULT_LOGIN, vo.getResultAboutLogin());
            data.put(RESULT_SIGNUP, vo.getResultAboutSignUp());
            data.put(RESULT_MOBILE, vo.getResultAboutUserMobileChange());
            data.put(RESULT_USER_INFO, vo.getResultAboutModifyUserInfo());
            data.put(RESULT_USER_STATUS, vo.getResultAboutUserStatusChange());
            if (QUERY_DETAIL_OR_STATISTIC.equals(po.getType())) {
                data.put(RESULT_DETAIL, po.getResult());
            }
            data.put(TYPE, po.getType());
            jsonObject.put(DATA, data);
            vo.setJsonObject(jsonObject);
            return vo;
        }

        private void riskParamSolveAndClassify(CriticalLogTermsDTO item, DataCenterVisualDataVO vo) {
            queryResultClassify(item, vo);

            String partnerDes = RiskParamEnum.PARTNER.getContent().get(item.getPartner());
            String appnmDes = RiskParamEnum.APPNM.getContent().get(item.getAppnm());
            String platformDes = RiskParamEnum.PLATFORM.getContent().get(item.getPlatform());
            String actionDes = ActionEnum.getActionToNameMap().get(item.getAction());
            String errMsg = ErrorMsgEnum.getErrorMsgToNameMap().get(item.getErrMsg());
            if (StringUtils.isNotBlank(partnerDes)) {
                item.setPartner(partnerDes + "(" + item.getPartner() + ")");
            }
            if (StringUtils.isNotBlank(appnmDes)) {
                item.setAppnm(appnmDes + "(" + item.getAppnm() + ")");
            }
            if (StringUtils.isNotBlank(platformDes)) {
                item.setPlatform(platformDes + "(" + item.getPlatform() + ")");
            }
            if (StringUtils.isNotBlank(actionDes)) {
                item.setAction(actionDes);
            }
            if (StringUtils.isNotBlank(errMsg)) {
                item.setErrMsg(errMsg + "(" + item.getErrMsg() + ")");
            }
        }

        private void queryResultClassify(CriticalLogTermsDTO item, DataCenterVisualDataVO vo) {
            if (actionAboutLogin.contains(item.getAction())) {
                List<CriticalLogTermsDTO> resultAboutLogin = vo.getResultAboutLogin();
                resultAboutLogin.add(item);
                vo.setResultAboutLogin(resultAboutLogin);
                return;
            }
            if (actionAboutSignUp.contains(item.getAction())) {
                List<CriticalLogTermsDTO> resultAboutSignUp = vo.getResultAboutSignUp();
                resultAboutSignUp.add(item);
                vo.setResultAboutSignUp(resultAboutSignUp);
                return;
            }
            if (actionAboutUserStatusChange.contains(item.getAction())) {
                List<CriticalLogTermsDTO> resultAboutUserStatusChange = vo.getResultAboutUserStatusChange();
                resultAboutUserStatusChange.add(item);
                vo.setResultAboutUserStatusChange(resultAboutUserStatusChange);
                return;
            }
            if (actionAboutUserMobileChange.contains(item.getAction())) {
                List<CriticalLogTermsDTO> resultAboutUserMobileChange = vo.getResultAboutUserMobileChange();
                resultAboutUserMobileChange.add(item);
                vo.setResultAboutUserMobileChange(resultAboutUserMobileChange);
                return;
            }
            if (actionAboutModifyUserInfo.contains(item.getAction())) {
                List<CriticalLogTermsDTO> resultAboutModifyUserInfo = vo.getResultAboutModifyUserInfo();
                resultAboutModifyUserInfo.add(item);
                vo.setResultAboutModifyUserInfo(resultAboutModifyUserInfo);
            }
        }
    }
}
