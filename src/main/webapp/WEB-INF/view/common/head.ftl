<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width,initial-scale=1.0">
    <title>用户中心管理平台</title>

    <!-- 引入样式 -->
    <link rel="stylesheet" href="/dist/css/index.css">
    <link rel="stylesheet" href="/dist/css/style.min.css">

    <!-- 先引入 Vue -->
    <script src="/dist/js/vue.js"></script>
    <script src="/dist/js/echarts.min.js"></script>
    <script src="/dist/js/index.min.js"></script>
    <script src="/dist/js/vue-resource.min.js"></script>
    <script src="/dist/js/vue-clipboard.min.js"></script>
    <!-- 引入组件库 -->

    <!-- Main Quill library -->
    <script src="/dist/js/quill.js"></script>
    <!-- Theme included stylesheets -->
    <link href="/dist/css/quill.snow.css" rel="stylesheet">
    <link href="/dist/css/quill.bubble.css" rel="stylesheet">

    <!-- Core build with no theme, formatting, non-essential modules -->
    <link href="/dist/css/quill.core.css" rel="stylesheet">
    <script type="text/javascript" src="/dist/vue-quill-editor.js"></script>
    <script type="text/javascript">
        Vue.use(window.VueQuillEditor)
    </script>
    <script src="/dist/js/axios.js"></script>
    <script src="/dist/js/qs.js"></script>
    <script src="/dist/index.js"></script>
    <link href="/dist/css/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">

    <style>

        html,
        body,
        #app {
            height: 100%;
            margin: 0px;
            padding: 0px;
        }
        .chromeframe {
            margin: 0.2em 0;
            background: #ccc;
            color: #000;
            padding: 0.2em 0;
        }
        #loader-wrapper {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            z-index: 999999;
        }
        #loader {
            display: block;
            position: relative;
            left: 50%;
            top: 50%;
            width: 150px;
            height: 150px;
            margin: -75px 0 0 -75px;
            border-radius: 50%;
            border: 3px solid transparent;
            /* COLOR 1 */
            border-top-color: #FFF;
            -webkit-animation: spin 2s linear infinite;
            /* Chrome, Opera 15+, Safari 5+ */
            -ms-animation: spin 2s linear infinite;
            /* Chrome, Opera 15+, Safari 5+ */
            -moz-animation: spin 2s linear infinite;
            /* Chrome, Opera 15+, Safari 5+ */
            -o-animation: spin 2s linear infinite;
            /* Chrome, Opera 15+, Safari 5+ */
            animation: spin 2s linear infinite;
            /* Chrome, Firefox 16+, IE 10+, Opera */
            z-index: 1001;
        }
        #loader:before {
            content: "";
            position: absolute;
            top: 5px;
            left: 5px;
            right: 5px;
            bottom: 5px;
            border-radius: 50%;
            border: 3px solid transparent;
            /* COLOR 2 */
            border-top-color: #FFF;
            -webkit-animation: spin 3s linear infinite;
            /* Chrome, Opera 15+, Safari 5+ */
            -moz-animation: spin 3s linear infinite;
            /* Chrome, Opera 15+, Safari 5+ */
            -o-animation: spin 3s linear infinite;
            /* Chrome, Opera 15+, Safari 5+ */
            -ms-animation: spin 3s linear infinite;
            /* Chrome, Opera 15+, Safari 5+ */
            animation: spin 3s linear infinite;
            /* Chrome, Firefox 16+, IE 10+, Opera */
        }
        #loader:after {
            content: "";
            position: absolute;
            top: 15px;
            left: 15px;
            right: 15px;
            bottom: 15px;
            border-radius: 50%;
            border: 3px solid transparent;
            border-top-color: #FFF;
            /* COLOR 3 */
            -moz-animation: spin 1.5s linear infinite;
            /* Chrome, Opera 15+, Safari 5+ */
            -o-animation: spin 1.5s linear infinite;
            /* Chrome, Opera 15+, Safari 5+ */
            -ms-animation: spin 1.5s linear infinite;
            /* Chrome, Opera 15+, Safari 5+ */
            -webkit-animation: spin 1.5s linear infinite;
            /* Chrome, Opera 15+, Safari 5+ */
            animation: spin 1.5s linear infinite;
            /* Chrome, Firefox 16+, IE 10+, Opera */
        }
        @-webkit-keyframes spin {
            0% {
                -webkit-transform: rotate(0deg);
                /* Chrome, Opera 15+, Safari 3.1+ */
                -ms-transform: rotate(0deg);
                /* IE 9 */
                transform: rotate(0deg);
                /* Firefox 16+, IE 10+, Opera */
            }
            100% {
                -webkit-transform: rotate(360deg);
                /* Chrome, Opera 15+, Safari 3.1+ */
                -ms-transform: rotate(360deg);
                /* IE 9 */
                transform: rotate(360deg);
                /* Firefox 16+, IE 10+, Opera */
            }
        }
        @keyframes spin {
            0% {
                -webkit-transform: rotate(0deg);
                /* Chrome, Opera 15+, Safari 3.1+ */
                -ms-transform: rotate(0deg);
                /* IE 9 */
                transform: rotate(0deg);
                /* Firefox 16+, IE 10+, Opera */
            }
            100% {
                -webkit-transform: rotate(360deg);
                /* Chrome, Opera 15+, Safari 3.1+ */
                -ms-transform: rotate(360deg);
                /* IE 9 */
                transform: rotate(360deg);
                /* Firefox 16+, IE 10+, Opera */
            }
        }
        #loader-wrapper .loader-section {
            position: fixed;
            top: 0;
            width: 51%;
            height: 100%;
            background: #7171C6;
            /* Old browsers */
            z-index: 1000;
            -webkit-transform: translateX(0);
            /* Chrome, Opera 15+, Safari 3.1+ */
            -ms-transform: translateX(0);
            /* IE 9 */
            transform: translateX(0);
            /* Firefox 16+, IE 10+, Opera */
        }
        #loader-wrapper .loader-section.section-left {
            left: 0;
        }
        #loader-wrapper .loader-section.section-right {
            right: 0;
        }
        /* Loaded */
        .loaded #loader-wrapper .loader-section.section-left {
            -webkit-transform: translateX(-100%);
            /* Chrome, Opera 15+, Safari 3.1+ */
            -ms-transform: translateX(-100%);
            /* IE 9 */
            transform: translateX(-100%);
            /* Firefox 16+, IE 10+, Opera */
            -webkit-transition: all 0.7s 0.3s cubic-bezier(0.645, 0.045, 0.355, 1.000);
            transition: all 0.7s 0.3s cubic-bezier(0.645, 0.045, 0.355, 1.000);
        }
        .loaded #loader-wrapper .loader-section.section-right {
            -webkit-transform: translateX(100%);
            /* Chrome, Opera 15+, Safari 3.1+ */
            -ms-transform: translateX(100%);
            /* IE 9 */
            transform: translateX(100%);
            /* Firefox 16+, IE 10+, Opera */
            -webkit-transition: all 0.7s 0.3s cubic-bezier(0.645, 0.045, 0.355, 1.000);
            transition: all 0.7s 0.3s cubic-bezier(0.645, 0.045, 0.355, 1.000);
        }
        .loaded #loader {
            opacity: 0;
            -webkit-transition: all 0.3s ease-out;
            transition: all 0.3s ease-out;
        }
        .loaded #loader-wrapper {
            visibility: hidden;
            -webkit-transform: translateY(-100%);
            /* Chrome, Opera 15+, Safari 3.1+ */
            -ms-transform: translateY(-100%);
            /* IE 9 */
            transform: translateY(-100%);
            /* Firefox 16+, IE 10+, Opera */
            -webkit-transition: all 0.3s 1s ease-out;
            transition: all 0.3s 1s ease-out;
        }
        /* JavaScript Turned Off */
        .no-js #loader-wrapper {
            display: none;
        }
        .no-js h1 {
            color: #222222;
        }
        #loader-wrapper .load_title {
            font-family: 'Open Sans';
            color: #FFF;
            font-size: 19px;
            width: 100%;
            text-align: center;
            z-index: 9999999999999;
            position: absolute;
            top: 60%;
            opacity: 1;
            line-height: 30px;
        }
        #loader-wrapper .load_title span {
            font-weight: normal;
            font-style: italic;
            font-size: 13px;
            color: #FFF;
            opacity: 0.5;
        }

        .el-row {
            margin-bottom: 20px;

        &
        :last-child {
            margin-bottom: 0;
        }

        }
        .el-col {
            border-radius: 4px;
        }

        .bg-purple-dark {
            background: #99a9bf;
        }

        .bg-purple {
            background: #d3dce6;
        }

        .bg-purple-light {
            background: #e5e9f2;
        }

        .grid-content {
            border-radius: 4px;
            min-height: 36px;
        }

        .row-bg {
            padding: 10px 0;
            background-color: #f9fafc;
        }

    </style>