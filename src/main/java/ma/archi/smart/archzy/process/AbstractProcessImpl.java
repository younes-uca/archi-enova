package ma.archi.smart.archzy.process;

import ma.archi.smart.archzy.dto.AuditBaseDto;
import ma.archi.smart.archzy.util.RefelexivityUtil;
import org.springframework.beans.BeanUtils;

public abstract class AbstractProcessImpl<T extends AbstractProcessInput, K> {


    public Result<T,K> execute(T input, K output) {
        Result<T, K> result = new Result();
        result.setInput(input);
        result.setOutput(output);
        init(input, output);
        BeanUtils.copyProperties(input, output);
        validate(input, output, result);
        if (result.hasNoError()) {
            run(input, output, result);
        } else {
            cleanUp(input, output, result);
        }
        return result;
    }

    public void cleanUp(T input, K output, Result<T, K> result) {
    }

    public void init(T input, K output) {

    }


    public abstract void validate(T input, K output, Result<T, K> result);

    public abstract void run(T input, K output, Result<T, K> result);

    public <D extends AuditBaseDto, I extends AbstractProcessInput> D constructDto(I input, Class<D> classDto) {
        D dto =  RefelexivityUtil.constructObjectUsingDefaultConstr(classDto);
        BeanUtils.copyProperties(input,dto);
        return dto;
    }

}
