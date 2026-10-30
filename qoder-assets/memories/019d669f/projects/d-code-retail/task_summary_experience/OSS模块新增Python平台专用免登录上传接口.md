---
title: "OSS模块新增Python平台专用免登录上传接口"
usage_scenario:
    - "When integrating external platforms (e.g., AI/algorithm services) with Java backend OSS upload capability"
    - "When exposing authenticated internal APIs without requiring full user login flow"
keywords:
    - "OSS"
    - "Python platform"
    - "inner upload"
    - "X-Api-Key"
    - "SaIgnore"
source: "auto"
---

## Task description
- Core requirement: provide an OSS upload interface for the Python platform to call
- Task background: RuoYi-Vue-Plus already has SysOssController and OssUploadController; Python is an external system and should not use login-based token authentication; internal API key validation was added and later restored after user feedback

## Execution process
1. OssUploadController.java: added POST /fireworks/oss/inner/upload method with @SaIgnore annotation to bypass login
2. Added X-Api-Key header validation logic using @Value("${fireworks.inner-api-key}")
3. application.yml: added fireworks.inner-api-key configuration item in the security section
4. Verified @SaIgnore works by checking CaptchaController usage pattern
5. Discussed architectural alternatives: direct OSS key (rejected due to security and maintenance concerns) vs pre-signed URL (future option for large-file throughput)
6. Confirmed OssClient supports presign via grep of ruoyi-common-oss

## Related files
- d:\code\retail\anwei-fireworks-retail\ruoyi-modules\ruoyi-fireworks\src\main\java\org\dromara\fireworks\oss\controller\OssUploadController.java
- d:\code\retail\anwei-fireworks-retail\ruoyi-admin\src\main\resources\application.yml

## Notes
- PowerShell shell fallback fails for mvnw.cmd execution due to casting syntax in Node environment — consistent with known 'PowerShell hex dump fails in Node fallback' pitfall
- @SaIgnore annotation works independently of global excludes; verified via CaptchaController example

## Task overview
Successfully implemented a Python-platform-specific OSS upload interface: added an inner upload endpoint with SaIgnore and X-Api-Key validation; documented trade-offs between direct OSS key and pre-signed URL; confirmed underlying OSS client supports presign for future scaling.
