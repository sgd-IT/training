---
title: "RuoYi导出下载：静态直链必预览、blob吞JSON与下载文件落点"
usage_scenario:
    - "实现浏览器直接下载文件的接口时"
    - "排查导出下载到打不开的坏文件时"
    - "确认导出文件下载到本机哪个目录时"
keywords:
    - "Content-Disposition attachment"
    - "浏览器下载目录"
    - "file-saver saveAs"
    - "静态资源直链预览"
    - "responseType blob 跳过code判断"
    - "poi-tl图片占位null与空串"
source: "auto"
---

Bug class: RuoYi（Spring Boot + Vue）"导出即下载"类接口的三个必踩缺陷。
Root cause: ① 返回 Constants.RESOURCE_PREFIX（/profile/**）静态资源直链时，Spring 静态资源处理器不设置 Content-Disposition，浏览器对 Content-Type=application/pdf 一律走内置阅读器内联预览，配合 window.open 永远做不到"点击即下载"，且会被浏览器弹窗拦截；② 前端 request.js 响应拦截器对二进制响应直接 return res.data 并**跳过业务 code 判断**，后端全局异常处理器返回的是 HTTP 200 + JSON（msg），该 JSON 会被 file-saver 当作 PDF 落盘成打不开的坏文件；③ poi-tl 模板渲染阶段对占位数据零容错：给图片占位符传 null（Pictures.ofUrl(null)）抛 NullPointerException，传空字符串则被当作图片格式解析抛 Unsupported picture: . Expected emf|wmf|jpeg|png...——两种写法都会让整份导出 500。
Fix pattern: ① 下载必须走 Controller 写文件流：定位 RuoYiConfig.getProfile() + resourcePath 去掉 RESOURCE_PREFIX 后的本地路径、校验文件存在、setContentType("application/pdf") + setHeader("Content-Disposition", "attachment; filename=\"x\"; filename*=UTF-8''x")（中文名需 URLEncoder 并把 + 替换为 %20，双写 filename 与 filename* 兼容老浏览器），前端用 responseType:'blob' + saveAs；② 前端在 then 里先判 data.type.indexOf('application/json') !== -1，命中则 FileReader.readAsText 解析出 msg 弹错并 return，不落盘；③ 组装模板 Map 时对每个可能为空的图片/日期键做条件放入：值非空才 put（空时**不放键**而非放 ""），日期用三元 `obj == null ? "" : DateUtils.parseDateToStr(...)` 保护；另须确保模板目录 {profile}/template/ 与输出目录 {profile}/print/ 预先存在，否则渲染或写盘直接 FileNotFoundException。
Notes on locating the produced file: 走 responseType:'blob' + file-saver saveAs 的链路，文件只落在**浏览器自身设置的下载目录**（Edge/Chrome 默认 %USERPROFILE%\Downloads，可在浏览器"设置→下载内容"查看或修改），服务端日志与代码中查不到该路径；要核对产物内容应看服务端 {profile}/print/ 下的生成文件。若用户反馈"没看到下载文件"，先区分三种情况：a) 命中本记忆的 ① ——实际走了静态直链被内联预览而未落盘；b) 命中 ② ——落盘了但是被打成 JSON 的坏文件（同名文件大小异常小或打开报错）；c) 浏览器把重复导出的文件保存成了 xxx(1).pdf 之类带序号的名字。
Reusable lesson: Don't rely on a /profile/** static URL (or window.open) when the requirement is "click and download", because browsers inline-render PDF without Content-Disposition and popup blockers silently kill it; instead stream the file from a controller with an attachment header and guard the blob path against JSON error payloads. Template engines that bind placeholders to typed objects (poi-tl Pictures) are fail-fast on null/empty — omitting the key is the safe degradation, not substituting "". Applies to RuoYi-style projects where responses are wrapped as HTTP 200 + {code,msg,data} and the axios interceptor short-circuits on binary responseType; does not apply to endpoints that already return proper HTTP error status codes, nor to same-origin <a download> links used outside the axios layer.
