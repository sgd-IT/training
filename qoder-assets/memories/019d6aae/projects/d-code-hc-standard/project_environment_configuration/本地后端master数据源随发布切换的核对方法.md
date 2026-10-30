---
title: "本地后端master数据源随发布切换的核对方法"
usage_scenario:
    - "本地API写测试或造数前核对master生效行"
    - "上线打包前核对数据源与移动端API地址"
    - "排查接口写入库与MCP查询库不一致"
keywords:
    - "application-druid.yml"
    - "master数据源生效行"
    - "按账号路由代理"
    - "三端发布核对"
    - "git hash-object落盘验证"
source: "auto"
---

核心风险口径：本机 IDEA 后端（28080）的数据源指向会随发布动作被改动，一切 API 写测试与上线打包前必须先核对 ruoyi-framework 的 application-druid.yml 中 master 实际生效的那一行，确认写入目标是测试库还是正式库，不能凭记忆假定。
1. 曾实测 master 指向正式库 220.203.23.144:3306/tgr-gz-vue（@@hostname=anwei1-0001），192.168.250.160 开发库被注释；此后又出现反向切换记录，因此该文件的生效行是唯一可信依据。
2. 若 master 指向正式库，本机所有写操作直接落正式库，测试造数必须当场清理，并在待上正式库变更清单登记。
3. 存在按账号路由的代理层：后端进程 TCP 显示连 192.168.250.160:3306/6379，实际读写可能发生在 220.203.23.144——以数据验证（SELECT @@hostname / 业务表计数）为准，不轻信 TCP 连接目标。
4. 前端 dev server 在 localhost:80，代理 /dev-api → http://localhost:28080；PC 端 .env.production 用 VUE_APP_BASE_API='/prod-api' 相对路径，真实落点在部署服务器 nginx 反代（仓库内无 nginx.conf）。
5. 上线打包务必同时核对 application-druid.yml 的 master 生效行与移动端 API 地址常量（Android 侧定义见"移动端Android应用主备API地址配置"），避免生产包连开发库或开发机连正式库。

## Notes
- 第 1 条的最新取证：三端地址核查一轮实读磁盘，master 生效行为开发库 192.168.250.160:3306/tgr-gz-vue、生产 220.203.23.144 处于注释态（该状态已被 commit 进仓库，HEAD 与工作区一致），再次证明生效行只能现读不可沿用历史结论。
- 判"改动是否落盘"不能只看 git status/diff 干净就断定用户没改（三种可能：真无改动、编辑器未保存、沙箱 shell 吞操作）。具体编排：① Read 取界面态关键行 → ② git hash-object 工作区文件 比对 git rev-parse HEAD:<路径> 两个 blob sha → ③ Phase check：若①新值但②旧值则差异在未持久化编辑层，转为收集未落盘证据 → ④ 查看文件 mtime（停留数天前是"从未保存"的强证据）→ ⑤ 用独立进程（node/python）直接读盘打印目标行绕开管控终端管道乱码 → ⑥ 汇总三方证据向用户回报要求先 Ctrl+S 再继续构建/重启。凡涉及"改了什么"的结论一律以读盘证据为准。
