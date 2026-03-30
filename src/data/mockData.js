export const articleCategories = ['全部', '政策法规', '农技科普', '市场动态', '补贴资讯']
export const productCategories = ['全部', '农产品', '农业生产资料', '农机设备']
export const orderStatuses = [
  '全部',
  '待付款',
  '待发货',
  '待收货',
  '已完成',
  '已取消',
  '退款中',
]

export const dashboardStats = {
  visitToday: 2841,
  salesMonth: 138820,
  pendingOrders: 23,
  publishedArticles: 47,
  articleReads: [
    { name: '政策法规', value: 4350, color: 'var(--accent)' },
    { name: '农技科普', value: 3600, color: 'var(--accent-2)' },
    { name: '市场动态', value: 2750, color: 'var(--accent-3)' },
    { name: '补贴资讯', value: 4650, color: 'var(--accent)' },
  ],
  salesByCategory: [
    { name: '农产品', value: 65000, color: 'var(--accent)' },
    { name: '农业生产资料', value: 48000, color: 'var(--info)' },
    { name: '农机设备', value: 30000, color: 'var(--warning)' },
  ],
}

export const initialArticles = [
  {
    id: 1,
    title: '2025年粮食直补政策解读：每亩补贴标准大幅提升',
    category: '政策法规',
    cover:
      'https://images.unsplash.com/photo-1500382017468-9049fed747ef?auto=format&fit=crop&w=1200&q=80',
    summary: '围绕粮食直补、良种补贴和农机补贴三方面，梳理 2025 年主要申报口径与发放节点。',
    content:
      '为进一步稳定粮食生产，2025 年多地继续提高粮食直补标准。农户可通过村委公示、乡镇农办和平台资讯栏目同步获取最新申报要求。文中对申报材料、发放时间以及常见疑问做了集中说明。',
    source: '农业农村部',
    isPush: true,
    status: '已发布',
    viewCount: 3421,
    publishedAt: '2025-03-15',
  },
  {
    id: 2,
    title: '春耕生产技术指导：小麦防倒伏关键措施',
    category: '农技科普',
    cover:
      'https://images.unsplash.com/photo-1464226184884-fa280b87c399?auto=format&fit=crop&w=1200&q=80',
    summary: '从播量控制、水肥管理、病虫害防治三个角度总结小麦防倒伏的实操建议。',
    content:
      '针对春季温度波动和大风天气，本篇文章重点介绍小麦返青拔节期的控旺、追肥和病虫预防措施，并结合实际种植案例给出建议。',
    source: '省农科院',
    isPush: false,
    status: '已发布',
    viewCount: 2108,
    publishedAt: '2025-03-14',
  },
  {
    id: 3,
    title: '3月蔬菜批发价格行情报告',
    category: '市场动态',
    cover:
      'https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=1200&q=80',
    summary: '重点监测叶菜、瓜果、根茎三类产品价格走势，帮助农户安排上市节奏。',
    content:
      '本周蔬菜批发市场整体平稳，叶菜价格受天气影响波动较大。建议种植户关注本地冷链能力和城市批发市场需求变化，合理安排采收。',
    source: '省农业信息中心',
    isPush: false,
    status: '已发布',
    viewCount: 1876,
    publishedAt: '2025-03-13',
  },
  {
    id: 4,
    title: '农机购置补贴申请流程详解',
    category: '补贴资讯',
    cover:
      'https://images.unsplash.com/photo-1500937386664-56d1dfef3854?auto=format&fit=crop&w=1200&q=80',
    summary: '梳理农机购置补贴申请流程、材料要求和注意事项。',
    content:
      '申请农机购置补贴需准备购机发票、身份证明和备案信息，平台也整理了常见机具分类与补贴比例，帮助农户减少申报失误。',
    source: '农业农村局',
    isPush: true,
    status: '已发布',
    viewCount: 4512,
    publishedAt: '2025-03-12',
  },
  {
    id: 5,
    title: '有机农业认证新规：申请材料更新说明',
    category: '政策法规',
    cover:
      'https://images.unsplash.com/photo-1523741543316-beb7fc7023d8?auto=format&fit=crop&w=1200&q=80',
    summary: '汇总有机农业认证新规中的关键变化，适合合作社和家庭农场阅读。',
    content:
      '新版有机认证规则对产地管理、投入品记录和追溯资料提出了更细要求。文中提供标准模板，便于提前准备。',
    source: '认证中心',
    isPush: false,
    status: '草稿',
    viewCount: 987,
    publishedAt: '2025-03-10',
  },
  {
    id: 6,
    title: '冬小麦返青期水肥管理技术要点',
    category: '农技科普',
    cover:
      'https://images.unsplash.com/photo-1471193945509-9ad0617afabf?auto=format&fit=crop&w=1200&q=80',
    summary: '返青期追肥和灌溉应如何配合，适合北方种植区参考。',
    content:
      '返青期是小麦形成有效穗的重要时期，建议结合苗情和墒情分次进行水肥管理，避免一次性施肥过量造成倒伏风险。',
    source: '农业技术推广站',
    isPush: false,
    status: '已下架',
    viewCount: 1542,
    publishedAt: '2025-03-08',
  },
]

export const initialProducts = [
  {
    id: 101,
    name: '优质水稻种子（5kg）',
    categoryL1: '农业生产资料',
    categoryL2: '种子种苗',
    image:
      'https://images.unsplash.com/photo-1586201375761-83865001e31c?auto=format&fit=crop&w=1200&q=80',
    detail:
      '精选高产抗病品种，适合南方双季稻区，发芽率高，附种植建议卡。',
    freightType: '包邮',
    price: 89,
    oldPrice: 120,
    stock: 342,
    salesCount: 156,
    status: '销售中',
    skus: [
      { name: '5kg', price: 89, stock: 210 },
      { name: '10kg', price: 168, stock: 132 },
    ],
  },
  {
    id: 102,
    name: '有机复合肥（25kg）',
    categoryL1: '农业生产资料',
    categoryL2: '有机肥料',
    image:
      'https://images.unsplash.com/photo-1615811361523-6bd03d7748e7?auto=format&fit=crop&w=1200&q=80',
    detail: '通用型复合肥，适用于蔬菜、果树、粮食作物，可提高土壤有机质。',
    freightType: '统一运费',
    price: 185,
    oldPrice: 210,
    stock: 280,
    salesCount: 89,
    status: '销售中',
    skus: [
      { name: '25kg', price: 185, stock: 180 },
      { name: '50kg', price: 350, stock: 100 },
    ],
  },
  {
    id: 103,
    name: '新鲜有机西红柿（5斤装）',
    categoryL1: '农产品',
    categoryL2: '新鲜果蔬',
    image:
      'https://images.unsplash.com/photo-1546470427-e5ac89cd0b6d?auto=format&fit=crop&w=1200&q=80',
    detail: '基地现摘直发，口感沙甜，适合家庭日常烹饪。',
    freightType: '包邮',
    price: 38,
    oldPrice: null,
    stock: 120,
    salesCount: 312,
    status: '销售中',
    skus: [
      { name: '5斤装', price: 38, stock: 120 },
    ],
  },
  {
    id: 104,
    name: '小型电动喷雾器',
    categoryL1: '农机设备',
    categoryL2: '植保设备',
    image:
      'https://images.unsplash.com/photo-1592982537447-7440770cbfc9?auto=format&fit=crop&w=1200&q=80',
    detail: '轻量化机身，适合果园和温室作业，续航稳定。',
    freightType: '按地区',
    price: 680,
    oldPrice: 880,
    stock: 45,
    salesCount: 23,
    status: '销售中',
    skus: [
      { name: '标准版', price: 680, stock: 30 },
      { name: '长续航版', price: 799, stock: 15 },
    ],
  },
  {
    id: 105,
    name: '农用无人机（基础版）',
    categoryL1: '农机设备',
    categoryL2: '植保设备',
    image:
      'https://images.unsplash.com/photo-1473448912268-2022ce9509d8?auto=format&fit=crop&w=1200&q=80',
    detail: '适合中小规模植保作业，支持自动航线和一键返航。',
    freightType: '按地区',
    price: 12800,
    oldPrice: null,
    stock: 8,
    salesCount: 5,
    status: '销售中',
    skus: [
      { name: '基础版', price: 12800, stock: 8 },
    ],
  },
  {
    id: 106,
    name: '绿色无公害鸡蛋（30枚）',
    categoryL1: '农产品',
    categoryL2: '禽蛋肉类',
    image:
      'https://images.unsplash.com/photo-1506976785307-8732e854ad03?auto=format&fit=crop&w=1200&q=80',
    detail: '散养土鸡蛋，蛋黄饱满，适合家庭日常补充营养。',
    freightType: '统一运费',
    price: 45,
    oldPrice: null,
    stock: 0,
    salesCount: 421,
    status: '销售中',
    skus: [
      { name: '30枚', price: 45, stock: 0 },
    ],
  },
]

export const initialUsers = [
  { id: 1001, name: '张大农', phone: '13800008821', status: '正常', createdAt: '2025-01-05', orders: 6, spend: 1247, lastActive: '2025-03-15' },
  { id: 1002, name: '李秋华', phone: '13900005612', status: '正常', createdAt: '2025-01-18', orders: 3, spend: 580, lastActive: '2025-03-14' },
  { id: 1003, name: '王建国', phone: '17700002234', status: '正常', createdAt: '2025-02-03', orders: 1, spend: 680, lastActive: '2025-03-13' },
  { id: 1004, name: '刘明珠', phone: '15800007890', status: '禁用', createdAt: '2025-02-14', orders: 0, spend: 0, lastActive: '2025-02-20' },
  { id: 1005, name: '陈旺财', phone: '18600003301', status: '正常', createdAt: '2025-03-01', orders: 2, spend: 180, lastActive: '2025-03-11' },
]

export const initialOrders = [
  {
    id: '20250315001',
    userId: 1001,
    buyer: '张大农',
    phone: '13800008821',
    items: [{ productId: 101, name: '优质水稻种子（5kg）', sku: '5kg', quantity: 2, price: 89 }],
    payAmount: 178,
    freightAmount: 0,
    status: '待发货',
    createdAt: '2025-03-15 09:22',
    shipCompany: '',
    shipNo: '',
    receiver: { name: '张大农', phone: '13800008821', address: '江苏省南京市玄武区农业路12号' },
  },
  {
    id: '20250314088',
    userId: 1002,
    buyer: '李秋华',
    phone: '13900005612',
    items: [{ productId: 102, name: '有机复合肥（25kg）', sku: '25kg', quantity: 1, price: 185 }],
    payAmount: 185,
    freightAmount: 0,
    status: '已完成',
    createdAt: '2025-03-14 14:35',
    shipCompany: '顺丰速运',
    shipNo: 'SF1234567890',
    receiver: { name: '李秋华', phone: '13900005612', address: '山东省济南市历城区农场路8号' },
  },
  {
    id: '20250313045',
    userId: 1003,
    buyer: '王建国',
    phone: '17700002234',
    items: [{ productId: 104, name: '小型电动喷雾器', sku: '标准版', quantity: 1, price: 680 }],
    payAmount: 680,
    freightAmount: 20,
    status: '待收货',
    createdAt: '2025-03-13 11:08',
    shipCompany: '中通快递',
    shipNo: 'ZT1234567890',
    receiver: { name: '王建国', phone: '17700002234', address: '河南省郑州市中原区农业大道16号' },
  },
  {
    id: '20250312019',
    userId: 1001,
    buyer: '张大农',
    phone: '13800008821',
    items: [{ productId: 103, name: '新鲜有机西红柿（5斤装）', sku: '5斤装', quantity: 3, price: 38 }],
    payAmount: 114,
    freightAmount: 0,
    status: '待付款',
    createdAt: '2025-03-12 16:47',
    shipCompany: '',
    shipNo: '',
    receiver: { name: '张大农', phone: '13800008821', address: '湖北省武汉市洪山区农科院路9号' },
  },
  {
    id: '20250310033',
    userId: 1005,
    buyer: '陈旺财',
    phone: '18600003301',
    items: [{ productId: 105, name: '农用无人机（基础版）', sku: '基础版', quantity: 1, price: 12800 }],
    payAmount: 12800,
    freightAmount: 100,
    status: '退款中',
    createdAt: '2025-03-10 10:30',
    shipCompany: '',
    shipNo: '',
    receiver: { name: '陈旺财', phone: '18600003301', address: '浙江省杭州市余杭区农业园区9号' },
  },
]

export const currentUser = {
  id: 1001,
  name: '张大农',
  phone: '13800008821',
  nickname: '丰收老张',
  avatar: '张',
  addressBook: [
    { id: 1, name: '张大农', phone: '13800008821', address: '江苏省南京市玄武区农业路12号', isDefault: true },
    { id: 2, name: '张大农', phone: '13800008821', address: '江苏省南京市栖霞区稻香路88号', isDefault: false },
  ],
}
