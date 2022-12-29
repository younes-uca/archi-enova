package ma.archi.smart.archzy.controller;

import ma.archi.smart.archzy.audit.AuditBusinessObject;
import ma.archi.smart.archzy.converter.AbstractConverter;
import ma.archi.smart.archzy.criteria.BaseCriteria;
import ma.archi.smart.archzy.dto.AuditEntityDto;
import ma.archi.smart.archzy.dto.BaseDto;
import ma.archi.smart.archzy.exception.GlobalException;
import ma.archi.smart.archzy.export.ExportModel;
import ma.archi.smart.archzy.history.HistBusinessObject;
import ma.archi.smart.archzy.service.IService;
import ma.archi.smart.archzy.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class AbstractController<T extends AuditBusinessObject, DTO extends BaseDto, H extends HistBusinessObject, Criteria extends BaseCriteria, HistoryCriteria extends BaseCriteria, SERV extends IService<T, DTO, Criteria, HistoryCriteria>, CONV extends AbstractConverter<T, DTO, H>> {

    protected SERV service;
    protected AbstractConverter<T, DTO, H> abstractConverter;
    @Autowired
    private MessageSource messageSource;
    @Value("${reporting.url}")
    private String REPORTING_URL;
    @Value("${jwt.header}")
    private String tokenHeader;
    @Autowired
    private JwtUtils jwtUtil;
    @Value("${uploads.location.directory}")
    private String UPLOADED_FOLDER;

    public AbstractController(SERV service, CONV abstractConverter) {
        this.service = service;
        this.abstractConverter = abstractConverter;
    }

    // Download file
    public static ResponseEntity<InputStreamResource> getExportedFileResource(ExportModel exportModel, String uploadFolder) throws Exception {
        if (exportModel != null && exportModel.getList() != null && !exportModel.getList().isEmpty()) {
            String fichier = ExportUtil.exportedList(exportModel, uploadFolder);
            File file = new File(fichier);
            FileInputStream inputStream = new FileInputStream(file);
            InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
            String fileName = FileUtils.getFileName(file.getName());
            return ResponseEntity.ok().eTag(fileName).contentLength(file.length()).contentType(MediaType.parseMediaType(Files.probeContentType(file.toPath()))).body(inputStreamResource);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    protected static ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(200000);
        factory.setConnectTimeout(200000);
        return factory;
    }

    private static boolean isNotEmpty(ExportModel exportModel) {
        return exportModel != null && exportModel.getList() != null && !exportModel.getList().isEmpty();
    }

    public ResponseEntity<DTO> findById(Long id, String[] includes, String[] excludes) throws Exception {
        DTO dto = service.findById(id);
        return getDtoResponseEntity(dto, includes, excludes);
    }

    public ResponseEntity<DTO> findByIdWithAssociatedList( Long id, String[] includes, String[] excludes) throws Exception {
        DTO dto =  service.findByIdWithAssociatedList(id);
        return getDtoResponseEntity(dto, includes, excludes);
    }
    private  ResponseEntity<DTO> getDtoResponseEntity(DTO dto, String[] includes, String[] excludes) throws Exception {
        if (StringUtil.isNoEmpty(includes) || StringUtil.isNoEmpty(excludes))
            dto = abstractConverter.copyIncludeExclude(dto, includes, excludes);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
    public ResponseEntity<DTO> save(DTO dto) throws Exception {
        dto = service.create(dto);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    public ResponseEntity<DTO> update(DTO dto) throws Exception {
        ResponseEntity<DTO> res = null;
        if (dto.getId() == null)
            res = new ResponseEntity<>(HttpStatus.CONFLICT);
        else {
            dto = service.update(dto);
            res = new ResponseEntity<>(dto, HttpStatus.OK);
        }
        return res;
    }

    public ResponseEntity<Void> delete(List<DTO> dtos) throws Exception {
        ResponseEntity<Void> res = null;
        if (dtos == null || dtos.isEmpty())
            res = new ResponseEntity<Void>(HttpStatus.CONFLICT);
        else {
            service.delete(dtos);
            res = new ResponseEntity<>(HttpStatus.OK);
        }
        return res;
    }

    public ResponseEntity<List<DTO>> findMultipleByCriteria(Criteria criteria) throws Exception {
        ResponseEntity<List<DTO>> res = null;
        List<DTO> list = service.findMultipleByCriteria(criteria);
        if (criteria != null) {
            list = abstractConverter.copyIncludeExclude(list, criteria.getIncludes(), criteria.getExcludes());
        }
        if (list == null || list.isEmpty())
            res = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else
            res = new ResponseEntity<>(list, HttpStatus.OK);
        return res;
    }

    public ResponseEntity<List<DTO>> findAll() throws Exception {
        return findMultipleByCriteria(null);
    }

    public ResponseEntity<PaginatedList> findPaginatedByCriteria(Criteria criteria) throws Exception {
        List<DTO> list = service.findPaginatedByCriteria(criteria, criteria.getPage(), criteria.getMaxResults(), criteria.getSortOrder(), criteria.getSortField());
        list = abstractConverter.copyIncludeExclude(list, criteria.getIncludes(), criteria.getExcludes());
        PaginatedList paginatedList = new PaginatedList();
        paginatedList.setList(list);
        if (list != null && !list.isEmpty()) {
            int dateSize = service.getDataSize(criteria);
            paginatedList.setDataSize(dateSize);
        }
        return new ResponseEntity<>(paginatedList, HttpStatus.OK);
    }

    public ResponseEntity<InputStreamResource> export(Criteria criteria) throws Exception {
        ResponseEntity<InputStreamResource> res = null;
        if (criteria.getExportModel() == null)
            res = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else {
            criteria.setMaxResults(null);
            List<DTO> list = service.findMultipleByCriteria(criteria);
            criteria.getExportModel().setList(list);
            res = getExportedFileResource(criteria.getExportModel());
        }
        return res;
    }

    public ResponseEntity<Integer> getDataSize(Criteria criteria) throws Exception {
        int count = service.getDataSize(criteria);
        return new ResponseEntity<Integer>(count, HttpStatus.OK);

    }

    public ResponseEntity<AuditEntityDto> findHistoryById(Long id) throws Exception {
        AuditEntityDto h = service.findHistoryById(id);
        return new ResponseEntity<>(h, HttpStatus.OK);
    }

    public ResponseEntity<PaginatedList> findHistoryPaginatedByCriteria(HistoryCriteria criteria) throws Exception {
        List<AuditEntityDto> list = service.findHistoryPaginatedByCriteria(criteria, criteria.getPage(), criteria.getMaxResults(), criteria.getSortOrder(), criteria.getSortField());
        PaginatedList paginatedList = new PaginatedList();
        paginatedList.setList(list);
        if (list != null && !list.isEmpty()) {
            int dateSize = service.getHistoryDataSize(criteria);
            paginatedList.setDataSize(dateSize);
        }
        return new ResponseEntity<PaginatedList>(paginatedList, HttpStatus.OK);
    }

    public ResponseEntity<InputStreamResource> exportHistory(@RequestBody HistoryCriteria criteria) throws Exception {
        if (criteria.getExportModel() == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        criteria.setMaxResults(null);
        return null;//TODO correct this bug

    }

    public ResponseEntity<Integer> getHistoryDataSize(@RequestBody HistoryCriteria criteria) throws Exception {
        int count = service.getHistoryDataSize(criteria);
        return new ResponseEntity<Integer>(count, HttpStatus.OK);

    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> exceptionHandler(Exception e, HttpServletRequest request) throws IOException {
        GlobalException globalException = new GlobalException(e, messageSource, request.getRequestURI());
        ErrorResponse errorResponse = new ErrorResponse(globalException.getStatus(), e, globalException.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(errorResponse, globalException.getStatus());
    }

    // Download file
    protected ResponseEntity<InputStreamResource> getExportedFileResource(ExportModel exportModel) throws Exception {
        if (isNotEmpty(exportModel)) {
            String fichier = ExportUtil.exportedList(exportModel, UPLOADED_FOLDER);
            File file = new File(fichier);
            FileInputStream inputStream = new FileInputStream(file);
            InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
            String fileName = FileUtils.getFileName(file.getName());
            return ResponseEntity.ok().eTag(fileName).contentLength(file.length()).contentType(MediaType.parseMediaType(Files.probeContentType(file.toPath()))).body(inputStreamResource);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Download file
    protected ResponseEntity<InputStreamResource> getFileResource(String fichier, String fileName) throws Exception {
        if (fichier != null && !fichier.isEmpty()) {
            File file = new File(UPLOADED_FOLDER + fichier);
            if (file.exists()) {
                FileInputStream inputStream = new FileInputStream(file);
                InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
                return ResponseEntity.ok().eTag(fileName).contentLength(file.length()).contentType(MediaType.parseMediaType(Files.probeContentType(file.toPath()))).body(inputStreamResource);
            }
        }
        return new ResponseEntity<InputStreamResource>(HttpStatus.NOT_FOUND);
    }




}
