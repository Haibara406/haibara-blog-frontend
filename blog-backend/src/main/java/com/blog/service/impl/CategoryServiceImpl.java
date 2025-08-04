package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.constants.FunctionConst;
import com.blog.domain.dto.CategoryDTO;
import com.blog.domain.dto.SearchCategoryDTO;
import com.blog.domain.entity.Article;
import com.blog.domain.entity.Category;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.CategoryVO;
import com.blog.mapper.ArticleMapper;
import com.blog.mapper.CategoryMapper;
import com.blog.service.CategoryService;
import com.blog.utils.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author haibara
 * @description 文章分类服务实现类
 * @since 2025/7/27
 */
@Slf4j
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private CategoryMapper categoryMapper;


    /**
     * 查询所有分类
     *
     * @return vo
     */
    @Override
    public List<CategoryVO> listAllCategory() {
        List<Category> categories = this.query().list();

        if (categories.isEmpty()) {
            return List.of();
        }

        // 批量查询文章数量（复用已有的方法）
        List<Long> categoryIds = categories.stream().map(Category::getId).toList();
        Map<Long, Long> articleCountMap = getArticleCountByCategoryIds(categoryIds);

        return categories.stream()
                .map(category -> category.asViewObject(CategoryVO.class, item ->
                        item.setArticleCount(articleCountMap.getOrDefault(category.getId(), 0L))))
                .toList();
    }

    /**
     * 添加分类
     * @param categoryDTO 分类
     * @return 是否成功
     */
    @Override
    public ResponseResult<Void> addCategory(CategoryDTO categoryDTO) {
        categoryDTO.setId(null);
        if (this.save(categoryDTO.asViewObject(Category.class))) return ResponseResult.success();
        return ResponseResult.failure();
    }

    /**
     * 搜索分类
     * @param searchCategoryDTO 搜索标签DTO
     * @return 分类列表
     */
    @Override
    public List<CategoryVO> searchCategory(SearchCategoryDTO searchCategoryDTO) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(searchCategoryDTO.getCategoryName()), Category::getCategoryName, searchCategoryDTO.getCategoryName());
        if (StringUtils.isNotNull(searchCategoryDTO.getStartTime()) && StringUtils.isNotNull(searchCategoryDTO.getEndTime()))
            queryWrapper.between(Category::getCreateTime, searchCategoryDTO.getStartTime(), searchCategoryDTO.getEndTime());

        List<Category> categories = categoryMapper.selectList(queryWrapper);

        if (categories.isEmpty()) {
            return List.of();
        }

        // 批量查询文章数量
        List<Long> categoryIds = categories.stream().map(Category::getId).toList();
        Map<Long, Long> articleCountMap = getArticleCountByCategoryIds(categoryIds);

        return categories.stream()
                .map(category -> category.asViewObject(CategoryVO.class, item ->
                        item.setArticleCount(articleCountMap.getOrDefault(category.getId(), 0L))))
                .toList();
    }

    /**
     * 根据id查询
     * @param id id
     * @return 标签
     */
    @Override
    public CategoryVO getCategoryById(Long id) {
        return categoryMapper.selectById(id).asViewObject(CategoryVO.class);
    }

    /**
     * 新增或修改标签
     * @param categoryDTO 标签DTO
     * @return 是否成功
     */
    @Transactional
    @Override
    public ResponseResult<Void> addOrUpdateCategory(CategoryDTO categoryDTO) {
        if (this.saveOrUpdate(categoryDTO.asViewObject(Category.class))) return ResponseResult.success();
        return ResponseResult.failure();
    }

    /**
     * 根据id删除
     * @param ids id
     * @return 是否成功
     */
    @Transactional
    @Override
    public ResponseResult<Void> deleteCategoryByIds(List<Long> ids) {
        // 是否有剩下文章
        Long count = articleMapper.selectCount(new LambdaQueryWrapper<Article>().in(Article::getCategoryId, ids));
        if (count > 0) return ResponseResult.failure(FunctionConst.CATEGORY_EXIST_ARTICLE);
        // 执行删除
        if (this.removeByIds(ids)) return ResponseResult.success();
        return ResponseResult.failure();
    }

    /**
     * 批量查询文章数量
     */
    private Map<Long, Long> getArticleCountByCategoryIds(List<Long> categoryIds) {
        if (categoryIds.isEmpty()) return Map.of();

        return articleMapper.selectList(new LambdaQueryWrapper<Article>()
                .in(Article::getCategoryId, categoryIds))
                .stream()
                .collect(Collectors.groupingBy(Article::getCategoryId, Collectors.counting()));
    }
}
