package errs;

import org.jilt.Builder;

@Builder(toBuilder = "copy")
public record ErrorInfoDto(String uri, String exception) {

}