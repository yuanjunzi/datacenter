<#assign pageConfig = {
"pageId" : "dataquery",
"title" : "近期日志统计"
}>
<#include "../../../common/head.ftl">
<#include "../../../common/header.ftl">

<el-row>
    <h3>日志统计查询</h3>
</el-row>

<el-row type="flex">
    <el-col :span="18">
        <table class="table">
            <tr>
                <td>
                    查询模版
                </td>
                <td>
                    <template>
                        <el-select v-model="queryTemplate">
                            <el-option
                                    v-for="item in queryTemplateList"
                                    :label="item.value"
                                    :value="item.value"
                                    :key="item.value">
                            </el-option>
                        </el-select>
                    </template>
                </td>
                <td>
                    时间范围（选填）
                </td>
                <td>
                    <template>
                        <div class="block">
                            <el-date-picker
                                    v-model="timeLimit"
                                    type="datetimerange"
                                    value-format="timestamp"
                                    align="right"
                                    unlink-panels
                                    start-placeholder="开始日期"
                                    end-placeholder="结束日期"
                                    @change="getRangeTime"
                                    :picker-options="pickerOptions">
                            </el-date-picker>
                        </div>
                    </template>
                </td>
            </tr>
        </table>
    </el-col>
</el-row>

<el-row>
    <el-button type="primary" @click="showQueryResult">查询</el-button>
    <el-button @click="showCustomMode">自定义模式</el-button>
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

<el-row v-show="queryType == 1">
    <el-col :span="24">
        <el-table
                :data="dataFlow"
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
                    prop="uuid"
                    label="uuid"
                    width="120">
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
                    prop="appName"
                    label="appName"
                    width="120">
            </el-table-column>
            <el-table-column
                    prop="source"
                    label="source"
                    width="200">
            </el-table-column>
        </el-table>
    </el-col>
</el-row>

<el-row v-show="queryType == 2 || queryType == 3">
    <el-col>
        <el-select v-model="selectedShowType" @change="showSelected">
            <el-option
                    v-for="item in showTypeList"
                    :label="item.value"
                    :value="item.value"
                    :key="item.value">
            </el-option>
        </el-select>
    </el-col>
</el-row>
<el-row v-show="queryType == 2 || queryType == 3">
    <el-col>
        <template v-if="showType == 0" id="app">
            <ve-line :data="chartData" :settings="chartSettings"></ve-line>
        </template>
        <el-table v-if="showType == 1" :data="data_list" v-loading="listLoading" style="width: 100%" strip>
            <el-table-column :label="head" v-for="(head, key) in titleFlow" :key="head" width="120">
                <template slot-scope="scope">
                    {{data_list[scope.$index][key]}}
                </template>
            </el-table-column>
        </el-table>
    </el-col>
</el-row>

<el-dialog title="自定义模式" :visible.sync="detailDialogVisible" :close-on-click-modal="false">
    <el-row>
        <h3>筛选条件</h3>
    </el-row>

    <el-row>
        <el-col :span="24">
            <table class="table">
                <template v-for="ff in mustForm">
                    <tr v-if="ff.seen">
                        <td width="80" align="center">
                            AND条件
                        </td>
                        <td width="160">
                            <template>
                                <el-select v-model="ff.name">
                                    <el-option
                                            v-for="item in criticalTerms"
                                            :label="item.value"
                                            :value="item.value"
                                            :key="item.value">
                                    </el-option>
                                </el-select>
                            </template>
                        </td>
                        <td width="80" align="center">
                            字段值
                        </td>
                        <td width="160">
                            <el-input v-model="ff.value"></el-input>
                        </td>
                        <td width="40">&nbsp;&nbsp;&nbsp;</td>
                        <td v-if="ff.plusSeen">
                            <el-button icon="el-icon-plus" size="small" @click="handleMustPlusClick(ff)"></el-button>
                        </td>
                        <td v-if="!ff.plusSeen">
                            <el-button icon="el-icon-minus" size="small" @click="handleMustMinusClick(ff)"></el-button>
                        </td>
                    </tr>
                    <div v-if="ff.seen" style="margin-top: 15px;"></div>
                </template>
            </table>
        </el-col>
    </el-row>

    <el-row>
        <el-col :span="24">
            <table class="table">
                <template v-for="ff in mustNotForm">
                    <tr v-if="ff.seen">
                        <td width="80" align="center">
                            NOT条件
                        </td>
                        <td width="160">
                            <template>
                                <el-select v-model="ff.name">
                                    <el-option
                                            v-for="item in criticalTerms"
                                            :label="item.value"
                                            :value="item.value"
                                            :key="item.value">
                                    </el-option>
                                </el-select>
                            </template>
                        </td>
                        <td width="80" align="center">
                            字段值
                        </td>
                        <td width="160">
                            <el-input v-model="ff.value"></el-input>
                        </td>
                        <td width="40">&nbsp;&nbsp;&nbsp;</td>
                        <td v-if="ff.plusSeen">
                            <el-button icon="el-icon-plus" size="small" @click="handleMustNotPlusClick(ff)"></el-button>
                        </td>
                        <td v-if="!ff.plusSeen">
                            <el-button icon="el-icon-minus" size="small"
                                       @click="handleMustNotMinusClick(ff)"></el-button>
                        </td>
                    </tr>
                    <div v-if="ff.seen" style="margin-top: 15px;"></div>
                </template>
            </table>
        </el-col>
    </el-row>

    <el-row>
        <el-col :span="24">
            <table class="table">
                <template v-for="ff in shouldForm">
                    <tr v-if="ff.seen">
                        <td width="80" align="center">
                            OR条件
                        </td>
                        <td width="160">
                            <template>
                                <el-select v-model="ff.name">
                                    <el-option
                                            v-for="item in criticalTerms"
                                            :label="item.value"
                                            :value="item.value"
                                            :key="item.value">
                                    </el-option>
                                </el-select>
                            </template>
                        </td>
                        <td width="80" align="center">
                            字段值
                        </td>
                        <td width="160">
                            <el-input v-model="ff.value"></el-input>
                        </td>
                        <td width="40">&nbsp;&nbsp;&nbsp;</td>
                        <td v-if="ff.plusSeen">
                            <el-button icon="el-icon-plus" size="small" @click="handleShouldPlusClick(ff)"></el-button>
                        </td>
                        <td v-if="!ff.plusSeen">
                            <el-button icon="el-icon-minus" size="small"
                                       @click="handleShouldMinusClick(ff)"></el-button>
                        </td>
                    </tr>
                    <div v-if="ff.seen" style="margin-top: 15px;"></div>
                </template>
            </table>
        </el-col>
    </el-row>

    <el-row>
        <el-button type="primary" @click="handleQuery">查询</el-button>
        <el-button type="primary" @click="resetQueryCondition">清空</el-button>
    </el-row>

    <el-row>
        <h3>统计条件</h3>
    </el-row>

    <el-row>
        <el-col :span="24">
            <table class="table">
                <tr>
                    <td width="120" align="center">
                        分桶字段
                    </td>
                    <td width="240">
                        <template>
                            <el-select v-model="bucketTerm">
                                <el-option
                                        v-for="item in criticalTerms"
                                        :label="item.value"
                                        :value="item.value"
                                        :key="item.value">
                                </el-option>
                            </el-select>
                        </template>
                    </td>
                    <td width="80" align="center">
                        统计方式
                    </td>
                    <td width="240">
                        <template>
                            <el-select v-model="statisticType">
                                <el-option
                                        v-for="item in statisticTypes"
                                        :label="item.value"
                                        :value="item.value"
                                        :key="item.value">
                                </el-option>
                            </el-select>
                        </template>
                    </td>
                    <td width="80" align="center">
                        统计字段
                    </td>
                    <td width="240">
                        <template>
                            <el-select v-model="statisticTerm">
                                <el-option
                                        v-for="item in criticalTerms"
                                        :label="item.value"
                                        :value="item.value"
                                        :key="item.value">
                                </el-option>
                            </el-select>
                        </template>
                    </td>
                    <td>&nbsp;&nbsp;&nbsp;</td>
                </tr>
            </table>
        </el-col>
    </el-row>

    <el-row>
        <el-button type="primary" @click="handleStatistic">筛选并统计</el-button>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
    </el-row>
</el-dialog>

<#include "../../../common/footer.ftl">
<script>
    var app = new Vue({
        el: '#app',

        data: function () {
            this.chartSettings = {
                yAxisType: ['percent']
            };
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
                        text: '全量',
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(start.getTime() - 3600 * 1000 * 24 * 80);
                            picker.$emit('pick', [start, end]);
                        }
                    }]
                },
                timeLimit: [new Date().valueOf() - 3600 * 1000 * 24 * 80, new Date().valueOf()],

                queryType: 0,
                dataFlow: [],
                data_list: [],
                titleFlow: [],
                listLoading: false,
                detailDialogVisible: false,
                showType: 1,

                bucketTerm: '',
                statisticTerm: '',
                errMsg: '',
                criticalTerms: [
                <#if criticalTerms??>
                    <#list criticalTerms as list>
                        {
                            value: "${list}"
                        },
                    </#list>
                </#if>],

                queryTemplate: '',
                queryTemplateList: [
                <#if queryTemplateList??>
                    <#list queryTemplateList as list>
                        {
                            value: "${list}"
                        },
                    </#list>
                </#if>],

                statisticType: '',
                statisticTypes: [
                <#if statisticTypeList??>
                    <#list statisticTypeList as list>
                        {
                            value: "${list}"
                        },
                    </#list>
                </#if>],

                selectedShowType: '表格',
                showTypeList: [
                    {
                        value: '折线图',
                        label: '折线图'
                    },
                    {
                        value: '表格',
                        label: '表格'
                    }
                ],
                showTypeMap: [
                    {name: '折线图', type: 0},
                    {name: '表格', type: 1}
                ],

                mustForm: [
                    {index: 0, name: '', value: '', seen: true, plusSeen: true},
                    {index: 1, name: '', value: '', seen: false, plusSeen: true},
                    {index: 2, name: '', value: '', seen: false, plusSeen: true},
                    {index: 3, name: '', value: '', seen: false, plusSeen: true},
                    {index: 4, name: '', value: '', seen: false, plusSeen: true},
                    {index: 5, name: '', value: '', seen: false, plusSeen: true},
                    {index: 6, name: '', value: '', seen: false, plusSeen: true},
                    {index: 7, name: '', value: '', seen: false, plusSeen: true},
                    {index: 8, name: '', value: '', seen: false, plusSeen: true},
                    {index: 9, name: '', value: '', seen: false, plusSeen: true}
                ],

                mustNotForm: [
                    {index: 0, name: '', value: '', seen: true, plusSeen: true},
                    {index: 1, name: '', value: '', seen: false, plusSeen: true},
                    {index: 2, name: '', value: '', seen: false, plusSeen: true},
                    {index: 3, name: '', value: '', seen: false, plusSeen: true},
                    {index: 4, name: '', value: '', seen: false, plusSeen: true},
                    {index: 5, name: '', value: '', seen: false, plusSeen: true},
                    {index: 6, name: '', value: '', seen: false, plusSeen: true},
                    {index: 7, name: '', value: '', seen: false, plusSeen: true},
                    {index: 8, name: '', value: '', seen: false, plusSeen: true},
                    {index: 9, name: '', value: '', seen: false, plusSeen: true}
                ],

                shouldForm: [
                    {index: 0, name: '', value: '', seen: true, plusSeen: true},
                    {index: 1, name: '', value: '', seen: false, plusSeen: true},
                    {index: 2, name: '', value: '', seen: false, plusSeen: true},
                    {index: 3, name: '', value: '', seen: false, plusSeen: true},
                    {index: 4, name: '', value: '', seen: false, plusSeen: true},
                    {index: 5, name: '', value: '', seen: false, plusSeen: true},
                    {index: 6, name: '', value: '', seen: false, plusSeen: true},
                    {index: 7, name: '', value: '', seen: false, plusSeen: true},
                    {index: 8, name: '', value: '', seen: false, plusSeen: true},
                    {index: 9, name: '', value: '', seen: false, plusSeen: true}
                ],

                chartData: {
                    columns: [],
                    rows: []
                }
            }
        },

        methods: {

            handleMustPlusClick: function (ff) {
                if (ff.name === '' || ff.value === '') {
                    this.$message('内容不完整');
                    return;
                }
                if (ff.index + 1 < this.mustForm.length) {
                    this.mustForm[ff.index + 1].seen = true;
                    this.mustForm[ff.index].plusSeen = false;
                }
            },

            handleMustMinusClick: function (ff) {
                this.mustForm[ff.index].seen = false;
                this.mustForm[ff.index].plusSeen = true;
                this.mustForm[ff.index].name = '';
                this.mustForm[ff.index].value = '';
            },

            handleShouldPlusClick: function (ff) {
                if (ff.name === '' || ff.value === '') {
                    this.$message('内容不完整');
                    return;
                }
                if (ff.index + 1 < this.shouldForm.length) {
                    this.shouldForm[ff.index + 1].seen = true;
                    this.shouldForm[ff.index].plusSeen = false;
                }
            },

            handleShouldMinusClick: function (ff) {
                this.shouldForm[ff.index].seen = false;
                this.shouldForm[ff.index].plusSeen = true;
                this.shouldForm[ff.index].name = '';
                this.shouldForm[ff.index].value = '';
            },

            handleMustNotPlusClick: function (ff) {
                if (ff.name === '' || ff.value === '') {
                    this.$message('内容不完整');
                    return;
                }
                if (ff.index + 1 < this.mustNotForm.length) {
                    this.mustNotForm[ff.index + 1].seen = true;
                    this.mustNotForm[ff.index].plusSeen = false;
                }
            },

            handleMustNotMinusClick: function (ff) {
                this.mustNotForm[ff.index].seen = false;
                this.mustNotForm[ff.index].plusSeen = true;
                this.mustNotForm[ff.index].name = '';
                this.mustNotForm[ff.index].value = '';
            },

            showCustomMode: function () {
                this.detailDialogVisible = true;
            },

            handleQuery: function () {
                var _self = this;
                this.listLoading = true;
                var must = [];
                for (var x = 0; x < this.mustForm.length; x++) {
                    if (!this.mustForm[x].seen) {
                        continue;
                    }
                    if (this.mustForm[x].name === '' || this.mustForm[x].value === '') {
                        continue;
                    }
                    var itemX = {};
                    itemX.term = this.mustForm[x].name;
                    itemX.value = this.mustForm[x].value;
                    must.push(itemX);
                }
                var timeLimit = {};
                timeLimit.term = "timeLimit";
                timeLimit.value = this.timeLimit.toString();
                must.push(timeLimit);

                var mustNot = [];
                for (var y = 0; y < this.mustNotForm.length; y++) {
                    if (!this.mustNotForm[y].seen) {
                        continue;
                    }
                    if (this.mustNotForm[y].name === '' || this.mustNotForm[y].value === '') {
                        continue;
                    }
                    var itemY = {};
                    itemY.term = this.mustNotForm[y].name;
                    itemY.value = this.mustNotForm[y].value;
                    mustNot.push(itemY);
                }
                var should = [];
                for (var z = 0; z < this.shouldForm.length; z++) {
                    if (!this.shouldForm[z].seen) {
                        continue;
                    }
                    if (this.shouldForm[z].name === '' || this.shouldForm[z].value === '') {
                        continue;
                    }
                    var itemZ = {};
                    itemZ.term = this.shouldForm[z].name;
                    itemZ.value = this.shouldForm[z].value;
                    should.push(itemZ);
                }

                if (must.length < 1 && should.length < 1) {
                    this.listLoading = false;
                    this.$message('请输入筛选条件');
                    return;
                }
                axios.post('/service/admin/data/query', {
                    mustCondition: JSON.stringify(must),
                    shouldCondition: JSON.stringify(should),
                    mustNotCondition: JSON.stringify(mustNot),
                    operationType: '1',
                }).then(function (response) {
                    _self.listLoading = false;
                    _self.detailDialogVisible = false;
                    if (response.data.status <= 0) {
                        _self.errMsg = response.data.data.errMsg;
                        if (_self.errMsg === '') {
                            app.$message("未知错误，请联系yuanjunzi定位问题");
                            return;
                        }
                        app.$message(_self.errMsg);
                        return;
                    }
                    _self.queryType = response.data.data.queryType;
                    _self.dataFlow = response.data.data.dataFlow;
                    app.$message("提交成功!");
                }).catch(function (error) {
                    _self.listLoading = false;
                    _self.detailDialogVisible = false;
                    app.$message("内部异常" + error);
                });
            },

            showQueryResult: function () {
                var _self = this;
                this.listLoading = true;
                if (this.queryTemplate === '') {
                    this.$message('请选择查询模版或进入自定义模式');
                    this.listLoading = false;
                    return;
                }
                var must = [];
                var timeLimit = {};
                timeLimit.term = "timeLimit";
                timeLimit.value = this.timeLimit.toString();
                must.push(timeLimit);

                axios.post('/service/admin/data/query', {
                    mustCondition: JSON.stringify(must),
                    queryTemplate: this.queryTemplate,
                }).then(function (response) {
                    _self.listLoading = false;
                    _self.detailDialogVisible = false;
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
                            this.showQueryResult();
                        }).catch(() => {
                            this.$message({
                                type: 'info',
                                message: '已取消'
                            });
                        });
                        return;
                    }
                    _self.queryType = response.data.data.queryType;
                    _self.dataFlow = response.data.data.dataFlow;
                    _self.titleFlow = response.data.data.titleFlow;
                    _self.data_list = response.data.data.dataFlow;
                    _self.chartData.columns = response.data.data.titleFlow;
                    var chartDataRows = [];
                    for (var cnt = _self.data_list.length - 1; cnt > 0; cnt--) {
                        var itemList = [];
                        itemList = _self.data_list[cnt];
                        var itemMap = {};
                        for (var cnt2 = 0; cnt2 < itemList.length; cnt2++) {
                            itemMap[_self.chartData.columns[cnt2]] = itemList[cnt2];
                        }
                        chartDataRows.push(itemMap);
                    }
                    var yType = [];
                    if (_self.queryType === 2) {
                        yType.push('normal');
                    } else if (_self.queryType === 3) {
                        yType.push('percent');
                    }
                    _self.chartSettings.yAxisType = yType;
                    _self.chartData.rows = chartDataRows;
                    app.$message("提交成功");
                }).catch(function (error) {
                    _self.listLoading = false;
                    _self.detailDialogVisible = false;
                    app.$message("内部异常" + error);
                });
            },

            handleStatistic: function () {
                var _self = this;
                this.listLoading = true;
                var must = [];
                var timeLimit = {};
                timeLimit.term = "timeLimit";
                timeLimit.value = this.timeLimit.toString();
                must.push(timeLimit);
                for (var x = 0; x < this.mustForm.length; x++) {
                    if (!this.mustForm[x].seen) {
                        continue;
                    }
                    if (this.mustForm[x].name === '' || this.mustForm[x].value === '') {
                        continue;
                    }
                    var itemX = {};
                    itemX.term = this.mustForm[x].name;
                    itemX.value = this.mustForm[x].value;
                    must.push(itemX);
                }
                var mustNot = [];
                for (var y = 0; y < this.mustNotForm.length; y++) {
                    if (!this.mustNotForm[y].seen) {
                        continue;
                    }
                    if (this.mustNotForm[y].name === '' || this.mustNotForm[y].value === '') {
                        continue;
                    }
                    var itemY = {};
                    itemY.term = this.mustNotForm[y].name;
                    itemY.value = this.mustNotForm[y].value;
                    mustNot.push(itemY);
                }
                var should = [];
                for (var z = 0; z < this.shouldForm.length; z++) {
                    if (!this.shouldForm[z].seen) {
                        continue;
                    }
                    if (this.shouldForm[z].name === '' || this.shouldForm[z].value === '') {
                        continue;
                    }
                    var itemZ = {};
                    itemZ.term = this.shouldForm[z].name;
                    itemZ.value = this.shouldForm[z].value;
                    should.push(itemZ);
                }

                if (this.bucketTerm === '') {
                    this.$message('请选择分桶字段');
                    this.listLoading = false;
                    return;
                }

                if (this.statisticType === '') {
                    this.$message('请选择统计方式');
                    this.listLoading = false;
                    return;
                }

                if (this.statisticTerm === '') {
                    this.$message('请选择统计字段');
                    this.listLoading = false;
                    return;
                }

                axios.post('/service/admin/data/query', {
                    mustCondition: JSON.stringify(must),
                    shouldCondition: JSON.stringify(should),
                    mustNotCondition: JSON.stringify(mustNot),
                    operationType: '2',
                    bucketTerm: this.bucketTerm,
                    statisticType: this.statisticType,
                    statisticTerm: this.statisticTerm,
                }).then(function (response) {
                    _self.listLoading = false;
                    _self.detailDialogVisible = false;
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
                            this.handleStatistic();
                        }).catch(() => {
                            this.$message({
                                type: 'info',
                                message: '已取消'
                            });
                        });
                        return;
                    }
                    _self.queryType = response.data.data.queryType;
                    _self.titleFlow = response.data.data.titleFlow;
                    _self.data_list = response.data.data.dataFlow;
                    _self.chartData.columns = response.data.data.titleFlow;
                    var chartDataRows = [];
                    for (var cnt = _self.data_list.length - 1; cnt > 0; cnt--) {
                        var itemList = [];
                        itemList = _self.data_list[cnt];
                        var itemMap = {};
                        for (var cnt2 = 0; cnt2 < itemList.length; cnt2++) {
                            itemMap[_self.chartData.columns[cnt2]] = itemList[cnt2];
                        }
                        chartDataRows.push(itemMap);
                    }
                    var yType = [];
                    if (_self.queryType === 2) {
                        yType.push('normal');
                    } else if (_self.queryType === 3) {
                        yType.push('percent');
                    }
                    _self.chartSettings.yAxisType = yType;
                    _self.chartData.rows = chartDataRows;
                    app.$message("提交成功");
                }).catch(function (error) {
                    _self.listLoading = false;
                    _self.detailDialogVisible = false;
                    app.$message("内部异常" + error);
                });
            },

            resetQueryCondition: function () {
                for (var i = 0; i < this.mustForm.length; i++) {
                    if (!this.mustForm[i].seen) {
                        continue;
                    }
                    if (this.mustForm[i].name === '' && this.mustForm[i].value === '') {
                        continue;
                    }
                    this.mustForm[i].name = '';
                    this.mustForm[i].value = '';
                    this.mustForm[i].plusSeen = true;
                    if (i !== 0) {
                        this.mustForm[i].seen = false;
                    }
                }
                for (var j = 0; j < this.mustNotForm.length; j++) {
                    if (!this.mustNotForm[j].seen) {
                        continue;
                    }
                    if (this.mustNotForm[j].name === '' && this.mustNotForm[j].value === '') {
                        continue;
                    }
                    this.mustNotForm[j].name = '';
                    this.mustNotForm[j].value = '';
                    this.mustNotForm[j].plusSeen = true;
                    if (j !== 0) {
                        this.mustNotForm[j].seen = false;
                    }
                }
                for (var k = 0; k < this.shouldForm.length; k++) {
                    if (!this.shouldForm[k].seen) {
                        continue;
                    }
                    if (this.shouldForm[k].name === '' && this.shouldForm[k].value === '') {
                        continue;
                    }
                    this.shouldForm[k].name = '';
                    this.shouldForm[k].value = '';
                    this.shouldForm[k].plusSeen = true;
                    if (k !== 0) {
                        this.shouldForm[k].seen = false;
                    }
                }
            },

            showSelected: function (value) {
                for (var i = 0; i < this.showTypeMap.length; i++) {
                    if (this.showTypeMap[i].name === this.selectedShowType) {
                        this.showType = this.showTypeMap[i].type;
                        break;
                    }
                }
            },

            getRangeTime(value) {
                this.timeLimit = value;
            },

            handleSelect: function (index, indexPath) {
                window.location.href = index;
            }
        }
    });
</script>
<#include "../../../common/foot.ftl">

