package com.archi.smart.archzy.service;

import com.archi.smart.archzy.bean.BusinessObject;
import com.archi.smart.archzy.dto.AuditEntityDto;
import com.archi.smart.archzy.dto.BaseDto;
import com.archi.smart.archzy.criteria.BaseCriteria;

import java.util.List;

public interface IService<T extends BusinessObject, DTO extends BaseDto, Criteria extends BaseCriteria, HistoryCriteria> {

    DTO create(DTO dto) throws Exception;

    DTO update(DTO dto) throws Exception;

    DTO findById(Long id) throws Exception;

    DTO findByIdWithAssociatedList(Long id) throws Exception;

    List<DTO> findMultipleByCriteria(Criteria critera) throws Exception;

    List<DTO> findPaginatedByCriteria(Criteria critera, int page, int pageSize, String order, String sortField) throws Exception;

    int getDataSize(Criteria criteria) throws Exception;

    void delete(List<DTO> dtos) throws Exception;


    // History

    AuditEntityDto findHistoryById(Long id) throws Exception;

    List<AuditEntityDto> findHistoryByCriteria(HistoryCriteria historyCriteria) throws Exception;

    List<AuditEntityDto> findHistoryPaginatedByCriteria(HistoryCriteria historyCriteria, int page, int pageSize, String order, String sortField) throws Exception;

    int getHistoryDataSize(HistoryCriteria historyCriteria) throws Exception;

}
