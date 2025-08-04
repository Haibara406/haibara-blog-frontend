package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.constants.FunctionConst;
import com.blog.domain.dto.SearchTagDTO;
import com.blog.domain.dto.TagDTO;
import com.blog.domain.entity.ArticleTag;
import com.blog.domain.entity.Tag;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.TagVO;
import com.blog.mapper.ArticleTagMapper;
import com.blog.mapper.TagMapper;
import com.blog.service.TagService;
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
 * @description 文章标签服务实现类
 * @since 2025/7/27
 */
@Slf4j
@Service("tagService")
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Resource
    private ArticleTagMapper articleTagMapper;

    @Resource
    private TagMapper tagMapper;


    /**
     * 查询所有标签
     * @return 标签列表
     */
    @Override
    public List<TagVO> listAllTag() {
        List<Tag> tags = this.query().list();

        if (tags.isEmpty()) {
            return List.of();
        }

        // 批量查询标签文章数量
        List<Long> tagIds = tags.stream().map(Tag::getId).toList();
        Map<Long, Long> articleCountMap = getArticleCountByTagIds(tagIds);

        return tags.stream()
                .map(tag -> tag.asViewObject(TagVO.class, item ->
                    item.setArticleCount(articleCountMap.getOrDefault(tag.getId(), 0L))))
                .toList();
    }

    /**
     * 添加标签
     * @param tagDTO 标签DTO
     * @return 是否成功
     */
    @Override
    public ResponseResult<Void> addTag(TagDTO tagDTO) {
        if (this.save(tagDTO.asViewObject(Tag.class))) return ResponseResult.success();
        return ResponseResult.failure();
    }

    /**
     * 搜索标签
     * @param searchTagDTO 搜索标签DTO
     * @return 标签列表
     */
    @Override
    public List<TagVO> searchTag(SearchTagDTO searchTagDTO) {
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(searchTagDTO.getTagName()), Tag::getTagName, searchTagDTO.getTagName());
        if (StringUtils.isNotNull(searchTagDTO.getStartTime()) && StringUtils.isNotNull(searchTagDTO.getEndTime()))
            queryWrapper.between(Tag::getCreateTime, searchTagDTO.getStartTime(), searchTagDTO.getEndTime());

        List<Tag> tags = tagMapper.selectList(queryWrapper);

        if (tags.isEmpty()) {
            return List.of();
        }

        // 批量查询标签文章数量
        List<Long> tagIds = tags.stream().map(Tag::getId).toList();
        Map<Long, Long> articleCountMap = getArticleCountByTagIds(tagIds);

        return tags.stream()
                .map(tag -> tag.asViewObject(TagVO.class, item ->
                    item.setArticleCount(articleCountMap.getOrDefault(tag.getId(), 0L))))
                .toList();
    }

    /**
     * 根据id查询
     * @param id id
     * @return 标签
     */
    @Override
    public TagVO getTagById(Long id) {
        return tagMapper.selectById(id).asViewObject(TagVO.class);
    }

    /**
     * 新增或修改标签
     * @param tagDTO 标签DTO
     * @return 是否成功
     */
    @Transactional
    @Override
    public ResponseResult<Void> addOrUpdateTag(TagDTO tagDTO) {
        if (this.saveOrUpdate(tagDTO.asViewObject(Tag.class))) return ResponseResult.success();
        return ResponseResult.failure();
    }

    /**
     * 根据id删除
     * @param ids id
     * @return 是否成功
     */
    @Transactional
    @Override
    public ResponseResult<Void> deleteTagByIds(List<Long> ids) {
        // 是否有剩下文章
        Long count = articleTagMapper.selectCount(new LambdaQueryWrapper<ArticleTag>().in(ArticleTag::getTagId, ids));
        if (count > 0) return ResponseResult.failure(FunctionConst.TAG_EXIST_ARTICLE);
        // 执行删除
        if (this.removeByIds(ids)) return ResponseResult.success();
        return ResponseResult.failure();
    }

    /**
     * 批量查询标签文章数量
     */
    private Map<Long, Long> getArticleCountByTagIds(List<Long> tagIds) {
        if (tagIds.isEmpty()) return Map.of();

        return articleTagMapper.selectList(new LambdaQueryWrapper<ArticleTag>()
                .in(ArticleTag::getTagId, tagIds))
                .stream()
                .collect(Collectors.groupingBy(ArticleTag::getTagId, Collectors.counting()));
    }
}
