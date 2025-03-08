package br.com.jbssistemas.efdclient.controller;

import br.com.jbssistemas.efdclient.response.PaginatedResponse;
import br.com.jbssistemas.efdclient.response.dto.UserDto;
import br.com.jbssistemas.efdclient.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<UserDto>> getPaginatedResponse(
            @RequestParam(required = false, defaultValue = "0") int pageNumber,
            @RequestParam(required = false, defaultValue = "25") int pageSize
    ) {

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<UserDto> page = userService.read(pageRequest);

        PaginatedResponse<UserDto> response = PaginatedResponse.<UserDto>builder()
                .items(page.getContent())
                .pageSize(page.getPageable().getPageSize())
                .pages(page.getTotalPages())
                .totalRecords(page.getTotalElements())
                .build();

        return ResponseEntity.ok(response);

    }

}
