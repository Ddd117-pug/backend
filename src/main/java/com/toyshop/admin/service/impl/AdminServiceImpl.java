package com.toyshop.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.toyshop.admin.dto.AdminStatsResponse;
import com.toyshop.admin.dto.DailyTrendPoint;
import com.toyshop.admin.dto.OrderStatusStat;
import com.toyshop.admin.dto.RankItem;
import com.toyshop.admin.service.AdminService;
import com.toyshop.common.exception.BusinessException;
import com.toyshop.common.response.ResultCode;
import com.toyshop.favorite.entity.UserFavorite;
import com.toyshop.favorite.mapper.UserFavoriteMapper;
import com.toyshop.order.dto.OrderDetailResponse;
import com.toyshop.order.entity.ToyOrder;
import com.toyshop.order.entity.ToyOrderItem;
import com.toyshop.order.mapper.ToyOrderItemMapper;
import com.toyshop.order.mapper.ToyOrderMapper;
import com.toyshop.order.service.OrderService;
import com.toyshop.product.dto.PageResponse;
import com.toyshop.product.entity.Brand;
import com.toyshop.product.entity.Category;
import com.toyshop.product.entity.Product;
import com.toyshop.product.mapper.BrandMapper;
import com.toyshop.product.mapper.CategoryMapper;
import com.toyshop.product.mapper.ProductMapper;
import com.toyshop.review.dto.ReviewPageResponse;
import com.toyshop.review.entity.ProductReview;
import com.toyshop.review.mapper.ProductReviewMapper;
import com.toyshop.review.service.ReviewService;
import com.toyshop.user.entity.SysUser;
import com.toyshop.user.mapper.SysUserMapper;
import com.toyshop.address.entity.UserAddress;
import com.toyshop.address.mapper.UserAddressMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {
    private final ProductMapper productMapper;
    private final BrandMapper brandMapper;
    private final CategoryMapper categoryMapper;
    private final ProductReviewMapper productReviewMapper;
    private final UserFavoriteMapper userFavoriteMapper;
    private final ToyOrderMapper toyOrderMapper;
    private final ToyOrderItemMapper toyOrderItemMapper;
    private final SysUserMapper sysUserMapper;
    private final UserAddressMapper userAddressMapper;
    private final OrderService orderService;
    private final ReviewService reviewService;

    public AdminServiceImpl(ProductMapper productMapper,
                            BrandMapper brandMapper,
                            CategoryMapper categoryMapper,
                            ProductReviewMapper productReviewMapper,
                            UserFavoriteMapper userFavoriteMapper,
                            ToyOrderMapper toyOrderMapper,
                            ToyOrderItemMapper toyOrderItemMapper,
                            SysUserMapper sysUserMapper,
                            UserAddressMapper userAddressMapper,
                            OrderService orderService,
                            ReviewService reviewService) {
        this.productMapper = productMapper;
        this.brandMapper = brandMapper;
        this.categoryMapper = categoryMapper;
        this.productReviewMapper = productReviewMapper;
        this.userFavoriteMapper = userFavoriteMapper;
        this.toyOrderMapper = toyOrderMapper;
        this.toyOrderItemMapper = toyOrderItemMapper;
        this.sysUserMapper = sysUserMapper;
        this.userAddressMapper = userAddressMapper;
        this.orderService = orderService;
        this.reviewService = reviewService;
    }

    @Override
    public PageResponse<Product> productPage(String keyword, Long categoryId, Integer status, Integer pageNum, Integer pageSize) {
        long current = pageNum == null || pageNum < 1 ? 1L : pageNum;
        long size = pageSize == null || pageSize < 1 ? 10L : pageSize;
        QueryWrapper<Product> qw = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            qw.and(w -> w.like("name", keyword).or().like("sub_title", keyword));
        }
        if (categoryId != null) qw.eq("category_id", categoryId);
        if (status != null) qw.eq("status", status);
        qw.orderByDesc("created_at");
        Page<Product> page = productMapper.selectPage(new Page<>(current, size), qw);
        return new PageResponse<>(current, size, page.getTotal(), page.getRecords());
    }

    @Override
    public Product productDetail(Long id) {
        Product p = productMapper.selectById(id);
        if (p == null) throw new BusinessException(ResultCode.BUSINESS_ERROR, "商品不存在");
        return p;
    }

    @Override
    public Long addProduct(Product p) {
        if (!StringUtils.hasText(p.getName())) throw new BusinessException(ResultCode.VALIDATION_ERROR, "商品名称不能为空");
        if (p.getCategoryId() == null || categoryMapper.selectById(p.getCategoryId()) == null) throw new BusinessException(ResultCode.VALIDATION_ERROR, "请选择有效分类");
        if (p.getPrice() == null) throw new BusinessException(ResultCode.VALIDATION_ERROR, "商品价格不能为空");
        p.setStatus(p.getStatus() == null ? 1 : p.getStatus());
        p.setStock(p.getStock() == null ? 0 : p.getStock());
        p.setSaleCount(p.getSaleCount() == null ? 0 : p.getSaleCount());
        p.setOriginalPrice(p.getOriginalPrice() == null ? p.getPrice() : p.getOriginalPrice());
        p.setIsHot(p.getIsHot() == null ? 0 : p.getIsHot());
        p.setIsNew(p.getIsNew() == null ? 0 : p.getIsNew());
        p.setCreatedAt(LocalDateTime.now());
        p.setUpdatedAt(LocalDateTime.now());
        productMapper.insert(p);
        return p.getId();
    }

    @Override
    public PageResponse<Brand> brandPage(String keyword, Integer status, Integer featured, Integer pageNum, Integer pageSize) {
        long current = pageNum == null || pageNum < 1 ? 1L : pageNum;
        long size = pageSize == null || pageSize < 1 ? 10L : pageSize;
        QueryWrapper<Brand> qw = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) qw.and(w -> w.like("name", keyword).or().like("description", keyword).or().like("id", keyword));
        if (status != null) qw.eq("status", status);
        qw.orderByDesc("sort_order").orderByDesc("id");
        Page<Brand> page = brandMapper.selectPage(new Page<>(current, size), qw);
        page.getRecords().forEach(this::decorateBrand);
        return new PageResponse<>(current, size, page.getTotal(), page.getRecords());
    }

    @Override
    public Brand brandDetail(Long id) {
        Brand brand = brandMapper.selectByIdWithProductCount(id);
        if (brand == null) throw new BusinessException(ResultCode.BUSINESS_ERROR, "品牌不存在");
        decorateBrand(brand);
        return brand;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addBrand(Brand brand) {
        if (brand == null || !StringUtils.hasText(brand.getName())) throw new BusinessException(ResultCode.VALIDATION_ERROR, "品牌名称不能为空");
        brand.setStatus(brand.getStatus() == null ? 1 : brand.getStatus());
        brand.setSortOrder(brand.getSortOrder() == null ? 0 : brand.getSortOrder());
        if (brand.getFeatured() == null) brand.setFeatured(0);
        if (brand.getHot() == null) brand.setHot(0);
        brand.setCreatedAt(LocalDateTime.now());
        brand.setUpdatedAt(LocalDateTime.now());
        brandMapper.insert(brand);
        return brand.getId();
    }

    @Override
    public void updateBrand(Long id, Brand brand) {
        Brand db = brandMapper.selectById(id);
        if (db == null) throw new BusinessException(ResultCode.BUSINESS_ERROR, "品牌不存在");
        if (StringUtils.hasText(brand.getName())) db.setName(brand.getName());
        if (brand.getLogo() != null) db.setLogo(brand.getLogo());
        if (brand.getDescription() != null) db.setDescription(brand.getDescription());
        if (brand.getSortOrder() != null) db.setSortOrder(brand.getSortOrder());
        if (brand.getStatus() != null) db.setStatus(brand.getStatus());
        db.setUpdatedAt(LocalDateTime.now());
        brandMapper.updateById(db);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBrand(Long id) {
        Brand db = brandMapper.selectById(id);
        if (db == null) throw new BusinessException(ResultCode.BUSINESS_ERROR, "品牌不存在");
        brandMapper.deleteById(id);
    }

    @Override
    public PageResponse<Product> brandProducts(Long brandId, Integer pageNum, Integer pageSize) {
        long current = pageNum == null || pageNum < 1 ? 1L : pageNum;
        long size = pageSize == null || pageSize < 1 ? 10L : pageSize;
        QueryWrapper<Product> qw = new QueryWrapper<>();
        qw.eq("brand_id", brandId).orderByDesc("created_at");
        Page<Product> page = productMapper.selectPage(new Page<>(current, size), qw);
        return new PageResponse<>(current, size, page.getTotal(), page.getRecords());
    }

    private void decorateBrand(Brand brand) {
        if (brand == null) return;
        brand.setFeatured(brand.getFeatured() == null ? 0 : brand.getFeatured());
        brand.setHot(brand.getHot() == null ? 0 : brand.getHot());
        brand.setInitial(StringUtils.hasText(brand.getName()) ? brand.getName().substring(0, 1).toUpperCase() : null);
    }

    @Override
    public void updateProduct(Long id, Product p) {
        Product db = productDetail(id);
        if (p.getName() != null) db.setName(p.getName());
        if (p.getCategoryId() != null) db.setCategoryId(p.getCategoryId());
        if (p.getSubTitle() != null) db.setSubTitle(p.getSubTitle());
        if (p.getDescription() != null) db.setDescription(p.getDescription());
        if (p.getCoverUrl() != null) db.setCoverUrl(p.getCoverUrl());
        if (p.getBannerUrls() != null) db.setBannerUrls(p.getBannerUrls());
        if (p.getDetailImageUrls() != null) db.setDetailImageUrls(p.getDetailImageUrls());
        if (p.getPrice() != null) db.setPrice(p.getPrice());
        if (p.getOriginalPrice() != null) db.setOriginalPrice(p.getOriginalPrice());
        if (p.getStock() != null) db.setStock(p.getStock());
        if (p.getSaleCount() != null) db.setSaleCount(p.getSaleCount());
        if (p.getMaterial() != null) db.setMaterial(p.getMaterial());
        if (p.getSize() != null) db.setSize(p.getSize());
        if (p.getStyleDesc() != null) db.setStyleDesc(p.getStyleDesc());
        if (p.getIsHot() != null) db.setIsHot(p.getIsHot());
        if (p.getIsNew() != null) db.setIsNew(p.getIsNew());
        if (p.getStatus() != null) db.setStatus(p.getStatus());
        db.setUpdatedAt(LocalDateTime.now());
        productMapper.updateById(db);
    }

    @Override
    public void updateProductStatus(Long id, Integer status) {
        if (status == null || (status != 0 && status != 1)) throw new BusinessException(ResultCode.VALIDATION_ERROR, "status 只能是 0 或 1");
        Product p = productDetail(id);
        p.setStatus(status);
        p.setUpdatedAt(LocalDateTime.now());
        productMapper.updateById(p);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Long id) {
        Product p = productDetail(id);
        if (toyOrderItemMapper.selectCount(new QueryWrapper<ToyOrderItem>().eq("product_id", id)) > 0) throw new BusinessException(ResultCode.BUSINESS_ERROR, "该商品已有关联订单，不能删除");
        productReviewMapper.delete(new QueryWrapper<ProductReview>().eq("product_id", id));
        userFavoriteMapper.delete(new QueryWrapper<UserFavorite>().eq("product_id", id));
        productMapper.deleteById(p.getId());
    }

    @Override
    public List<Category> categoryList() {
        return categoryMapper.selectList(new QueryWrapper<Category>().orderByDesc("sort_order").orderByAsc("id"));
    }

    @Override
    public Long addCategory(Category c) {
        if (!StringUtils.hasText(c.getName())) throw new BusinessException(ResultCode.VALIDATION_ERROR, "分类名称不能为空");
        c.setParentId(c.getParentId() == null ? 0L : c.getParentId());
        c.setSortOrder(c.getSortOrder() == null ? 0 : c.getSortOrder());
        c.setStatus(c.getStatus() == null ? 1 : c.getStatus());
        c.setCreatedAt(LocalDateTime.now());
        c.setUpdatedAt(LocalDateTime.now());
        categoryMapper.insert(c);
        return c.getId();
    }

    @Override
    public void updateCategory(Long id, Category c) {
        Category db = categoryMapper.selectById(id);
        if (db == null) throw new BusinessException(ResultCode.BUSINESS_ERROR, "分类不存在");
        if (StringUtils.hasText(c.getName())) db.setName(c.getName());
        if (c.getParentId() != null) db.setParentId(c.getParentId());
        if (c.getSortOrder() != null) db.setSortOrder(c.getSortOrder());
        if (c.getStatus() != null) db.setStatus(c.getStatus());
        if (c.getIconUrl() != null) db.setIconUrl(c.getIconUrl());
        db.setUpdatedAt(LocalDateTime.now());
        categoryMapper.updateById(db);
    }

    @Override
    public void deleteCategory(Long id) {
        if (categoryMapper.selectById(id) == null) throw new BusinessException(ResultCode.BUSINESS_ERROR, "分类不存在");
        if (productMapper.selectCount(new QueryWrapper<Product>().eq("category_id", id)) > 0) throw new BusinessException(ResultCode.BUSINESS_ERROR, "该分类下仍有商品，不能删除");
        categoryMapper.deleteById(id);
    }

    @Override
    public PageResponse<ToyOrder> orderPage(String orderNo, Long userId, Integer status, Integer pageNum, Integer pageSize) {
        long current = pageNum == null || pageNum < 1 ? 1L : pageNum;
        long size = pageSize == null || pageSize < 1 ? 10L : pageSize;
        QueryWrapper<ToyOrder> qw = new QueryWrapper<>();
        if (StringUtils.hasText(orderNo)) qw.like("order_no", orderNo);
        if (userId != null) qw.eq("user_id", userId);
        if (status != null) qw.eq("status", status);
        qw.orderByDesc("created_at");
        Page<ToyOrder> page = toyOrderMapper.selectPage(new Page<>(current, size), qw);
        return new PageResponse<>(current, size, page.getTotal(), page.getRecords());
    }

    @Override
    public OrderDetailResponse orderDetail(Long orderId) {
        ToyOrder order = toyOrderMapper.selectById(orderId);
        if (order == null) throw new BusinessException(ResultCode.BUSINESS_ERROR, "订单不存在");
        return new OrderDetailResponse(order, toyOrderItemMapper.selectList(new QueryWrapper<ToyOrderItem>().eq("order_id", orderId)));
    }

    @Override
    public void shipOrder(Long orderId) {
        orderService.ship(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long orderId) {
        ToyOrder o = toyOrderMapper.selectById(orderId);
        if (o == null) throw new BusinessException(ResultCode.BUSINESS_ERROR, "订单不存在");
        if (o.getStatus() == null || (o.getStatus() != 0 && o.getStatus() != 1)) throw new BusinessException(ResultCode.BUSINESS_ERROR, "仅待支付或已支付订单可后台取消");
        if (o.getStatus() == 1) rollbackStockAndSaleCount(orderId);
        o.setStatus(4);
        o.setCloseTime(LocalDateTime.now());
        o.setUpdatedAt(LocalDateTime.now());
        toyOrderMapper.updateById(o);
    }

    @Override
    public PageResponse<SysUser> userPage(String keyword, Integer status, Integer pageNum, Integer pageSize) {
        long current = pageNum == null || pageNum < 1 ? 1L : pageNum;
        long size = pageSize == null || pageSize < 1 ? 10L : pageSize;
        QueryWrapper<SysUser> qw = new QueryWrapper<>();
        qw.eq("is_deleted", 0);
        if (StringUtils.hasText(keyword)) qw.and(w -> w.like("username", keyword).or().like("phone", keyword).or().like("email", keyword));
        if (status != null) qw.eq("status", status);
        qw.orderByDesc("created_at");
        Page<SysUser> page = sysUserMapper.selectPage(new Page<>(current, size), qw);
        page.getRecords().forEach(u -> u.setPassword(null));
        return new PageResponse<>(current, size, page.getTotal(), page.getRecords());
    }

    @Override
    public SysUser userDetail(Long userId) {
        SysUser u = sysUserMapper.selectOne(new QueryWrapper<SysUser>().eq("id", userId).eq("is_deleted", 0).last("limit 1"));
        if (u == null) throw new BusinessException(ResultCode.BUSINESS_ERROR, "用户不存在");
        u.setPassword(null);
        return u;
    }

    @Override
    public SysUser userDetailWithMeta(Long userId) { return userDetail(userId); }

    @Override
    public SysUser userMeta(Long userId) { return userDetail(userId); }

    @Override
    public java.util.List<java.util.Map<String, Object>> userLoginRecords(Long userId) {
        java.util.List<java.util.Map<String, Object>> rows = new java.util.ArrayList<>();
        SysUser u = userDetail(userId);
        java.util.Map<String, Object> item = new java.util.HashMap<>();
        item.put("id", 1L);
        item.put("loginAt", u.getLastLoginAt());
        item.put("createdAt", u.getLastLoginAt());
        item.put("time", u.getLastLoginAt());
        item.put("ip", "-");
        item.put("device", "系统未记录登录明细");
        item.put("success", true);
        rows.add(item);
        return rows;
    }

    @Override
    public PageResponse<ToyOrder> userRecentOrders(Long userId, Integer pageNum, Integer pageSize) {
        long current = pageNum == null || pageNum < 1 ? 1L : pageNum;
        long size = pageSize == null || pageSize < 1 ? 5L : pageSize;
        QueryWrapper<ToyOrder> qw = new QueryWrapper<>();
        qw.eq("user_id", userId).orderByDesc("created_at");
        Page<ToyOrder> page = toyOrderMapper.selectPage(new Page<>(current, size), qw);
        return new PageResponse<>(current, size, page.getTotal(), page.getRecords());
    }

    @Override
    public java.util.List<UserAddress> userAddresses(Long userId) {
        return userAddressMapper.selectList(new QueryWrapper<UserAddress>().eq("user_id", userId).orderByDesc("is_default").orderByDesc("id"));
    }

    @Override
    public void updateUserStatus(Long userId, Integer status) {
        if (status == null || (status != 0 && status != 1)) throw new BusinessException(ResultCode.VALIDATION_ERROR, "status 只能是 0 或 1");
        SysUser u = sysUserMapper.selectOne(new QueryWrapper<SysUser>().eq("id", userId).eq("is_deleted", 0).last("limit 1"));
        if (u == null) throw new BusinessException(ResultCode.BUSINESS_ERROR, "用户不存在");
        u.setStatus(status);
        u.setUpdatedAt(LocalDateTime.now());
        sysUserMapper.updateById(u);
    }

    @Override
    public void updateUserRole(Long userId, Integer role) {
        if (role == null || (role != 0 && role != 1)) throw new BusinessException(ResultCode.VALIDATION_ERROR, "role 只能是 0 或 1");
        SysUser u = sysUserMapper.selectOne(new QueryWrapper<SysUser>().eq("id", userId).eq("is_deleted", 0).last("limit 1"));
        if (u == null) throw new BusinessException(ResultCode.BUSINESS_ERROR, "用户不存在");
        u.setRole(role);
        u.setUpdatedAt(LocalDateTime.now());
        sysUserMapper.updateById(u);
    }

    @Override
    public ReviewPageResponse reviewPage(Integer pageNum, Integer pageSize, Long productId, Integer status) {
        return reviewService.adminList(pageNum, pageSize, productId, status);
    }

    @Override
    public AdminStatsResponse stats() {
        long uc = sysUserMapper.selectCount(new QueryWrapper<SysUser>().eq("is_deleted", 0));
        long pc = productMapper.selectCount(new QueryWrapper<>());
        long oc = toyOrderMapper.selectCount(new QueryWrapper<>());
        long poc = toyOrderMapper.selectCount(new QueryWrapper<ToyOrder>().eq("status", 1));
        BigDecimal amt = sumOrderAmount(new QueryWrapper<ToyOrder>().eq("status", 1));
        List<OrderStatusStat> orderStatusStats = buildOrderStatusStats();
        List<RankItem> brandSalesRank = buildBrandSalesRank();
        List<RankItem> hotProductRank = buildHotProductRank();
        return new AdminStatsResponse(uc, pc, oc, poc, amt, orderStatusStats, brandSalesRank, hotProductRank);
    }

    @Override
    public List<DailyTrendPoint> trend7d() {
        return trendByRange(LocalDate.now().minusDays(6), LocalDate.now());
    }

    @Override
    public List<OrderStatusStat> orderStatusStats() {
        return buildOrderStatusStats();
    }

    @Override
    public List<RankItem> brandSalesRank() {
        return buildBrandSalesRank();
    }

    @Override
    public List<RankItem> hotProductRank() {
        return buildHotProductRank();
    }

    private void rollbackStockAndSaleCount(Long orderId) {
        for (ToyOrderItem item : toyOrderItemMapper.selectList(new QueryWrapper<ToyOrderItem>().eq("order_id", orderId))) {
            Product p = productMapper.selectById(item.getProductId());
            if (p == null) continue;
            int qty = item.getQuantity() == null ? 0 : item.getQuantity();
            p.setStock((p.getStock() == null ? 0 : p.getStock()) + qty);
            p.setSaleCount(Math.max(0, (p.getSaleCount() == null ? 0 : p.getSaleCount()) - qty));
            p.setUpdatedAt(LocalDateTime.now());
            productMapper.updateById(p);
        }
    }

    private BigDecimal sumOrderAmount(QueryWrapper<ToyOrder> queryWrapper) {
        List<ToyOrder> orders = toyOrderMapper.selectList(queryWrapper.select("total_amount"));
        BigDecimal total = BigDecimal.ZERO;
        for (ToyOrder order : orders) {
            if (order.getTotalAmount() != null) {
                total = total.add(order.getTotalAmount());
            }
        }
        return total;
    }

    private List<DailyTrendPoint> trendByRange(LocalDate startDate, LocalDate endDate) {
        List<DailyTrendPoint> rs = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            LocalDateTime start = LocalDateTime.of(current, LocalTime.MIN);
            LocalDateTime end = LocalDateTime.of(current.plusDays(1), LocalTime.MIN);
            long orderCount = toyOrderMapper.selectCount(new QueryWrapper<ToyOrder>().ge("created_at", start).lt("created_at", end));
            BigDecimal salesAmount = sumOrderAmount(new QueryWrapper<ToyOrder>().ge("pay_time", start).lt("pay_time", end).in("status", 1, 2, 3));
            rs.add(new DailyTrendPoint(current.format(fmt), orderCount, salesAmount));
            current = current.plusDays(1);
        }
        return rs;
    }

    private List<OrderStatusStat> buildOrderStatusStats() {
        Map<Integer, String> labelMap = new HashMap<>();
        labelMap.put(0, "待付款");
        labelMap.put(1, "已支付");
        labelMap.put(2, "已发货");
        labelMap.put(3, "已完成");
        labelMap.put(4, "已取消");
        List<OrderStatusStat> stats = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : labelMap.entrySet()) {
            long count = toyOrderMapper.selectCount(new QueryWrapper<ToyOrder>().eq("status", entry.getKey()));
            stats.add(new OrderStatusStat(entry.getKey(), entry.getValue(), count));
        }
        stats.sort(Comparator.comparingInt(o -> o.getStatus() == null ? Integer.MAX_VALUE : o.getStatus()));
        return stats;
    }

    private List<RankItem> buildHotProductRank() {
        List<Product> products = productMapper.selectList(new QueryWrapper<Product>().eq("status", 1).orderByDesc("sale_count").orderByDesc("id").last("limit 5"));
        List<RankItem> result = new ArrayList<>();
        for (Product product : products) {
            result.add(new RankItem(product.getId(), product.getName(), BigDecimal.valueOf(product.getSaleCount() == null ? 0 : product.getSaleCount())));
        }
        return result;
    }

    private List<RankItem> buildBrandSalesRank() {
        List<RankItem> result = new ArrayList<>();
        List<Brand> brands = brandMapper.selectList(new QueryWrapper<Brand>().orderByDesc("sort_order").orderByAsc("id"));
        for (Brand brand : brands) {
            Long brandId = brand.getId();
            if (brandId == null) continue;
            List<Product> products = productMapper.selectList(new QueryWrapper<Product>().eq("brand_id", brandId));
            BigDecimal total = BigDecimal.ZERO;
            for (Product product : products) {
                total = total.add(BigDecimal.valueOf(product.getSaleCount() == null ? 0 : product.getSaleCount()));
            }
            result.add(new RankItem(brandId, brand.getName(), total));
        }
        result.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        if (result.size() > 5) {
            return result.subList(0, 5);
        }
        return result;
    }
}
