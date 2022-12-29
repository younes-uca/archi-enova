package com.archi.smart.archzy.criteria;

import com.archi.smart.archzy.export.ExportModel;

import java.io.Serializable;
import java.util.List;

public class BaseCriteria implements Serializable {

    protected Long etablissementId;
    private Long id;
    private Long notId;
    private String[] orderByAsc;
    private String[] orderByDesc;
    private List<Long> idsIn;
    private List<Long> idsNotIn;
    private boolean log;
    private Integer maxResults;
    private Integer page;
    private String sortField;
    private String sortOrder;
    private boolean peagable;
    private String filterName;
    private String filterWord;
    private ExportModel exportModel;
    private String[] includes;
    private String[] excludes;

    public Long getEtablissementId() {
        return this.etablissementId;
    }

    public void setEtablissementId(Long etablissementId) {
        this.etablissementId = etablissementId;
    }


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isLog() {
        return this.log;
    }

    public void setLog(boolean log) {
        this.log = log;
    }

    public Long getNotId() {
        return this.notId;
    }

    public void setNotId(Long notId) {
        this.notId = notId;
    }

    public String[] getOrderByAsc() {
        return this.orderByAsc;
    }

    public void setOrderByAsc(String[] orderByAsc) {
        this.orderByAsc = orderByAsc;
    }

    public String[] getOrderByDesc() {
        return this.orderByDesc;
    }

    public void setOrderByDesc(String[] orderByDesc) {
        this.orderByDesc = orderByDesc;
    }

    public List<Long> getIdsIn() {
        return this.idsIn;
    }

    public void setIdsIn(List<Long> idsIn) {
        this.idsIn = idsIn;
    }

    public List<Long> getIdsNotIn() {
        return this.idsNotIn;
    }

    public void setIdsNotIn(List<Long> idsNotIn) {
        this.idsNotIn = idsNotIn;
    }

    public Integer getMaxResults() {
        return this.maxResults;
    }

    public void setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
    }

    public Integer getPage() {
        return this.page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String getSortField() {
        return this.sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortOrder() {
        return this.sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isPeagable() {
        if (this.maxResults != null && this.maxResults > 0)
            this.peagable = true;
        return this.peagable;
    }

    public void setPeagable(boolean peagable) {
        this.peagable = peagable;
    }

    public String getFilterName() {
        return this.filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public String getFilterWord() {
        return this.filterWord;
    }

    public void setFilterWord(String filterWord) {
        this.filterWord = filterWord;
    }

    public ExportModel getExportModel() {
        return exportModel;
    }

    public void setExportModel(ExportModel exportModel) {
        this.exportModel = exportModel;
    }

    public String[] getIncludes() {
        return includes;
    }

    public void setIncludes(String[] includes) {
        this.includes = includes;
    }

    public String[] getExcludes() {
        return excludes;
    }

    public void setExcludes(String[] excludes) {
        this.excludes = excludes;
    }

}