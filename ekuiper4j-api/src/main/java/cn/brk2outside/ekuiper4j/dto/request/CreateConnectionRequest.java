package cn.brk2outside.ekuiper4j.dto.request;


import cn.brk2outside.ekuiper4j.model.ReusableConn;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

/**
 * <p> </p>
 *
 * @author liushenglong_8597@outlook.com
 * @since 2025/5/14
 */
@Data
@ToString
public class CreateConnectionRequest<T extends ReusableConn> {

    @NotNull
    private String id;
    @NotNull
    private String typ;
    @Valid
    private T props;

}
