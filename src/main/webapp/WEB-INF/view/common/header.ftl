</head>
<body>

<div id="app">
    <el-container style="line-height: 12px; border: 2px solid #eee">

        <template>
        <#if okMsg??>
            <el-alert
                    title="${okMsg}"
                    type="success"
                    show-icon>
            </el-alert>
        </#if>
        <#if tipMsg??>
            <el-alert
                    title="${tipMsg}"
                    type="info"
                    show-icon>
            </el-alert>
        </#if>
        <#if errMsg??>
            <el-alert
                    title="${errMsg}"
                    type="warning"
                    show-icon>
            </el-alert>
        </#if>
        </template>

        <el-header style="padding:0 0; text-align: right; font-size: 12px">
            <el-menu
                    default-active="/"
                    background-color="#545c64"
                    text-color="#fff"
                    active-text-color="#ffd04b"
                    class="el-menu-demo"
                    mode="horizontal"
                    type="flex" justify="end"
                    @select="handleSelect">
                <el-menu-item index="/">数据平台</el-menu-item>

                </el-menu-item>
                <el-menu-item index="/service/normal/online">线上</el-menu-item>
                <el-menu-item index="/service/normal/offline">线下</el-menu-item>
            </el-menu>
        </el-header>



        <el-container>
            <el-aside width="200px">
                <el-menu default-active="${_currentURI}"
                         background-color="#545c64"
                         text-color="#fff"
                         active-text-color="#ffd04b"
                         class="el-menu-vertical-demo"
                         @select="handleSelect">
                <#if _currentMenus??>
                    <#list _currentMenus as menu>
                        <el-menu-item-group title="${menu.title}">
                            <#if menu.menus?? && menu.menus?size != 0>
                                <#list menu.menus as subMenu>
                                    <el-menu-item index="${subMenu.url}">&nbsp;&nbsp;&nbsp;${subMenu.title}
                                    </el-menu-item>
                                </#list>
                            <#else>
                                <el-menu-item index="${menu.url}">&nbsp;&nbsp;&nbsp;${menu.title}
                                </el-menu-item>
                            </#if>
                        </el-menu-item-group>
                    </#list>
                </#if>
                </el-menu>
            </el-aside>

            <el-main :span="20">




