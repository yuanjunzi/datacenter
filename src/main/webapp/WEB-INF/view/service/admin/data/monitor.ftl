<#assign pageConfig = {
"pageId" : "dataquery",
"title" : "业务指标监控"
}>
<#include "../../../common/head.ftl">
<#include "../../../common/header.ftl">

<el-row>
    <h3>业务指标监控</h3>
</el-row>

<el-row>
    <el-col :span="12">
        <table class="table">
            <tr>
                <td width="40" align="left">
                    业务指标
                </td>
                <td width="240" align="left">
                    <template>
                        <el-select v-model="monitorTemplate" @change="showSelected">
                            <el-option
                                    v-for="item in monitorTemplateList"
                                    :label="item.value"
                                    :value="item.value"
                                    :key="item.value">
                            </el-option>
                        </el-select>
                    </template>
                </td>
            </tr>
        </table>
    </el-col>
</el-row>

<el-row>
    <el-col>
        <template id="app">
            <ve-line :data="chartData" :settings="chartSettings"></ve-line>
        </template>
    </el-col>
</el-row>

<#include "../../../common/footer.ftl">
<script>
    var app = new Vue({
        el: '#app',

        data: function () {
            this.chartSettings = {
                yAxisType: ['percent']
            };
            return {
                dataFlow: [],
                data_list: [],
                titleFlow: [],

                errMsg: '',
                monitorTemplate: '',
                monitorTemplateList: [
                <#if monitorTemplateList??>
                    <#list monitorTemplateList as list>
                        {
                            value: "${list}"
                        },
                    </#list>
                </#if>],

                chartData: {
                    columns: [],
                    rows: []
                }
            }
        },

        methods: {
            showSelected: function (value) {
                var _self = this;
                if (this.monitorTemplate === '') {
                    this.$message('请选择查询模版');
                    return;
                }
                axios.post('/service/admin/data/monitor', {
                    monitorTemplate: this.monitorTemplate,
                }).then(function (response) {
                    if (response.data === null || response.data.status <= 0) {
                        _self.errMsg = response.data.data.errMsg;
                        if (_self.errMsg === '') {
                            app.$message("未知错误，请联系yuanjunzi定位问题");
                            return;
                        }
                        app.$message(_self.errMsg);
                        return;
                    }
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
                    if (response.data.data.queryType === 2) {
                        yType.push('normal');
                    } else if (response.data.data.queryType === 3) {
                        yType.push('percent');
                    }
                    _self.chartSettings.yAxisType = yType;
                    _self.chartData.rows = chartDataRows;
                    app.$message("提交成功");
                }).catch(function (error) {
                    app.$message("内部异常" + error);
                });
            },

            handleSelect: function (index, indexPath) {
                window.location.href = index;
            }
        }
    });
</script>
<#include "../../../common/foot.ftl">

