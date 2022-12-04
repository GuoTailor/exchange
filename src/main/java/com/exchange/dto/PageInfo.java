package com.exchange.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collection;

/**
 * create by GYH on 2022/11/21
 */
public class PageInfo<T> extends ResponseInfo<Collection<T>> {
    @Schema(description = "总条数")
    private Long total;

    @Schema(description = "当前页")
    private Integer page;

    @Schema(description = "页大小")
    private Integer pageSize;

    public static <T> PageInfo<T> ok(Long total, Integer page, Integer pageSize, Collection<T> data) {
        PageInfo<T> pageInfo = new PageInfo<>();
        pageInfo.setPage(page);
        pageInfo.setTotal(total);
        pageInfo.setPageSize(pageSize);
        pageInfo.setData(data);
        pageInfo.setCode(ResponseInfo.OK_CODE);
        pageInfo.setMsg("成功");
        return pageInfo;
    }

    public static <T> PageInfo<T> ok(Long total, PageReq page, Collection<T> data) {
        PageInfo<T> pageInfo = new PageInfo<>();
        pageInfo.setPage(page.getPage());
        pageInfo.setTotal(total);
        pageInfo.setPageSize(page.getPageSize());
        pageInfo.setData(data);
        pageInfo.setCode(ResponseInfo.OK_CODE);
        pageInfo.setMsg("成功");
        return pageInfo;
    }


    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
