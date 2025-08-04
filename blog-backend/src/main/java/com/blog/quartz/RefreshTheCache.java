package com.blog.quartz;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.blog.constants.RedisConst;
import com.blog.domain.entity.Article;
import com.blog.mapper.ArticleMapper;
import com.blog.utils.RedisCache;
import jakarta.annotation.Resource;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.List;

/**
 * @author haibara
 * @description 刷新缓存任务 / 5分钟刷新一次
 * @since 2025/7/27 16:36
 */
@Slf4j
public class RefreshTheCache extends QuartzJobBean {

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private RedisCache redisCache;
    @Override
    protected void executeInternal(@NonNull JobExecutionContext context) {
        log.info("-------------------------------开始同步文章浏览量到数据库-------------------------------");
        try {
            // 获取所有文章id
            List<Long> articleIds = articleMapper.selectList(null).stream().map(Article::getId).toList();
            // 通过id从redis中获取缓存的访问量
            articleIds.forEach(id -> {
                // 把访问量设置到mysql数据库中
                Long cacheObject = Long.valueOf((Integer)redisCache.getCacheObject(RedisConst.ARTICLE_VISIT_COUNT + id));
                // 不会触发自动填充
                articleMapper.update(null,new LambdaUpdateWrapper<Article>().eq(Article::getId,id).set(Article::getVisitCount,cacheObject));
            });
            log.info("-------------------------------同步文章浏览量成功-------------------------------");
        } catch (Exception e) {
            log.error("同步文章浏览量失败",e);
        }
    }
}
