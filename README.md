# Farmer Platform

基于 `Vue 3 + Vite + Pinia + Vue Router` 的智慧三农平台前端演示项目，覆盖用户端与管理端两套基础流程。

## 当前能力

- 用户端：首页、资讯列表/详情、商城、商品详情、购物车、订单、个人中心
- 管理端：数据概览、资讯管理、商品管理、订单管理、用户管理
- 认证模块：登录、注册、忘记密码、登录态持久化、用户端/管理端路由守卫
- 数据层：当前使用本地 Pinia mock 数据，便于后续替换成真实后端接口

## 内置账号

- 用户账号：`张大农`
- 管理员账号：`tfgkk`
- 默认密码：`123456`

忘记密码功能为本地模拟流程：

- 登录页点击“忘记密码？”
- 输入账号、手机号、页面展示的模拟验证码
- 设置新密码后即可重新登录

## 技术栈

- Vue 3
- Vite
- Pinia
- Vue Router
- ESLint
- Prettier

## 项目启动

```bash
npm install
npm run dev
```

开发环境默认访问：

- 用户端：`/`
- 登录页：`/auth`
- 管理端：`/admin/dashboard`

## 构建与校验

生产构建：

```bash
npm run build
```

代码检查：

```bash
npm run lint
```

## 目录说明

```text
src/
  layouts/       前后台布局
  router/        路由与守卫
  stores/        Pinia 状态管理
  data/          mock 数据
  views/
    user/        用户端页面
    admin/       管理端页面
    AuthView.vue 登录 / 注册 / 忘记密码
```

## 后续建议

- 接入真实登录、注册、短信验证码、忘记密码接口
- 给管理端增加操作日志和更细的权限控制
- 将商城、资讯、订单模块替换为真实分页接口
- 给个人中心补充地址管理表单与编辑能力
