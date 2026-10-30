---
title: "移动端小程序必须用HBuilderX CLI发行构建且上传取build目录"
usage_scenario:
    - "重新发布微信小程序生产包时"
    - "排查真机看不到新功能UI时核对上传目录"
    - "选择运行还是发行编译方式时"
keywords:
    - "HBuilderX cli publish"
    - "mp-weixin发行构建"
    - "dist/build与dist/dev"
    - "微信开发者工具上传目录"
    - "uni-app生产包"
source: "auto"
---

移动端（anwei-fire-remote-monitor-mobile，HBuilderX 项目，根目录无 package.json）的微信小程序生产包必须由 HBuilderX CLI 发行生成：D:\Tool\HBuilderX.5.14.2026070214\HBuilderX\cli.exe publish --platform mp-weixin --project <移动端目录>。产物输出到 unpackage/dist/build/mp-weixin，构建成功后自动打开微信开发者工具；微信开发者工具 CLI 的 open --project 与上传必须指向该 build 目录。「运行」编译只刷新 unpackage/dist/dev/mp-weixin，dev 目录不会更新 build，且 dev 产物内 BASE_URL 是内网地址，直接上传 dev 目录会导致线上接口全部不通。上线前须核验 build 产物 app.json 页面清单与 mtime 晚于功能 commit。
