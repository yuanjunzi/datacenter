<#assign pageConfig = {
"pageId" : "fulllogquery",
"title" : "全量日志查询"
}>
<#include "../../../common/head.ftl">
<#include "../../../common/header.ftl">

<el-row>
    <h3>历史日志查询</h3>
</el-row>
<el-row>
    <h4>丰富查询条件 查询速度会显著加快哦</h4>
</el-row>

<el-row>
    <el-col :span="24">
        <table class="table">
            <tr>
                <td width="160" align="center">
                    查询条件
                </td>
                <td width="160" align="center">
                    <template>
                        <el-select v-model="queryDimension">
                            <el-option
                                    v-for="item in queryDimensionList"
                                    :label="item.value"
                                    :value="item.value"
                                    :key="item.value">
                            </el-option>
                        </el-select>
                    </template>
                </td>
                <td width="200" align="left">
                    <el-input v-model="queryValue"></el-input>
                </td>
                <td>&nbsp;&nbsp;&nbsp;</td>
            </tr>
            <tr>
                <td width="160" align="center">
                    日志类型（选填）
                </td>
                <td width="160" align="center">
                    <template>
                        <el-select v-model="actionName" multiple clearable placeholder="默认全选">
                            <el-option
                                    v-for="item in actionNameList"
                                    :key="item.value"
                                    :label="item.label"
                                    :value="item.value">
                            </el-option>
                        </el-select>
                    </template>
                </td>
                <td>&nbsp;&nbsp;&nbsp;</td>
            </tr>
            <tr>
                <td width="160" align="center">
                    时间范围（选填）
                </td>
                <td width="160" align="center">
                    <template>
                        <div class="block">
                            <el-date-picker
                                    v-model="timeLimit"
                                    type="daterange"
                                    value-format="timestamp"
                                    align="right"
                                    unlink-panels
                                    start-placeholder="开始日期"
                                    end-placeholder="结束日期"
                                    @change="getRangeTime"
                                    :default-time="['00:00:00', '23:59:59']"
                                    :picker-options="pickerOptions">
                            </el-date-picker>
                        </div>
                    </template>
                </td>
                <td>&nbsp;&nbsp;&nbsp;</td>
            </tr>
        </table>
    </el-col>
</el-row>

<el-row>
    <el-button type="primary" @click="handleQueryClick">查询</el-button>
</el-row>

<el-row v-show="listLoading">
    <el-col :span="24" class="el-pagination__editor">
        <div id="app">
            <div id="loader-wrapper">
                <div id="loader"></div>
                <div class="loader-section section-left"></div>
                <div class="loader-section section-right"></div>
                <div class="load_title">查询中,请耐心等待
                    <br>
                    <span>V1.9</span>
                </div>
            </div>
        </div>
    </el-col>
</el-row>

<el-row v-show="isShow">
    <el-select v-model="selectedTable" @change="showSelected">
        <el-option
                v-for="item in tableList"
                :label="item.value"
                :value="item.value"
                :key="item.value">
        </el-option>
    </el-select>
</el-row>
<el-row v-show="selected == 0">
    <el-col :span="24">
        <el-table
                :data="dataFlow.slice((dataFlowCurrentPage-1)*pageSize,dataFlowCurrentPage*pageSize)"
                style="width: 100%"
                v-loading="listLoading"
                strip>
            <el-table-column
                    prop="time"
                    label="time"
                    width="220">
            </el-table-column>
            <el-table-column
                    prop="userId"
                    label="userId"
                    width="180">
            </el-table-column>
            <el-table-column
                    prop="userName"
                    label="userName"
                    width="200">
            </el-table-column>
            <el-table-column
                    prop="mobile"
                    label="mobile"
                    width="180">
            </el-table-column>
            <el-table-column
                    prop="oldMobile"
                    label="oldMobile"
                    width="180">
            </el-table-column>
            <el-table-column
                    prop="action"
                    label="action"
                    width="100">
            </el-table-column>
            <el-table-column
                    prop="status"
                    label="status"
                    width="100">
            </el-table-column>
            <el-table-column
                    prop="errMsg"
                    label="errMsg"
                    width="180">
            </el-table-column>
            <el-table-column
                    prop="platform"
                    label="platform"
                    width="120">
            </el-table-column>
            <el-table-column
                    prop="partner"
                    label="partner"
                    width="120">
            </el-table-column>
            <el-table-column
                    prop="appnm"
                    label="appnm"
                    width="120">
            </el-table-column>
            <el-table-column
                    prop="source"
                    label="source"
                    width="200">
            </el-table-column>
            <el-table-column label="附加信息" width="120">
                <template slot-scope="scope">
                    <el-button
                            size="small"
                            @click="showDetail(scope.row)">查看
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
    </el-col>
</el-row>
<el-row v-show="selected == 0 && dataFlowTotalSize > 0">
    <el-col :span="24" class="el-pagination__editor">
        <el-pagination layout="prev, pager, next" @current-change="total_current_change" :page-size="pageSize"
                       :total="dataFlowTotalSize">
        </el-pagination>
    </el-col>
</el-row>

<el-row v-show="selected == 2">
    <el-col :span="24">
        <el-table
                :data="dataLogin.slice((dataLoginCurrentPage-1)*pageSize,dataLoginCurrentPage*pageSize)"
                style="width: 100%"
                v-loading="listLoading"
                strip>
            <el-table-column
                    prop="time"
                    label="time"
                    width="220">
            </el-table-column>
            <el-table-column
                    prop="userId"
                    label="userId"
                    width="180">
            </el-table-column>
            <el-table-column
                    prop="userName"
                    label="userName"
                    width="200">
            </el-table-column>
            <el-table-column
                    prop="mobile"
                    label="mobile"
                    width="180">
            </el-table-column>
            <el-table-column
                    prop="oldMobile"
                    label="oldMobile"
                    width="180">
            </el-table-column>
            <el-table-column
                    prop="action"
                    label="action"
                    width="100">
            </el-table-column>
            <el-table-column
                    prop="status"
                    label="status"
                    width="100">
            </el-table-column>
            <el-table-column
                    prop="errMsg"
                    label="errMsg"
                    width="180">
            </el-table-column>
            <el-table-column
                    prop="platform"
                    label="platform"
                    width="120">
            </el-table-column>
            <el-table-column
                    prop="partner"
                    label="partner"
                    width="120">
            </el-table-column>
            <el-table-column
                    prop="appnm"
                    label="appnm"
                    width="120">
            </el-table-column>
            <el-table-column
                    prop="source"
                    label="source"
                    width="200">
            </el-table-column>
            <el-table-column label="附加信息" width="120">
                <template slot-scope="scope">
                    <el-button
                            size="small"
                            @click="showDetail(scope.row)">查看
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
    </el-col>
</el-row>
<el-row v-show="selected == 2 && dataLoginTotalSize > 0">
    <el-col :span="24" class="el-pagination__editor">
        <el-pagination layout="prev, pager, next" @current-change="login_current_change" :page-size="pageSize"
                       :total="dataLoginTotalSize">
        </el-pagination>
    </el-col>
</el-row>

<el-row v-show="selected == 1">
    <el-col :span="24">
        <el-table
                :data="dataSignUp.slice((dataSignUpCurrentPage-1)*pageSize,dataSignUpCurrentPage*pageSize)"
                style="width: 100%"
                v-loading="listLoading"
                strip>
            <el-table-column
                    prop="time"
                    label="time"
                    width="220">
            </el-table-column>
            <el-table-column
                    prop="userId"
                    label="userId"
                    width="180">
            </el-table-column>
            <el-table-column
                    prop="userName"
                    label="userName"
                    width="200">
            </el-table-column>
            <el-table-column
                    prop="mobile"
                    label="mobile"
                    width="180">
            </el-table-column>
            <el-table-column
                    prop="oldMobile"
                    label="oldMobile"
                    width="180">
            </el-table-column>
            <el-table-column
                    prop="action"
                    label="action"
                    width="100">
            </el-table-column>
            <el-table-column
                    prop="status"
                    label="status"
                    width="100">
            </el-table-column>
            <el-table-column
                    prop="errMsg"
                    label="errMsg"
                    width="180">
            </el-table-column>
            <el-table-column
                    prop="platform"
                    label="platform"
                    width="120">
            </el-table-column>
            <el-table-column
                    prop="partner"
                    label="partner"
                    width="120">
            </el-table-column>
            <el-table-column
                    prop="appnm"
                    label="appnm"
                    width="120">
            </el-table-column>
            <el-table-column
                    prop="source"
                    label="source"
                    width="200">
            </el-table-column>
            <el-table-column label="附加信息" width="120">
                <template slot-scope="scope">
                    <el-button
                            size="small"
                            @click="showDetail(scope.row)">查看
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
    </el-col>
</el-row>
<el-row v-show="selected == 1 && dataSignUpTotalSize > 0">
    <el-col :span="24" class="el-pagination__editor">
        <el-pagination layout="prev, pager, next" @current-change="signup_current_change" :page-size="pageSize"
                       :total="dataSignUpTotalSize">
        </el-pagination>
    </el-col>
</el-row>

<el-row v-show="selected == 3">
    <el-col :span="24">
        <el-table
                :data="dataUserStatus.slice((dataUserStatusCurrentPage-1)*pageSize,dataUserStatusCurrentPage*pageSize)"
                style="width: 100%"
                v-loading="listLoading"
                strip>
            <el-table-column
                    prop="time"
                    label="time"
                    width="220">
            </el-table-column>
            <el-table-column
                    prop="userId"
                    label="userId"
                    width="180">
            </el-table-column>
            <el-table-column
                    prop="userName"
                    label="userName"
                    width="200">
            </el-table-column>
            <el-table-column
                    prop="mobile"
                    label="mobile"
                    width="180">
            </el-table-column>
            <el-table-column
                    prop="oldMobile"
                    label="oldMobile"
                    width="180">
            </el-table-column>
            <el-table-column
                    prop="action"
                    label="action"
                    width="100">
            </el-table-column>
            <el-table-column
                    prop="status"
                    label="status"
                    width="100">
            </el-table-column>
            <el-table-column
                    prop="errMsg"
                    label="errMsg"
                    width="180">
            </el-table-column>
            <el-table-column
                    prop="platform"
                    label="platform"
                    width="120">
            </el-table-column>
            <el-table-column
                    prop="partner"
                    label="partner"
                    width="120">
            </el-table-column>
            <el-table-column
                    prop="appnm"
                    label="appnm"
                    width="120">
            </el-table-column>
            <el-table-column
                    prop="source"
                    label="source"
                    width="200">
            </el-table-column>
            <el-table-column label="附加信息" width="120">
                <template slot-scope="scope">
                    <el-button
                            size="small"
                            @click="showDetail(scope.row)">查看
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
    </el-col>
</el-row>
<el-row v-show="selected == 3 && dataUserStatusTotalSize > 0">
    <el-col :span="24" class="el-pagination__editor">
        <el-pagination layout="prev, pager, next" @current-change="status_current_change" :page-size="pageSize"
                       :total="dataUserStatusTotalSize">
        </el-pagination>
    </el-col>
</el-row>

<el-row v-show="selected == 4">
    <el-col :span="24">
        <el-table
                :data="dataMobile.slice((dataMobileCurrentPage-1)*pageSize,dataMobileCurrentPage*pageSize)"
                style="width: 100%"
                v-loading="listLoading"
                strip>
            <el-table-column
                    prop="time"
                    label="time"
                    width="220">
            </el-table-column>
            <el-table-column
                    prop="userId"
                    label="userId"
                    width="180">
            </el-table-column>
            <el-table-column
                    prop="userName"
                    label="userName"
                    width="200">
            </el-table-column>
            <el-table-column
                    prop="mobile"
                    label="mobile"
                    width="180">
            </el-table-column>
            <el-table-column
                    prop="oldMobile"
                    label="oldMobile"
                    width="180">
            </el-table-column>
            <el-table-column
                    prop="action"
                    label="action"
                    width="100">
            </el-table-column>
            <el-table-column
                    prop="status"
                    label="status"
                    width="100">
            </el-table-column>
            <el-table-column
                    prop="errMsg"
                    label="errMsg"
                    width="180">
            </el-table-column>
            <el-table-column
                    prop="platform"
                    label="platform"
                    width="120">
            </el-table-column>
            <el-table-column
                    prop="partner"
                    label="partner"
                    width="120">
            </el-table-column>
            <el-table-column
                    prop="appnm"
                    label="appnm"
                    width="120">
            </el-table-column>
            <el-table-column
                    prop="source"
                    label="source"
                    width="200">
            </el-table-column>
            <el-table-column label="附加信息" width="120">
                <template slot-scope="scope">
                    <el-button
                            size="small"
                            @click="showDetail(scope.row)">查看
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
    </el-col>
</el-row>
<el-row v-show="selected == 4 && dataMobileTotalSize > 0">
    <el-col :span="24" class="el-pagination__editor">
        <el-pagination layout="prev, pager, next" @current-change="mobile_current_change" :page-size="pageSize"
                       :total="dataMobileTotalSize">
        </el-pagination>
    </el-col>
</el-row>

<el-row v-show="selected == 5">
    <el-col :span="24">
        <el-table
                :data="dataUserInfo.slice((dataUserInfoCurrentPage-1)*pageSize,dataUserInfoCurrentPage*pageSize)"
                style="width: 100%"
                v-loading="listLoading"
                strip>
            <el-table-column
                    prop="time"
                    label="time"
                    width="220">
            </el-table-column>
            <el-table-column
                    prop="userId"
                    label="userId"
                    width="180">
            </el-table-column>
            <el-table-column
                    prop="userName"
                    label="userName"
                    width="200">
            </el-table-column>
            <el-table-column
                    prop="mobile"
                    label="mobile"
                    width="180">
            </el-table-column>
            <el-table-column
                    prop="oldMobile"
                    label="oldMobile"
                    width="180">
            </el-table-column>
            <el-table-column
                    prop="action"
                    label="action"
                    width="100">
            </el-table-column>
            <el-table-column
                    prop="status"
                    label="status"
                    width="100">
            </el-table-column>
            <el-table-column
                    prop="errMsg"
                    label="errMsg"
                    width="180">
            </el-table-column>
            <el-table-column
                    prop="platform"
                    label="platform"
                    width="120">
            </el-table-column>
            <el-table-column
                    prop="partner"
                    label="partner"
                    width="120">
            </el-table-column>
            <el-table-column
                    prop="appnm"
                    label="appnm"
                    width="120">
            </el-table-column>
            <el-table-column
                    prop="source"
                    label="source"
                    width="200">
            </el-table-column>
            <el-table-column label="附加信息" width="120">
                <template slot-scope="scope">
                    <el-button
                            size="small"
                            @click="showDetail(scope.row)">查看
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
    </el-col>
</el-row>
<el-row v-show="selected == 5 && dataUserInfoTotalSize > 0">
    <el-col :span="24" class="el-pagination__editor">
        <el-pagination layout="prev, pager, next" @current-change="info_current_change" :page-size="pageSize"
                       :total="dataUserInfoTotalSize">
        </el-pagination>
    </el-col>
</el-row>

<el-dialog title="附加信息" :visible.sync="detailDialogVisible" :close-on-click-modal="false">
    <el-row>
        <el-col :span="24">
            <el-table
                    :data="dataDetail"
                    style="width: 100%"
                    v-loading="detailListLoading"
                    strip>
                <el-table-column
                        prop="time"
                        label="time"
                        width="220">
                </el-table-column>
                <el-table-column
                        prop="userId"
                        label="userId"
                        width="180">
                </el-table-column>
                <el-table-column
                        prop="uuid"
                        label="uuid"
                        width="180">
                </el-table-column>
                <el-table-column
                        prop="ip"
                        label="ip"
                        width="180">
                </el-table-column>
                <el-table-column
                        prop="data"
                        label="data"
                        width="500">
                </el-table-column>
                <el-table-column
                        prop="signuptype"
                        label="signuptype"
                        width="100">
                </el-table-column>
                <el-table-column
                        prop="logintype"
                        label="logintype"
                        width="180">
                </el-table-column>
                <el-table-column
                        prop="useragent"
                        label="useragent"
                        width="200">
                </el-table-column>
            </el-table>
        </el-col>
    </el-row>
    <el-row>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
    </el-row>
</el-dialog>

<#include "../../../common/footer.ftl">
<script>
    var app = new Vue({
        el: '#app',

        data: function () {
            return {
                pickerOptions: {
                    shortcuts: [{
                        text: '最近一周',
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
                            picker.$emit('pick', [start, end]);
                        }
                    }, {
                        text: '最近一个月',
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
                            picker.$emit('pick', [start, end]);
                        }
                    }, {
                        text: '最近三个月',
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
                            picker.$emit('pick', [start, end]);
                        }
                    }, {
                        text: '全量',
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date(2012, 8, 12, 0, 0);
                            picker.$emit('pick', [start, end]);
                        }
                    }]
                },
                timeLimit: [new Date(2012, 8, 12, 0, 0).valueOf(), new Date().valueOf()],

                queryValue: '',
                errMsg: '',
                queryDimension: 'USERID',

                pageSize: 15,

                queryDimensionList: [
                <#if queryDimensionList??>
                    <#list queryDimensionList as list>
                        {
                            value: "${list}"
                        },
                    </#list>
                </#if>],
                actionNameList: [
                <#if actionNameList??>
                    <#list actionNameList as list>
                        {
                            value: "${list}"
                        },
                    </#list>
                </#if>],
                dataDetail: [],
                actionName: [],

                selected: '-1',
                selectedTable: '全量日志',
                tableList: [
                    {
                        value: '全量日志',
                        label: '全量日志'
                    },
                    {
                        value: '注册日志',
                        label: '注册日志'
                    },
                    {
                        value: '登录日志',
                        label: '登录日志'
                    },
                    {
                        value: '状态变更日志',
                        label: '状态变更日志'
                    },
                    {
                        value: '换绑手机号日志',
                        label: '换绑手机号日志'
                    },
                    {
                        value: '信息修改日志',
                        label: '信息修改日志'
                    }
                ],
                selectedTableMap: [
                    {name: '全量日志', id: '0'},
                    {name: '注册日志', id: '1'},
                    {name: '登录日志', id: '2'},
                    {name: '状态变更日志', id: '3'},
                    {name: '换绑手机号日志', id: '4'},
                    {name: '信息修改日志', id: '5'}
                ],

                dataFlow: [],
                dataLogin: [],
                dataSignUp: [],
                dataUserInfo: [],
                dataUserStatus: [],
                dataMobile: [],

                dataFlowTotalSize: 0,
                dataLoginTotalSize: 0,
                dataSignUpTotalSize: 0,
                dataUserInfoTotalSize: 0,
                dataUserStatusTotalSize: 0,
                dataMobileTotalSize: 0,

                dataFlowCurrentPage: 1,
                dataLoginCurrentPage: 1,
                dataSignUpCurrentPage: 1,
                dataUserInfoCurrentPage: 1,
                dataUserStatusCurrentPage: 1,
                dataMobileCurrentPage: 1,

                form: {
                    time: '',
                    userId: '',
                    userName: '',
                    mobile: '',
                    oldMobile: '',
                    action: '',
                    status: '',
                    errMsg: '',
                    platform: '',
                    partner: '',
                    appnm: '',
                    source: '',
                },

                isShow: false,
                listLoading: false,
                detailListLoading: false,
                detailDialogVisible: false
            }
        },

        methods: {
            handleQueryClick() {
                var _self = this;
                this.listLoading = true;
                var aa = [];

                if (this.queryValue === '') {
                    this.listLoading = false;
                    this.$message('请输入查询条件');
                    return;
                }
                var queryDimension = {};
                queryDimension.term = 'queryDimension';
                queryDimension.value = this.queryDimension;
                aa.push(queryDimension);
                if (this.queryDimension === 'USERID') {
                    var userId = {};
                    userId.term = 'userid';
                    userId.value = this.queryValue;
                    aa.push(userId);
                } else {
                    var mobile = {};
                    mobile.term = 'mobile';
                    mobile.value = this.queryValue;
                    aa.push(mobile);
                }

                if (this.actionName.length !== 0) {
                    var actionName = {};
                    actionName.term = 'actionName';
                    actionName.value = this.actionName.toString();
                    aa.push(actionName);
                }

                if (this.timeLimit.length !== 0) {
                    var timeLimit = {};
                    timeLimit.term = 'timeLimit';
                    timeLimit.value = this.timeLimit.toString();
                    aa.push(timeLimit);
                }


                axios.post('/service/admin/data/fulllogquery', {
                    mustCondition: JSON.stringify(aa),
                }).then(function (response) {
                    _self.listLoading = false;
                    _self.detailDialogVisible = false;

                    if (response.data.status <= 0) {
                        _self.errMsg = response.data.data.errMsg;
                        if (_self.errMsg === '' || _self.errMsg === null) {
                            app.$message("未知错误，请联系yuanjunzi定位问题");
                            _self.isShow = false;
                            _self.selected = '-1';
                            return;
                        }
                        app.$message(_self.errMsg);
                        _self.isShow = false;
                        _self.selected = '-1';
                        this.$confirm(_self.errMsg + '。继续查询很有可能获得数据, 是否继续?', '提示', {
                            confirmButtonText: '确定',
                            cancelButtonText: '取消',
                            type: 'warning'
                        }).then(() => {
                            this.$message({
                                type: 'success',
                                message: '数据量巨大，请耐心等待!'
                            });
                            this.handleQueryClick();
                        }).catch(() => {
                            this.$message({
                                type: 'info',
                                message: '已取消'
                            });
                        });
                        return;
                    }
                    _self.selected = '0';
                    _self.selectedTable = '全量日志';
                    _self.isShow = true;
                    _self.dataFlow = response.data.data.dataFlow;
                    _self.dataLogin = response.data.data.dataLogin;
                    _self.dataSignUp = response.data.data.dataSignUp;
                    _self.dataUserInfo = response.data.data.dataUserInfo;
                    _self.dataUserStatus = response.data.data.dataUserStatus;
                    _self.dataMobile = response.data.data.dataMobile;

                    _self.dataFlowTotalSize = _self.dataFlow.length;
                    _self.dataLoginTotalSize = _self.dataLogin.length;
                    _self.dataSignUpTotalSize = _self.dataSignUp.length;
                    _self.dataUserInfoTotalSize = _self.dataUserInfo.length;
                    _self.dataUserStatusTotalSize = _self.dataUserStatus.length;
                    _self.dataMobileTotalSize = _self.dataMobile.length;

                    _self.dataFlowCurrentPage = 1;
                    _self.dataLoginCurrentPage = 1;
                    _self.dataSignUpCurrentPage = 1;
                    _self.dataUserInfoCurrentPage = 1;
                    _self.dataUserStatusCurrentPage = 1;
                    _self.dataMobileCurrentPage = 1;

                    app.$message("提交成功");
                }).catch(function (error) {
                    _self.selected = '-1';
                    _self.listLoading = false;
                    _self.detailDialogVisible = false;
                    _self.isShow = false;
                    app.$message("内部异常" + error);
                });
            },

            showDetail: function (row) {
                var _self = this;
                this.detailDialogVisible = true;
                this.detailListLoading = true;
                this.form = Object.assign({}, row);

                var aa = [];

                var userId = {};
                userId.term = 'userid';
                userId.value = this.form.userId;
                aa.push(userId);

                var tableName = {};
                tableName.term = 'tableName';
                tableName.value = 'bp_user_detailedpassport';
                aa.push(tableName);

                var time = {};
                time.term = 'mt_datetime';
                time.value = this.form.time;
                aa.push(time);

                var action = {};
                action.term = 'action';
                action.value = this.form.action;
                aa.push(action);

                axios.post('/service/admin/data/fulllogquery', {
                    mustCondition: JSON.stringify(aa),
                }).then(function (response) {
                    _self.detailListLoading = false;
                    if (response.data.status <= 0) {
                        _self.errMsg = response.data.data.errMsg;
                        if (_self.errMsg === '') {
                            app.$message("未知错误，请联系yuanjunzi定位问题");
                            return;
                        }
                        this.$confirm(_self.errMsg + '。继续查询很有可能获得数据, 是否继续?', '提示', {
                            confirmButtonText: '确定',
                            cancelButtonText: '取消',
                            type: 'warning'
                        }).then(() => {
                            this.$message({
                                type: 'success',
                                message: '数据量巨大，请耐心等待!'
                            });
                            this.showDetail(row);
                        }).catch(() => {
                            this.$message({
                                type: 'info',
                                message: '已取消'
                            });
                        });
                        return;
                    }
                    _self.dataDetail = response.data.data.dataDetail;
                    app.$message("提交成功");
                }).catch(function (error) {
                    app.$message("内部异常" + error);
                });
            },

            total_current_change: function (currentPage) {
                this.dataFlowCurrentPage = currentPage;
            },

            login_current_change: function (currentPage) {
                this.dataLoginCurrentPage = currentPage;
            },

            signup_current_change: function (currentPage) {
                this.dataSignUpCurrentPage = currentPage;
            },

            mobile_current_change: function (currentPage) {
                this.dataMobileCurrentPage = currentPage;
            },

            info_current_change: function (currentPage) {
                this.dataUserInfoCurrentPage = currentPage;
            },

            status_current_change: function (currentPage) {
                this.dataUserStatusCurrentPage = currentPage;
            },

            showSelected: function (value) {
                for (var i = 0; i < this.selectedTableMap.length; i++) {
                    if (this.selectedTableMap[i].name === this.selectedTable) {
                        this.selected = this.selectedTableMap[i].id;
                        break;
                    }
                }
            },

            getRangeTime(value) {
                this.timeLimit = value;
            },

            handleSelect(index, indexPath) {
                window.location.href = index;
            }
        }
    });
</script>
<#include "../../../common/foot.ftl">

